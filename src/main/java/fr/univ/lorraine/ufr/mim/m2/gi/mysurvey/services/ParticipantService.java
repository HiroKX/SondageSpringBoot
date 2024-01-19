package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services;

import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Participant;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.repositories.ParticipantRepository;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.utils.ErrorMessages;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.utils.StringUtils;
import jakarta.persistence.NoResultException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ParticipantService {

    @Autowired
    private ParticipantRepository repository;

    public Participant create(Participant participant) {
        return repository.save(participant);
    }

    public Participant getById(Long id) {
        if(!exists(id))
            throw new NoResultException(ErrorMessages.PARTICIPANT_DOES_NOT_EXISTS);
        return repository.getReferenceById(id);
    }

    public List<Participant> getAll() {
        List<Participant> participants = repository.findAll();
        if (participants.isEmpty())
            throw new NoResultException("Aucun participant n'a été trouvé.");
        return participants;
    }

    public Participant update(Long id, Participant newParticipant) {
        try {
            Participant participant = getById(id);
            if (!StringUtils.isNullOrEmpty(newParticipant.getNom()))
                participant.setNom(newParticipant.getNom());
            if (!StringUtils.isNullOrEmpty(newParticipant.getPrenom()))
                participant.setPrenom(newParticipant.getPrenom());
            return repository.save(participant);
        }
        catch (NoResultException e) {
            throw new NoSuchElementException(ErrorMessages.PARTICIPANT_DOES_NOT_EXISTS);
        }
    }

    public void delete(Long id) {
        if (!exists(id))
            throw new NoSuchElementException(ErrorMessages.PARTICIPANT_DOES_NOT_EXISTS);
        repository.deleteById(id);
    }

    public boolean exists(Long id) {
        return repository.existsById(id);
    }
}
