package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.unit.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.junit.jupiter.api.Assertions.*;

import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.controllers.ParticipantController;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.dtos.ParticipantDto;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Participant;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services.ParticipantService;
import jakarta.persistence.NoResultException;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.http.MediaType;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.mock.web.MockHttpServletResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;

@ExtendWith(MockitoExtension.class)
class ParticipantControllerUnitTest {

    private MockMvc mvc;

    @Mock
    private ParticipantService service;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private ParticipantController controller;

    private JacksonTester<ParticipantDto> jsonParticipant;

    private Participant participant;

    private ParticipantDto participantDto;

    private ParticipantDto participantDtoNull;

    private final long id = 1L;

    @BeforeEach
    void setup() {
        participant = new Participant();

        participantDto = new ParticipantDto();
        participantDto.setPrenom("prenom");
        participantDto.setNom("nom");
        participantDtoNull = new ParticipantDto();
        JacksonTester.initFields(this, new ObjectMapper());
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void givenValidParameters_whenCreate_thenReturnCreated() throws Exception {
        when(mapper.map(participantDto, Participant.class)).thenReturn(participant);
        when(service.create(participant)).thenReturn(participant);
        when(mapper.map(participant, ParticipantDto.class)).thenReturn(participantDto);

        MockHttpServletResponse response = mvc.perform(post("/api/participant/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonParticipant.write(participantDto).getJson()))
                .andReturn().getResponse();

        verify(service, times(1)).create(participant);
        verify(mapper, times(1)).map(participantDto, Participant.class);
        verify(mapper, times(1)).map(participant, ParticipantDto.class);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getContentAsString()).isNotEqualTo("");
    }

    @Test
    void givenInvalidParameters_whenCreate_thenReturnBadRequest() throws Exception {
        MockHttpServletResponse response = mvc.perform(post("/api/participant/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonParticipant.write(new ParticipantDto()).getJson()))
                .andReturn().getResponse();

        verify(service, never()).create(participant);
        verify(mapper, never()).map(participantDto, Participant.class);
        verify(mapper, never()).map(participant, ParticipantDto.class);
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void givenInvalidParticipantName_whenCreate_thenReturnBadRequest() throws Exception {
        ParticipantDto p = new ParticipantDto();
        p.setNom("Test");
        MockHttpServletResponse response = mvc.perform(post("/api/participant/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonParticipant.write(p).getJson()))
                .andReturn().getResponse();

        verify(service, never()).create(participant);
        verify(mapper, never()).map(participantDto, Participant.class);
        verify(mapper, never()).map(participant, ParticipantDto.class);
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void givenInvalidParticipantPrenom_whenCreate_thenReturnBadRequest() throws Exception {
        ParticipantDto p = new ParticipantDto();
        p.setPrenom("Test");
        MockHttpServletResponse response = mvc.perform(post("/api/participant/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonParticipant.write(p).getJson()))
                .andReturn().getResponse();

        verify(service, never()).create(participant);
        verify(mapper, never()).map(participantDto, Participant.class);
        verify(mapper, never()).map(participant, ParticipantDto.class);
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void givenValidParameters_whenCreateButServerError_thenReturnInternalServerError() throws Exception {
        when(mapper.map(participantDto, Participant.class)).thenReturn(participant);
        when(service.create(participant)).thenThrow(NullPointerException.class);

        MockHttpServletResponse response = mvc.perform(post("/api/participant/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonParticipant.write(participantDto).getJson()))
                .andReturn().getResponse();

        verify(service, times(1)).create(participant);
        verify(mapper, times(1)).map(participantDto, Participant.class);
        verify(mapper, never()).map(participant, ParticipantDto.class);
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    void givenValidParameters_whenGetAllParticipants_thenReturnOk() throws Exception {
        when(service.getAll()).thenReturn(List.of(participant));
        when(mapper.map(participant, ParticipantDto.class)).thenReturn(participantDto);

        MockHttpServletResponse response = mvc.perform(get("/api/participant/"))
                .andReturn().getResponse();

        verify(service, times(1)).getAll();
        verify(mapper, times(1)).map(participant, ParticipantDto.class);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertEquals(response.getContentAsString(),"[" + jsonParticipant.write(participantDto).getJson() + "]");
    }

    @Test
    void givenValidParameters_whenGetAllParticipantsButNoParticipantExist_thenReturnNoContent() throws Exception {
        when(service.getAll()).thenThrow(NoResultException.class);

        MockHttpServletResponse response = mvc.perform(get("/api/participant/"))
                .andReturn().getResponse();

        verify(service, times(1)).getAll();
        verify(mapper, never()).map(participant, ParticipantDto.class);
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void givenValidParameters_whenGetAllParticipantsButServerError_thenReturnInternalServerError() throws Exception {
        when(service.getAll()).thenThrow(NullPointerException.class);

        MockHttpServletResponse response = mvc.perform(get("/api/participant/"))
                .andReturn().getResponse();

        verify(service, times(1)).getAll();
        verify(mapper, never()).map(participant, ParticipantDto.class);
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    void givenValidParameters_whenGetParticipantById_thenReturnOk() throws Exception {
        when(service.getById(id)).thenReturn(participant);
        when(mapper.map(participant, ParticipantDto.class)).thenReturn(participantDto);

        MockHttpServletResponse response = mvc.perform(get("/api/participant/" + id))
                .andReturn().getResponse();

        verify(service, times(1)).getById(id);
        verify(mapper, times(1)).map(participant, ParticipantDto.class);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertEquals(response.getContentAsString(),jsonParticipant.write(participantDto).getJson());
    }

    @Test
    void givenValidParameters_whenGetParticipantByIdButNoParticipantExist_thenReturnNotFound() throws Exception {
        when(service.getById(id)).thenThrow(NoResultException.class);

        MockHttpServletResponse response = mvc.perform(get("/api/participant/" + id))
                .andReturn().getResponse();

        verify(service, times(1)).getById(id);
        verify(mapper, never()).map(participant, ParticipantDto.class);
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void givenValidParameters_whenGetParticipantByIdButServerError_thenReturnInternalServerError() throws Exception {
        when(service.getById(id)).thenThrow(NullPointerException.class);

        MockHttpServletResponse response = mvc.perform(get("/api/participant/" + id))
                .andReturn().getResponse();

        verify(service, times(1)).getById(id);
        verify(mapper, never()).map(participant, ParticipantDto.class);
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    void givenValidParameters_whenUpdate_thenReturnOk() throws Exception {
        when(service.getById(id)).thenReturn(new Participant());
        when(service.update(id, participant)).thenReturn(participant);

        when(mapper.map(participantDto, Participant.class)).thenReturn(participant);
        when(mapper.map(any(), eq(ParticipantDto.class))).thenReturn(new ParticipantDto());

        MockHttpServletResponse response = mvc.perform(
                put("/api/participant/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonParticipant.write(participantDto).getJson())).andReturn().getResponse();

        verify(service, times(1)).getById(id);
        verify(service, times(1)).update(id, participant);
        assertThat(response.getContentAsString()).isNotEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void givenValidParameters_whenUpdateButNoChange_thenReturnNoContent() throws Exception {
        when(service.getById(id)).thenReturn(new Participant());
        when(mapper.map(any(), eq(ParticipantDto.class))).thenReturn(participantDto);

        MockHttpServletResponse response = mvc.perform(
                put("/api/participant/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonParticipant.write(participantDto).getJson())).andReturn().getResponse();

        verify(service, times(1)).getById(id);
        verify(service, never()).update(id, participant);
        System.out.println(response.getContentAsString());
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void givenInvalidParticipant_whenUpdateParticipant_thenReturnBadRequest() throws Exception {
        MockHttpServletResponse response = mvc.perform(
                put("/api/participant/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonParticipant.write(new ParticipantDto()).getJson())).andReturn().getResponse();

        verify(service, times(0)).getById(id);
        verify(service, times(0)).update(id, participant);
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void givenInvalidParticipantName_whenUpdateParticipant_thenReturnBadRequest() throws Exception {
        ParticipantDto p = new ParticipantDto();
        p.setNom("Test");
        MockHttpServletResponse response = mvc.perform(
                put("/api/participant/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonParticipant.write(p).getJson())).andReturn().getResponse();

        verify(service, times(0)).getById(id);
        verify(service, times(0)).update(id, participant);
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void givenInvalidParticipantPrenom_whenUpdateParticipant_thenReturnBadRequest() throws Exception {
        ParticipantDto p = new ParticipantDto();
        p.setPrenom("Test");
        MockHttpServletResponse response = mvc.perform(
                put("/api/participant/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonParticipant.write(p).getJson())).andReturn().getResponse();

        verify(service, times(0)).getById(id);
        verify(service, times(0)).update(id, participant);
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void givenValidParameters_whenUpdateButParticipantDoesNotExist_thenReturnBadRequest() throws Exception {
        when(service.getById(id)).thenReturn(new Participant());
        when(service.update(id, participant)).thenThrow(NoSuchElementException.class);

        when(mapper.map(participantDto, Participant.class)).thenReturn(participant);
        when(mapper.map(any(), eq(ParticipantDto.class))).thenReturn(new ParticipantDto());

        MockHttpServletResponse response = mvc.perform(
                put("/api/participant/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonParticipant.write(participantDto).getJson())).andReturn().getResponse();

        verify(service, times(1)).getById(id);
        verify(service, times(1)).update(id, participant);
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void givenValidParameters_whenUpdateButParticipantDoesNotExist_thenReturnNoContent() throws Exception {
        when(service.getById(id)).thenThrow(NoResultException.class);

        MockHttpServletResponse response = mvc.perform(
                put("/api/participant/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonParticipant.write(participantDto).getJson())).andReturn().getResponse();

        verify(service, times(1)).getById(id);
        verify(service, never()).update(id, participant);
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void givenValidParameters_whenUpdateButServerError_thenReturnInternalServerError() throws Exception {
        when(service.getById(id)).thenReturn(new Participant());
        when(service.update(id, participant)).thenThrow(NullPointerException.class);

        when(mapper.map(participantDto, Participant.class)).thenReturn(participant);
        when(mapper.map(any(), eq(ParticipantDto.class))).thenReturn(new ParticipantDto());

        MockHttpServletResponse response = mvc.perform(
                put("/api/participant/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonParticipant.write(participantDto).getJson())).andReturn().getResponse();

        verify(service, times(1)).getById(id);
        verify(service, times(1)).update(id, participant);
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    void givenValidParameters_whenDelete_ReturnNoContent() throws Exception {
        MockHttpServletResponse response = mvc.perform(
                        delete("/api/participant/"+id))
                .andReturn().getResponse();

        verify(service,times(1)).delete(eq(id));
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void givenValidParameters_whenDeleteButServerError_thenReturnInternalServerError() throws Exception {
        doThrow(NoResultException.class).when(service).delete(id);
        MockHttpServletResponse response = mvc.perform(
                        delete("/api/participant/"+id))
                .andReturn().getResponse();

        verify(service,times(1)).delete(eq(id));
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    void givenValidParameters_whenDeleteButParticipantDoesNotExist_thenReturnBadRequest() throws Exception {
        doThrow(NoSuchElementException.class).when(service).delete(id);
        MockHttpServletResponse response = mvc.perform(
                        delete("/api/participant/"+id))
                .andReturn().getResponse();

        verify(service,times(1)).delete(eq(id));
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
