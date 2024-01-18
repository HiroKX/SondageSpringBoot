package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.units.services;

import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Participant;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Sondage;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.repositories.SondageRepository;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services.ParticipantService;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services.SondageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SondageServiceUnitTest {

    @Mock
    private SondageRepository sondageRepository;

    @Mock
    private ParticipantService participantService;

    @InjectMocks
    private SondageService sondageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenAnId_whenGetAll_thenSondageRepositoryIsCalled() {
        ArrayList<Sondage> expectedSondage = new ArrayList<Sondage>();
        expectedSondage.add(new Sondage());
        when(sondageRepository.findAll()).thenReturn(expectedSondage);

        List<Sondage> result = sondageService.getAll();

        verify(sondageRepository, times(1)).findAll();
        assertEquals(expectedSondage, result);
    }

    @Test
    void givenAnId_whenGetById_thenSondageRepositoryIsCalled() {
        Long sondageId = 1L;
        Sondage expectedSondage = new Sondage();
        when(sondageRepository.getReferenceById(sondageId)).thenReturn(expectedSondage);

        Sondage result = sondageService.getById(sondageId);

        verify(sondageRepository, times(1)).getReferenceById(same(sondageId));
        assertEquals(expectedSondage, result);
    }

    @Test
    void whenGetAll_thenSondageRepositoryIsCalled() {
        when(sondageRepository.findAll()).thenReturn(Collections.emptyList());

        List<Sondage> result = sondageService.getAll();

        verify(sondageRepository, times(1)).findAll();
        assertTrue(result.isEmpty());
    }

    @Test
    void givenAnIdAndASondage_whenCreate_thenParticipantServiceAndSondageRepositoryAreCalled() {
        Long participantId = 1L;
        Sondage sondageToCreate = new Sondage();
        Participant participant = new Participant();
        when(participantService.getById(participantId)).thenReturn(participant);
        when(sondageRepository.save(sondageToCreate)).thenReturn(sondageToCreate);

        Sondage result = sondageService.create(participantId, sondageToCreate);

        verify(participantService, times(1)).getById(same(participantId));
        verify(sondageRepository, times(1)).save(same(sondageToCreate));
        assertEquals(participant, sondageToCreate.getCreateBy());
        assertEquals(sondageToCreate, result);
    }

    @Test
    void givenASondageIdThatExists_whenUpdate_thenSondageRepositoryIsCalledThreeTimes() {
        Long sondageId = 1L;
        Sondage existingSondage = new Sondage();
        Sondage updatedSondage = new Sondage();
        updatedSondage.setSondageId(sondageId);

        when(sondageRepository.findById(sondageId)).thenReturn(Optional.of(existingSondage));
        when(sondageRepository.save(updatedSondage)).thenReturn(updatedSondage);

        Sondage result = sondageService.update(sondageId, updatedSondage);

        verify(sondageRepository, times(1)).findById(same(sondageId));
        verify(sondageRepository, times(1)).save(same(updatedSondage));
        assertNotNull(result);
        assertEquals(sondageId, result.getSondageId());
    }

    @Test
    void givenASondageIdAThatDoesNotExists_whenUpdate_thenSondageRepositoryIsCalledOneTime() {
        Long sondageId = 1L;
        Sondage updatedSondage = new Sondage();

        when(sondageRepository.findById(sondageId)).thenReturn(Optional.empty());

        Sondage result = sondageService.update(sondageId, updatedSondage);

        verify(sondageRepository, times(1)).findById(same(sondageId));
        verify(sondageRepository, never()).save(same(updatedSondage));
        assertNull(result);
    }

    @Test
    void givenASondageIdThatExists_whenDelete_thenSondageRepositoryIsCalledTwoTimes() {
        Long sondageId = 1L;

        when(sondageRepository.findById(sondageId)).thenReturn(Optional.of(new Sondage()));

        boolean result = sondageService.delete(sondageId);

        verify(sondageRepository, times(1)).findById(same(sondageId));
        verify(sondageRepository, times(1)).deleteById(sondageId);
        assertTrue(result);
    }

    @Test
    void givenASondageIdAThatDoesNotExists_whenDelete_thenSondageRepositoryIsCalledOneTime() {
        Long sondageId = 1L;

        when(sondageRepository.findById(sondageId)).thenReturn(Optional.empty());

        boolean result = sondageService.delete(sondageId);

        verify(sondageRepository, times(1)).findById(same(sondageId));
        verify(sondageRepository, never()).deleteById(sondageId);
        assertFalse(result);
    }
}

