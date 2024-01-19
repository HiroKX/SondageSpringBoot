package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.units.services;

import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.exception.DateSondageAlreadyExistsException;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.exception.DateSondeeAlreadyExistsException;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.exception.SondageCloturedException;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.DateSondage;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.DateSondee;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Participant;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Sondage;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.repositories.DateSondeeRepository;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services.DateSondageService;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services.DateSondeeService;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services.ParticipantService;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services.SondageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DateSondeeServiceUnitTest {

    @Mock
    private DateSondeeRepository repository;

    @Mock
    private DateSondageService dateSondageService;

    @Mock
    private SondageService sondageService;

    @Mock
    private ParticipantService participantService;

    @InjectMocks
    private DateSondeeService dateSondeeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenIdAndParticipantIdAndDateSondee_whenCreate_thenServicesAndRepositoryAreCalled() throws SondageCloturedException, DateSondageAlreadyExistsException {
        Long id = 1L;
        Long participantId = 1L;
        DateSondee dateSondee = new DateSondee();
        DateSondage dateSondage = new DateSondage();
        dateSondage.setSondage(new Sondage()); // Créer et configurer une instance de DateSondage
        dateSondage.getSondage().setCloture(false);
        Participant p = new Participant();
        p.setNom("test");
        when(dateSondageService.getById(id)).thenReturn(dateSondage);
        when(participantService.getById(participantId)).thenReturn(p);
        when(repository.save(dateSondee)).thenReturn(dateSondee);

        DateSondee result = dateSondeeService.create(id, participantId, dateSondee);

        verify(dateSondageService, times(1)).getById(id);
        verify(participantService, times(1)).getById(participantId);
        verify(repository, times(1)).save(dateSondee);
        assertEquals(dateSondee, result);
        assertEquals(dateSondee.getDateSondage(),dateSondage);
        assertEquals(dateSondee.getParticipant().getNom(),p.getNom());
    }

    @Test
    void givenIdAndParticipantIdAndDateSondee_whenCreateSondageIsClosed_thenServicesAndRepositoryAreCalled() throws SondageCloturedException {
        Long id = 1L;
        Long participantId = 1L;
        DateSondee dateSondee = new DateSondee();

        DateSondage dateSondage = new DateSondage();
        dateSondage.setSondage(new Sondage()); // Créer et configurer une instance de DateSondage
        dateSondage.getSondage().setCloture(true);

        when(dateSondageService.getById(id)).thenReturn(dateSondage);

        assertThrows(SondageCloturedException.class, () -> dateSondeeService.create(id, participantId, dateSondee));

        verify(dateSondageService, times(1)).getById(id);
        verify(participantService, times(0)).getById(participantId);
        verify(repository, times(0)).save(dateSondee);
    }

    @Test
    void givenIdAndParticipantIdAndDateSondee_whenDateSondeeAlreadyExists_thenServicesAndRepositoryAreCalled() throws SondageCloturedException {
        Long id = 1L;
        Long participantId = 1L;
        DateSondee dateSondee = new DateSondee();

        DateSondage dateSondage = new DateSondage();
        dateSondage.setSondage(new Sondage()); // Créer et configurer une instance de DateSondage
        dateSondage.getSondage().setCloture(false);

        when(dateSondageService.getById(id)).thenReturn(dateSondage);
        when(repository.save(dateSondee)).thenThrow(DataIntegrityViolationException.class);

        assertThrows(DateSondageAlreadyExistsException.class, () -> dateSondeeService.create(id, participantId, dateSondee));

        verify(dateSondageService, times(1)).getById(id);
        verify(participantService, times(1)).getById(participantId);
        verify(repository, times(1)).save(dateSondee);
    }

    @Test
    void givenId_whenBestDate_thenRepositoryIsCalled() {
        Long id = 1L;
        List<Date> expectedDates = Arrays.asList(new Date(), new Date());
        when(sondageService.exists(id)).thenReturn(true);
        when(repository.bestDate(id)).thenReturn(expectedDates);

        List<Date> result = dateSondeeService.getBestDateBySondageId(id);

        verify(repository, times(1)).bestDate(id);
        verify(sondageService, times(1)).exists(id);
        assertEquals(expectedDates, result);
    }

    @Test
    void givenNotExistingId_whenBestDate_thenRepositoryIsCalled() {
        Long id = 1L;
        when(sondageService.exists(id)).thenReturn(false);

        assertThrows(NoSuchElementException.class,() -> dateSondeeService.getBestDateBySondageId(id));

        verify(repository, never()).maybeBestDate(id);
        verify(sondageService, times(1)).exists(id);
    }

    @Test
    void givenId_whenMaybeBestDate_thenRepositoryIsCalled() {
        Long id = 1L;
        List<Date> expectedDates = Arrays.asList(new Date(), new Date());
        when(sondageService.exists(id)).thenReturn(true);
        when(repository.maybeBestDate(id)).thenReturn(expectedDates);

        List<Date> result = dateSondeeService.getMaybeBestDateBySondageId(id);

        verify(repository, times(1)).maybeBestDate(id);
        verify(sondageService, times(1)).exists(id);
        assertEquals(expectedDates, result);
    }

    @Test
    void givenNotExistingId_whenMaybeBestDate_thenRepositoryIsCalled() {
        Long id = 1L;
        when(sondageService.exists(id)).thenReturn(false);

        assertThrows(NoSuchElementException.class,() -> dateSondeeService.getMaybeBestDateBySondageId(id));

        verify(repository, never()).maybeBestDate(id);
        verify(sondageService, times(1)).exists(id);
    }

    @Test
    void givenAnIdAndDateSondage_whenCheckIfDateAlreadyExists_thenNotThrowException() {
        Long dateSondageId = 1L;
        Long participantId = 1L;
        when(repository.getAllByDateSondageAndParticipant(dateSondageId, participantId)).thenReturn(new ArrayList<>());
        assertDoesNotThrow(() -> dateSondeeService.checkIfDateSondeeAlreadyExists(dateSondageId,participantId));
    }

    @Test
    void givenAnIdAndDateSondage_whenCheckIfDateAlreadyExists_thenThrowDateSondeeAlreadyExists() {
        Long dateSondageId = 1L;
        Long participantId = 1L;
        when(repository.getAllByDateSondageAndParticipant(dateSondageId, participantId)).thenReturn(List.of(new DateSondee()));
        assertThrows(DateSondeeAlreadyExistsException.class,() -> dateSondeeService.checkIfDateSondeeAlreadyExists(dateSondageId,participantId));
    }
}
