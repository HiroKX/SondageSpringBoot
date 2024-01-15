package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.unit.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.controllers.CommentaireController;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.dtos.CommentaireDto;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Commentaire;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Participant;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Sondage;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services.CommentaireService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@ExtendWith(MockitoExtension.class)
public class CommentaireControllerTest {

    protected MockMvc mvc;

    @Mock
    private CommentaireService service;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private CommentaireController controller;

    // This object will be magically initialized by the initFields method below.
    private JacksonTester<Commentaire> jsonCommentaire;

    @BeforeEach
    public void setup() {
        // We would need this line if we would not use the MockitoExtension
        // MockitoAnnotations.initMocks(this);
        // Here we can't use @AutoConfigureJsonTesters because there isn't a Spring context
        JacksonTester.initFields(this, new ObjectMapper());
        // MockMvc standalone approach
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }
    @Test
    public void testUpdate() throws Exception {
        Long id = 1L;
        String uri = "/api/commentaire/"+id;
        Participant participant = new Participant();
        participant.setParticipantId(id);
        Commentaire commentaire = new Commentaire();
        commentaire.setCommentaireId(id);
        commentaire.setCommentaire("Je suis un commentaire");
        commentaire.setParticipant(participant);
        CommentaireDto dto = new CommentaireDto();
        dto.setCommentaire("Je suis un commentaire");
        dto.setCommentaireId(id);
        dto.setParticipant(id);

        when(mapper.map(dto, Commentaire.class)).thenReturn(commentaire);
        when(service.update(id, commentaire)).thenReturn(commentaire);
        when(mapper.map(commentaire, CommentaireDto.class)).thenReturn(dto);
/**
        String inputJson = this.mapToJson(dto);
        MvcResult mvcResult = mvc.perform(put(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        verify(service, times(1)).update(any(), any());
        verify(mapper).map(dto, Commentaire.class);
        verify(mapper).map(commentaire, CommentaireDto.class);*/
    }


    @Test
    public void testUpdate2() throws Exception {
        Long id = 1L;

        Participant participant = new Participant();
        participant.setParticipantId(id);
        Commentaire commentaire = new Commentaire();
        commentaire.setCommentaireId(id);
        commentaire.setCommentaire("Je suis un commentaire");
        commentaire.setParticipant(participant);
        CommentaireDto dto = new CommentaireDto();
        dto.setCommentaire("Je suis un commentaire");
        dto.setCommentaireId(id);
        dto.setParticipant(id);
        when(mapper.map(dto, Commentaire.class)).thenReturn(commentaire);
        when(service.update(id, commentaire)).thenReturn(commentaire);
        when(mapper.map(commentaire, CommentaireDto.class)).thenReturn(dto);
        System.out.println(jsonCommentaire.write(commentaire).getJson());
        MvcResult result = mvc.perform(
                put("/api/commentaire/"+id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCommentaire.write(commentaire).getJson())
                        .characterEncoding("UTF-8"))
                        .andReturn();

        String requestBody = result.getRequest().getContentAsString();
        String requestUrl = result.getRequest().getRequestURL().toString();
        String requestMethod = result.getRequest().getMethod();

        // Print the request details
        System.out.println("Request URL: " + requestUrl);
        System.out.println("Request Method: " + requestMethod);
        System.out.println("Request Body: " + requestBody);

        // Get and print the response
        MockHttpServletResponse response = result.getResponse();
        System.out.println("Response Status: " + response.getStatus());
        System.out.println("Response Body: " + response.getContentAsString());
        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void testDelete() {
        Long id = 1L;

        controller.delete(id);

        verify(service).delete(id);
    }
}
