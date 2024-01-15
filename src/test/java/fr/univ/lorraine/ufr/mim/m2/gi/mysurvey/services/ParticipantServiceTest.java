package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services;

import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Participant;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.repositories.ParticipantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ParticipantServiceTest {

    @Mock
    private ParticipantRepository repository;

    @InjectMocks
    private ParticipantService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenAnId_whenGetById_thenRepositoryIsCalled() {
        Long id = 1L;
        Participant expectedParticipant = new Participant();
        when(repository.getById(id)).thenReturn(expectedParticipant);

        Participant result = service.getById(id);

        verify(repository, times(1)).getById(id);
        assertEquals(expectedParticipant, result);
    }

    @Test
    void whenGetAll_thenRepositoryIsCalled() {
        List<Participant> expectedParticipants = Arrays.asList(new Participant(), new Participant());
        when(repository.findAll()).thenReturn(expectedParticipants);

        List<Participant> result = service.getAll();

        verify(repository, times(1)).findAll();
        assertEquals(expectedParticipants, result);
    }

    @Test
    void givenAParticipant_whenCreate_thenRepositoryIsCalled() {
        Participant participant = new Participant();
        when(repository.save(participant)).thenReturn(participant);

        Participant result = service.create(participant);

        verify(repository, times(1)).save(participant);
        assertEquals(participant, result);
    }

    @Test
    void givenAnIdAndAParticipant_whenUpdate_thenRepositoryIsCalled() {
        Long id = 1L;
        Participant participant = new Participant();
        when(repository.findById(id)).thenReturn(Optional.of(new Participant()));
        when(repository.save(participant)).thenReturn(participant);

        Participant result = service.update(id, participant);

        verify(repository, times(1)).findById(id);
        verify(repository, times(1)).save(participant);
        assertEquals(participant, result);
    }

    @Test
    void givenAnIdThatDoesNotExist_whenUpdate_thenRepositoryIsCalled() {
        Long id = 1L;
        Participant participant = new Participant();
        when(repository.findById(id)).thenReturn(Optional.empty());

        Participant result = service.update(id, participant);

        verify(repository, times(1)).findById(id);
        verify(repository, never()).save(participant);
        assertNull(result);
    }

    @Test
    void givenAnIdThatExists_whenDelete_thenRepositoryIsCalled() {
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.of(new Participant()));

        boolean result = service.delete(id);

        verify(repository, times(1)).findById(id);
        verify(repository, times(1)).deleteById(id);
        assertTrue(result);
    }

    @Test
    void givenAnIdThatDoesNotExist_whenDelete_thenRepositoryIsCalled() {
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        boolean result = service.delete(id);

        verify(repository, times(1)).findById(id);
        verify(repository, never()).deleteById(id);
        assertFalse(result);
    }
}
