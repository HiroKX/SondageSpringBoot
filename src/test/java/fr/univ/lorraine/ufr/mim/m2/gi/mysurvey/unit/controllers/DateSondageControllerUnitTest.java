package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.unit.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.controllers.DateSondageController;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.dtos.DateSondageDto;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.dtos.DateSondeeDto;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.DateSondage;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Participant;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Sondage;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services.DateSondageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(MockitoExtension.class)
class DateSondageControllerUnitTest {

    protected MockMvc mvc;

    @Mock
    private DateSondageService service;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private DateSondageController controller;

    // This object will be magically initialized by the initFields method below.
    private JacksonTester<DateSondageDto> jsonDateSondage;

    private DateSondageDto dateSondageDto;

    private DateSondageDto dateSondageDtoNull;

    private DateSondage dateSondage;

    private Sondage sondage;

    private long id;

    @BeforeEach
    void setup() {
        id = 1L;
        Participant participant = new Participant();
        participant.setParticipantId(id);
        participant.setNom("Lagler");
        participant.setPrenom("Nicolas");
        Calendar calendar = Calendar.getInstance(); // Obtient une instance de Calendar représentant la date/heure actuelle
        calendar.add(Calendar.YEAR, 1); // Ajoute un an à la date/heure actuelle

        Date dateDansUnAn = calendar.getTime(); // Convertit le Calendar en Date

        sondage = new Sondage();
        sondage.setSondageId(id);
        sondage.setNom("Sondage");
        sondage.setCloture(false);
        sondage.setFin(dateDansUnAn);
        sondage.setCreateBy(participant);
        sondage.setDescription("Description");
        dateSondage = new DateSondage();
        dateSondage.setSondage(sondage);
        dateSondage.setDate(new Date());
        dateSondage.setDateSondageId(id);
        dateSondageDtoNull = new DateSondageDto();

        dateSondageDto = new DateSondageDto();
        dateSondageDto.setDate(dateDansUnAn);
        dateSondageDto.setDateSondageId(id);
        JacksonTester.initFields(this, new ObjectMapper());
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }


    @Test
    void testCreate() throws Exception {
        when(mapper.map(dateSondageDto, DateSondage.class)).thenReturn(dateSondage);
        when(service.create(id, dateSondage)).thenReturn(dateSondage);
        when(mapper.map(dateSondage, DateSondageDto.class)).thenReturn(dateSondageDto);
        MockHttpServletResponse response = mvc.perform(
                        post("/api/datesondage/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonDateSondage.write(dateSondageDto).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();

        verify(mapper, times(1)).map(dateSondageDto, DateSondage.class);
        verify(service, times(1)).create(eq(id), eq(dateSondage));
        verify(mapper, times(1)).map(dateSondage, DateSondageDto.class);
        assertThat(response.getContentAsString()).isEqualTo(jsonDateSondage.write(dateSondageDto).getJson());
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    void testCreateFailedDueToIntegrity() throws Exception {
        when(mapper.map(dateSondageDto, DateSondage.class)).thenReturn(dateSondage);
        when(service.create(id, dateSondage)).thenThrow(DataIntegrityViolationException.class);
        MockHttpServletResponse response = mvc.perform(
                        post("/api/datesondage/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonDateSondage.write(dateSondageDto).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();

        verify(mapper, times(1)).map(dateSondageDto, DateSondage.class);
        verify(service, times(1)).create(eq(id), eq(dateSondage));
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void testCreateFailed() throws Exception {
        MockHttpServletResponse response = mvc.perform(
                        post("/api/datesondage/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonDateSondage.write(dateSondageDtoNull).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();
        assertThat(response.getContentAsString()).isEqualTo("");
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }


    @Test
    void testGetDates() throws Exception {
        ArrayList<DateSondage> dateSondages = new ArrayList<>();
        dateSondages.add(dateSondage);
        when(service.getBySondageId(id)).thenReturn(dateSondages);
        when(mapper.map(dateSondage, DateSondageDto.class)).thenReturn(dateSondageDto);
        MockHttpServletResponse response = mvc.perform(
                            get("/api/datesondage/" + id)
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();

        verify(service, times(1)).getBySondageId(id);
        verify(mapper, times(1)).map(dateSondage, DateSondageDto.class);
        assertThat(response.getContentAsString()).isEqualTo("["+jsonDateSondage.write(dateSondageDto).getJson()+"]");
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void testGetDatesEmpty() throws Exception {
        ArrayList<DateSondage> dateSondages = new ArrayList<>();
        when(service.getBySondageId(id)).thenReturn(dateSondages);
        MockHttpServletResponse response = mvc.perform(
                        get("/api/datesondage/" + id)
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();

        verify(service, times(1)).getBySondageId(id);
        assertThat(response.getContentAsString()).isEqualTo("[]");
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }



    @Test
    void testDelete() throws Exception {
        when(service.delete(id)).thenReturn(true);
        MockHttpServletResponse response = mvc.perform(
                        delete("/api/datesondage/" + id))
                .andReturn().getResponse();

        verify(service, times(1)).delete(eq(id));
        assertThat(response.getContentAsString()).isEqualTo("");
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void testDeleteFailed() throws Exception {
        when(service.delete(id)).thenReturn(false);
        MockHttpServletResponse response = mvc.perform(
                        delete("/api/datesondage/" + id))
                .andReturn().getResponse();

        verify(service, times(1)).delete(eq(id));
        assertThat(response.getContentAsString()).isEqualTo("");
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}

