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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

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

    private CommentaireDto dto;

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
        dto = new CommentaireDto();
        dto.setCommentaire("Je suis un commentaire");
        dto.setCommentaireId(id);
        dto.setParticipant(id);

        JacksonTester.initFields(this, new ObjectMapper());
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
     void testUpdate() throws Exception {
        when(mapper.map(dto, Commentaire.class)).thenReturn(commentaire);
        when(service.update(id, commentaire)).thenReturn(commentaire);
        when(mapper.map(commentaire, CommentaireDto.class)).thenReturn(dto);
        MockHttpServletResponse response = mvc.perform(
                put("/api/commentaire/"+id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCommentaire.write(dto).getJson())
                        .characterEncoding("UTF-8"))
                        .andReturn().getResponse();

        verify(mapper,times(1)).map(eq(dto), eq(Commentaire.class));
        verify(service,times(1)).update(eq(id), eq(commentaire));
        verify(mapper,times(1)).map(eq(commentaire), eq(CommentaireDto.class));
        assertThat(response.getContentAsString()).isEqualTo(jsonCommentaire.write(dto).getJson());
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
     void testUpdateFailed() throws Exception {
        long IdEdited = 2L;
        when(mapper.map(dto, Commentaire.class)).thenReturn(commentaire);
        when(service.update(IdEdited, commentaire)).thenReturn(null);
        MockHttpServletResponse response = mvc.perform(
                        put("/api/commentaire/"+IdEdited)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonCommentaire.write(dto).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();

        verify(mapper,times(1)).map(eq(dto), eq(Commentaire.class));
        verify(service,times(1)).update(eq(IdEdited), eq(commentaire));
        verify(mapper,times(0)).map(eq(commentaire), eq(CommentaireDto.class));
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
     void testDelete() throws Exception {
        when(service.delete(id)).thenReturn(true);
        MockHttpServletResponse response = mvc.perform(
                        delete("/api/commentaire/"+id))
                .andReturn().getResponse();

        verify(service,times(1)).delete(eq(id));
        assertThat(response.getContentAsString()).isEqualTo("");
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
     void testDeleteFailed() throws Exception {
        when(service.delete(id)).thenReturn(false);
        MockHttpServletResponse response = mvc.perform(
                        delete("/api/commentaire/"+id))
                .andReturn().getResponse();

        verify(service,times(1)).delete(eq(id));
        assertThat(response.getContentAsString()).isEqualTo("");
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}
