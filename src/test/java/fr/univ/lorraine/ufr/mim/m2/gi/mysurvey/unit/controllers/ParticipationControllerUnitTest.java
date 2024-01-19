package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.unit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.controllers.ParticipationController;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.dtos.DateSondeeDto;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.exception.DateSondageAlreadyExistsException;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.exception.DateSondeeAlreadyExistsException;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.exception.SondageCloturedException;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.DateSondage;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.DateSondee;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Participant;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Sondage;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services.DateSondeeService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;
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

import java.util.Date;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(MockitoExtension.class)
public class ParticipationControllerUnitTest {
    protected MockMvc mvc;


    @Mock
    private DateSondeeService sds;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private ParticipationController controller;

    // This object will be magically initialized by the initFields method below.
    private JacksonTester<DateSondeeDto> jsonDateSondee;


    private DateSondee dateSondee;

    private DateSondeeDto dtoDS;

    private long id;


    @BeforeEach
    void setup() {
        id = 1L;
        Participant participant = new Participant();
        participant.setParticipantId(id);
        participant.setNom("Lagler");
        participant.setPrenom("Nicolas");
        Sondage sondage = new Sondage();
        sondage.setSondageId(id);
        sondage.setNom("Sondage");
        sondage.setCloture(false);
        sondage.setFin(new Date());
        sondage.setCreateBy(participant);
        sondage.setDescription("Description");
        DateSondage dateSondage = new DateSondage();
        dateSondage.setSondage(sondage);
        dateSondage.setDate(new Date());
        dateSondage.setDateSondageId(id);
        dateSondee = new DateSondee();
        dtoDS = new DateSondeeDto();
        dtoDS.setChoix("DISPONIBLE");
        dtoDS.setParticipant(id);
        dtoDS.setDateSondeeId(id);
        dateSondee = new DateSondee();
        dateSondee.setDateSondage(dateSondage);
        dateSondee.setParticipant(participant);
        dateSondee.setChoix("DISPONIBLE");
        dateSondee.setDateSondeeId(id);
        JacksonTester.initFields(this, new ObjectMapper());
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }
    @Test
    void givenValidParameter_whenCreate_thenReturnCreated() throws Exception {
        when(mapper.map(dtoDS, DateSondee.class)).thenReturn(dateSondee);
        when(sds.create(id, id, dateSondee)).thenReturn(dateSondee);
        when(mapper.map(dateSondee, DateSondeeDto.class)).thenReturn(dtoDS);
        MockHttpServletResponse response = mvc.perform(
                        post("/api/participer/"+id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonDateSondee.write(dtoDS).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();

        verify(mapper,times(1)).map(eq(dtoDS), eq(DateSondee.class));
        verify(sds,times(1)).create(eq(id),eq(dtoDS.getParticipant()), eq(dateSondee));
        verify(mapper,times(1)).map(eq(dateSondee), eq(DateSondeeDto.class));
        assertThat(response.getContentAsString()).isEqualTo(jsonDateSondee.write(dtoDS).getJson());
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    void givenParticipantNull_whenCreate_thenReturnBadRequest() throws Exception {
        dtoDS.setParticipant(null);
        MockHttpServletResponse response = mvc.perform(
                        post("/api/participer/"+id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonDateSondee.write(dtoDS).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();
        verify(mapper,never()).map(eq(dtoDS), eq(DateSondee.class));
        verify(sds,never()).create(eq(id),eq(dtoDS.getParticipant()), eq(dateSondee));
        verify(mapper,never()).map(eq(dateSondee), eq(DateSondeeDto.class));
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void givenInvalidChoice_whenCreate_thenReturnBadRequest() throws Exception {
        dtoDS.setChoix(null);
        MockHttpServletResponse response = mvc.perform(
                        post("/api/participer/"+id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonDateSondee.write(dtoDS).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();
        verify(mapper,never()).map(eq(dtoDS), eq(DateSondee.class));
        verify(sds,never()).create(eq(id),eq(dtoDS.getParticipant()), eq(dateSondee));
        verify(mapper,never()).map(eq(dateSondee), eq(DateSondeeDto.class));
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void givenValidParameter_whenCreateButParticipationAlreadyExists_thenReturnBadRequest() throws Exception {
        when(mapper.map(dtoDS, DateSondee.class)).thenReturn(dateSondee);
        when(sds.create(id, id, dateSondee)).thenThrow(DateSondageAlreadyExistsException.class);
        MockHttpServletResponse response = mvc.perform(
                        post("/api/participer/"+id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonDateSondee.write(dtoDS).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();
        verify(mapper,times(1)).map(eq(dtoDS), eq(DateSondee.class));
        verify(sds,times(1)).create(eq(id),eq(dtoDS.getParticipant()), eq(dateSondee));
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void givenValidParameter_whenCreateButSondageCloture_thenReturnBadRequest() throws Exception {
        when(mapper.map(dtoDS, DateSondee.class)).thenReturn(dateSondee);
        when(sds.create(id, id, dateSondee)).thenThrow(SondageCloturedException.class);
        MockHttpServletResponse response = mvc.perform(
                        post("/api/participer/"+id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonDateSondee.write(dtoDS).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();
        verify(mapper,times(1)).map(eq(dtoDS), eq(DateSondee.class));
        verify(sds,times(1)).create(eq(id),eq(dtoDS.getParticipant()), eq(dateSondee));
        verify(mapper,never()).map(eq(dateSondee), eq(DateSondeeDto.class));
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void givenValidParameter_whenCreateButSondageOrParticipantDoesntExist_thenReturnNotFound() throws Exception {
        when(mapper.map(dtoDS, DateSondee.class)).thenReturn(dateSondee);
        when(sds.create(id, id, dateSondee)).thenThrow(NoResultException.class);
        MockHttpServletResponse response = mvc.perform(
                        post("/api/participer/"+id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonDateSondee.write(dtoDS).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();
        verify(mapper,times(1)).map(eq(dtoDS), eq(DateSondee.class));
        verify(sds,times(1)).create(eq(id),eq(dtoDS.getParticipant()), eq(dateSondee));
        verify(mapper,never()).map(eq(dateSondee), eq(DateSondeeDto.class));
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void givenValidParameter_whenCreateButServerError_thenReturnInternalServerError() throws Exception {
        when(mapper.map(dtoDS, DateSondee.class)).thenReturn(dateSondee);
        when(sds.create(id, id, dateSondee)).thenThrow(NullPointerException.class);
        MockHttpServletResponse response = mvc.perform(
                        post("/api/participer/"+id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonDateSondee.write(dtoDS).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();
        verify(mapper,times(1)).map(eq(dtoDS), eq(DateSondee.class));
        verify(sds,times(1)).create(eq(id),eq(dtoDS.getParticipant()), eq(dateSondee));
        verify(mapper,never()).map(eq(dateSondee), eq(DateSondeeDto.class));
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
