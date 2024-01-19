package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.unit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.controllers.CommentaireController;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.dtos.CommentaireDto;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Commentaire;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Participant;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services.CommentaireService;
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

import java.util.ArrayList;
import java.util.NoSuchElementException;

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
    void givenValidParameters_whenCreate_thenReturnCreated() throws Exception {
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
    void givenInvalidCommentaire_whenCreate_thenReturnBadRequest() throws Exception {
        CommentaireDto newDto = new CommentaireDto();
        newDto.setParticipant(1L);
        MockHttpServletResponse response = mvc.perform(
                        post("/api/commentaire/"+id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonCommentaire.write(newDto).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();

        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void givenInvalidParticipant_whenCreate_thenReturnBadRequest() throws Exception {
        CommentaireDto newDto = new CommentaireDto();
        newDto.setCommentaire("Je suis un commentaire");
        MockHttpServletResponse response = mvc.perform(
                        post("/api/commentaire/"+id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonCommentaire.write(newDto).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();

        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void givenValidParameters_whenCreateButSondageOrParticipantDoesNotExist_thenReturnBadRequest() throws Exception {
        when(mapper.map(dto, Commentaire.class)).thenReturn(commentaire);
        when(service.create(id,dto.getParticipant(),commentaire)).thenThrow(NoSuchElementException.class);
        MockHttpServletResponse response = mvc.perform(
                        post("/api/commentaire/"+id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonCommentaire.write(dto).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();

        verify(mapper, times(1)).map(eq(dto), eq(Commentaire.class));
        verify(service, times(1)).create(eq(id), eq(dto.getParticipant()), eq(commentaire));
        verify(mapper, never()).map(eq(commentaire), eq(CommentaireDto.class));
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void givenValidParameters_whenCreateButServerError_thenReturnInternalServerError() throws Exception {
        when(mapper.map(dto, Commentaire.class)).thenReturn(commentaire);
        when(service.create(id,dto.getParticipant(),commentaire)).thenThrow(NullPointerException.class);
        MockHttpServletResponse response = mvc.perform(
                        post("/api/commentaire/"+id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonCommentaire.write(dto).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();

        verify(mapper, times(1)).map(eq(dto), eq(Commentaire.class));
        verify(service, times(1)).create(eq(id), eq(dto.getParticipant()), eq(commentaire));
        verify(mapper, never()).map(eq(commentaire), eq(CommentaireDto.class));
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    void givenValidParameters_whenGetAllCommentairesBySondageId_thenReturnOk() throws Exception {
        ArrayList<Commentaire> commentaires = new ArrayList<>();
        Commentaire com = new Commentaire();
        commentaires.add(com);
        when(service.getBySondageId(id)).thenReturn(commentaires);
        when(mapper.map(com,CommentaireDto.class)).thenReturn(new CommentaireDto());

        MockHttpServletResponse response = mvc.perform(
                        get("/api/commentaire/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonCommentaire.write(dto).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();

        verify(mapper, times(1)).map(com, CommentaireDto.class);
        verify(service, times(1)).getBySondageId(id);
        assertThat(response.getContentAsString()).isEqualTo("[" + jsonCommentaire.write(new CommentaireDto()).getJson() + "]");
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }



    @Test
    void givenValidParameters_whenGetAllCommentairesBySondageIdButSondageDoesNotExist_thenReturnBadRequest() throws Exception {
        when(service.getBySondageId(id)).thenThrow(NoSuchElementException.class);
        MockHttpServletResponse response = mvc.perform(
                        get("/api/commentaire/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonCommentaire.write(dto).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();

        verify(service, times(1)).getBySondageId(eq(id));
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void givenValidParameters_whenGetAllCommentairesBySondageIdButNoResultFound_thenReturnNotFound() throws Exception {
        when(service.getBySondageId(id)).thenThrow(NoResultException.class);
        MockHttpServletResponse response = mvc.perform(
                        get("/api/commentaire/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonCommentaire.write(dto).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();

        verify(service, times(1)).getBySondageId(eq(id));
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void givenValidParameters_whenGetAllCommentairesBySondageIdButServerError_thenReturnInternalServerError() throws Exception {
        when(service.getBySondageId(id)).thenThrow(NullPointerException.class);
        MockHttpServletResponse response = mvc.perform(
                        get("/api/commentaire/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonCommentaire.write(dto).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();

        verify(service, times(1)).getBySondageId(eq(id));
        assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    void givenValidParameters_whenUpdate_thenReturnOk() throws Exception {
        when(service.getById(id)).thenReturn(new Commentaire());
        when(service.update(id, commentaire)).thenReturn(commentaire);

        when(mapper.map(dto, Commentaire.class)).thenReturn(commentaire);
        when(mapper.map(any(), eq(CommentaireDto.class))).thenReturn(new CommentaireDto());
        MockHttpServletResponse response = mvc.perform(
                        put("/api/commentaire/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonCommentaire.write(dto).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();
        verify(service, times(1)).getById(eq(id));
        verify(service, times(1)).update(eq(id), eq(commentaire));
        assertThat(response.getContentAsString()).isEqualTo(jsonCommentaire.write(new CommentaireDto()).getJson());
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void givenValidParameters_whenUpdateButNoChange_thenReturnNoContent() throws Exception {
        when(service.getById(id)).thenReturn(new Commentaire());
        when(mapper.map(any(), eq(CommentaireDto.class))).thenReturn(dto);
        MockHttpServletResponse response = mvc.perform(
                        put("/api/commentaire/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonCommentaire.write(dto).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();
        verify(service, times(1)).getById(eq(id));
        verify(service, never()).update(eq(id), eq(commentaire));
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void givenValidParameters_whenUpdateButCommentaireDoesNotExists_thenReturnBadRequest() throws Exception {
        when(service.getById(id)).thenReturn(new Commentaire());
        when(service.update(id, commentaire)).thenThrow(NoSuchElementException.class);

        when(mapper.map(dto, Commentaire.class)).thenReturn(commentaire);
        when(mapper.map(any(), eq(CommentaireDto.class))).thenReturn(new CommentaireDto());
        MockHttpServletResponse response = mvc.perform(
                        put("/api/commentaire/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonCommentaire.write(dto).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();
        verify(service, times(1)).getById(eq(id));
        verify(service, times(1)).update(eq(id), eq(commentaire));
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void givenInvalidCommentaire_whenUpdateButCommentaireDoesNotExists_thenReturnBadRequest() throws Exception {

        MockHttpServletResponse response = mvc.perform(
                        put("/api/commentaire/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonCommentaire.write(new CommentaireDto()).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();
        verify(service, times(0)).getById(eq(id));
        verify(service, times(0)).update(eq(id), eq(commentaire));
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void givenValidParameters_whenUpdateButServerError_thenReturnInternalServerError() throws Exception {
        when(service.getById(id)).thenReturn(new Commentaire());
        when(service.update(id, commentaire)).thenThrow(NullPointerException.class);

        when(mapper.map(dto, Commentaire.class)).thenReturn(commentaire);
        when(mapper.map(any(), eq(CommentaireDto.class))).thenReturn(new CommentaireDto());
        MockHttpServletResponse response = mvc.perform(
                        put("/api/commentaire/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonCommentaire.write(dto).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();
        verify(service, times(1)).getById(eq(id));
        verify(service, times(1)).update(eq(id), eq(commentaire));
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    void givenValidParameters_whenDelete_thenReturnNoContent() throws Exception {
        MockHttpServletResponse response = mvc.perform(
                        delete("/api/commentaire/" + id))
                .andReturn().getResponse();

        verify(service, times(1)).delete(eq(id));
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void givenValidParameters_whenDeleteButCommentaireDoesNotExist_thenReturnBadRequest() throws Exception {
        doThrow(NoSuchElementException.class).when(service).delete(eq(id));

        MockHttpServletResponse response = mvc.perform(
                        delete("/api/commentaire/" + id))
                .andReturn().getResponse();

        verify(service, times(1)).delete(eq(id));
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void givenValidParameters_whenDeleteButServerError_thenReturnInternalServerError() throws Exception {
        doThrow(NullPointerException.class).when(service).delete(eq(id));

        MockHttpServletResponse response = mvc.perform(
                        delete("/api/commentaire/" + id))
                .andReturn().getResponse();

        verify(service, times(1)).delete(eq(id));
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
