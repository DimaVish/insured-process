package com.assignment.insuredprocess.controller;

import com.assignment.insuredprocess.model.Client;
import com.assignment.insuredprocess.model.Product;
import com.assignment.insuredprocess.service.ClientService;
import com.assignment.insuredprocess.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClientController.class)
@DisplayName("ClientController Tests")
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClientService clientService;

    @MockitoBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private TestDataBuilder testData;

    @BeforeEach
    void setUp() {
        testData = TestDataBuilder.builder()
                .clientId("C001")
                .email("test@example.com")
                .productId("P001")
                .productName("Health Insurance")
                .build();
    }

    @Test
    @DisplayName("Should create client successfully")
    void shouldCreateClientSuccessfully() throws Exception {
        when(clientService.createClient(anyString(), anyString(), anyString())).thenReturn(testData.createClient());

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testData.createAuthRequestJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testData.clientId));
    }

    @Test
    @DisplayName("Should return bad request when client creation fails")
    void shouldReturnBadRequestWhenCreationFails() throws Exception {
        when(clientService.createClient(anyString(), anyString(), anyString()))
                .thenThrow(new IllegalArgumentException("Client already exists"));

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testData.createAuthRequestJson()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should authenticate client successfully")
    void shouldAuthenticateClientSuccessfully() throws Exception {
        when(clientService.authenticateClient(testData.clientId, "email", testData.email)).thenReturn(true);

        mockMvc.perform(post("/api/clients/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testData.createAuthRequestJson()))
                .andExpect(status().isOk())
                .andExpect(content().string("Client authenticated successfully"));
    }

    @Test
    @DisplayName("Should return unauthorized for failed authentication")
    void shouldReturnUnauthorizedForFailedAuthentication() throws Exception {
        when(clientService.authenticateClient(testData.clientId, "email", testData.email)).thenReturn(false);

        mockMvc.perform(post("/api/clients/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testData.createAuthRequestJson()))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Authentication failed"));
    }

    @Test
    @DisplayName("Should return client products")
    void shouldReturnClientProducts() throws Exception {
        when(clientService.findClientById(testData.clientId)).thenReturn(Optional.of(testData.createClient()));
        when(productService.getClientProducts(testData.clientId)).thenReturn(List.of(testData.createProduct()));

        mockMvc.perform(get("/api/clients/{clientId}/products", testData.clientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(testData.productId))
                .andExpect(jsonPath("$[0].name").value(testData.productName));
    }

    @Test
    @DisplayName("Should return not found for non-existing client")
    void shouldReturnNotFoundForNonExistingClient() throws Exception {
        when(clientService.findClientById(testData.clientId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/clients/{clientId}/products", testData.clientId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return empty list when client has no products")
    void shouldReturnEmptyListWhenClientHasNoProducts() throws Exception {
        when(clientService.findClientById(testData.clientId)).thenReturn(Optional.of(testData.createClient()));
        when(productService.getClientProducts(testData.clientId)).thenReturn(List.of());

        mockMvc.perform(get("/api/clients/{clientId}/products", testData.clientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    private static class TestDataBuilder {
        private String clientId;
        private String email;
        private String productId;
        private String productName;

        static TestDataBuilder builder() {
            return new TestDataBuilder();
        }

        TestDataBuilder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        TestDataBuilder email(String email) {
            this.email = email;
            return this;
        }

        TestDataBuilder productId(String productId) {
            this.productId = productId;
            return this;
        }

        TestDataBuilder productName(String productName) {
            this.productName = productName;
            return this;
        }

        TestDataBuilder build() {
            return this;
        }

        Client createClient() {
            return new Client(clientId);
        }

        Product createProduct() {
            return new Product(productId, productName, "Test description");
        }

        String createAuthRequestJson() {
            return String.format("""
                    {
                        "clientId": "%s",
                        "contactType": "email",
                        "contactValue": "%s"
                    }
                    """, clientId, email);
        }
    }
}