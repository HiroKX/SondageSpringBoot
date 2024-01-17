package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.unit.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.controllers.DateSondageController;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.dtos.DateSondeeDto;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.DateSondage;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.DateSondee;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Participant;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Sondage;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services.DateSondageService;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services.DateSondeeService;
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
    private JacksonTester<DateSondeeDto> jsonDateSondee;


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
        JacksonTester.initFields(this, new ObjectMapper());
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
     void testDelete() throws Exception {
        when(service.delete(id)).thenReturn(true);
        MockHttpServletResponse response = mvc.perform(
                        delete("/api/datesondage/"+id))
                .andReturn().getResponse();

        verify(service,times(1)).delete(eq(id));
        assertThat(response.getContentAsString()).isEqualTo("");
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
     void testDeleteFailed() throws Exception {
        when(service.delete(id)).thenReturn(false);
        MockHttpServletResponse response = mvc.perform(
                        delete("/api/datesondage/"+id))
                .andReturn().getResponse();

        verify(service,times(1)).delete(eq(id));
        assertThat(response.getContentAsString()).isEqualTo("");
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}

