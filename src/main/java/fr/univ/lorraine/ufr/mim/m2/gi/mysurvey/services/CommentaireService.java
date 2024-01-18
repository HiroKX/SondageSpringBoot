package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services;

import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Commentaire;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.repositories.CommentaireRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CommentaireService {

    @Autowired
    private CommentaireRepository repository;

    @Autowired
    private SondageService sondageService;

    @Autowired
    private ParticipantService participantService;

    public List<Commentaire> getBySondageId(Long sondageId) {
        if (!sondageService.exists(sondageId)) throw new NoSuchElementException("Le sondage n'existe pas");
        return repository.getAllBySondage(sondageId);
    }

    public Commentaire create(Long idSondage, Long idParticipant, Commentaire commentaire) {
        if (!sondageService.exists(idSondage)) throw new NoSuchElementException("Le sondage n'existe pas");
        commentaire.setSondage(sondageService.getById(idSondage));
        if (!participantService.exists(idParticipant)) throw new NoSuchElementException("Le participant n'existe pas");
        commentaire.setParticipant(participantService.getById(idParticipant));
        return repository.save(commentaire);
    }

    public Commentaire update(Long id, Commentaire newCommentaire) {
        if (!exists(id)) throw new NoSuchElementException("Le commentaire n'existe pas");
        var commentaire = repository.getReferenceById(id);
        commentaire.setCommentaire(newCommentaire.getCommentaire());
        return repository.save(commentaire);
    }

    public void delete(Long id) {
        if (!exists(id)) throw new NoSuchElementException("Le commentaire n'existe pas");
        repository.deleteById(id);
    }

    public boolean exists(Long id) {
        return repository.findById(id).isPresent();
    }
}
