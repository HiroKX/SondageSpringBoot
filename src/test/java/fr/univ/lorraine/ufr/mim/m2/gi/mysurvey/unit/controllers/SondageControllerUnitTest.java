package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.unit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.controllers.SondageController;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.dtos.SondageDto;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Participant;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Sondage;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services.DateSondeeService;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services.SondageService;
import jakarta.persistence.EntityNotFoundException;
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

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
@ExtendWith(MockitoExtension.class)
public class SondageControllerUnitTest {


    protected MockMvc mvc;
    @Mock
    private SondageService service;
    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private SondageController controller;

    private JacksonTester<SondageDto> jsonSondage;

    @Mock
    private DateSondeeService request;

    private Sondage sondage;

    private SondageDto dto;
    private long id;

    private Date dateDansUnAn;

    @BeforeEach
    public void setup() {
        id = 1L;
        Calendar calendar = Calendar.getInstance(); // Obtient une instance de Calendar représentant la date/heure actuelle
        calendar.add(Calendar.YEAR, 1); // Ajoute un an à la date/heure actuelle

        dateDansUnAn = calendar.getTime(); // Convertit le Calendar en Date
        sondage = new Sondage();
        sondage.setSondageId(id);
        sondage.setNom("Sondage test");
        sondage.setCreateBy(new Participant());
        sondage.setDescription("Description test");
        sondage.setFin(dateDansUnAn);
        dto = new SondageDto();
        dto.setSondageId(id);
        dto.setNom("Sondage test");
        dto.setCreateBy(id);
        dto.setFin(dateDansUnAn);
        dto.setCloture(false);
        dto.setDescription("Description test");
        JacksonTester.initFields(this, new ObjectMapper());
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void testGetAll() throws Exception {
        when(service.getAll()).thenReturn(List.of(sondage));
        when(mapper.map(sondage, SondageDto.class)).thenReturn(dto);
        MockHttpServletResponse response = mvc.perform(
                        get("/api/sondage/"))
                .andReturn().getResponse();

        verify(service,times(1)).getAll();
        verify(mapper,times(1)).map(eq(sondage), eq(SondageDto.class));
        assertThat(response.getContentAsString()).isEqualTo("[" + jsonSondage.write(dto).getJson() + "]");
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void testGetAllEmpty() throws Exception {
        when(service.getAll()).thenReturn(List.of());
        MockHttpServletResponse response = mvc.perform(
                        get("/api/sondage/"))
                .andReturn().getResponse();

        verify(service,times(1)).getAll();
        assertThat(response.getContentAsString()).isEqualTo("[]");
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void testGetById() throws Exception {
        when(service.getById(id)).thenReturn(sondage);
        when(mapper.map(sondage, SondageDto.class)).thenReturn(dto);
        MockHttpServletResponse response = mvc.perform(
                        get("/api/sondage/"+id))
                .andReturn().getResponse();

        verify(service,times(1)).getById(eq(id));
        verify(mapper,times(1)).map(eq(sondage), eq(SondageDto.class));
        assertThat(response.getContentAsString()).isEqualTo(jsonSondage.write(dto).getJson());
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void testGetByIdFailed() throws Exception {
        when(service.getById(id)).thenThrow(EntityNotFoundException.class);
        MockHttpServletResponse response = mvc.perform(
                        get("/api/sondage/"+id))
                .andReturn().getResponse();

        verify(service,times(1)).getById(eq(id));
        assertThat(response.getContentAsString()).isEqualTo("");
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void testGetBest() throws Exception {
        Date d = new Date();
        when(request.getBestDateBySondageId(id)).thenReturn(List.of(d));
        MockHttpServletResponse response = mvc.perform(
                        get("/api/sondage/"+id+"/best"))
                .andReturn().getResponse();

        verify(request,times(1)).getBestDateBySondageId(id);
        assertThat(response.getContentAsString()).isEqualTo("[" + d.getTime() + "]");
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void testGetBestEmpty() throws Exception {
        when(request.getBestDateBySondageId(id)).thenReturn(List.of());
        MockHttpServletResponse response = mvc.perform(
                        get("/api/sondage/"+id+"/best"))
                .andReturn().getResponse();

        verify(request,times(1)).getBestDateBySondageId(id);
        assertThat(response.getContentAsString()).isEqualTo("[]");
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void testGetBestNotFound() throws Exception {
        when(request.getBestDateBySondageId(id)).thenThrow(EntityNotFoundException.class);
        MockHttpServletResponse response = mvc.perform(
                        get("/api/sondage/"+id+"/best"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }


    @Test
    public void testGetMaybeBest() throws Exception {
        Date d = new Date();
        when(request.getMaybeBestDateBySondageId(id)).thenReturn(List.of(d));
        MockHttpServletResponse response = mvc.perform(
                        get("/api/sondage/"+id+"/maybe"))
                .andReturn().getResponse();

        verify(request,times(1)).getMaybeBestDateBySondageId(id);
        assertThat(response.getContentAsString()).isEqualTo("[" + d.getTime() + "]");
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void testGetMaybeBestEmpty() throws Exception {
        when(request.getMaybeBestDateBySondageId(id)).thenReturn(List.of());
        MockHttpServletResponse response = mvc.perform(
                        get("/api/sondage/"+id+"/maybe"))
                .andReturn().getResponse();

        verify(request,times(1)).getMaybeBestDateBySondageId(id);
        assertThat(response.getContentAsString()).isEqualTo("[]");
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void testGetMaybeBestNotFound() throws Exception {
        when(request.getMaybeBestDateBySondageId(id)).thenThrow(EntityNotFoundException.class);
        MockHttpServletResponse response = mvc.perform(
                        get("/api/sondage/"+id+"/maybe"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }


    @Test
    public void testCreate() throws Exception {
        when(mapper.map(dto, Sondage.class)).thenReturn(sondage);
        when(service.create(id, sondage)).thenReturn(sondage);
        when(mapper.map(sondage, SondageDto.class)).thenReturn(dto);
        MockHttpServletResponse response = mvc.perform(
                        post("/api/sondage/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonSondage.write(dto).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();

        verify(mapper,times(1)).map(eq(dto), eq(Sondage.class));
        verify(service,times(1)).create(dto.getCreateBy(),sondage);
        verify(mapper,times(1)).map(eq(sondage), eq(SondageDto.class));
        assertThat(response.getContentAsString()).isEqualTo(jsonSondage.write(dto).getJson());
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    public void testCreateWithoutInfo() throws Exception {
        SondageDto s = new SondageDto();
        s.setFin(dateDansUnAn);
        MockHttpServletResponse response = mvc.perform(
                        post("/api/sondage/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonSondage.write(s).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void testCreateDateBefore() throws Exception {
        SondageDto s = new SondageDto();
        s.setFin(new Date());
        MockHttpServletResponse response = mvc.perform(
                        post("/api/sondage/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonSondage.write(s).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void testUpdate() throws Exception {
        when(mapper.map(dto, Sondage.class)).thenReturn(sondage);
        when(service.update(id, sondage)).thenReturn(sondage);
        when(mapper.map(sondage, SondageDto.class)).thenReturn(dto);
        MockHttpServletResponse response = mvc.perform(
                        put("/api/sondage/"+id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonSondage.write(dto).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();

        verify(mapper,times(1)).map(eq(dto), eq(Sondage.class));
        verify(service,times(1)).update(eq(id), eq(sondage));
        verify(mapper,times(1)).map(eq(sondage), eq(SondageDto.class));
        assertThat(response.getContentAsString()).isEqualTo(jsonSondage.write(dto).getJson());
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void testUpdateFailedDueToLackOfInfo() throws Exception {
        SondageDto s = new SondageDto();
        s.setFin(dateDansUnAn);
        MockHttpServletResponse response = mvc.perform(
                        put("/api/sondage/"+id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonSondage.write(s).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void testUpdateFailedNotFound() throws Exception {
        when(mapper.map(dto, Sondage.class)).thenThrow(EntityNotFoundException.class);
        MockHttpServletResponse response = mvc.perform(
                        put("/api/sondage/"+id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonSondage.write(dto).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void testDelete() throws Exception {
        MockHttpServletResponse response = mvc.perform(
                        delete("/api/sondage/"+id))
                .andReturn().getResponse();

        verify(service,times(1)).delete(eq(id));
        assertThat(response.getContentAsString()).isEqualTo("");
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    public void testDeleteFailed() throws Exception {
        doThrow(NoSuchElementException.class).when(service).delete(id);
        MockHttpServletResponse response = mvc.perform(
                        delete("/api/sondage/"+id))
                .andReturn().getResponse();

        verify(service,times(1)).delete(eq(id));
        assertThat(response.getContentAsString()).isEqualTo("");
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

}