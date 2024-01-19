package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.unit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.controllers.SondageController;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.dtos.SondageDto;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Participant;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Sondage;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services.DateSondeeService;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services.SondageService;
import jakarta.persistence.NoResultException;
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
    void setup() {
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
    void givenValidParameters_whenCreate_thenReturnCreated() throws Exception {
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
    void givenInvalidDateFin_whenCreate_thenReturnBadRequest() throws Exception {
        dto.setFin(new Date());
        MockHttpServletResponse response = mvc.perform(
                        post("/api/sondage/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonSondage.write(dto).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();

        verify(mapper,times(0)).map(eq(dto), eq(Sondage.class));
        verify(service,times(0)).create(dto.getCreateBy(),sondage);
        verify(mapper,times(0)).map(eq(sondage), eq(SondageDto.class));
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void givenInvalidDateFinNull_whenCreate_thenReturnBadRequest() throws Exception {
        dto.setFin(null);
        MockHttpServletResponse response = mvc.perform(
                        post("/api/sondage/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonSondage.write(dto).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();

        verify(mapper,times(0)).map(eq(dto), eq(Sondage.class));
        verify(service,times(0)).create(dto.getCreateBy(),sondage);
        verify(mapper,times(0)).map(eq(sondage), eq(SondageDto.class));
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void givenInvalidCreatedBy_whenCreate_thenReturnBadRequest() throws Exception {
        dto.setCreateBy(null);
        MockHttpServletResponse response = mvc.perform(
                        post("/api/sondage/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonSondage.write(dto).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();

        verify(mapper,times(0)).map(eq(dto), eq(Sondage.class));
        verify(service,times(0)).create(dto.getCreateBy(),sondage);
        verify(mapper,times(0)).map(eq(sondage), eq(SondageDto.class));
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }


    @Test
    void givenInvalidNom_whenCreate_thenReturnBadRequest() throws Exception {
        dto.setNom(null);
        MockHttpServletResponse response = mvc.perform(
                        post("/api/sondage/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonSondage.write(dto).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();

        verify(mapper,times(0)).map(eq(dto), eq(Sondage.class));
        verify(service,times(0)).create(dto.getCreateBy(),sondage);
        verify(mapper,times(0)).map(eq(sondage), eq(SondageDto.class));
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void givenInvalidDescription_whenCreate_thenReturnBadRequest() throws Exception {
        dto.setDescription(null);
        MockHttpServletResponse response = mvc.perform(
                        post("/api/sondage/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonSondage.write(dto).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();

        verify(mapper,times(0)).map(eq(dto), eq(Sondage.class));
        verify(service,times(0)).create(dto.getCreateBy(),sondage);
        verify(mapper,times(0)).map(eq(sondage), eq(SondageDto.class));
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void givenInvalidCloture_whenCreate_thenReturnBadRequest() throws Exception {
        dto.setCloture(null);
        MockHttpServletResponse response = mvc.perform(
                        post("/api/sondage/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonSondage.write(dto).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();

        verify(mapper,times(0)).map(eq(dto), eq(Sondage.class));
        verify(service,times(0)).create(dto.getCreateBy(),sondage);
        verify(mapper,times(0)).map(eq(sondage), eq(SondageDto.class));
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void givenInvalidClotureTrue_whenCreate_thenReturnBadRequest() throws Exception {
        dto.setCloture(true);
        MockHttpServletResponse response = mvc.perform(
                        post("/api/sondage/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonSondage.write(dto).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();

        verify(mapper,times(0)).map(eq(dto), eq(Sondage.class));
        verify(service,times(0)).create(dto.getCreateBy(),sondage);
        verify(mapper,times(0)).map(eq(sondage), eq(SondageDto.class));
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void givenValidParameters_whenCreateButParticipantDoesNotExist_thenReturnBadRequest() throws Exception {
        when(mapper.map(dto, Sondage.class)).thenReturn(sondage);
        when(service.create(id, sondage)).thenThrow(NoSuchElementException.class);
        MockHttpServletResponse response = mvc.perform(
                        post("/api/sondage/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonSondage.write(dto).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();

        verify(mapper,times(1)).map(eq(dto), eq(Sondage.class));
        verify(service,times(1)).create(dto.getCreateBy(),sondage);
        verify(mapper,times(0)).map(eq(sondage), eq(SondageDto.class));
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void givenValidParameters_whenCreateButServerError_thenReturnInternalServerError() throws Exception {
        when(mapper.map(dto, Sondage.class)).thenReturn(sondage);
        when(service.create(id, sondage)).thenThrow(NullPointerException.class);
        MockHttpServletResponse response = mvc.perform(
                        post("/api/sondage/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonSondage.write(dto).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();

        verify(mapper,times(1)).map(eq(dto), eq(Sondage.class));
        verify(service,times(1)).create(dto.getCreateBy(),sondage);
        verify(mapper,times(0)).map(eq(sondage), eq(SondageDto.class));
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    void givenValidParameters_whenGetAllSondages_thenReturnOk() throws Exception {
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
    void givenValidParameters_whenGetAllSondagesButNoSondageExist_thenRetuNoContent() throws Exception {
        when(service.getAll()).thenThrow(NoResultException.class);
        MockHttpServletResponse response = mvc.perform(
                        get("/api/sondage/"))
                .andReturn().getResponse();

        verify(service,times(1)).getAll();
        verify(mapper,times(0)).map(eq(sondage), eq(SondageDto.class));
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void givenValidParameters_whenGetAllSondagesButErrorServer_thenReturnInternalServerError() throws Exception {
        when(service.getAll()).thenThrow(NullPointerException.class);
        MockHttpServletResponse response = mvc.perform(
                        get("/api/sondage/"))
                .andReturn().getResponse();

        verify(service,times(1)).getAll();
        verify(mapper,times(0)).map(eq(sondage), eq(SondageDto.class));
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    void givenValidParameters_whenGetSondageById_thenReturnOk() throws Exception {
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
    void givenValidParameters_whenGetSondageByIdButSondageDoesNotExist_thenReturnNotFound() throws Exception {
        when(service.getById(id)).thenThrow(NoResultException.class);
        MockHttpServletResponse response = mvc.perform(
                        get("/api/sondage/"+id))
                .andReturn().getResponse();

        verify(service,times(1)).getById(eq(id));
        assertThat(response.getContentAsString()).isEqualTo("");
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void givenValidParameters_whenGetSondageByIdButServerError_thenReturnInternalServerError() throws Exception {
        when(service.getById(id)).thenThrow(NullPointerException.class);
        MockHttpServletResponse response = mvc.perform(
                        get("/api/sondage/"+id))
                .andReturn().getResponse();

        verify(service,times(1)).getById(eq(id));
        assertThat(response.getContentAsString()).isEqualTo("");
        assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    void givenValidParameters_whenGetBestDateBySondageId_thenReturnOk() throws Exception {
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
    void givenValidParameters_whenGetBestDateBySondageIdButSondageDoesNotExist_thenReturnBadRequest() throws Exception {
        when(request.getBestDateBySondageId(id)).thenThrow(NoSuchElementException.class);
        MockHttpServletResponse response = mvc.perform(
                        get("/api/sondage/"+id+"/best"))
                .andReturn().getResponse();

        verify(request,times(1)).getBestDateBySondageId(id);
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void givenValidParameters_whenGetBestDateBySondageIdButServerError_thenReturnInternalServerError() throws Exception {
        when(request.getBestDateBySondageId(id)).thenThrow(NullPointerException.class);
        MockHttpServletResponse response = mvc.perform(
                        get("/api/sondage/"+id+"/best"))
                .andReturn().getResponse();

        verify(request,times(1)).getBestDateBySondageId(id);
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    void givenValidParameters_whenGetMaybeBestDateBySondageId_thenReturnOk() throws Exception {
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
    void givenValidParameters_whenGetMaybeBestDateBySondageIdButSondageDoesNotExist_thenReturnBadRequest() throws Exception {
        when(request.getMaybeBestDateBySondageId(id)).thenThrow(NoSuchElementException.class);
        MockHttpServletResponse response = mvc.perform(
                        get("/api/sondage/"+id+"/maybe"))
                .andReturn().getResponse();

        verify(request,times(1)).getMaybeBestDateBySondageId(id);
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void givenValidParameters_whenGetMaybeBestDateBySondageIdButServerError_thenReturnInternalServerError() throws Exception {
        when(request.getMaybeBestDateBySondageId(id)).thenThrow(NullPointerException.class);
        MockHttpServletResponse response = mvc.perform(
                        get("/api/sondage/"+id+"/maybe"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    void givenValidParameters_whenUpdate_thenReturnOk() throws Exception {
        when(service.getById(id)).thenReturn(new Sondage());
        when(mapper.map(dto, Sondage.class)).thenReturn(sondage);
        when(service.update(id, sondage)).thenReturn(sondage);
        when(mapper.map(sondage, SondageDto.class)).thenReturn(dto);
        MockHttpServletResponse response = mvc.perform(
                        put("/api/sondage/"+id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonSondage.write(dto).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();

        verify(service,times(1)).update(eq(id), eq(sondage));
        assertThat(response.getContentAsString()).isEqualTo(jsonSondage.write(dto).getJson());
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void givenInvalidDateFin_whenUpdate_thenReturnBadRequest() throws Exception {
        SondageDto dto = new SondageDto();
        dto.setFin(new Date());
        MockHttpServletResponse response = mvc.perform(
                        put("/api/sondage/"+id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonSondage.write(dto).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();

        verify(service,times(0)).update(eq(id), eq(sondage));
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void givenInvalidDateFinNull_whenUpdate_thenReturnBadRequest() throws Exception {
        SondageDto dto = new SondageDto();
        dto.setFin(null);
        MockHttpServletResponse response = mvc.perform(
                        put("/api/sondage/"+id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonSondage.write(dto).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();

        verify(service,times(0)).update(eq(id), eq(sondage));
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void givenValidParameters_whenUpdateButSondageHasNotChanged_thenReturnNoContent() throws Exception {
        when(service.getById(id)).thenReturn(sondage);
        when(mapper.map(dto, Sondage.class)).thenReturn(sondage);
        MockHttpServletResponse response = mvc.perform(
                        put("/api/sondage/"+id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonSondage.write(dto).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();

        verify(service,times(0)).update(eq(id), eq(sondage));
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void givenValidParameters_whenUpdateButSondageDoesNotExist_thenReturnBadRequest() throws Exception {
        when(service.getById(id)).thenThrow(NoResultException.class);
        MockHttpServletResponse response = mvc.perform(
                        put("/api/sondage/"+id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonSondage.write(dto).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();

        verify(service,times(0)).update(eq(id), eq(sondage));
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void givenValidParameters_whenUpdateButServerError_thenReturnInternalServerError() throws Exception {
        when(service.getById(id)).thenThrow(NullPointerException.class);
        MockHttpServletResponse response = mvc.perform(
                        put("/api/sondage/"+id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonSondage.write(dto).getJson())
                                .characterEncoding("UTF-8"))
                .andReturn().getResponse();

        verify(service,times(0)).update(eq(id), eq(sondage));
        assertThat(response.getContentAsString()).isEmpty();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    void givenValidParameters_whenDelete_thenReturnNoContent() throws Exception {
        MockHttpServletResponse response = mvc.perform(
                        delete("/api/sondage/"+id))
                .andReturn().getResponse();

        verify(service,times(1)).delete(eq(id));
        assertThat(response.getContentAsString()).isEqualTo("");
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void givenValidParameters_whenDeleteButSondageDoesNotExist_thenReturnBadRequest() throws Exception {
        doThrow(NoSuchElementException.class).when(service).delete(id);
        MockHttpServletResponse response = mvc.perform(
                        delete("/api/sondage/"+id))
                .andReturn().getResponse();

        verify(service,times(1)).delete(eq(id));
        assertThat(response.getContentAsString()).isEqualTo("");
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void givenValidParameters_whenDeleteButServerError_thenReturnInternalServerError() throws Exception {
        doThrow(NullPointerException.class).when(service).delete(id);
        MockHttpServletResponse response = mvc.perform(
                        delete("/api/sondage/"+id))
                .andReturn().getResponse();

        verify(service,times(1)).delete(eq(id));
        assertThat(response.getContentAsString()).isEqualTo("");
        assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}