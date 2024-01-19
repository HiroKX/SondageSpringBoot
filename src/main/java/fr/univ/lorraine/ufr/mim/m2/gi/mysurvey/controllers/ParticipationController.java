package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.controllers;

import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.dtos.DateSondeeDto;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.exception.DateSondageAlreadyExistsException;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.exception.SondageCloturedException;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Choix;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.DateSondee;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services.DateSondeeService;
import io.swagger.v3.oas.annotations.Operation;
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
     * @param id de la DateSondage
     * @param dto DateSondeeDto de la participation
     * @return DateSondee créée
     */
    @PostMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer un vote d'un participant sur une date", description = "Retourne l'objet DateSondee créé correspondant à la participation créée.")
    public DateSondeeDto create(@PathVariable("id") Long id, @RequestBody DateSondeeDto dto) {
        if(dto.getParticipant() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Précisez un participant.");
        if(!EnumUtils.isValidEnum(Choix.class, dto.getChoix()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Précisez un choix valide.");
        try {
            DateSondee model = mapper.map(dto, DateSondee.class);
            var result = dateSondeeService.create(id, dto.getParticipant(), model);
            return mapper.map(result, DateSondeeDto.class);
        } catch (DateSondageAlreadyExistsException | SondageCloturedException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (NoResultException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la création de la participation.");
        }
    }
}
