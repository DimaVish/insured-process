package com.assignment.insuredprocess.service;

import com.assignment.insuredprocess.model.Client;
import com.assignment.insuredprocess.model.ContactMethod;
import com.assignment.insuredprocess.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClientService Tests")
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientService clientService;

    private TestDataBuilder testData;

    @BeforeEach
    void setUp() {
        testData = TestDataBuilder.builder()
                .clientId("C001")
                .email("test@example.com")
                .phone("555-1234")
                .build();
    }

    private static class TestDataBuilder {
        private String clientId;
        private String email;
        private String phone;

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

        TestDataBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        TestDataBuilder build() {
            return this;
        }

        Client createClient() {
            Client client = new Client(clientId);
            client.addContactMethod(new ContactMethod("email", email));
            client.addContactMethod(new ContactMethod("phone", phone));
            return client;
        }
    }

    @Test
    @DisplayName("Should create client successfully with valid data")
    void shouldCreateClientSuccessfully() {
        when(clientRepository.existsById(testData.clientId)).thenReturn(false);
        when(clientRepository.save(any(Client.class))).thenReturn(testData.createClient());

        Client result = clientService.createClient(testData.clientId, "email", testData.email);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testData.clientId);
        assertThat(result.hasContactMethod("email", testData.email)).isTrue();
        verify(clientRepository).existsById(testData.clientId);
        verify(clientRepository).save(any(Client.class));
    }

    @Test
    @DisplayName("Should throw exception when client ID already exists")
    void shouldThrowExceptionWhenClientExists() {
        when(clientRepository.existsById(testData.clientId)).thenReturn(true);

        assertThatThrownBy(() -> clientService.createClient(testData.clientId, "email", testData.email))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Client with ID " + testData.clientId + " already exists");

        verify(clientRepository).existsById(testData.clientId);
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    @DisplayName("Should authenticate client with valid email")
    void shouldAuthenticateWithValidEmail() {
        when(clientRepository.findById(testData.clientId)).thenReturn(Optional.of(testData.createClient()));

        boolean result = clientService.authenticateClient(testData.clientId, "email", testData.email);

        assertThat(result).isTrue();
        verify(clientRepository).findById(testData.clientId);
    }

    @Test
    @DisplayName("Should fail authentication with wrong email")
    void shouldFailAuthenticationWithWrongEmail() {
        when(clientRepository.findById(testData.clientId)).thenReturn(Optional.of(testData.createClient()));

        boolean result = clientService.authenticateClient(testData.clientId, "email", "wrong@example.com");

        assertThat(result).isFalse();
        verify(clientRepository).findById(testData.clientId);
    }

    @Test
    @DisplayName("Should fail authentication when client not found")
    void shouldFailAuthenticationWhenClientNotFound() {
        when(clientRepository.findById(testData.clientId)).thenReturn(Optional.empty());

        boolean result = clientService.authenticateClient(testData.clientId, "email", testData.email);

        assertThat(result).isFalse();
        verify(clientRepository).findById(testData.clientId);
    }

    @Test
    @DisplayName("Should find existing client")
    void shouldFindExistingClient() {
        when(clientRepository.findById(testData.clientId)).thenReturn(Optional.of(testData.createClient()));

        Optional<Client> result = clientService.findClientById(testData.clientId);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(testData.clientId);
        verify(clientRepository).findById(testData.clientId);
    }

    @Test
    @DisplayName("Should return empty for non-existing client")
    void shouldReturnEmptyForNonExistingClient() {
        when(clientRepository.findById("NONEXISTENT")).thenReturn(Optional.empty());

        Optional<Client> result = clientService.findClientById("NONEXISTENT");

        assertThat(result).isEmpty();
        verify(clientRepository).findById("NONEXISTENT");
    }
}