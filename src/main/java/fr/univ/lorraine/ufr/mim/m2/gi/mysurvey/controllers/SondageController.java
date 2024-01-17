package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.controllers;

import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.dtos.CommentaireDto;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.dtos.DateSondageDto;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.dtos.SondageDto;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Commentaire;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.DateSondage;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Sondage;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services.CommentaireService;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services.DateSondageService;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services.DateSondeeService;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services.SondageService;
import jakarta.servlet.http.HttpServletResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/sondage")
public class SondageController {

    @Autowired
    private SondageService service;

    @Autowired
    private CommentaireService scommentaire;

    @Autowired
    private DateSondageService sdate;

    @Autowired
    private DateSondeeService request;

    @Autowired
    private ModelMapper mapper;

    public SondageController(SondageService service, ModelMapper mapper, CommentaireService c, DateSondageService d, DateSondeeService r) {
        this.service = service;
        this.mapper = mapper;
        this.sdate = d;
        this.scommentaire = c;
        this.request = r;
    }

    /**
     * Get ALl sondages
     * Verifier
     * @return
     */
    @GetMapping(value = "/")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<SondageDto> get() {
        var models = service.getAll();

        return models.stream()
                .map(model -> mapper.map(model, SondageDto.class))
                .collect(Collectors.toList());
    }

    /**
     * Get sondage by id
     * Verified
     * @param id
     * @return
     */
    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public SondageDto get(@PathVariable("id") Long id) {
        try{
            var model = service.getById(id);
            return mapper.map(model, SondageDto.class);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sondage non trouvé", null);
        }
    }

    /**
     * Get Meilleur date d'un sondage
     * Verifier
     * @param id
     * @return
     */
    @GetMapping(value = "/{id}/best")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<Date> getBest(@PathVariable("id") Long id) {
        try {
            return request.bestDate(id);
        }catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sondage non trouvé", null);
        }
    }

    /**
     * Get Possible meilleur date d'un sondage
     * Verifier
     * @param id
     * @return
     */
    @GetMapping(value = "/{id}/maybe")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<Date> getMaybeBest(@PathVariable("id") Long id) {
        try {
            return request.maybeBestDate(id);
        }catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sondage non trouvé", null);
        }
    }

    /**
     * Create a sondage
     * Vérifier
     * @param sondageDto
     * @return
     */
    @PostMapping(value = "/")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public SondageDto create(@RequestBody SondageDto sondageDto, HttpServletResponse response) {
        sondageDto.setCloture(false);
        verifySondage(sondageDto);
        var model = mapper.map(sondageDto, Sondage.class);
        if(model.getFin().before(new Date())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"La date de fin doit être supérieure à la date de début",null);
        var result = service.create(sondageDto.getCreateBy(), model);
        return mapper.map(result, SondageDto.class);
    }

    private void verifySondage(@RequestBody SondageDto sondageDto) {
        if(sondageDto.getCreateBy() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Le sondage doit avoir un créateur",null);
        if(sondageDto.getNom() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Le sondage doit avoir un nom",null);
        if(sondageDto.getDescription() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Le sondage doit avoir une description",null);
    }

    /**
     * Update a sondage
     * Verifier
     * @param id
     * @param sondageDto
     * @return
     */
    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public SondageDto update(@PathVariable("id") Long id, @RequestBody SondageDto sondageDto) {
        verifySondage(sondageDto);
        Sondage model;
        try {
            model = mapper.map(sondageDto, Sondage.class);
            var result = service.update(id, model);
            return mapper.map(result, SondageDto.class);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sondage non trouvé", null);
        }
    }

    /**
     * Delete a sondage
     * Verifier
     * @param id
     */
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") Long id) {
        service.delete(id);
    }
}
