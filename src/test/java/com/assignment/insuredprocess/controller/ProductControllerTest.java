package com.assignment.insuredprocess.controller;

import com.assignment.insuredprocess.model.Product;
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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@DisplayName("ProductController Tests")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private TestDataBuilder testData;

    @BeforeEach
    void setUp() {
        testData = TestDataBuilder.builder()
                .clientId("C001")
                .productId("P001")
                .productName("Health Insurance")
                .productDescription("Comprehensive health coverage")
                .build();
    }

    @Test
    @DisplayName("Should create product successfully")
    void shouldCreateProductSuccessfully() throws Exception {
        when(productService.createProduct(anyString(), anyString(), anyString())).thenReturn(testData.createProduct());

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testData.createProductJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testData.productId))
                .andExpect(jsonPath("$.name").value(testData.productName));
    }

    @Test
    @DisplayName("Should return bad request when product creation fails")
    void shouldReturnBadRequestWhenCreationFails() throws Exception {
        when(productService.createProduct(anyString(), anyString(), anyString()))
                .thenThrow(new IllegalArgumentException("Product already exists"));

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testData.createProductJson()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should buy product successfully")
    void shouldBuyProductSuccessfully() throws Exception {
        when(productService.buyProduct(testData.clientId, testData.productId)).thenReturn(testData.createProduct());

        mockMvc.perform(post("/api/products/{productId}/buy", testData.productId)
                        .param("clientId", testData.clientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testData.productId))
                .andExpect(jsonPath("$.name").value(testData.productName));
    }

    @Test
    @DisplayName("Should return bad request when buy fails")
    void shouldReturnBadRequestWhenBuyFails() throws Exception {
        when(productService.buyProduct(testData.clientId, testData.productId))
                .thenThrow(new IllegalArgumentException("Client already owns this product"));

        mockMvc.perform(post("/api/products/{productId}/buy", testData.productId)
                        .param("clientId", testData.clientId))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should update product successfully")
    void shouldUpdateProductSuccessfully() throws Exception {
        doNothing().when(productService).updateProduct(anyString(), anyString(), anyString(), anyString());

        mockMvc.perform(put("/api/products/{productId}", testData.productId)
                        .param("clientId", testData.clientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testData.createUpdateRequestJson()))
                .andExpect(status().isOk())
                .andExpect(content().string("Product updated successfully"));
    }

    @Test
    @DisplayName("Should return bad request when update fails")
    void shouldReturnBadRequestWhenUpdateFails() throws Exception {
        String errorMessage = "Client does not own this product";
        doThrow(new IllegalArgumentException(errorMessage))
                .when(productService).updateProduct(anyString(), anyString(), anyString(), anyString());

        mockMvc.perform(put("/api/products/{productId}", testData.productId)
                        .param("clientId", testData.clientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testData.createUpdateRequestJson()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(errorMessage));
    }

    private static class TestDataBuilder {
        private String clientId;
        private String productId;
        private String productName;
        private String productDescription;

        static TestDataBuilder builder() {
            return new TestDataBuilder();
        }

        TestDataBuilder clientId(String clientId) {
            this.clientId = clientId;
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

        TestDataBuilder productDescription(String productDescription) {
            this.productDescription = productDescription;
            return this;
        }

        TestDataBuilder build() {
            return this;
        }

        Product createProduct() {
            return new Product(productId, productName, productDescription);
        }

        String createProductJson() {
            return String.format("""
                    {
                        "id": "%s",
                        "name": "%s",
                        "description": "%s"
                    }
                    """, productId, productName, productDescription);
        }

        String createUpdateRequestJson() {
            return String.format("""
                    {
                        "name": "%s",
                        "description": "%s"
                    }
                    """, "Updated " + productName, "Updated " + productDescription);
        }
    }
}