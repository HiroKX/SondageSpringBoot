package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services;

import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.DateSondage;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.repositories.DateSondageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class DateSondageService {

    @Autowired
    private DateSondageRepository repository;

    @Autowired
    private SondageService sondageService;

    public DateSondage getById(Long id) {
        return repository.getReferenceById(id);
    }

    public List<DateSondage> getBySondageId(Long sondageId) {
        if (!sondageService.exists(sondageId)) throw new NoSuchElementException("Le sondage n'existe pas");
        return repository.getAllBySondage(sondageId);
    }

    public DateSondage create(Long id, DateSondage date){
        if (!sondageService.exists(id)) throw new NoSuchElementException("Le sondage n'existe pas");
        date.setSondage(sondageService.getById(id));
        return repository.save(date);
    }

    public void delete(Long id) {
        if (!exists(id)) throw new NoSuchElementException("La date de sondage n'existe pas");
        repository.deleteById(id);
    }

    public boolean exists(Long id) {
        return repository.findById(id).isPresent();
    }
}
