package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services;

import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.exception.DateSondageAlreadyExistsException;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.exception.DateSondeeAlreadyExistsException;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.exception.SondageCloturedException;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.DateSondage;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.DateSondee;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Participant;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.repositories.DateSondeeRepository;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.utils.ErrorMessages;
import jakarta.persistence.NoResultException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class DateSondeeService {

    @Autowired
    private DateSondeeRepository repository;

    @Autowired
    private SondageService sondageService;

    @Autowired
    private DateSondageService dateSondageService;

    @Autowired
    private ParticipantService participantService;

    public DateSondee create(Long dateSondageId, Long participantId, DateSondee dateSondee) throws SondageCloturedException, NoResultException, DateSondageAlreadyExistsException {
        DateSondage date = dateSondageService.getById(dateSondageId);
        if(Boolean.TRUE.equals(date.getSondage().getCloture()))
            throw new SondageCloturedException();
        Participant participant = participantService.getById(participantId);
        dateSondee.setDateSondage(date);
        dateSondee.setParticipant(participant);
        try {
            return repository.save(dateSondee);
        }catch (DataIntegrityViolationException e){
            throw new DateSondageAlreadyExistsException();
        }
    }

    public List<Date> getBestDateBySondageId(Long id) {
        if(!sondageService.exists(id))
            throw new NoSuchElementException(ErrorMessages.SONDAGE_DOES_NOT_EXISTS);
        return repository.bestDate(id);
    }

    public List<Date> getMaybeBestDateBySondageId(Long id) {
        if(!sondageService.exists(id))
            throw new NoSuchElementException(ErrorMessages.SONDAGE_DOES_NOT_EXISTS);
        return repository.maybeBestDate(id);
    }

    public void checkIfDateSondeeAlreadyExists(Long dateSondageId, Long participantId) throws DateSondeeAlreadyExistsException {
        List<DateSondee> datesSondees = repository.getAllByDateSondageAndParticipant(dateSondageId, participantId);
        if (!datesSondees.isEmpty())
            throw new DateSondeeAlreadyExistsException();
    }
}
