package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.controllers;

import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.dtos.ParticipantDto;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.exception.NoUpdateException;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Participant;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services.ParticipantService;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.utils.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.NoResultException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping(value = "/api/participant")
public class ParticipantController {

    @Autowired
    private ParticipantService service;

    @Autowired
    private ModelMapper mapper;

    /**
     * Create a participant
     * @param participantDto du participant à créer
     * @return participant créé
     */
    @PostMapping(value = "/")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer un participant", description = "Retourne le participant créé.")
    public ParticipantDto create(@RequestBody ParticipantDto participantDto) {
        if(StringUtils.isNullOrEmpty(participantDto.getNom()) || StringUtils.isNullOrEmpty(participantDto.getPrenom()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Précisez un nom et un prénom.");
        try {
            var model = mapper.map(participantDto, Participant.class);
            var result = service.create(model);
            return mapper.map(result, ParticipantDto.class);
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la création du participant.");
        }
    }

    /**
     * Get all participants
     * @return liste de tous les participants existants
     */
    @GetMapping(value = "/")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Récupérer tous les participants", description = "Retourne tous les participants existants.")
    public List<ParticipantDto> getAllParticipants() {
        try {
            var models = service.getAll();
            return models.stream().map(model -> mapper.map(model, ParticipantDto.class)).toList();
        }
        catch(NoResultException e) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, e.getMessage());
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la récupération des participants.");
        }
    }
    /**
     * Get participant by id
     * @param id du participant à récupérer
     * @return participant correspondant à l'id
     */
    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Récupérer un participant par son id", description = "Retourne le participant s'il existe.")
    public ParticipantDto getParticipantById(@PathVariable("id") Long id) {
        try{
            var model = service.getById(id);
            return mapper.map(model, ParticipantDto.class);
        }
        catch(NoResultException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la récupération du participant.");
        }
    }

    /**
     * Update a participant
     * @param id du participant à modifier
     * @param participantDto du participant à modifier
     * @return participant modifié
     */
    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Modifier un participant", description = "Retourne le participant modifié.")
    public ParticipantDto update(@PathVariable("id") Long id, @RequestBody ParticipantDto participantDto) {
        if(StringUtils.isNullOrEmpty(participantDto.getNom()) || StringUtils.isNullOrEmpty(participantDto.getPrenom()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Précisez au moins un nom ou un prénom.");
        try {
            if (mapper.map(service.getById(id), ParticipantDto.class).equals(participantDto))
                throw new NoUpdateException("Le participant n'a pas été modifié.");
            var model = mapper.map(participantDto, Participant.class);
            var result = service.update(id, model);
            return mapper.map(result, ParticipantDto.class);
        }
        catch(NoUpdateException e) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, e.getMessage());
        }
        catch(NoResultException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
        catch(NoSuchElementException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la mise à jour du participant.");
        }
    }

    /**
     * Delete a participant
     * @param id du participant à supprimer
     */
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer un participant", description = "Ne retourne rien.")
    public void delete(@PathVariable("id") Long id) {
        try {
            service.delete(id);
        }
        catch(NoSuchElementException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la suppression du commentaire.");
        }
    }
}