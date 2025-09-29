package com.assignment.insuredprocess.service;

import com.assignment.insuredprocess.model.Client;
import com.assignment.insuredprocess.model.ContactMethod;
import com.assignment.insuredprocess.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

    public Client createClient(String id, String contactType, String contactValue) {
        if (clientRepository.existsById(id)) {
            throw new IllegalArgumentException("Client with ID " + id + " already exists");
        }

        Client client = new Client(id);
        client.addContactMethod(new ContactMethod(contactType, contactValue));
        return clientRepository.save(client);
    }

    public boolean authenticateClient(String id, String contactType, String contactValue) {
        Optional<Client> clientOpt = clientRepository.findById(id);
        return clientOpt.isPresent() && clientOpt.get().hasContactMethod(contactType, contactValue);
    }

    public Optional<Client> findClientById(String id) {
        return clientRepository.findById(id);
    }
}