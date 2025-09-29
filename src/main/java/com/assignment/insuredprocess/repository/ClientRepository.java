package com.assignment.insuredprocess.repository;

import com.assignment.insuredprocess.model.Client;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class ClientRepository {
    private final Map<String, Client> clients = new HashMap<>();

    public Client save(Client client) {
        clients.put(client.getId(), client);
        return client;
    }

    public Optional<Client> findById(String id) {
        return Optional.ofNullable(clients.get(id));
    }

    public boolean existsById(String id) {
        return clients.containsKey(id);
    }
}