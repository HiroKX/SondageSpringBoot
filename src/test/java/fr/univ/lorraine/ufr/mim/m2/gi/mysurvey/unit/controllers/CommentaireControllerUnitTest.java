package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.unit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.controllers.CommentaireController;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.dtos.CommentaireDto;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Commentaire;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Participant;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services.CommentaireService;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(MockitoExtension.class)
class CommentaireControllerUnitTest {

    protected MockMvc mvc;

    @Mock
    private CommentaireService service;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private CommentaireController controller;

    // This object will be magically initialized by the initFields method below.
    private JacksonTester<CommentaireDto> jsonCommentaire;

    private Commentaire commentaire;

    private Commentaire commentaireNull;

    private CommentaireDto dto;

    private CommentaireDto dtoNull;

    private long id;

    @BeforeEach
    void setup() {
        id = 1L;
        Participant participant = new Participant();
        participant.setParticipantId(id);
        participant.setNom("Lagler");
        participant.setPrenom("Nicolas");
        commentaire = new Commentaire();
        commentaire.setCommentaireId(id);
        commentaire.setCommentaire("Je suis un commentaire");
        commentaire.setParticipant(participant);

        commentaireNull = new Commentaire();
        dtoNull = new CommentaireDto();

        dto = new CommentaireDto();
        dto.setCommentaire("Je suis un commentaire");
        dto.setCommentaireId(id);
        dto.setParticipant(id);

        JacksonTester.initFields(this, new ObjectMapper());
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testCreate() throws Exception {
        when(mapper.map(dto, Commentaire.class)).thenReturn(commentaire);
        when(service.create(id,dto.getParticipant(),commentaire)).thenReturn(commentaire);
        when(mapper.map(commentaire, CommentaireDto.class)).thenReturn(dto);
        MockHttpServletResponse response = mvc.perform(
                        post("/api/commentaire/"+id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonCommentaire.write(dto).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();

        verify(mapper, times(1)).map(eq(dto), eq(Commentaire.class));
        verify(service, times(1)).create(eq(id), eq(dto.getParticipant()), eq(commentaire));
        verify(mapper, times(1)).map(eq(commentaire), eq(CommentaireDto.class));
        assertThat(response.getContentAsString()).isEqualTo(jsonCommentaire.write(dto).getJson());
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    void testCreateFailed() throws Exception {
        MockHttpServletResponse response = mvc.perform(
                        post("/api/commentaire/"+id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonCommentaire.write(dtoNull).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void testCreateFailedDataViolation() throws Exception {
        when(mapper.map(dto, Commentaire.class)).thenReturn(commentaire);
        when(service.create(id,dto.getParticipant(),commentaire)).thenThrow(DataIntegrityViolationException.class);

        MockHttpServletResponse response = mvc.perform(
                        post("/api/commentaire/"+id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonCommentaire.write(dto).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();
        verify(mapper, times(1)).map(eq(dto), eq(Commentaire.class));
        verify(service, times(1)).create(eq(id), eq(dto.getParticipant()), eq(commentaire));
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void testGetCommentaireFromSondageEmpty() throws Exception {
        ArrayList<Commentaire> commentaires = new ArrayList<>();
        when(service.getBySondageId(id)).thenReturn(commentaires);
        MockHttpServletResponse response = mvc.perform(
                        get("/api/commentaire/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonCommentaire.write(dto).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();

        verify(service, times(1)).getBySondageId(eq(id));
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertEquals(response.getContentAsString(), "[]");
    }

    @Test
    void testGetCommentaireFromSondage() throws Exception {
        ArrayList<Commentaire> commentaires = new ArrayList<>();
        commentaires.add(commentaire);
        when(service.getBySondageId(id)).thenReturn(commentaires);
        when(mapper.map(commentaire, CommentaireDto.class)).thenReturn(dto);
        MockHttpServletResponse response = mvc.perform(
                        get("/api/commentaire/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonCommentaire.write(dto).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();

        verify(service, times(1)).getBySondageId(eq(id));
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertEquals("["+jsonCommentaire.write(dto).getJson()+"]",response.getContentAsString());
    }

    @Test
    void testUpdate() throws Exception {
        when(mapper.map(dto, Commentaire.class)).thenReturn(commentaire);
        when(service.update(id, commentaire)).thenReturn(commentaire);
        when(mapper.map(commentaire, CommentaireDto.class)).thenReturn(dto);
        MockHttpServletResponse response = mvc.perform(
                        put("/api/commentaire/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonCommentaire.write(dto).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();

        verify(mapper, times(1)).map(eq(dto), eq(Commentaire.class));
        verify(service, times(1)).update(eq(id), eq(commentaire));
        verify(mapper, times(1)).map(eq(commentaire), eq(CommentaireDto.class));
        assertThat(response.getContentAsString()).isEqualTo(jsonCommentaire.write(dto).getJson());
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void testUpdateFailed() throws Exception {
        long IdEdited = 2L;
        when(mapper.map(dto, Commentaire.class)).thenReturn(commentaire);
        when(service.update(IdEdited, commentaire)).thenReturn(null);
        MockHttpServletResponse response = mvc.perform(
                        put("/api/commentaire/" + IdEdited)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonCommentaire.write(dto).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();

        verify(mapper, times(1)).map(eq(dto), eq(Commentaire.class));
        verify(service, times(1)).update(eq(IdEdited), eq(commentaire));
        verify(mapper, times(0)).map(eq(commentaire), eq(CommentaireDto.class));
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void testDelete() throws Exception {
        MockHttpServletResponse response = mvc.perform(
                        delete("/api/commentaire/" + id))
                .andReturn().getResponse();

        verify(service, times(1)).delete(eq(id));
        assertThat(response.getContentAsString()).isEqualTo("");
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void testDeleteFailed() throws Exception {
        MockHttpServletResponse response = mvc.perform(
                        delete("/api/commentaire/" + id))
                .andReturn().getResponse();

        verify(service, times(1)).delete(eq(id));
        assertThat(response.getContentAsString()).isEqualTo("");
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}
