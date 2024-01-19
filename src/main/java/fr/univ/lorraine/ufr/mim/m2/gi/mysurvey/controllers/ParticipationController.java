package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.controllers;

import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.dtos.DateSondeeDto;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.exception.DateSondeeAlreadyExistsException;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.exception.SondageCloturedException;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Choix;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.DateSondee;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services.DateSondeeService;
import jakarta.persistence.NoResultException;
import org.apache.commons.lang3.EnumUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "/api/participer")
public class ParticipationController {

    @Autowired
    private DateSondeeService dateSondeeService;

    @Autowired
    private ModelMapper mapper;

    /**
     * Ajout d'un participant (A utiliser sans dateSondeeId)
     * Verifier
     * @param id DateSondage
     * @param dto DateSondeeDto
     * @return DateSondeeDto
     */
    @PostMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public DateSondeeDto create(@PathVariable("id") Long id, @RequestBody DateSondeeDto dto) {
        if(dto.getParticipant() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Précisez un participant.");
        if(!EnumUtils.isValidEnum(Choix.class, dto.getChoix()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Précisez un choix valide.");
        try {
            dateSondeeService.checkIfDateSondeeAlreadyExists(id, dto.getParticipant());
            DateSondee model = mapper.map(dto, DateSondee.class);
            var result = dateSondeeService.create(id, dto.getParticipant(), model);
            return mapper.map(result, DateSondeeDto.class);
        } catch (DateSondeeAlreadyExistsException | SondageCloturedException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (NoResultException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la création de la participation.");
        }
    }
}
