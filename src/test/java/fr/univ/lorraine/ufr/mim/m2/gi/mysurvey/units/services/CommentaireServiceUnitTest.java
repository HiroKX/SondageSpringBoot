package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.units.services;

import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Commentaire;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Participant;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Sondage;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.repositories.CommentaireRepository;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services.CommentaireService;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services.ParticipantService;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services.SondageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentaireServiceUnitTest {

    @Mock
    private CommentaireRepository repository;

    @Mock
    private SondageService sondageService;

    @Mock
    private ParticipantService participantService;

    @InjectMocks
    private CommentaireService commentaireService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenASondageId_whenGetBySondageId_thenRepositoryIsCalled() {
        Long sondageId = 1L;
        List<Commentaire> expectedCommentaires = Arrays.asList(new Commentaire(), new Commentaire());
        when(sondageService.exists(sondageId)).thenReturn(true);
        when(repository.getAllBySondage(sondageId)).thenReturn(expectedCommentaires);

        List<Commentaire> result = commentaireService.getBySondageId(sondageId);

        verify(repository, times(1)).getAllBySondage(same(sondageId));
        assertEquals(expectedCommentaires, result);
    }

    @Test
    void givenASondageIdThatDoesNotExist_whenGetBySondageId_thenThrowNoSuchElementException() {
        Long sondageId = 1L;
        when(sondageService.exists(sondageId)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> commentaireService.getBySondageId(sondageId));

        verify(repository, never()).getAllBySondage(same(sondageId));
    }

    @Test
    void givenSondageIdAndParticipantIdAndCommentaire_whenCreate_thenServicesAndRepositoryAreCalled() {
        Long sondageId = 1L;
        Long participantId = 1L;
        Commentaire commentaire = new Commentaire();

        Sondage expectedSondage = new Sondage(); // Créer une instance de Sondage attendue
        Participant expectedParticipant = new Participant(); // Créer une instance de Participant attendue

        // Simuler le comportement des services et du repository
        when(sondageService.exists(sondageId)).thenReturn(true);
        when(sondageService.getById(sondageId)).thenReturn(expectedSondage);
        when(participantService.exists(participantId)).thenReturn(true);
        when(participantService.getById(participantId)).thenReturn(expectedParticipant);
        when(repository.save(commentaire)).thenReturn(commentaire);

        Commentaire result = commentaireService.create(sondageId, participantId, commentaire);

        // Vérifier les interactions avec les mocks
        verify(sondageService, times(1)).getById(same(sondageId));
        verify(participantService, times(1)).getById(same(participantId));
        verify(repository, times(1)).save(commentaire);

        // Vérifier que les propriétés du commentaire sont correctement définies
        assertEquals(expectedSondage, commentaire.getSondage());
        assertEquals(expectedParticipant, commentaire.getParticipant());
        assertEquals(commentaire, result);
    }

    @Test
    void givenSondageIdThatDoesNotExist_whenCreate_thenThrowNoSuchElementException() {
        Long sondageId = 1L;
        Long participantId = 1L;
        Commentaire commentaire = new Commentaire();

        // Simuler le comportement des services et du repository
        when(sondageService.exists(sondageId)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> commentaireService
                .create(sondageId, participantId, commentaire));

        // Vérifier les interactions avec les mocks
        verify(sondageService, times(1)).exists(sondageId);
        verify(sondageService, never()).getById(same(sondageId));
        verify(participantService, never()).exists(participantId);
        verify(participantService, never()).getById(same(participantId));
        verify(repository, never()).save(commentaire);
    }

    @Test
    void givenParticipantIdThatDoesNotExist_whenCreate_thenThrowNoSuchElementException() {
        Long sondageId = 1L;
        Long participantId = 1L;
        Commentaire commentaire = new Commentaire();

        // Simuler le comportement des services et du repository
        when(sondageService.exists(sondageId)).thenReturn(true);
        when(participantService.exists(participantId)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> commentaireService
                .create(sondageId, participantId, commentaire));

        // Vérifier les interactions avec les mocks
        verify(sondageService, times(1)).exists(sondageId);
        verify(sondageService, times(1)).getById(same(sondageId));
        verify(participantService, times(1)).exists(participantId);
        verify(participantService, never()).getById(same(participantId));
        verify(repository, never()).save(commentaire);
    }


    @Test
    void givenAnIdAndACommentaire_whenUpdate_thenRepositoryIsCalled() {
        Long id = 1L;
        Commentaire commentaire = new Commentaire();
        when(repository.findById(id)).thenReturn(Optional.of(commentaire));
        when(repository.getReferenceById(id)).thenReturn(commentaire);
        Commentaire commentaire1 = new Commentaire();
        commentaire1.setCommentaireId(id);
        when(repository.save(commentaire)).thenReturn(commentaire1);

        Commentaire result = commentaireService.update(id, commentaire);
        verify(repository, times(1)).findById(same(id));
        verify(repository, times(1)).save(same(commentaire));
        assertEquals(id, result.getCommentaireId());

        assertNotNull(result);
    }

    @Test
    void givenAnIdThatDoesNotExist_whenUpdate_thenThrowNoSuchElementException() {
        Long id = 1L;
        Commentaire commentaire = new Commentaire();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> commentaireService.update(id, commentaire));

        verify(repository, times(1)).findById(same(id));
        verify(repository, never()).save(same(commentaire));
    }

    @Test
    void givenAnIdThatExists_whenDelete_thenRepositoryIsCalled() {
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.of(new Commentaire()));

        commentaireService.delete(id);

        verify(repository, times(1)).findById(same(id));
        verify(repository, times(1)).deleteById(id);
    }

    @Test
    void givenAnIdThatDoesNotExist_whenDelete_thenThrowNoSuchElementException() {
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> commentaireService.delete(id));

        verify(repository, times(1)).findById(same(id));
        verify(repository, never()).deleteById(id);
    }

    @Test
    void givenAnIdThatExists_whenExists_thenReturnTrue() {
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.of(new Commentaire()));

        var result = commentaireService.exists(id);

        assertTrue(result);
    }

    @Test
    void givenAnIdThatExists_whenExists_thenReturnFalse() {
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        var result = commentaireService.exists(id);

        assertFalse(result);
    }
}
