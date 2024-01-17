package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.controllers;

import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.dtos.DateSondageDto;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.DateSondage;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services.DateSondageService;
import jakarta.persistence.NoResultException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

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
     * @param dto
     * @return
     */
    @PostMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public DateSondageDto createDate(@PathVariable("id") Long id, @RequestBody DateSondageDto dto) {
        if(dto.getDate() == null || dto.getDate().before(new Date())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vérifier la date");
        try{
            var model = mapper.map(dto, DateSondage.class);
            var result = service.create(id, model);
            return mapper.map(result, DateSondageDto.class);
        } catch(DataIntegrityViolationException e){
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Il existe déjà une date correspondante à ce sondage ou bien, le sondage n'existe pas");
        }
        catch(NoSuchElementException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la création du commentaire");
        }
    }

    /**
     * Récupération d'une date d'un sondage
     * @param id
     * @return
     */
    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<DateSondageDto> getDates(@PathVariable("id") Long id) {
        try {
            var models = service.getBySondageId(id);
            if (models.isEmpty()) throw new NoResultException();
            return models.stream()
                    .map(model -> mapper.map(model, DateSondageDto.class))
                    .collect(Collectors.toList());
        }
        catch(NoSuchElementException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        catch(NoResultException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Aucune date de sondage n'a été trouvée");
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la création du commentaire");
        }
    }

    /**
     * Suppression d'une date d'un sondage
     * @param id
     */
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") Long id) {
        try {
            service.delete(id);
        }
        catch(NoSuchElementException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la création du commentaire");
        }
    }
}
