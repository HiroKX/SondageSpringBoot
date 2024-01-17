package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.controllers;

import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.dtos.CommentaireDto;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Commentaire;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services.CommentaireService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/commentaire")
public class CommentaireController {

    @Autowired
    private CommentaireService service;

    @Autowired
    private ModelMapper mapper;

    /**
     * Modifier un commentaire
     * @param id du commentaire
     * @param commentaireDto
     * @return
     */
    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CommentaireDto update(@PathVariable("id") Long id, @RequestBody CommentaireDto commentaireDto) {
        var model = mapper.map(commentaireDto, Commentaire.class);
        if(commentaireDto.getCommentaire() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vérifier le commentaire");
        if(commentaireDto.getParticipant() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vérifier le participant");
        var result = service.update(id, model);
        if(result == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return mapper.map(result, CommentaireDto.class);
    }

    /**
     *
     * @param id du sondage
     * @param commentaireDto
     * @return
     */
    @PostMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public CommentaireDto createCommentaire(@PathVariable("id") Long id, @RequestBody CommentaireDto commentaireDto) {
        var model = mapper.map(commentaireDto, Commentaire.class);
        if(commentaireDto.getCommentaire() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vérifier le commentaire");
        if(commentaireDto.getParticipant() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vérifier le participant");
        try{
            var result = service.addCommantaire(id, commentaireDto.getParticipant(), model);
            return mapper.map(result, CommentaireDto.class);
        }catch(DataIntegrityViolationException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Le sondage n'existe pas");
        }
    }

    /**
     * Récupération des commentaires d'un sondage
     * @param id
     * @return
     */
    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<CommentaireDto> getCommentaires(@PathVariable("id") Long id) {
        var models = service.getBySondageId(id);
        return models.stream()
                .map(model -> mapper.map(model, CommentaireDto.class))
                .collect(Collectors.toList());
    }

    /**
     * Suppression d'un commentaire
     * @param id
     */
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") Long id) {
        if(!service.delete(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
}
