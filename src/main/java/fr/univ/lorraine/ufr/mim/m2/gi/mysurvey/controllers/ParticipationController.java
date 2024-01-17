package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.controllers;

import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.dtos.DateSondeeDto;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.exception.ExceptionSondageClotured;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.DateSondee;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services.DateSondageService;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services.DateSondeeService;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

@RestController
@RequestMapping(value = "/api/participer")
public class ParticipationController {

    @Autowired
    private DateSondeeService sds;

    @Autowired
    private ModelMapper mapper;

    /**
     * Ajout d'un participant (A utiliser sans dateSondeeId)
     * Verifier
     * @param id DateSondage
     * @param dto
     * @return
     */
    @PostMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public DateSondeeDto createParticipation(@PathVariable("id") Long id, @RequestBody DateSondeeDto dto) throws ExceptionSondageClotured{
        if(dto.getParticipant() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vérifier le participant");

        DateSondee model;
        try{
            model = mapper.map(dto, DateSondee.class);
        }catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vérifier le choix qui a été fait");
        }
        try {
            var result = sds.create(id, dto.getParticipant(), model);
            return mapper.map(result, DateSondeeDto.class);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Vous avez déjà voté !");
        } catch(ExceptionSondageClotured e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Le sondage est cloturé");
        } catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Il n'y a pas de date correspondante à ce sondage ou le participant n'existe pas");
        }
    }
}
