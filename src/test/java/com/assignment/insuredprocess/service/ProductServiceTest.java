package com.assignment.insuredprocess.service;

import com.assignment.insuredprocess.model.Client;
import com.assignment.insuredprocess.model.ClientProduct;
import com.assignment.insuredprocess.model.Product;
import com.assignment.insuredprocess.repository.ClientProductRepository;
import com.assignment.insuredprocess.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService Tests")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ClientProductRepository clientProductRepository;

    @Mock
    private ClientService clientService;

    @InjectMocks
    private ProductService productService;

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
    @DisplayName("Should return client products")
    void shouldReturnClientProducts() {
        when(clientProductRepository.findByClientId(testData.clientId))
                .thenReturn(List.of(testData.createClientProduct()));
        when(productRepository.findById(testData.productId))
                .thenReturn(Optional.of(testData.createProduct()));

        List<Product> result = productService.getClientProducts(testData.clientId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(testData.productId);
        verify(clientProductRepository).findByClientId(testData.clientId);
        verify(productRepository).findById(testData.productId);
    }

    @Test
    @DisplayName("Should return empty list when client has no products")
    void shouldReturnEmptyListWhenNoProducts() {
        when(clientProductRepository.findByClientId(testData.clientId)).thenReturn(List.of());

        List<Product> result = productService.getClientProducts(testData.clientId);

        assertThat(result).isEmpty();
        verify(clientProductRepository).findByClientId(testData.clientId);
        verify(productRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Should buy product successfully")
    void shouldBuyProductSuccessfully() {
        when(clientService.findClientById(testData.clientId)).thenReturn(Optional.of(testData.createClient()));
        when(productRepository.findById(testData.productId)).thenReturn(Optional.of(testData.createProduct()));
        when(clientProductRepository.existsByClientIdAndProductId(testData.clientId, testData.productId)).thenReturn(false);
        when(clientProductRepository.save(any(ClientProduct.class))).thenReturn(testData.createClientProduct());

        Product result = productService.buyProduct(testData.clientId, testData.productId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testData.productId);
        verify(clientService).findClientById(testData.clientId);
        verify(productRepository).findById(testData.productId);
        verify(clientProductRepository).existsByClientIdAndProductId(testData.clientId, testData.productId);
        verify(clientProductRepository).save(any(ClientProduct.class));
    }

    @Test
    @DisplayName("Should throw exception when client not found")
    void shouldThrowExceptionWhenClientNotFound() {
        when(clientService.findClientById(testData.clientId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.buyProduct(testData.clientId, testData.productId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Client not found: " + testData.clientId);

        verify(clientService).findClientById(testData.clientId);
        verify(productRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Should throw exception when product not found")
    void shouldThrowExceptionWhenProductNotFound() {
        when(clientService.findClientById(testData.clientId)).thenReturn(Optional.of(testData.createClient()));
        when(productRepository.findById(testData.productId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.buyProduct(testData.clientId, testData.productId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product not found: " + testData.productId);

        verify(productRepository).findById(testData.productId);
    }

    @Test
    @DisplayName("Should throw exception when client already owns product")
    void shouldThrowExceptionWhenClientAlreadyOwnsProduct() {
        when(clientService.findClientById(testData.clientId)).thenReturn(Optional.of(testData.createClient()));
        when(productRepository.findById(testData.productId)).thenReturn(Optional.of(testData.createProduct()));
        when(clientProductRepository.existsByClientIdAndProductId(testData.clientId, testData.productId)).thenReturn(true);

        assertThatThrownBy(() -> productService.buyProduct(testData.clientId, testData.productId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Client already owns this product");

        verify(clientProductRepository).existsByClientIdAndProductId(testData.clientId, testData.productId);
        verify(clientProductRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update product successfully")
    void shouldUpdateProductSuccessfully() {
        when(clientProductRepository.existsByClientIdAndProductId(testData.clientId, testData.productId)).thenReturn(true);
        when(productRepository.findById(testData.productId)).thenReturn(Optional.of(testData.createProduct()));

        productService.updateProduct(testData.clientId, testData.productId, "New Name", "New Description");

        verify(clientProductRepository).existsByClientIdAndProductId(testData.clientId, testData.productId);
        verify(productRepository).findById(testData.productId);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw exception when client does not own product")
    void shouldThrowExceptionWhenClientDoesNotOwnProduct() {
        when(clientProductRepository.existsByClientIdAndProductId(testData.clientId, testData.productId)).thenReturn(false);

        assertThatThrownBy(() -> productService.updateProduct(testData.clientId, testData.productId, "New Name", "New Description"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Client does not own this product");

        verify(clientProductRepository).existsByClientIdAndProductId(testData.clientId, testData.productId);
        verify(productRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Should create product successfully")
    void shouldCreateProductSuccessfully() {
        when(productRepository.existsById(testData.productId)).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(testData.createProduct());

        Product result = productService.createProduct(testData.productId, testData.productName, testData.productDescription);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testData.productId);
        verify(productRepository).existsById(testData.productId);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw exception when product ID already exists")
    void shouldThrowExceptionWhenProductExists() {
        when(productRepository.existsById(testData.productId)).thenReturn(true);

        assertThatThrownBy(() -> productService.createProduct(testData.productId, testData.productName, testData.productDescription))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product with ID " + testData.productId + " already exists");

        verify(productRepository).existsById(testData.productId);
        verify(productRepository, never()).save(any());
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

        Client createClient() {
            return new Client(clientId);
        }

        Product createProduct() {
            return new Product(productId, productName, productDescription);
        }

        ClientProduct createClientProduct() {
            return new ClientProduct(clientId, productId);
        }
    }
}