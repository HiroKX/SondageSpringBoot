package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.controllers;

import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.dtos.SondageDto;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.exception.NoUpdateException;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Sondage;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services.DateSondeeService;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services.SondageService;
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
     * @param sondageDto sondage à créer
     * @return sondage créé
     */
    @PostMapping(value = "/")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer un sondage", description = "Retourne le sondage créé.")
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
     * @return Liste des sondages existants
     */
    @GetMapping(value = "/")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Récupérer tous les sondages", description = "Retourne tous les sondages existants.")
    public List<SondageDto> getAllSondages() {
        try {
            var models = service.getAll();
            return models.stream().map(model -> mapper.map(model, SondageDto.class)).toList();
        }
        catch(NoResultException e) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, e.getMessage());
        }
        catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la récupération des sondages.");
        }
    }

    /**
     * Get sondage by id
     * @param id du sondage
     * @return Sondage correspondant à l'id
     */
    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Récupérer un sondage par son id", description = "Retourne le sondage correspondant.")
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
     * @param id du sondage
     * @return Liste des meilleures dates
     */
    @GetMapping(value = "/{id}/best")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Récupérer les meilleures dates pour un sondage", description = "Retourne la liste des meilleures dates pour un sondage donné.")
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
     * @param id du sondage
     * @return Liste des potentielles meilleures dates
     */
    @GetMapping(value = "/{id}/maybe")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Récupérer les potentielles meilleures dates d'un sondage", description = "Retourne la liste des potentielles meilleures dates pour un sondage donné.")
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
     * @param id du sondage à modifier
     * @param sondageDto sondage à modifier
     * @return sondage modifié
     */
    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Modifier un sondage", description = "Retourne le sondage modifié.")
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
     * @param id du sondage à supprimer
     */
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer un sondage", description = "Ne retourne rien.")
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
