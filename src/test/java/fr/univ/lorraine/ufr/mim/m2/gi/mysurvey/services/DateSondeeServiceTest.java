package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services;

import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.DateSondage;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.DateSondee;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Participant;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Sondage;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.repositories.DateSondeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.List;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DateSondeeServiceTest {

    @Mock
    private DateSondeeRepository repository;

    @Mock
    private DateSondageService dateSondageService;

    @Mock
    private ParticipantService participantService;

    @InjectMocks
    private DateSondeeService dateSondeeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenIdAndParticipantIdAndDateSondee_whenCreate_thenServicesAndRepositoryAreCalled() {
        Long id = 1L;
        Long participantId = 1L;
        DateSondee dateSondee = new DateSondee();

        DateSondage dateSondage = new DateSondage();
        dateSondage.setSondage(new Sondage()); // Créer et configurer une instance de DateSondage
        dateSondage.getSondage().setCloture(false);

        when(dateSondageService.getById(id)).thenReturn(dateSondage);
        when(participantService.getById(participantId)).thenReturn(new Participant());
        when(repository.save(dateSondee)).thenReturn(dateSondee);

        DateSondee result = dateSondeeService.create(id, participantId, dateSondee);

        verify(dateSondageService, times(1)).getById(id);
        verify(participantService, times(1)).getById(participantId);
        verify(repository, times(1)).save(dateSondee);
        assertEquals(dateSondee, result);
    }

    @Test
    void givenId_whenBestDate_thenRepositoryIsCalled() {
        Long id = 1L;
        List<Date> expectedDates = Arrays.asList(new Date(), new Date());
        when(repository.bestDate(id)).thenReturn(expectedDates);

        List<Date> result = dateSondeeService.bestDate(id);

        verify(repository, times(1)).bestDate(id);
        assertEquals(expectedDates, result);
    }

    @Test
    void givenId_whenMaybeBestDate_thenRepositoryIsCalled() {
        Long id = 1L;
        List<Date> expectedDates = Arrays.asList(new Date(), new Date());
        when(repository.maybeBestDate(id)).thenReturn(expectedDates);

        List<Date> result = dateSondeeService.maybeBestDate(id);

        verify(repository, times(1)).maybeBestDate(id);
        assertEquals(expectedDates, result);
    }
}
