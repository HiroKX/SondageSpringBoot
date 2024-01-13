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
        when(repository.getAllBySondage(sondageId)).thenReturn(expectedCommentaires);

        List<Commentaire> result = commentaireService.getBySondageId(sondageId);

        verify(repository, times(1)).getAllBySondage(same(sondageId));
        assertEquals(expectedCommentaires, result);
    }

    @Test
    void givenSondageIdAndParticipantIdAndCommentaire_whenAddCommentaire_thenServicesAndRepositoryAreCalled() {
        Long sondageId = 1L;
        Long participantId = 1L;
        Commentaire commentaire = new Commentaire();

        Sondage expectedSondage = new Sondage(); // Créer une instance de Sondage attendue
        Participant expectedParticipant = new Participant(); // Créer une instance de Participant attendue

        // Simuler le comportement des services et du repository
        when(sondageService.getById(sondageId)).thenReturn(expectedSondage);
        when(participantService.getById(participantId)).thenReturn(expectedParticipant);
        when(repository.save(commentaire)).thenReturn(commentaire);

        Commentaire result = commentaireService.addCommantaire(sondageId, participantId, commentaire);

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
    void givenAnIdAndACommentaire_whenUpdate_thenRepositoryIsCalled() {
        Long id = 1L;
        Commentaire commentaire = new Commentaire();
        when(repository.findById(id)).thenReturn(Optional.of(commentaire));
        when(repository.save(commentaire)).thenReturn(commentaire);

        Commentaire result = commentaireService.update(id, commentaire);

        verify(repository, times(1)).findById(same(id));
        verify(repository, times(1)).save(same(commentaire));
        assertNotNull(result);
    }

    @Test
    void givenAnIdThatDoesNotExist_whenUpdate_thenRepositoryIsCalled() {
        Long id = 1L;
        Commentaire commentaire = new Commentaire();
        when(repository.findById(id)).thenReturn(Optional.empty());

        Commentaire result = commentaireService.update(id, commentaire);

        verify(repository, times(1)).findById(same(id));
        verify(repository, never()).save(same(commentaire));
        assertNull(result);
    }

    @Test
    void givenAnIdThatExists_whenDelete_thenRepositoryIsCalled() {
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.of(new Commentaire()));

        boolean result = commentaireService.delete(id);

        verify(repository, times(1)).findById(same(id));
        verify(repository, times(1)).deleteById(id);
        assertTrue(result);
    }

    @Test
    void givenAnIdThatDoesNotExist_whenDelete_thenRepositoryIsCalled() {
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        boolean result = commentaireService.delete(id);

        verify(repository, times(1)).findById(same(id));
        verify(repository, never()).deleteById(id);
        assertFalse(result);
    }
}
