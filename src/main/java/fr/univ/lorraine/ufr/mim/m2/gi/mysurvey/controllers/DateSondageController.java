package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.controllers;

import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.dtos.DateSondageDto;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.exception.DateSondageAlreadyExistsException;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.DateSondage;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services.DateSondageService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.NoResultException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.TimeZone;

@RestController
@RequestMapping(value = "/api/datesondage")
public class DateSondageController {

    @Autowired
    private DateSondageService service;

    @Autowired
    private ModelMapper mapper;

    /**
     * Création d'une date de sondage pour un sondage
     * @param id d'un sondage
     * @param dto du sondage à créer
     * @return sondage créé
     */
    @PostMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer un sondage", description = "Retourne le sondage créé.")
    public DateSondageDto create(@PathVariable("id") Long id, @RequestBody DateSondageDto dto) {
        TimeZone tz = TimeZone.getDefault();        // pour enlever le fuseau horaire set par le système local
        if (dto.getDate() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Précisez une date valide.");
        Date localDTO = new Date(dto.getDate().getTime() - tz.getRawOffset());
        if(localDTO.before(new Date()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vous ne pouvez pas donner une date dans le passé");
        try{
            service.checkIfDateAlreadyExists(id, dto);
            var model = mapper.map(dto, DateSondage.class);
            var result = service.create(id, model);
            return mapper.map(result, DateSondageDto.class);
        } catch(DateSondageAlreadyExistsException | NoSuchElementException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la création de la date.");
        }
    }

    /**
     * Récupération des dates d'un sondage
     * @param id du sondage
     * @return liste des dates récupérées
     */
    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Récupérer la liste des dates d'un sondage", description = "Retourne la liste récupérée.")
    public List<DateSondageDto> getAllDatesBySondageId(@PathVariable("id") Long id) {
        try {
            var models = service.getBySondageId(id);
            return models.stream()
                    .map(model -> mapper.map(model, DateSondageDto.class))
                    .toList();
        }
        catch(NoSuchElementException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        catch(NoResultException e) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, e.getMessage());
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la récupération des dates.");
        }
    }

    /**
     * Suppression d'une date d'un sondage
     * @param id de la date à supprimer
     */
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer une date de sondage", description = "Ne retourne rien.")
    public void delete(@PathVariable("id") Long id) {
        try {
            service.delete(id);
        }
        catch(NoSuchElementException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la suppression de la date.");
        }
    }
}
