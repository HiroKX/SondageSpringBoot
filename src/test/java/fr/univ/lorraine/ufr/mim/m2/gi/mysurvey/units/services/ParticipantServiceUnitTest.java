package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.units.services;

import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Participant;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.repositories.ParticipantRepository;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services.ParticipantService;
import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ParticipantServiceUnitTest {

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
        when(service.exists(id)).thenReturn(true);
        when(repository.getReferenceById(id)).thenReturn(expectedParticipant);

        Participant result = service.getById(id);

        verify(repository, times(1)).getReferenceById(id);
        verify(repository, times(1)).existsById(id);
        assertEquals(expectedParticipant, result);
    }

    @Test
    void givenAnIdNotExisting_whenGetById_thenRepositoryIsCalled() {
        Long id = 1L;
        Participant expectedParticipant = new Participant();
        when(service.exists(id)).thenReturn(false);
        when(repository.getReferenceById(id)).thenReturn(expectedParticipant);

        assertThrows(NoResultException.class,() -> service.getById(id));

        verify(repository, never()).getReferenceById(id);
        verify(repository, times(1)).existsById(id);
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
    void whenGetAll_thenThrowError() {
        List<Participant> expectedParticipants = new ArrayList<>();
        when(repository.findAll()).thenReturn(expectedParticipants);

        assertThrows(NoResultException.class, () -> service.getAll());

        verify(repository, times(1)).findAll();
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
        Participant participant = mock(Participant.class);
        participant.setNom("Nom");
        participant.setPrenom("Prenom");
        Participant participant2 = mock(Participant.class);
        participant2.setNom("Nom2");
        participant2.setPrenom("Prenom2");
        when(service.exists(id)).thenReturn(true);
        when(service.getById(id)).thenReturn(participant);
        when(repository.save(participant)).thenReturn(participant2);

        Participant result = service.update(id, participant);

        verify(repository, times(1)).save(participant);
        verify(participant2).setNom("Nom2");
        verify(participant2).setPrenom("Prenom2");
        assertEquals(participant2.getNom(), result.getNom());
        assertEquals(participant2.getPrenom(), result.getPrenom());
        assertEquals(participant2, result);
    }


    @Test
    void givenAnIdAndAParticipantChangeNomOnly_whenUpdate_thenRepositoryIsCalled() {
        Long id = 1L;
        Participant participant = mock(Participant.class);
        participant.setNom("Nom");
        participant.setPrenom("Prenom");

        Participant participant2 = new Participant();
        participant2.setNom("Nouvo");
        when(repository.existsById(id)).thenReturn(true);
        when(repository.getReferenceById(id)).thenReturn(participant);
        when(repository.save(participant)).thenReturn(participant);

        Participant result = service.update(id, participant2);

        verify(participant).setNom("Nouvo");
        verify(repository, times(1)).save(participant);
        assertEquals(participant, result);
    }

    @Test
    void givenAnIdAndAParticipantChangePrenomOnly_whenUpdate_thenRepositoryIsCalled() {
        Long id = 1L;
        Participant participant = mock(Participant.class);
        participant.setNom("Nom");
        participant.setPrenom("Prenom");

        Participant participant2 = new Participant();
        participant2.setPrenom("Nouvo");
        when(repository.existsById(id)).thenReturn(true);
        when(repository.getReferenceById(id)).thenReturn(participant);
        when(repository.save(participant)).thenReturn(participant);

        Participant result = service.update(id, participant2);

        verify(participant).setPrenom("Nouvo");
        verify(repository, times(1)).save(participant);
        assertEquals(participant, result);
    }

    @Test
    void givenAnIdThatDoesNotExist_whenUpdate_thenRepositoryIsCalled() {
        Long id = 1L;
        Participant participant = new Participant();
        participant.setNom("Nom");
        participant.setPrenom("Prenom");
        when(repository.existsById(id)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> service.update(id, participant));
        verify(repository, times(1)).existsById(id);
        verify(repository, never()).save(participant);
        verify(repository, never()).getReferenceById(id);
    }

    @Test
    void givenAnIdThatExists_whenDelete_thenRepositoryIsCalled() {
        Long id = 1L;
        when(repository.existsById(id)).thenReturn(true);
        when(repository.findById(id)).thenReturn(Optional.of(new Participant()));

        service.delete(id);

        verify(repository, times(1)).existsById(id);
        verify(repository, times(1)).deleteById(id);
    }

    @Test
    void givenAnIdThatDoesNotExist_whenDelete_thenRepositoryIsCalled() {
        Long id = 1L;
        when(repository.existsById(id)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> service.delete(id));

        verify(repository, times(1)).existsById(id);
        verify(repository, never()).deleteById(id);
    }
}
