package ru.microservice.photoservice.controllers;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.instancio.Instancio;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import ru.microservice.photoservice.entities.Post;
import ru.microservice.photoservice.utill.JsonHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.instancio.Select.field;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.microservice.photoservice.utill.JsonHelper.fromJson;

@AutoConfigureMockMvc()
@AutoConfigureWireMock(port = 9999)
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PostControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @Test
    void createPost() throws Exception {
        var post = Instancio.of(Post.class)
                .ignore(field(Post::getUuid))
                .create();
        post.setPhotos(new ArrayList<>());

        var filePart = new MockMultipartFile("photos", "test2.jpg", MediaType.IMAGE_PNG_VALUE,
                Files.readAllBytes(Paths.get("testimg/test2.jpg")));
        var postPart = new MockPart("post", JsonHelper.toJson(post).getBytes());
        postPart.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        var result = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/posts")
                        .file(filePart)
                        .part(postPart))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString(UTF_8);

        String postUuid = fromJson(Post.class, result).getUuid().toString();
        Assertions.assertNotNull(postUuid);

        Post postResult = fromJson(Post.class, mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/posts/" + postUuid))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString(UTF_8));

        Assertions.assertEquals(result, new ObjectMapper().writeValueAsString(postResult));
    }
}