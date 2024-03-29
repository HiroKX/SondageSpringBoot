package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services;

import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.dtos.DateSondageDto;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.exception.DateSondageAlreadyExistsException;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.DateSondage;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.repositories.DateSondageRepository;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.utils.ErrorMessages;
import jakarta.persistence.NoResultException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class DateSondageService {

    @Autowired
    private DateSondageRepository repository;

    @Autowired
    private SondageService sondageService;

    public DateSondage create(Long id, DateSondage date){
        if (!sondageService.exists(id))
            throw new NoSuchElementException(ErrorMessages.SONDAGE_DOES_NOT_EXISTS);
        date.setSondage(sondageService.getById(id));
        return repository.save(date);
    }

    public DateSondage getById(Long id) {
        if(!exists(id))
            throw new NoResultException(ErrorMessages.DATE_DOES_NOT_EXISTS);
        return repository.getReferenceById(id);
    }

    public List<DateSondage> getBySondageId(Long sondageId) {
        if (!sondageService.exists(sondageId))
            throw new NoSuchElementException(ErrorMessages.SONDAGE_DOES_NOT_EXISTS);
        List<DateSondage> dateSondages = repository.getAllBySondage(sondageId);
        if (dateSondages.isEmpty())
            throw new NoResultException("Aucune date de sondage n'a été trouvé.");
        return dateSondages;
    }

    public void delete(Long id) {
        if (!exists(id))
            throw new NoSuchElementException(ErrorMessages.DATE_DOES_NOT_EXISTS);
        repository.deleteById(id);
    }

    public boolean exists(Long id) {
        return repository.existsById(id);
    }

    public void checkIfDateAlreadyExists(Long sondageId, DateSondageDto dto) throws DateSondageAlreadyExistsException {
        sondageService.exists(sondageId);
        if(dto.getDate()==null)
            throw new NoSuchElementException(ErrorMessages.DATE_DOES_NOT_EXISTS);
        List<DateSondage> datesSondages = repository.getAllBySondage(sondageId);
        Calendar calendarDTO = Calendar.getInstance();
        calendarDTO.setTime(dto.getDate());
        Calendar calendarCheck = Calendar.getInstance();
        for (DateSondage dateSondage : datesSondages) {
            calendarCheck.setTime(dateSondage.getDate());
            if (!calendarDTO.getTime().equals(dto.getDate()) || (
                    calendarDTO.getWeekYear() == calendarCheck.getWeekYear() &&
                    calendarDTO.get(Calendar.MONTH) == calendarCheck.get(Calendar.MONTH) &&
                    calendarDTO.get(Calendar.DAY_OF_MONTH) == calendarCheck.get(Calendar.DAY_OF_MONTH) &&
                    calendarDTO.get(Calendar.HOUR) == calendarCheck.get(Calendar.HOUR) &&
                    calendarDTO.get(Calendar.MINUTE) == calendarCheck.get(Calendar.MINUTE)))
                throw new DateSondageAlreadyExistsException();
        }
    }
}
