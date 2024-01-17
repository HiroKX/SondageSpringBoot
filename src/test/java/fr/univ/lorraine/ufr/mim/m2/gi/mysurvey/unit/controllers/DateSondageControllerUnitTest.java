package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.unit.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.controllers.DateSondageController;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.dtos.DateSondageDto;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.NoSuchElementException;

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
    void givenValidParameters_whenCreate_thenReturnCreated() throws Exception {
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
    void givenDateNull_whenCreate_thenReturnBadRequest() throws Exception {
        var dto = new DateSondageDto();
        dto.setDate(null);
        MockHttpServletResponse response = mvc.perform(
                        post("/api/datesondage/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonDateSondage.write(dto).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();

        verify(mapper, never()).map(dateSondageDto, DateSondage.class);
        verify(service, never()).create(eq(id), eq(dateSondage));
        verify(mapper, never()).map(dateSondage, DateSondageDto.class);
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void givenInvalidDate_whenCreate_thenReturnBadRequest() throws Exception {
        var dto = new DateSondageDto();
        dto.setDate(new Date());
        MockHttpServletResponse response = mvc.perform(
                        post("/api/datesondage/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonDateSondage.write(dto).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();

        verify(mapper, never()).map(dateSondageDto, DateSondage.class);
        verify(service, never()).create(eq(id), eq(dateSondage));
        verify(mapper, never()).map(dateSondage, DateSondageDto.class);
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void givenValidParameters_whenCreateButDateAlreadyExists_thenReturnNotAcceptable() throws Exception {
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
        verify(mapper, never()).map(dateSondage, DateSondageDto.class);
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_ACCEPTABLE.value());
    }

    @Test
    void givenValidParameters_whenCreateButSondageDoesNotExists_thenReturnBadRequest() throws Exception {
        when(mapper.map(dateSondageDto, DateSondage.class)).thenReturn(dateSondage);
        when(service.create(id, dateSondage)).thenThrow(NoSuchElementException.class);
        MockHttpServletResponse response = mvc.perform(
                        post("/api/datesondage/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonDateSondage.write(dateSondageDto).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();

        verify(mapper, times(1)).map(dateSondageDto, DateSondage.class);
        verify(service, times(1)).create(eq(id), eq(dateSondage));
        verify(mapper, never()).map(dateSondage, DateSondageDto.class);
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void givenValidParameters_whenCreateButServerError_thenReturnInternalServerError() throws Exception {
        when(mapper.map(dateSondageDto, DateSondage.class)).thenReturn(dateSondage);
        when(service.create(id, dateSondage)).thenThrow(NullPointerException.class);
        MockHttpServletResponse response = mvc.perform(
                        post("/api/datesondage/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonDateSondage.write(dateSondageDto).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();

        verify(mapper, times(1)).map(dateSondageDto, DateSondage.class);
        verify(service, times(1)).create(eq(id), eq(dateSondage));
        verify(mapper, never()).map(dateSondage, DateSondageDto.class);
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    void givenValidParameters_whenGetDates_thenReturnOk() throws Exception {
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
    void givenValidParameters_whenGetDatesButSondageDoesNotExist_thenReturnBadRequest() throws Exception {
        when(service.getBySondageId(id)).thenThrow(NoSuchElementException.class);
        MockHttpServletResponse response = mvc.perform(
                        get("/api/datesondage/" + id)
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();

        verify(service, times(1)).getBySondageId(id);
        verify(mapper, never()).map(dateSondage, DateSondageDto.class);
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void givenValidParameters_whenGetDatesButDateDoesNotExist_thenReturnNotFound() throws Exception {
        when(service.getBySondageId(id)).thenReturn(new ArrayList<>());
        MockHttpServletResponse response = mvc.perform(
                        get("/api/datesondage/" + id)
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();

        verify(service, times(1)).getBySondageId(id);
        verify(mapper, never()).map(dateSondage, DateSondageDto.class);
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void givenValidParameters_whenGetDatesButServerError_thenReturnInternalServerError() throws Exception {
        when(service.getBySondageId(id)).thenThrow(NullPointerException.class);
        MockHttpServletResponse response = mvc.perform(
                        get("/api/datesondage/" + id)
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();

        verify(service, times(1)).getBySondageId(id);
        verify(mapper, never()).map(dateSondage, DateSondageDto.class);
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    void givenExistingId_whenDelete_thenReturnOk() throws Exception {
        doNothing().when(service).delete(id);
        MockHttpServletResponse response = mvc.perform(
                        delete("/api/datesondage/" + id))
                .andReturn().getResponse();

        verify(service, times(1)).delete(eq(id));
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void givenNotExistingId_whenDelete_thenReturnBadRequest() throws Exception {
        doThrow(NoSuchElementException.class).when(service).delete(id);
        MockHttpServletResponse response = mvc.perform(
                        delete("/api/datesondage/" + id))
                .andReturn().getResponse();

        verify(service, times(1)).delete(eq(id));
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void givenExistingId_whenDeleteButServerError_thenReturnInternalServerError() throws Exception {
        doThrow(NullPointerException.class).when(service).delete(id);
        MockHttpServletResponse response = mvc.perform(
                        delete("/api/datesondage/" + id))
                .andReturn().getResponse();

        verify(service, times(1)).delete(eq(id));
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}

