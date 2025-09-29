package com.assignment.insuredprocess.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("Insured Process Integration Tests")
class InsuredProcessIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should authenticate existing client successfully")
    void shouldAuthenticateExistingClient() throws Exception {
        mockMvc.perform(post("/api/clients/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "clientId": "C001",
                                    "contactType": "email",
                                    "contactValue": "john@example.com"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string("Client authenticated successfully"));
    }

    @Test
    @DisplayName("Should fail authentication with wrong credentials")
    void shouldFailAuthenticationWithWrongCredentials() throws Exception {
        mockMvc.perform(post("/api/clients/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "clientId": "C001",
                                    "contactType": "email",
                                    "contactValue": "wrong@example.com"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Authentication failed"));
    }

    @Test
    @DisplayName("Should create new client successfully")
    void shouldCreateNewClientSuccessfully() throws Exception {
        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "clientId": "C999",
                                    "contactType": "email",
                                    "contactValue": "newclient@example.com"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("C999"));

        mockMvc.perform(post("/api/clients/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "clientId": "C999",
                                    "contactType": "email",
                                    "contactValue": "newclient@example.com"
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should complete full product purchase flow")
    void shouldCompleteFullProductPurchaseFlow() throws Exception {
        mockMvc.perform(get("/api/clients/C001/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        mockMvc.perform(post("/api/products/P001/buy")
                        .param("clientId", "C001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("P001"))
                .andExpect(jsonPath("$.name").value("Health Insurance"));

        mockMvc.perform(get("/api/clients/C001/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value("P001"));

        mockMvc.perform(post("/api/products/P001/buy")
                        .param("clientId", "C001"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle product purchase errors correctly")
    void shouldHandleProductPurchaseErrors() throws Exception {
        mockMvc.perform(post("/api/products/NONEXISTENT/buy")
                        .param("clientId", "C001"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/products/P001/buy")
                        .param("clientId", "NONEXISTENT"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should update owned product successfully")
    void shouldUpdateOwnedProductSuccessfully() throws Exception {
        mockMvc.perform(post("/api/products/P001/buy")
                        .param("clientId", "C001"))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/products/P001")
                        .param("clientId", "C001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Premium Health Insurance",
                                    "description": "Enhanced comprehensive health coverage"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string("Product updated successfully"));

        mockMvc.perform(get("/api/clients/C001/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Premium Health Insurance"));
    }

    @Test
    @DisplayName("Should prevent updating non-owned product")
    void shouldPreventUpdatingNonOwnedProduct() throws Exception {
        mockMvc.perform(put("/api/products/P001")
                        .param("clientId", "C001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Updated Name",
                                    "description": "Updated Description"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Client does not own this product"));
    }

    @Test
    @DisplayName("Should handle complete client journey")
    void shouldHandleCompleteClientJourney() throws Exception {
        mockMvc.perform(post("/api/clients/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "clientId": "C001",
                                    "contactType": "email",
                                    "contactValue": "john@example.com"
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/products/P001/buy")
                        .param("clientId", "C001"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/products/P002/buy")
                        .param("clientId", "C001"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/clients/C001/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        mockMvc.perform(put("/api/products/P001")
                        .param("clientId", "C001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Updated Health Insurance",
                                    "description": "Updated description"
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should create and manage new product")
    void shouldCreateAndManageNewProduct() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": "P999",
                                    "name": "Travel Insurance",
                                    "description": "Comprehensive travel protection"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("P999"));

        mockMvc.perform(post("/api/products/P999/buy")
                        .param("clientId", "C001"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/clients/C001/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == 'P999')]").exists());
    }
}