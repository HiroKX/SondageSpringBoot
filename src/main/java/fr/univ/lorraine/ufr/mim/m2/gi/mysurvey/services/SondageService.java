package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services;

import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Sondage;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.repositories.SondageRepository;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.utils.ErrorMessages;
import jakarta.persistence.NoResultException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class SondageService {

    @Autowired
    private SondageRepository repository;

    @Autowired
    private ParticipantService participantService;

    public Sondage create(Long idParticipant, Sondage sondage) {
        try {
            sondage.setCreateBy(participantService.getById(idParticipant));
            return repository.save(sondage);
        }
        catch (NoResultException e) {
            throw new NoSuchElementException(ErrorMessages.PARTICIPANT_DOES_NOT_EXISTS);
        }
    }

    public Sondage getById(Long id) {
        if(!exists(id))
            throw new NoResultException(ErrorMessages.SONDAGE_DOES_NOT_EXISTS);
        return repository.getReferenceById(id);
    }

    public List<Sondage> getAll() {
        List<Sondage> sondages = repository.findAll();
        if (sondages.isEmpty())
            throw new NoResultException("Aucun sondage n'a été trouvé.");
        return sondages;
    }

    public Sondage update(Long id, Sondage newSondage) {
        Sondage sondage = getById(id);
        if (newSondage.getFin() != null) sondage.setFin(newSondage.getFin());
        if (newSondage.getNom() != null) sondage.setNom(newSondage.getNom());
        if (newSondage.getDescription() != null) sondage.setDescription(newSondage.getDescription());
        if (newSondage.getCloture() != null) sondage.setCloture(newSondage.getCloture());
        return repository.save(sondage);
    }

    public void delete(Long id) {
        if (!exists(id))
            throw new NoSuchElementException(ErrorMessages.SONDAGE_DOES_NOT_EXISTS);
        repository.deleteById(id);
    }

    public boolean exists(Long id) {
        return repository.existsById(id);
    }
}
