package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.unit.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.junit.jupiter.api.Assertions.*;

import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.controllers.ParticipantController;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.dtos.ParticipantDto;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Participant;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services.ParticipantService;
import jakarta.persistence.EntityNotFoundException;
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
    void testGetParticipant() throws Exception {
        when(service.getById(id)).thenReturn(participant);
        when(mapper.map(participant, ParticipantDto.class)).thenReturn(participantDto);

        MockHttpServletResponse response = mvc.perform(get("/api/participant/" + id))
                .andReturn().getResponse();

        verify(service).getById(id);
        verify(mapper).map(participant, ParticipantDto.class);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertEquals(response.getContentAsString(),jsonParticipant.write(participantDto).getJson());
    }

    @Test
    void testGetParticipantFailed() throws Exception {
        when(service.getById(id)).thenThrow(EntityNotFoundException.class);

        MockHttpServletResponse response = mvc.perform(get("/api/participant/" + id))
                .andReturn().getResponse();

        verify(service).getById(id);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }


    @Test
    void testGetAllParticipants() throws Exception {
        Participant participant2 = new Participant();
        List<Participant> participants = List.of(participant,participant2);
        System.out.println(participants);
        when(service.getAll()).thenReturn(participants);
        when(mapper.map(participant, ParticipantDto.class)).thenReturn(participantDto);

        MockHttpServletResponse response = mvc.perform(get("/api/participant/"))
                .andReturn().getResponse();

        verify(service).getAll();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("["+jsonParticipant.write(participantDto).getJson()+","+jsonParticipant.write(participantDto).getJson()+"]");

    }

    @Test
    void testCreateParticipant() throws Exception {

        when(mapper.map(participantDto, Participant.class)).thenReturn(participant);
        when(service.create(participant)).thenReturn(participant);
        when(mapper.map(participant, ParticipantDto.class)).thenReturn(participantDto);

        MockHttpServletResponse response = mvc.perform(post("/api/participant/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonParticipant.write(participantDto).getJson()))
                .andReturn().getResponse();

        verify(service).create(participant);
        verify(mapper).map(participantDto, Participant.class);
        verify(mapper).map(participant, ParticipantDto.class);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getContentAsString()).isNotEqualTo("");
    }

    @Test
    void testCreateParticipantFailed() throws Exception {
        MockHttpServletResponse response = mvc.perform(post("/api/participant/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonParticipant.write(participantDtoNull).getJson()))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void testCreateParticipantFailedNotFound() throws Exception {
        when(mapper.map(participantDto, Participant.class)).thenReturn(participant);
        when(service.create(participant)).thenReturn(null);
        MockHttpServletResponse response = mvc.perform(post("/api/participant/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonParticipant.write(participantDto).getJson()))
                .andReturn().getResponse();
        verify(service).create(participant);
        verify(mapper).map(participantDto, Participant.class);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void testUpdateParticipant() throws Exception {
        when(mapper.map(participantDto, Participant.class)).thenReturn(participant);
        when(service.update(id, participant)).thenReturn(participant);
        when(mapper.map(participant, ParticipantDto.class)).thenReturn(participantDto);

        MockHttpServletResponse response = mvc.perform(
                put("/api/participant/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonParticipant.write(participantDto).getJson())).andReturn().getResponse();
        verify(mapper).map(participantDto, Participant.class);
        verify(service).update(id, participant);
        verify(mapper).map(participant, ParticipantDto.class);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isNotEqualTo("");
    }

    @Test
    void testUpdateParticipantFailed() throws Exception {

        MockHttpServletResponse response = mvc.perform(
                put("/api/participant/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonParticipant.write(participantDtoNull).getJson())).andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void testUpdateParticipantFailedNotFound() throws Exception {
        when(mapper.map(participantDto, Participant.class)).thenReturn(participant);
        when(service.update(id,participant)).thenReturn(null);
        MockHttpServletResponse response = mvc.perform(
                put("/api/participant/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonParticipant.write(participantDto).getJson())).andReturn().getResponse();
        verify(service).update(id, participant);
        verify(mapper).map(participantDto, Participant.class);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
    @Test
    void testDelete() throws Exception {
        when(service.delete(id)).thenReturn(true);
        MockHttpServletResponse response = mvc.perform(
                        delete("/api/participant/"+id))
                .andReturn().getResponse();

        verify(service,times(1)).delete(eq(id));
        assertThat(response.getContentAsString()).isEqualTo("");
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void testDeleteFailed() throws Exception {
        when(service.delete(id)).thenReturn(false);
        MockHttpServletResponse response = mvc.perform(
                        delete("/api/participant/"+id))
                .andReturn().getResponse();

        verify(service,times(1)).delete(eq(id));
        assertThat(response.getContentAsString()).isEqualTo("");
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

}