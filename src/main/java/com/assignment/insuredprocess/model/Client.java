package com.assignment.insuredprocess.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class Client {
    private String id;
    private List<ContactMethod> contactMethods = new ArrayList<>();

    public Client(String id) {
        this.id = id;
        this.contactMethods = new ArrayList<>();
    }

    public void addContactMethod(ContactMethod contactMethod) {
        this.contactMethods.add(contactMethod);
    }

    //I kept it simple as possible
    public boolean hasContactMethod(String type, String value) {
        return contactMethods.stream()
                .anyMatch(contactMethod -> contactMethod.getType().equals(type) && contactMethod.getValue().equals(value));
    }
}