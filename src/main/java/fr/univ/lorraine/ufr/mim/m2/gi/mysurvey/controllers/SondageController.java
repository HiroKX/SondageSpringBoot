package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.controllers;

import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.dtos.SondageDto;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.exception.NoUpdateException;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Sondage;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services.DateSondeeService;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services.SondageService;
import jakarta.persistence.NoResultException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping(value = "/api/sondage")
public class SondageController {

    @Autowired
    private SondageService service;

    @Autowired
    private DateSondeeService request;

    @Autowired
    private ModelMapper mapper;

    /**
     * Create a sondage
     * @param sondageDto sondage
     * @return sondage
     */
    @PostMapping(value = "/")
    @ResponseStatus(HttpStatus.CREATED)
    public SondageDto create(@RequestBody SondageDto sondageDto) {
        if(sondageDto.getFin() == null || sondageDto.getFin().before(new Date()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La date de fin doit être supérieure à la date de début.");
        if(sondageDto.getCreateBy() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le sondage doit avoir un créateur.");
        if(sondageDto.getNom() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le sondage doit avoir un nom.");
        if(sondageDto.getDescription() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le sondage doit avoir une description.");
        if(sondageDto.getCloture() == null || sondageDto.getCloture())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le sondage doit être ouvert.");
        try {
            var model = mapper.map(sondageDto, Sondage.class);
            var result = service.create(sondageDto.getCreateBy(), model);
            return mapper.map(result, SondageDto.class);
        }
        catch(NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la création du sondage.");
        }
    }

    /**
     * Get ALl sondages
     * @return List de sondage
     */
    @GetMapping(value = "/")
    @ResponseStatus(HttpStatus.OK)
    public List<SondageDto> getAllSondages() {
        try {
            var models = service.getAll();
            return models.stream().map(model -> mapper.map(model, SondageDto.class)).toList();
        }
        catch(NoResultException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
        catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la récupération des sondages.");
        }
    }

    /**
     * Get sondage by id
     * @param id sondage
     * @return sondage
     */
    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public SondageDto getSondageById(@PathVariable("id") Long id) {
        try{
            var model = service.getById(id);
            return mapper.map(model, SondageDto.class);
        }
        catch(NoResultException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la récupération du sondage.");
        }
    }

    /**
     * Get Meilleur date d'un sondage
     * @param id sondage
     * @return List des meilleures dates
     */
    @GetMapping(value = "/{id}/best")
    @ResponseStatus(HttpStatus.OK)
    public List<Date> getBestDateBySondageId(@PathVariable("id") Long id) {
        try {
            return request.getBestDateBySondageId(id);
        }
        catch(NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la récupération du sondage.");
        }
    }

    /**
     * Get Possible meilleur date d'un sondage
     * @param id sondage
     * @return Liste des potentielles meilleures dates
     */
    @GetMapping(value = "/{id}/maybe")
    @ResponseStatus(HttpStatus.OK)
    public List<Date> getMaybeBestBySondageId(@PathVariable("id") Long id) {
        try {
            return request.getMaybeBestDateBySondageId(id);
        }
        catch(NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la récupération du sondage.");
        }
    }

    /**
     * Update a sondage
     * @param id id du sondage
     * @param sondageDto sondageDto
     * @return SondageDto
     */
    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public SondageDto update(@PathVariable("id") Long id, @RequestBody SondageDto sondageDto) {
        if(sondageDto.getFin() == null || sondageDto.getFin().before(new Date()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La date de fin doit être supérieure à la date de début.");
        try {
            if (service.getById(id).equals(mapper.map(sondageDto, Sondage.class)))
                throw new NoUpdateException("Le sondage n'a pas été modifié.");
            Sondage model = mapper.map(sondageDto, Sondage.class);
            var result = service.update(id, model);
            return mapper.map(result, SondageDto.class);
        }
        catch(NoUpdateException e) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, e.getMessage());
        }
        catch(NoResultException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la mise à jour du sondage.");
        }
    }

    /**
     * Delete a sondage
     * @param id du sondage
     */
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        try {
            service.delete(id);
        }
        catch(NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la suppression du sondage.");
        }
    }
}
