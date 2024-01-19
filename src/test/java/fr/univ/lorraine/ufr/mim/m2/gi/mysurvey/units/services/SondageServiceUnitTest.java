package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.units.services;

import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Participant;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Sondage;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.repositories.SondageRepository;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services.ParticipantService;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services.SondageService;
import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

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
        when(sondageService.exists(sondageId)).thenReturn(true);

        Sondage result = sondageService.getById(sondageId);

        verify(sondageRepository, times(1)).getReferenceById(same(sondageId));
        assertEquals(expectedSondage, result);
    }

    @Test
    void whenGetAll_thenSondageRepositoryIsCalled() {
        when(sondageRepository.findAll()).thenReturn(Collections.singletonList(new Sondage()));

        List<Sondage> result = sondageService.getAll();

        verify(sondageRepository, times(1)).findAll();
        assertNotNull(result);
        assertNotEquals(result, Collections.emptyList());
    }

    @Test
    void whenGetAllEmpty_thenSondageRepositoryIsCalled() {
        when(sondageRepository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(NoResultException.class,()-> sondageService.getAll());

        verify(sondageRepository, times(1)).findAll();
    }

    @Test
    void givenAnIdAndASondage_whenCreate_thenParticipantServiceAndSondageRepositoryAreCalled() {
        Long participantId = 1L;
        Sondage sondageToCreate = mock(Sondage.class);
        Participant participant = new Participant();
        when(participantService.getById(participantId)).thenReturn(participant);
        when(sondageRepository.save(sondageToCreate)).thenReturn(sondageToCreate);

        Sondage result = sondageService.create(participantId, sondageToCreate);

        verify(sondageToCreate, times(1)).setCreateBy(participant);
        verify(participantService, times(1)).getById(same(participantId));
        verify(sondageRepository, times(1)).save(same(sondageToCreate));
        assertEquals(sondageToCreate, result);
    }

    @Test
    void givenASondageIdThatExists_whenUpdate_thenSondageRepositoryIsCalledTwoTimes() {
        Long sondageId = 1L;
        Date d = new Date();
        Sondage existingSondage = mock(Sondage.class);
        Sondage updatedSondage = new Sondage();
        updatedSondage.setFin(d);
        updatedSondage.setNom("nom");
        updatedSondage.setDescription("description");
        updatedSondage.setCloture(false);
        updatedSondage.setSondageId(sondageId);
        when(sondageService.exists(sondageId)).thenReturn(true);
        when(sondageRepository.getReferenceById(sondageId)).thenReturn(existingSondage);
        when(sondageRepository.save(existingSondage)).thenReturn(updatedSondage);

        Sondage result = sondageService.update(sondageId, updatedSondage);

        verify(existingSondage, times(1)).setFin(d);
        verify(existingSondage, times(1)).setNom("nom");
        verify(existingSondage, times(1)).setDescription("description");
        verify(existingSondage, times(1)).setCloture(false);
        verify(sondageRepository, times(1)).getReferenceById(same(sondageId));
        verify(sondageRepository, times(1)).save(same(existingSondage));
        assertNotNull(result);
        assertEquals(sondageId, result.getSondageId());
    }

    @Test
    void givenASondageIdAThatDoesNotExists_whenUpdate_thenSondageRepositoryIsCalledOneTime() {
        Long sondageId = 1L;
        Sondage updatedSondage = new Sondage();
        when(sondageService.exists(sondageId)).thenReturn(false);
        when(sondageRepository.findById(sondageId)).thenReturn(Optional.empty());

        assertThrows(NoResultException.class, () -> sondageService.update(sondageId, updatedSondage));

        verify(sondageRepository, times(1)).existsById(same(sondageId));
        verify(sondageRepository, never()).save(same(updatedSondage));
    }

    @Test
    void givenASondageIdThatExists_whenDelete_thenSondageRepositoryIsCalledTwoTimes() {
        Long sondageId = 1L;

        when(sondageService.exists(sondageId)).thenReturn(true);
        when(sondageRepository.findById(sondageId)).thenReturn(Optional.of(new Sondage()));

        sondageService.delete(sondageId);

        verify(sondageRepository, times(1)).existsById(same(sondageId));
        verify(sondageRepository, times(1)).deleteById(same(sondageId));
    }

    @Test
    void givenASondageIdAThatDoesNotExists_whenDelete_thenSondageRepositoryIsCalledOneTime() {
        Long sondageId = 1L;
        when(sondageService.exists(sondageId)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> sondageService.delete(sondageId));

        verify(sondageRepository, times(1)).existsById(same(sondageId));
        verify(sondageRepository, never()).deleteById(sondageId);
    }
}

