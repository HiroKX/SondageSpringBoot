package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services;

import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Commentaire;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.repositories.CommentaireRepository;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.utils.ErrorMessages;
import jakarta.persistence.NoResultException;
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

    public Commentaire create(Long idSondage, Long idParticipant, Commentaire commentaire) {
        if (!sondageService.exists(idSondage))
            throw new NoSuchElementException(ErrorMessages.SONDAGE_DOES_NOT_EXISTS);
        commentaire.setSondage(sondageService.getById(idSondage));
        if (!participantService.exists(idParticipant))
            throw new NoSuchElementException(ErrorMessages.PARTICIPANT_DOES_NOT_EXISTS);
        commentaire.setParticipant(participantService.getById(idParticipant));
        return repository.save(commentaire);
    }

    public Commentaire getById(Long id) {
        if(!exists(id))
            throw new NoResultException(ErrorMessages.COMMENTAIRE_DOES_NOT_EXISTS);
        return repository.getReferenceById(id);
    }

    public List<Commentaire> getBySondageId(Long sondageId) {
        if (!sondageService.exists(sondageId))
            throw new NoSuchElementException(ErrorMessages.SONDAGE_DOES_NOT_EXISTS);
        List<Commentaire> commentaires = repository.getAllBySondage(sondageId);
        if (commentaires.isEmpty())
            throw new NoResultException("Aucun commentaire n'a été trouvé.");
        return commentaires;
    }

    public Commentaire update(Long id, Commentaire newCommentaire) {
        try {
            Commentaire commentaire = getById(id);
            commentaire.setCommentaire(newCommentaire.getCommentaire());
            return repository.save(commentaire);
        }
        catch (NoResultException e) {
            throw new NoSuchElementException(ErrorMessages.COMMENTAIRE_DOES_NOT_EXISTS);
        }
    }

    public void delete(Long id) {
        if (!exists(id))
            throw new NoSuchElementException(ErrorMessages.COMMENTAIRE_DOES_NOT_EXISTS);
        repository.deleteById(id);
    }

    public boolean exists(Long id) {
        return repository.existsById(id);
    }
}
