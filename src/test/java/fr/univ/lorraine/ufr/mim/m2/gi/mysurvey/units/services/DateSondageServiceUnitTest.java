package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.units.services;

import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.DateSondage;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Sondage;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.repositories.DateSondageRepository;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services.DateSondageService;
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

class DateSondageServiceUnitTest {

    @Mock
    private DateSondageRepository repository;

    @Mock
    private SondageService sondageService;

    @InjectMocks
    private DateSondageService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenAnId_whenGetById_thenRepositoryIsCalled() {
        Long id = 1L;
        DateSondage expectedDateSondage = new DateSondage();
        when(repository.getReferenceById(id)).thenReturn(expectedDateSondage);

        DateSondage result = service.getById(id);

        verify(repository, times(1)).getReferenceById(same(id));
        assertEquals(expectedDateSondage, result);
    }

    @Test
    void givenASondageId_whenGetBySondageId_thenRepositoryIsCalled() {
        Long sondageId = 1L;
        List<DateSondage> expectedDateSondages = Arrays.asList(new DateSondage(), new DateSondage());
        when(sondageService.exists(sondageId)).thenReturn(true);
        when(repository.getAllBySondage(sondageId)).thenReturn(expectedDateSondages);

        List<DateSondage> result = service.getBySondageId(sondageId);

        verify(sondageService, times(1)).exists(sondageId);
        verify(repository, times(1)).getAllBySondage(same(sondageId));
        assertEquals(expectedDateSondages, result);
    }

    @Test
    void givenAnIdThatDoesNotExist_whenGetBySondageId_thenThrowNoSuchElementException() {
        Long sondageId = 1L;
        when(sondageService.exists(sondageId)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> service.getById(sondageId));

        verify(sondageService, times(1)).exists(sondageId);
        verify(repository, never()).getAllBySondage(same(sondageId));
    }

    @Test
    void givenAnIdAndADateSondage_whenCreate_thenSondageServiceAndRepositoryAreCalled() {
        Long sondageId = 1L;
        DateSondage dateSondage = new DateSondage();
        Sondage expectedSondage = new Sondage(); // Créer une instance de Sondage attendue

        when(sondageService.exists(sondageId)).thenReturn(false);
        when(sondageService.getById(sondageId)).thenReturn(expectedSondage); // Retourner le Sondage attendu
        when(repository.save(dateSondage)).thenReturn(dateSondage); // Simuler l'enregistrement de DateSondage

        DateSondage result = service.create(sondageId, dateSondage);

        // Vérifier les interactions avec les mocks
        verify(sondageService, times(1)).exists(sondageId);
        verify(sondageService, times(1)).getById(same(sondageId));
        verify(repository, times(1)).save(dateSondage);

        // Vérifier que l'attribut Sondage du DateSondage est correctement défini
        assertEquals(expectedSondage, dateSondage.getSondage());
        assertEquals(dateSondage, result);
    }

    @Test
    void givenASondageIdThatDoesNotExist_whenCreate_thenThrowNoSuchElementException() {
        Long sondageId = 1L;
        DateSondage dateSondage = new DateSondage();

        when(sondageService.exists(sondageId)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> service.create(sondageId, dateSondage));

        // Vérifier les interactions avec les mocks
        verify(sondageService, times(1)).exists(sondageId);
        verify(sondageService, never()).getById(same(sondageId));
        verify(repository, never()).save(dateSondage);
    }

    @Test
    void givenAnIdThatExists_whenDelete_thenRepositoryIsCalledTwoTimes() {
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.of(new DateSondage()));

        service.delete(id);

        verify(repository, times(1)).findById(same(id));
        verify(repository, times(1)).deleteById(id);
    }

    @Test
    void givenAnIdThatDoesNotExists_whenDelete_thenThrowNoSuchElementException() {
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.delete(id));

        verify(repository, times(1)).findById(same(id));
        verify(repository, never()).deleteById(id);
    }
}
