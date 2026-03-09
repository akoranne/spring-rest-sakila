package com.example.app.config;

import com.example.app.config.properties.AppUriPropConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.MessageSource;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.templates.TemplateFormats;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Locale;

@WebMvcTest
@EnableConfigurationProperties(AppUriPropConfig.class)
@ExtendWith({MockitoExtension.class, RestDocumentationExtension.class})
@Slf4j
public abstract class RestDocsControllerSupport {
    protected static final String RESTDOCS_DOCUMENT_IDENTIFIER = "asciidoctor/{class-name}/{method-name}";
    protected static final String OPENAPI_DOCUMENT_IDENTIFIER = "openapi/{class-name}/{method-name}";

    @Autowired
    protected AppUriPropConfig uriPropConfig;
    @Autowired
    protected MessageSource messageSource;
    @Autowired
    protected ObjectMapper objectMapper;

    protected MockMvc mockMvc;
    protected String serverUrl;

    @BeforeEach
    void setUp(WebApplicationContext context, RestDocumentationContextProvider provider) {
        this.serverUrl = String.format("%s://%s:%d", this.uriPropConfig.getScheme(),
                this.uriPropConfig.getHost(), this.uriPropConfig.getPort());
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(MockMvcRestDocumentation.documentationConfiguration(provider)
                        .snippets().withEncoding("UTF-8"))
                .apply(MockMvcRestDocumentation.documentationConfiguration(provider)
                        .snippets().withTemplateFormat(TemplateFormats.asciidoctor()))
                .apply(MockMvcRestDocumentation.documentationConfiguration(provider)
                        .uris().withScheme(this.uriPropConfig.getScheme())
                        .withHost(this.uriPropConfig.getHost()).withPort(this.uriPropConfig.getPort()))
                .build();
    }

    @AfterEach
    void tearDown() {
    }

    @NotNull
    protected String getMessageSourceMessage(@NotNull String code) {
        return messageSource.getMessage(code, null, Locale.getDefault());
    }
}
