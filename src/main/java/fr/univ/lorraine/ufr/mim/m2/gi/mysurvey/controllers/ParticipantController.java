package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.controllers;

import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.dtos.ParticipantDto;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Participant;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services.ParticipantService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/participant")
public class ParticipantController {

    @Autowired
    private ParticipantService service;

    @Autowired
    private ModelMapper mapper;

    public ParticipantController(ParticipantService service, ModelMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }


    /**
     * Get all participants
     * @return
     */
    @GetMapping(value = "/")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<ParticipantDto> get() {
        var models = service.getAll();
        return models.stream().map(model -> mapper.map(model, ParticipantDto.class)).collect(Collectors.toList());
    }

    /**
     * Get participant by id
     * @param id
     * @return
     */
    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ParticipantDto get(@PathVariable("id") Long id) {
        try{
            var model = service.getById(id);
            return mapper.map(model, ParticipantDto.class);
        }catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Le participant n'existe pas");
        }
    }

    /**
     * Create a participant
     * @param participantDto
     * @return
     */
    @PostMapping(value = "/")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ParticipantDto create(@RequestBody ParticipantDto participantDto) {
        if(participantDto.getNom() == null || participantDto.getPrenom() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Le nom et le prénom sont obligatoires");
        var model = mapper.map(participantDto, Participant.class);
        var result = service.create(model);
        if(result == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return mapper.map(result, ParticipantDto.class);
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
        if(participantDto.getNom() == null || participantDto.getPrenom() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Le nom et le prénom sont obligatoires");
        var model = mapper.map(participantDto, Participant.class);
        var result = service.update(id, model);
        if(result == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return mapper.map(result, ParticipantDto.class);
    }

    /**
     * Delete a participant
     * @param id
     */
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") Long id) {
        if(!service.delete(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
}