package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.controllers;

import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.dtos.CommentaireDto;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.dtos.ParticipantDto;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Participant;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services.ParticipantService;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.utils.StringUtils;
import jakarta.persistence.NoResultException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/participant")
public class ParticipantController {

    @Autowired
    private ParticipantService service;

    @Autowired
    private ModelMapper mapper;

    /**
     * Create a participant
     * @param participantDto
     * @return
     */
    @PostMapping(value = "/")
    @ResponseStatus(HttpStatus.CREATED)
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
     * @return
     */
    @GetMapping(value = "/")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipantDto> getAllParticipants() {
        try {
            var models = service.getAll();
            return models.stream().map(model -> mapper.map(model, ParticipantDto.class)).toList();
        }
        catch(NoResultException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la récupération des participants.");
        }
    }
    /**
     * Get participant by id
     * @param id
     * @return
     */
    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
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
     * @param id
     * @param participantDto
     * @return
     */
    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ParticipantDto update(@PathVariable("id") Long id, @RequestBody ParticipantDto participantDto) {
        if(StringUtils.isNullOrEmpty(participantDto.getNom()) && StringUtils.isNullOrEmpty(participantDto.getPrenom()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Précisez au moins un nom ou un prénom.");
        try {
            if (mapper.map(service.getById(id), ParticipantDto.class).equals(participantDto))
                throw new ResponseStatusException(HttpStatus.NO_CONTENT, "Le participant n'a pas été modifié.");
            var model = mapper.map(participantDto, Participant.class);
            var result = service.update(id, model);
            return mapper.map(result, ParticipantDto.class);
        }
        catch(NoSuchElementException | NoResultException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la mise à jour du participant.");
        }
    }

    /**
     * Delete a participant
     * @param id
     */
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
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