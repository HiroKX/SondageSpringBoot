package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.controllers;

import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.dtos.CommentaireDto;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Commentaire;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services.CommentaireService;
import jakarta.persistence.NoResultException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;

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
        if(commentaireDto.getCommentaire() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vérifier le commentaire");
        try {
            var model = mapper.map(commentaireDto, Commentaire.class);
            var result = service.update(id, model);
            return mapper.map(result, CommentaireDto.class);
        }
        catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
        if(commentaireDto.getCommentaire() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vérifier le commentaire");
        if(commentaireDto.getParticipant() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vérifier le participant");
        try{
            var model = mapper.map(commentaireDto, Commentaire.class);
            var result = service.create(id, commentaireDto.getParticipant(), model);
            return mapper.map(result, CommentaireDto.class);
        }catch(NoSuchElementException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la création du commentaire");
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
        try {
            var models = service.getBySondageId(id);
            if (models.isEmpty()) throw new NoResultException();
            return models.stream()
                    .map(model -> mapper.map(model, CommentaireDto.class))
                    .toList();
        }
        catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        catch (NoResultException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Aucun commentaire n'a été trouvé");
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Suppression d'un commentaire
     * @param id
     */
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") Long id) {
        try {
            service.delete(id);
        }
        catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
