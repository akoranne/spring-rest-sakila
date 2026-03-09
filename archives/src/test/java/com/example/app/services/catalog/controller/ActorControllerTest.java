package com.example.app.services.catalog.controller;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.example.app.common.constant.*;
import com.example.app.common.domain.dto.FullName;
import com.example.app.common.security.JwtAuthenticationFilter;
import com.example.app.config.RestDocsControllerSupport;
import com.example.app.services.catalog.assembler.ActorDetailsRepresentationModelAssembler;
import com.example.app.services.catalog.assembler.ActorRepresentationModelAssembler;
import com.example.app.services.catalog.assembler.FilmDetailsRepresentationModelAssembler;
import com.example.app.services.catalog.assembler.FilmRepresentationModelAssembler;
import com.example.app.services.catalog.domain.dto.ActorDetailsDto;
import com.example.app.services.catalog.domain.dto.ActorDto;
import com.example.app.services.catalog.domain.dto.FilmDetailsDto;
import com.example.app.services.catalog.domain.dto.FilmDto;
import com.example.app.services.catalog.service.ActorService;
import com.example.app.util.ConstrainedFieldDocumentation;
import com.example.app.util.OpenApiDescriptorTransformer;
import com.google.common.base.CaseFormat;
import com.google.common.collect.Maps;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ActorController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class))
class ActorControllerTest extends RestDocsControllerSupport {
    @MockBean
    private ActorService actorService;
    @SpyBean
    private ActorRepresentationModelAssembler actorAssembler;
    @SpyBean
    private ActorDetailsRepresentationModelAssembler actorDetailsAssembler;
    @SpyBean
    private FilmRepresentationModelAssembler filmAssembler;
    @SpyBean
    private FilmDetailsRepresentationModelAssembler filmDetailsAssembler;

    @Nested
    class ActorListTests {
        private ActorDto.Actor guiness;
        private ActorDto.Actor walhberg;

        @BeforeEach
        void setUp() {
            guiness = ActorDto.Actor.builder()
                    .actorId(1)
                    .fullName(FullName.builder().firstName("PENELOPE").lastName("GUINESS").build())
                    .lastUpdate(LocalDateTime.of(2006, 2, 15, 9, 34, 33))
                    .build();
            walhberg = ActorDto.Actor.builder()
                    .actorId(2)
                    .fullName(FullName.builder().firstName("JENNIFER").lastName("WALHBERG").build())
                    .lastUpdate(LocalDateTime.of(2006, 2, 15, 9, 34, 33))
                    .build();
        }

        @Test
        void getActorList_success() throws Exception {
            // arrange
            final var pageable = Pageable.ofSize(10).withPage(0);
            when(actorService.getActorList(pageable)).thenReturn(List.of(guiness, walhberg));

            // act
            final var execute = mockMvc.perform(get("/actors")
                    .accept(MediaTypes.HAL_JSON));

            // assert
            execute.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(handler().handlerType(ActorController.class))
                    .andExpect(handler().methodName("getActorList"))
                    .andExpect(jsonPath("_links.self.href").value(serverUrl + "/actors"));
            verify(actorService, times(1)).getActorList(pageable);

            // descriptors
            final var responseFieldList = List.of(
                    fieldWithPath("_embedded.actorList[].actorId").description(getMessageSourceMessage("actor.read.responseField.actorId")),
                    fieldWithPath("_embedded.actorList[].firstName").description(getMessageSourceMessage("actor.read.responseField.firstName")),
                    fieldWithPath("_embedded.actorList[].lastName").description(getMessageSourceMessage("actor.read.responseField.lastName")),
                    fieldWithPath("_embedded.actorList[].lastUpdate").description(getMessageSourceMessage("actor.read.responseField.lastUpdate")),
                    subsectionWithPath("_embedded.actorList[]._links").description(getMessageSourceMessage("actor.hal.collection._links")),
                    subsectionWithPath("_links").description(getMessageSourceMessage("actor.hal.item._links")));
            final var linkList = List.of(
                    linkWithRel(HalRelation.Fields.self).description(getMessageSourceMessage("actor.hal.item.self")));

            // restdocs
            execute.andDo(MockMvcRestDocumentation.document(RESTDOCS_DOCUMENT_IDENTIFIER,
                    preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                    responseFields(responseFieldList), links(linkList)));

            // openapi
            execute.andDo(MockMvcRestDocumentationWrapper.document(OPENAPI_DOCUMENT_IDENTIFIER,
                    ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                            .tag(getMessageSourceMessage("actor.controller.getActorList.tag"))
                            .summary(getMessageSourceMessage("actor.controller.getActorList.summary"))
                            .description(getMessageSourceMessage("actor.controller.getActorList.description"))
                            .responseSchema(Schema.schema(CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, "actorList")))
                            .responseFields(responseFieldList)
                            .links(linkList)
                            .build())));
        }

        @Test
        void getActorList_trailingSlash() throws Exception {
            // arrange
            final var pageable = Pageable.ofSize(10).withPage(0);
            when(actorService.getActorList(pageable)).thenReturn(List.of(guiness, walhberg));

            // act
            final var execute = mockMvc.perform(get("/actors/")
                    .accept(MediaTypes.HAL_JSON));

            // assert
            execute.andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(handler().handlerType(ResourceHttpRequestHandler.class));
            verify(actorService, times(0)).getActorList(pageable);
        }

        @Test
        void getActor_success() throws Exception {
            // arrange
            final var actorId = 1;
            when(actorService.getActor(actorId)).thenReturn(Optional.of(guiness));

            // act
            final var execute = mockMvc.perform(get("/actors/{actorId}", actorId)
                    .accept(MediaTypes.HAL_JSON));

            // assert
            execute.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(handler().handlerType(ActorController.class))
                    .andExpect(handler().methodName("getActor"))
                    .andExpect(jsonPath(ActorDto.Actor.Fields.actorId).value(1))
                    .andExpect(jsonPath(FullName.Fields.firstName).value("PENELOPE"))
                    .andExpect(jsonPath(FullName.Fields.lastName).value("GUINESS"))
                    .andExpect(jsonPath(ActorDto.Actor.Fields.lastUpdate).value("2006-02-15T09:34:33"))
                    .andExpect(jsonPath("_links.self.href").value(serverUrl + "/actors/" + actorId))
                    .andExpect(jsonPath("_links.update.href").value(serverUrl + "/actors/" + actorId))
                    .andExpect(jsonPath("_links.delete.href").value(serverUrl + "/actors/" + actorId))
                    .andExpect(jsonPath("_links.details.href").value(serverUrl + "/actors/" + actorId + "/details"))
                    .andExpect(jsonPath("_links.create.href").value(serverUrl + "/actors"))
                    .andExpect(jsonPath("_links.actorList.href").value(serverUrl + "/actors"));
            verify(actorService, times(1)).getActor(actorId);

            // descriptors
            final var pathParameterList = List.of(
                    parameterWithName(ActorDto.Actor.Fields.actorId).description(getMessageSourceMessage("actor.read.pathParameter.actorId")));
            final var responseFieldList = List.of(
                    fieldWithPath(ActorDto.Actor.Fields.actorId).description(getMessageSourceMessage("actor.read.responseField.actorId")),
                    fieldWithPath(FullName.Fields.firstName).description(getMessageSourceMessage("actor.read.responseField.firstName")),
                    fieldWithPath(FullName.Fields.lastName).description(getMessageSourceMessage("actor.read.responseField.lastName")),
                    fieldWithPath(ActorDto.Actor.Fields.lastUpdate).description(getMessageSourceMessage("actor.read.responseField.lastUpdate")),
                    subsectionWithPath("_links").description(getMessageSourceMessage("actor.hal.item._links")));
            final var linkList = List.of(
                    linkWithRel(HalRelation.Fields.self).description(getMessageSourceMessage("actor.hal.item.self")),
                    linkWithRel(HalRelation.Fields.update).description(getMessageSourceMessage("actor.hal.item.update")),
                    linkWithRel(HalRelation.Fields.delete).description(getMessageSourceMessage("actor.hal.item.delete")),
                    linkWithRel(HalRelation.Fields.details).description(getMessageSourceMessage("actor.hal.item.details")),
                    linkWithRel(HalRelation.Fields.create).description(getMessageSourceMessage("actor.hal.item.create")),
                    linkWithRel(HalRelation.Fields.actorList).description(getMessageSourceMessage("actor.hal.collection.self")));
            final var openapiPathParameterList = OpenApiDescriptorTransformer.transformParameter(pathParameterList);

            // restdocs
            execute.andDo(MockMvcRestDocumentation.document(RESTDOCS_DOCUMENT_IDENTIFIER,
                    preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                    pathParameters(pathParameterList), responseFields(responseFieldList), links(linkList)));

            // openapi
            execute.andDo(MockMvcRestDocumentationWrapper.document(OPENAPI_DOCUMENT_IDENTIFIER,
                    ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                            .tag(getMessageSourceMessage("actor.controller.getActor.tag"))
                            .summary(getMessageSourceMessage("actor.controller.getActor.summary"))
                            .description(getMessageSourceMessage("actor.controller.getActor.description"))
                            .responseSchema(Schema.schema(CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, "actor")))
                            .pathParameters(openapiPathParameterList)
                            .responseFields(responseFieldList)
                            .links(linkList)
                            .build())));
        }
    }

    @Nested
    class ActorUpdateTests {
        private int actorId;
        private HashMap<String, String> payload;
        private ActorDto.ActorRequest requestModel;
        private ActorDto.Actor newActor;

        @BeforeEach
        void setUp() {
            actorId = 1;
            payload = new HashMap<>();
            payload.put("firstName", "HELLO");
            payload.put("lastName", "WORLD");
            requestModel = new ActorDto.ActorRequest(
                    payload.get(ActorDto.ActorRequest.Fields.firstName),
                    payload.get(ActorDto.ActorRequest.Fields.lastName)
            );
            newActor = ActorDto.Actor.builder()
                    .actorId(actorId)
                    .fullName(FullName.builder()
                            .firstName(payload.get(FullName.Fields.firstName))
                            .lastName(payload.get(FullName.Fields.lastName))
                            .build())
                    .lastUpdate(LocalDateTime.now())
                    .build();
        }

        @Test
        void addActor_success() throws Exception {
            // arrange
            when(actorService.addActor(requestModel)).thenReturn(newActor);

            // act
            final var execute = mockMvc.perform(post("/actors")
                    .accept(MediaTypes.HAL_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(payload)));

            // assert
            execute.andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(handler().handlerType(ActorController.class))
                    .andExpect(handler().methodName("addActor"))
                    .andExpect(header().string(HttpHeaders.LOCATION, serverUrl + "/actors/" + actorId));
            verify(actorService, times(1)).addActor(requestModel);
            verify(actorService, times(1)).addActor(any(ActorDto.ActorRequest.class));

            // descriptors
            final var requestFieldList = List.of(
                    fieldWithPath(ActorDto.ActorRequest.Fields.firstName).description(getMessageSourceMessage("actor.create.requestField.firstName")),
                    fieldWithPath(ActorDto.ActorRequest.Fields.lastName).description(getMessageSourceMessage("actor.create.requestField.lastName")));
            final var responseHeaderList = List.of(
                    headerWithName(HttpHeaders.LOCATION).description(getMessageSourceMessage("actor.create.responseHeader." + HttpHeaders.LOCATION)));
            final var openapiResponseHeaderList = OpenApiDescriptorTransformer.transformHeader(responseHeaderList);
            final var fieldDescriptors = new ConstrainedFieldDocumentation(ActorDto.ActorRequest.class);
            final var restdocsRequestFieldList = fieldDescriptors.restdocsFields(requestFieldList);
            final var openapiRequestFieldList = fieldDescriptors.openapiFields(requestFieldList);

            // restdocs
            execute.andDo(MockMvcRestDocumentation.document(RESTDOCS_DOCUMENT_IDENTIFIER,
                    preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                    responseHeaders(responseHeaderList), requestFields(restdocsRequestFieldList)));

            // openapi
            execute.andDo(MockMvcRestDocumentationWrapper.document(OPENAPI_DOCUMENT_IDENTIFIER,
                    ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                            .tag(getMessageSourceMessage("actor.controller.addActor.tag"))
                            .summary(getMessageSourceMessage("actor.controller.addActor.summary"))
                            .description(getMessageSourceMessage("actor.controller.addActor.description"))
                            .requestSchema(Schema.schema(CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, "actorRequest")))
                            .responseHeaders(openapiResponseHeaderList)
                            .requestFields(openapiRequestFieldList)
                            .build())));
        }

        @Test
        void updateActor_success() throws Exception {
            // arrange
            when(actorService.updateActor(actorId, requestModel)).thenReturn(newActor);

            // act
            final var execute = mockMvc.perform(put("/actors/{actorId}", actorId)
                    .accept(MediaTypes.HAL_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(payload)));

            // assert
            execute.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(handler().handlerType(ActorController.class))
                    .andExpect(handler().methodName("updateActor"));
            verify(actorService, times(1)).updateActor(actorId, requestModel);
            verify(actorService, times(1)).updateActor(anyInt(), any(ActorDto.ActorRequest.class));

            // descriptors
            final var pathParameterList = List.of(
                    parameterWithName(ActorDto.Actor.Fields.actorId).description(getMessageSourceMessage("actor.update.pathParameter.actorId")));
            final var requestFieldList = List.of(
                    fieldWithPath(ActorDto.ActorRequest.Fields.firstName).description(getMessageSourceMessage("actor.update.requestField.firstName")),
                    fieldWithPath(ActorDto.ActorRequest.Fields.lastName).description(getMessageSourceMessage("actor.update.requestField.lastName")));
            final var openapiPathParameterList = OpenApiDescriptorTransformer.transformParameter(pathParameterList);
            final var fieldDescriptors = new ConstrainedFieldDocumentation(ActorDto.ActorRequest.class);
            final var restdocsRequestFieldList = fieldDescriptors.restdocsFields(requestFieldList);
            final var openapiRequestFieldList = fieldDescriptors.openapiFields(requestFieldList);

            // restdocs
            execute.andDo(MockMvcRestDocumentation.document(RESTDOCS_DOCUMENT_IDENTIFIER,
                    preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                    pathParameters(pathParameterList), requestFields(restdocsRequestFieldList)));

            // openapi
            execute.andDo(MockMvcRestDocumentationWrapper.document(OPENAPI_DOCUMENT_IDENTIFIER,
                    ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                            .tag(getMessageSourceMessage("actor.controller.updateActor.tag"))
                            .summary(getMessageSourceMessage("actor.controller.updateActor.summary"))
                            .description(getMessageSourceMessage("actor.controller.updateActor.description"))
                            .requestSchema(Schema.schema(CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, "actorRequest")))
                            .pathParameters(openapiPathParameterList)
                            .requestFields(openapiRequestFieldList)
                            .build())));
        }

        @Test
        void deleteActor_success() throws Exception {
            // arrange
            doNothing().when(actorService).deleteActor(actorId);

            // act
            final var execute = mockMvc.perform(delete("/actors/{actorId}", actorId)
                    .accept(MediaTypes.HAL_JSON));

            // assert
            execute.andDo(print())
                    .andExpect(status().isNoContent())
                    .andExpect(handler().handlerType(ActorController.class))
                    .andExpect(handler().methodName("deleteActor"));
            verify(actorService, times(1)).deleteActor(actorId);

            // descriptors
            final var pathParameterList = List.of(
                    parameterWithName(ActorDto.Actor.Fields.actorId).description(
                            getMessageSourceMessage("actor.delete.pathParameter.actorId")));
            final var openapiPathParameterList = OpenApiDescriptorTransformer.transformParameter(pathParameterList);

            // restdocs
            execute.andDo(MockMvcRestDocumentation.document(RESTDOCS_DOCUMENT_IDENTIFIER,
                    preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                    pathParameters(pathParameterList)));

            // openapi
            execute.andDo(MockMvcRestDocumentationWrapper.document(OPENAPI_DOCUMENT_IDENTIFIER,
                    ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                            .tag(getMessageSourceMessage("actor.controller.deleteActor.tag"))
                            .summary(getMessageSourceMessage("actor.controller.deleteActor.summary"))
                            .description(getMessageSourceMessage("actor.controller.deleteActor.description"))
                            .pathParameters(openapiPathParameterList)
                            .build())));
        }
    }

    @Nested
    class ActorDetailsTests {
        @Test
        void getActorDetails_success() throws Exception {
            // arrange
            final var actorId = 1;
            final var model = ActorDetailsDto.ActorDetails.builder()
                    .actorId(actorId)
                    .firstName("PENELOPE")
                    .lastName("GUINESS")
                    .filmInfo("ACADEMY DINOSAUR, ANACONDA CONFESSIONS, ANGELS LIFE, BULWORTH COMMANDMENTS, " +
                            "CHEAPER CLYDE, COLOR PHILADELPHIA, ELEPHANT TROJAN, GLEAMING JAWBREAKER, HUMAN GRAFFITI, " +
                            "KING EVOLUTION, LADY STAGE, LANGUAGE COWBOY, MULHOLLAND BEAST, OKLAHOMA JUMANJI, " +
                            "RULES HUMAN, SPLASH GUMP, VERTIGO NORTHWEST, WESTWARD SEABISCUIT, WIZARD COLDBLOODED")
                    .build();
            when(actorService.getActorDetails(actorId)).thenReturn(Optional.of(model));

            // act
            final var execute = mockMvc.perform(get("/actors/{actorId}/details", actorId)
                    .accept(MediaTypes.HAL_JSON));

            // assert
            execute.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(handler().handlerType(ActorController.class))
                    .andExpect(handler().methodName("getActorDetails"))
                    .andExpect(jsonPath(ActorDetailsDto.ActorDetails.Fields.actorId).value(1))
                    .andExpect(jsonPath(ActorDetailsDto.ActorDetails.Fields.firstName).value("PENELOPE"))
                    .andExpect(jsonPath(ActorDetailsDto.ActorDetails.Fields.lastName).value("GUINESS"))
                    .andExpect(jsonPath(ActorDetailsDto.ActorDetails.Fields.filmInfo)
                            .value("ACADEMY DINOSAUR, ANACONDA CONFESSIONS, ANGELS LIFE, BULWORTH COMMANDMENTS, " +
                                    "CHEAPER CLYDE, COLOR PHILADELPHIA, ELEPHANT TROJAN, GLEAMING JAWBREAKER, HUMAN GRAFFITI, " +
                                    "KING EVOLUTION, LADY STAGE, LANGUAGE COWBOY, MULHOLLAND BEAST, OKLAHOMA JUMANJI, " +
                                    "RULES HUMAN, SPLASH GUMP, VERTIGO NORTHWEST, WESTWARD SEABISCUIT, WIZARD COLDBLOODED"))
                    .andExpect(jsonPath("_links.self.href").value(serverUrl + "/actors/" + actorId + "/details"))
                    .andExpect(jsonPath("_links.actor.href").value(serverUrl + "/actors/" + actorId))
                    .andExpect(jsonPath("_links.actorList.href").value(serverUrl + "/actors"));
            verify(actorService, times(1)).getActorDetails(actorId);

            // descriptors
            final var pathParameterList = List.of(
                    parameterWithName(ActorDto.Actor.Fields.actorId).description(getMessageSourceMessage("actor.details.pathParameter.actorId")));
            final var responseFieldList = List.of(
                    fieldWithPath(ActorDetailsDto.ActorDetails.Fields.actorId).description(getMessageSourceMessage("actor.details.responseField.actorId")),
                    fieldWithPath(ActorDetailsDto.ActorDetails.Fields.firstName).description(getMessageSourceMessage("actor.details.responseField.firstName")),
                    fieldWithPath(ActorDetailsDto.ActorDetails.Fields.lastName).description(getMessageSourceMessage("actor.details.responseField.lastName")),
                    fieldWithPath(ActorDetailsDto.ActorDetails.Fields.filmInfo).description(getMessageSourceMessage("actor.details.responseField.filmInfo")),
                    subsectionWithPath("_links").description(getMessageSourceMessage("actor.hal.item._links")));
            final var linkList = List.of(
                    linkWithRel(HalRelation.Fields.self).description(getMessageSourceMessage("actor.hal.item.self")),
                    linkWithRel(HalRelation.Fields.actor).description(getMessageSourceMessage("actor.hal.item.self")),
                    linkWithRel(HalRelation.Fields.actorList).description(getMessageSourceMessage("actor.hal.collection.self")));
            final var openapiPathParameterList = OpenApiDescriptorTransformer.transformParameter(pathParameterList);

            // restdocs
            execute.andDo(MockMvcRestDocumentation.document(RESTDOCS_DOCUMENT_IDENTIFIER,
                    preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                    pathParameters(pathParameterList), responseFields(responseFieldList), links(linkList)));

            // openapi
            execute.andDo(MockMvcRestDocumentationWrapper.document(OPENAPI_DOCUMENT_IDENTIFIER,
                    ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                            .tag(getMessageSourceMessage("actor.controller.getActorDetails.tag"))
                            .summary(getMessageSourceMessage("actor.controller.getActorDetails.summary"))
                            .description(getMessageSourceMessage("actor.controller.getActorDetails.description"))
                            .responseSchema(Schema.schema(CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, "actorDetails")))
                            .pathParameters(openapiPathParameterList)
                            .responseFields(responseFieldList)
                            .links(linkList)
                            .build())));
        }
    }

    @Nested
    class ActorFilmTests {
        private FilmDto.Film academyDinosaur;
        private FilmDto.Film aceGoldfinger;

        @BeforeEach
        void setUp() {
            academyDinosaur = FilmDto.Film.builder()
                    .filmId(1)
                    .title("ACADEMY DINOSAUR")
                    .description("A Epic Drama of a Feminist And a Mad Scientist who must Battle a Teacher in The Canadian Rockies")
                    .releaseYear(LocalDate.ofYearDay(2006, 1))
                    .languageId(Language.ENGLISH)
                    .originalLanguageId(Language.NONE)
                    .rentalDuration(6)
                    .rentalRate(BigDecimal.valueOf(0.99))
                    .length(86)
                    .replacementCost(BigDecimal.valueOf(20.99))
                    .rating(FilmRating.PG)
                    .specialFeatures(EnumSet.of(SpecialFeature.BEHIND_THE_SCENES, SpecialFeature.DELETED_SCENES))
                    .lastUpdate(LocalDateTime.now())
                    .build();
            aceGoldfinger = FilmDto.Film.builder()
                    .filmId(2)
                    .title("ACE GOLDFINGER")
                    .description("A Astounding Epistle of a Database Administrator And a Explorer who must Find a Car in Ancient China")
                    .releaseYear(LocalDate.ofYearDay(2006, 1))
                    .languageId(Language.ENGLISH)
                    .originalLanguageId(Language.NONE)
                    .rentalDuration(3)
                    .rentalRate(BigDecimal.valueOf(4.99))
                    .length(48)
                    .replacementCost(BigDecimal.valueOf(12.99))
                    .rating(FilmRating.G)
                    .specialFeatures(EnumSet.of(SpecialFeature.BEHIND_THE_SCENES, SpecialFeature.DELETED_SCENES))
                    .lastUpdate(LocalDateTime.now())
                    .build();
        }

        @Test
        void getActorFilmList_success() throws Exception {
            // arrange
            final var actorId = 1;
            final var pageable = Pageable.ofSize(10).withPage(0);
            when(actorService.getActorFilmList(actorId, FilmDto.FilterOption.builder().build(), pageable)).thenReturn(List.of(academyDinosaur, aceGoldfinger));

            // act
            final var execute = mockMvc.perform(get("/actors/{actorId}/films", actorId)
                    .accept(MediaTypes.HAL_JSON));

            // assert
            execute.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(handler().handlerType(ActorController.class))
                    .andExpect(handler().methodName("getActorFilmList"))
                    .andExpect(jsonPath("_links.self.href").value(serverUrl + "/actors/" + actorId + "/films{?category,releaseYear,rating}"))
                    .andExpect(jsonPath("_links.actor.href").value(serverUrl + "/actors/" + actorId));
            verify(actorService, times(1)).getActorFilmList(actorId, FilmDto.FilterOption.builder().build(), pageable);

            // descriptors
            final var pathParameterList = List.of(
                    parameterWithName(ActorDto.Actor.Fields.actorId).description(getMessageSourceMessage("actor.film.read.pathParameter.actorId")));
            final var responseFieldList = List.of(
                    fieldWithPath("_embedded.filmList[].filmId").description(getMessageSourceMessage("actor.film.read.responseField.filmId")),
                    fieldWithPath("_embedded.filmList[].title").description(getMessageSourceMessage("actor.film.read.responseField.title")),
                    fieldWithPath("_embedded.filmList[].description").description(getMessageSourceMessage("actor.film.read.responseField.description")),
                    fieldWithPath("_embedded.filmList[].releaseYear").description(getMessageSourceMessage("actor.film.read.responseField.releaseYear")),
                    fieldWithPath("_embedded.filmList[].languageId").description(getMessageSourceMessage("actor.film.read.responseField.languageId")),
                    fieldWithPath("_embedded.filmList[].originalLanguageId").description(getMessageSourceMessage("actor.film.read.responseField.originalLanguageId")),
                    fieldWithPath("_embedded.filmList[].rentalDuration").description(getMessageSourceMessage("actor.film.read.responseField.rentalDuration")),
                    fieldWithPath("_embedded.filmList[].rentalRate").description(getMessageSourceMessage("actor.film.read.responseField.rentalRate")),
                    fieldWithPath("_embedded.filmList[].length").description(getMessageSourceMessage("actor.film.read.responseField.length")),
                    fieldWithPath("_embedded.filmList[].replacementCost").description(getMessageSourceMessage("actor.film.read.responseField.replacementCost")),
                    fieldWithPath("_embedded.filmList[].rating").description(getMessageSourceMessage("actor.film.read.responseField.rating")),
                    fieldWithPath("_embedded.filmList[].specialFeatures").description(getMessageSourceMessage("actor.film.read.responseField.specialFeatures")),
                    fieldWithPath("_embedded.filmList[].lastUpdate").description(getMessageSourceMessage("actor.film.read.responseField.lastUpdate")),
                    subsectionWithPath("_embedded.filmList[]._links").description(getMessageSourceMessage("film.hal.collection._links")),
                    subsectionWithPath("_links").description(getMessageSourceMessage("film.hal.item._links")));
            final var linkList = List.of(
                    linkWithRel(HalRelation.Fields.self).description(getMessageSourceMessage("film.hal.item.self")),
                    linkWithRel(HalRelation.Fields.actor).description(getMessageSourceMessage("actor.hal.item._links")));
            final var openapiPathParameterList = OpenApiDescriptorTransformer.transformParameter(pathParameterList);

            // restdocs
            execute.andDo(MockMvcRestDocumentation.document(RESTDOCS_DOCUMENT_IDENTIFIER,
                    preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                    pathParameters(pathParameterList), responseFields(responseFieldList), links(linkList)));

            // openapi
            execute.andDo(MockMvcRestDocumentationWrapper.document(OPENAPI_DOCUMENT_IDENTIFIER,
                    ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                            .tag(getMessageSourceMessage("actor.controller.getActorFilmList.tag"))
                            .summary(getMessageSourceMessage("actor.controller.getActorFilmList.summary"))
                            .description(getMessageSourceMessage("actor.controller.getActorFilmList.description"))
                            .responseSchema(Schema.schema(CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, "filmList")))
                            .pathParameters(openapiPathParameterList)
                            .responseFields(responseFieldList)
                            .links(linkList)
                            .build())));
        }

        @Test
        void getActorFilm_success() throws Exception {
            // arrange
            final var actorId = 1;
            final var filmId = 1;
            when(actorService.getActorFilm(actorId, filmId)).thenReturn(Optional.ofNullable(academyDinosaur));

            // act
            final var execute = mockMvc.perform(get("/actors/{actorId}/films/{filmId}", actorId, filmId)
                    .accept(MediaTypes.HAL_JSON));

            // assert
            execute.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(handler().handlerType(ActorController.class))
                    .andExpect(handler().methodName("getActorFilm"))
                    .andExpect(jsonPath("_links.self.href").value(serverUrl + "/actors/" + actorId + "/films/" + filmId))
                    .andExpect(jsonPath("_links.actor.href").value(serverUrl + "/actors/" + actorId))
                    .andExpect(jsonPath("_links.filmList.href").value(serverUrl + "/actors/" + actorId + "/films{?category,releaseYear,rating}"));
            verify(actorService, times(1)).getActorFilm(actorId, filmId);

            // descriptors
            final var pathParameterList = List.of(
                    parameterWithName(ActorDto.Actor.Fields.actorId).description(getMessageSourceMessage("actor.film.read.pathParameter.actorId")),
                    parameterWithName(FilmDto.Film.Fields.filmId).description(getMessageSourceMessage("actor.film.read.pathParameter.filmId")));
            final var responseFieldList = List.of(
                    fieldWithPath(FilmDto.Film.Fields.filmId).description(getMessageSourceMessage("actor.film.read.responseField.filmId")),
                    fieldWithPath(FilmDto.Film.Fields.title).description(getMessageSourceMessage("actor.film.read.responseField.title")),
                    fieldWithPath(FilmDto.Film.Fields.description).description(getMessageSourceMessage("actor.film.read.responseField.description")),
                    fieldWithPath(FilmDto.Film.Fields.releaseYear).description(getMessageSourceMessage("actor.film.read.responseField.releaseYear")),
                    fieldWithPath(FilmDto.Film.Fields.languageId).description(getMessageSourceMessage("actor.film.read.responseField.languageId")),
                    fieldWithPath(FilmDto.Film.Fields.originalLanguageId).description(getMessageSourceMessage("actor.film.read.responseField.originalLanguageId")),
                    fieldWithPath(FilmDto.Film.Fields.rentalDuration).description(getMessageSourceMessage("actor.film.read.responseField.rentalDuration")),
                    fieldWithPath(FilmDto.Film.Fields.rentalRate).description(getMessageSourceMessage("actor.film.read.responseField.rentalRate")),
                    fieldWithPath(FilmDto.Film.Fields.length).description(getMessageSourceMessage("actor.film.read.responseField.length")),
                    fieldWithPath(FilmDto.Film.Fields.replacementCost).description(getMessageSourceMessage("actor.film.read.responseField.replacementCost")),
                    fieldWithPath(FilmDto.Film.Fields.rating).description(getMessageSourceMessage("actor.film.read.responseField.rating")),
                    fieldWithPath(FilmDto.Film.Fields.specialFeatures).description(getMessageSourceMessage("actor.film.read.responseField.specialFeatures")),
                    fieldWithPath(FilmDto.Film.Fields.lastUpdate).description(getMessageSourceMessage("actor.film.read.responseField.lastUpdate")),
                    subsectionWithPath("_links").description(getMessageSourceMessage("film.hal.item._links")));
            final var linkList = List.of(
                    linkWithRel(HalRelation.Fields.self).description(getMessageSourceMessage("film.hal.item.self")),
                    linkWithRel(HalRelation.Fields.filmList).description(getMessageSourceMessage("film.hal.collection.self")),
                    linkWithRel(HalRelation.Fields.actor).description(getMessageSourceMessage("actor.hal.item.self")));
            final var openapiPathParameterList = OpenApiDescriptorTransformer.transformParameter(pathParameterList);

            // restdocs
            execute.andDo(MockMvcRestDocumentation.document(RESTDOCS_DOCUMENT_IDENTIFIER,
                    preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                    pathParameters(pathParameterList), responseFields(responseFieldList), links(linkList)));

            // openapi
            execute.andDo(MockMvcRestDocumentationWrapper.document(OPENAPI_DOCUMENT_IDENTIFIER,
                    ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                            .tag(getMessageSourceMessage("actor.controller.getActorFilm.tag"))
                            .summary(getMessageSourceMessage("actor.controller.getActorFilm.summary"))
                            .description(getMessageSourceMessage("actor.controller.getActorFilm.description"))
                            .responseSchema(Schema.schema(CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, "film")))
                            .pathParameters(openapiPathParameterList)
                            .responseFields(responseFieldList)
                            .links(linkList)
                            .build())));
        }

        @Test
        void addActorFilm_success() throws Exception {
            // arrange
            final var actorId = 1;
            final var filmId = 1;
            final var payload = Maps.<String, String>newHashMap();
            payload.put(FilmDto.Film.Fields.filmId, String.valueOf(filmId));
            when(actorService.addActorFilm(actorId, filmId)).thenReturn(academyDinosaur);

            // act
            final var execute = mockMvc.perform(post("/actors/{actorId}/films", actorId)
                    .accept(MediaTypes.HAL_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(payload)));
            // assert
            execute.andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(handler().handlerType(ActorController.class))
                    .andExpect(handler().methodName("addActorFilm"))
                    .andExpect(header().string(HttpHeaders.LOCATION, serverUrl + "/actors/" + actorId + "/films/" + filmId));

            verify(actorService, times(1)).addActorFilm(actorId, filmId);

            // descriptors
            final var pathParameterList = List.of(
                    parameterWithName(ActorDto.Actor.Fields.actorId).description(getMessageSourceMessage("actor.film.create.pathParameter.actorId")));
            final var requestFieldList = List.of(
                    fieldWithPath(FilmDto.Film.Fields.filmId).description(getMessageSourceMessage("actor.film.create.requestField.filmId")));
            final var responseHeaderList = List.of(
                    headerWithName(HttpHeaders.LOCATION).description(getMessageSourceMessage("actor.film.create.responseHeader." + HttpHeaders.LOCATION)));
            final var openapiResponseHeaderList = OpenApiDescriptorTransformer.transformHeader(responseHeaderList);
            final var openapiPathParameterList = OpenApiDescriptorTransformer.transformParameter(pathParameterList);
            final var fieldDescriptors = new ConstrainedFieldDocumentation(ActorDto.ActorRequest.class);
            final var restdocsRequestFieldList = fieldDescriptors.restdocsFields(requestFieldList);
            final var openapiRequestFieldList = fieldDescriptors.openapiFields(requestFieldList);

            // restdocs
            execute.andDo(MockMvcRestDocumentation.document(RESTDOCS_DOCUMENT_IDENTIFIER,
                    preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                    responseHeaders(responseHeaderList), pathParameters(pathParameterList), requestFields(restdocsRequestFieldList)));

            // openapi
            execute.andDo(MockMvcRestDocumentationWrapper.document(OPENAPI_DOCUMENT_IDENTIFIER,
                    ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                            .tag(getMessageSourceMessage("actor.controller.addActorFilm.tag"))
                            .summary(getMessageSourceMessage("actor.controller.addActorFilm.summary"))
                            .description(getMessageSourceMessage("actor.controller.addActorFilm.description"))
                            .requestSchema(Schema.schema(CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, "filmRequest")))
                            .responseHeaders(openapiResponseHeaderList)
                            .pathParameters(openapiPathParameterList)
                            .requestFields(openapiRequestFieldList)
                            .build())));
        }

        @Test
        void deleteActorFilm_success() throws Exception {
            // arrange
            final var actorId = 1;
            final var filmId = 1;

            // act
            final var execute = mockMvc.perform(delete("/actors/{actorId}/films/{filmId}", actorId, filmId)
                    .accept(MediaTypes.HAL_JSON));

            // assert
            execute.andDo(print())
                    .andExpect(status().isNoContent())
                    .andExpect(handler().handlerType(ActorController.class))
                    .andExpect(handler().methodName("deleteActorFilm"));
            verify(actorService, times(1)).removeActorFilm(actorId, filmId);

            // descriptors
            final var pathParameterList = List.of(
                    parameterWithName(ActorDto.Actor.Fields.actorId).description(getMessageSourceMessage("actor.film.delete.pathParameter.actorId")),
                    parameterWithName(FilmDto.Film.Fields.filmId).description(getMessageSourceMessage("actor.film.delete.pathParameter.filmId")));
            final var openapiPathParameterList = OpenApiDescriptorTransformer.transformParameter(pathParameterList);

            // restdocs
            execute.andDo(MockMvcRestDocumentation.document(RESTDOCS_DOCUMENT_IDENTIFIER,
                    preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()), pathParameters(pathParameterList)));

            // openapi
            execute.andDo(MockMvcRestDocumentationWrapper.document(OPENAPI_DOCUMENT_IDENTIFIER,
                    ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                            .tag(getMessageSourceMessage("actor.controller.deleteActorFilm.tag"))
                            .summary(getMessageSourceMessage("actor.controller.deleteActorFilm.summary"))
                            .description(getMessageSourceMessage("actor.controller.deleteActorFilm.description"))
                            .pathParameters(openapiPathParameterList)
                            .build())));
        }
    }

    @Nested
    class ActorFilmDetailsTests {
        @Test
        void getActorFilmDetails_success() throws Exception {
            // arrange
            final var actorId = 1;
            final var filmId = 1;
            final var model = Optional.of(FilmDetailsDto.FilmDetails.builder()
                    .filmId(1)
                    .title("ACADEMY DINOSAUR")
                    .description("A Epic Drama of a Feminist And a Mad Scientist who must Battle a Teacher in The Canadian Rockies")
                    .category(Category.DOCUMENTARY)
                    .price(BigDecimal.valueOf(0.99))
                    .length(86)
                    .rating(FilmRating.PG)
                    .actors("PENELOPE GUINESS")
                    .build());
            when(actorService.getActorFilmDetails(actorId, filmId)).thenReturn(model);

            // act
            final var execute = mockMvc.perform(get("/actors/{actorId}/films/{filmId}/details", actorId, filmId)
                    .accept(MediaTypes.HAL_JSON));

            // assert
            execute.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(handler().handlerType(ActorController.class))
                    .andExpect(handler().methodName("getActorFilmDetails"))
                    .andExpect(jsonPath("_links.self.href").value(serverUrl + "/actors/" + actorId + "/films/" + filmId + "/details"))
                    .andExpect(jsonPath("_links.film.href").value(serverUrl + "/actors/" + actorId + "/films/" + filmId))
                    .andExpect(jsonPath("_links.filmList.href").value(serverUrl + "/actors/" + actorId + "/films{?category,releaseYear,rating}"))
                    .andExpect(jsonPath("_links.actor.href").value(serverUrl + "/actors/" + actorId));
            verify(actorService, times(1)).getActorFilmDetails(actorId, filmId);

            // descriptors
            final var pathParameterList = List.of(
                    parameterWithName(ActorDto.Actor.Fields.actorId).description(getMessageSourceMessage("actor.film.details.pathParameter.actorId")),
                    parameterWithName(FilmDto.Film.Fields.filmId).description(getMessageSourceMessage("actor.film.details.pathParameter.filmId")));
            final var responseFieldList = List.of(
                    fieldWithPath(FilmDetailsDto.FilmDetails.Fields.filmId).description(getMessageSourceMessage("actor.film.details.responseField.filmId")),
                    fieldWithPath(FilmDetailsDto.FilmDetails.Fields.title).description(getMessageSourceMessage("actor.film.details.responseField.title")),
                    fieldWithPath(FilmDetailsDto.FilmDetails.Fields.description).description(getMessageSourceMessage("actor.film.details.responseField.description")),
                    fieldWithPath(FilmDetailsDto.FilmDetails.Fields.category).description(getMessageSourceMessage("actor.film.details.responseField.category")),
                    fieldWithPath(FilmDetailsDto.FilmDetails.Fields.price).description(getMessageSourceMessage("actor.film.details.responseField.price")),
                    fieldWithPath(FilmDetailsDto.FilmDetails.Fields.length).description(getMessageSourceMessage("actor.film.details.responseField.length")),
                    fieldWithPath(FilmDetailsDto.FilmDetails.Fields.rating).description(getMessageSourceMessage("actor.film.details.responseField.rating")),
                    fieldWithPath(FilmDetailsDto.FilmDetails.Fields.actors).description(getMessageSourceMessage("actor.film.details.responseField.actors")),
                    subsectionWithPath("_links").description(getMessageSourceMessage("film.hal.item._links")));
            final var linkList = List.of(
                    linkWithRel(HalRelation.Fields.self).description(getMessageSourceMessage("film.hal.item.self")),
                    linkWithRel(HalRelation.Fields.film).description(getMessageSourceMessage("film.hal.item.self")),
                    linkWithRel(HalRelation.Fields.filmList).description(getMessageSourceMessage("film.hal.collection.self")),
                    linkWithRel(HalRelation.Fields.actor).description(getMessageSourceMessage("actor.hal.item.self")));
            final var openapiPathParameterList = OpenApiDescriptorTransformer.transformParameter(pathParameterList);

            // restdocs
            execute.andDo(MockMvcRestDocumentation.document(RESTDOCS_DOCUMENT_IDENTIFIER,
                    preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                    pathParameters(pathParameterList), responseFields(responseFieldList), links(linkList)));

            // openapi
            execute.andDo(MockMvcRestDocumentationWrapper.document(OPENAPI_DOCUMENT_IDENTIFIER,
                    ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                            .tag(getMessageSourceMessage("actor.controller.getActorFilmDetails.tag"))
                            .summary(getMessageSourceMessage("actor.controller.getActorFilmDetails.summary"))
                            .description(getMessageSourceMessage("actor.controller.getActorFilmDetails.description"))
                            .responseSchema(Schema.schema(CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, "filmDetails")))
                            .pathParameters(openapiPathParameterList)
                            .responseFields(responseFieldList)
                            .links(linkList)
                            .build())));
        }
    }
}
