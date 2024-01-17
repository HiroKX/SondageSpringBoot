package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services;

import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Sondage;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.repositories.SondageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SondageService {

    private final SondageRepository repository;
    private final ParticipantService participantService;

    public SondageService(SondageRepository repository, ParticipantService p) {
        this.repository = repository;
        this.participantService = p;
    }

    public Sondage getById(Long id) {
        return repository.getReferenceById(id);
    }

    public List<Sondage> getAll() {
        return repository.findAll();
    }

    public Sondage create(Long idParticipant, Sondage sondage) {
        sondage.setCreateBy(this.participantService.getById(idParticipant));
        return repository.save(sondage);
    }

    public Sondage update(Long id, Sondage sondage) {
        Optional<Sondage> sond = repository.findById(id);
        if (sond.isPresent()) {
            sondage.setCreateBy(sond.get().getCreateBy());
            return repository.save(sondage);
        }
        return null;
    }

    public boolean delete(Long id) {
        if (repository.findById(id).isPresent()) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean exists(Long id) {
        return repository.findById(id).isPresent();
    }
}
