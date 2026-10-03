package com.example.orm_exercise.controllers;

import com.example.orm_exercise.models.Address;
import com.example.orm_exercise.models.Contact;
import com.example.orm_exercise.repositories.ContactRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contacts")
public class ContactController {
    private final ContactRepository contactRepository;

    public ContactController(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @GetMapping
    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    @GetMapping("/{id}")
    public Contact getContactById(@PathVariable int id) {
        return contactRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Contact createContact(@RequestBody Contact contact) {
        if (contact.getAddresses() != null && !contact.getAddresses().isEmpty()) {
            for (Address address : contact.getAddresses()) {
                address.setContact(contact);
            }
        }

        return contactRepository.save(contact);
    }

    @PutMapping("/{id}")
    public Contact updateContact(@PathVariable int id, @RequestBody Contact updatedContact) {
        return contactRepository.findById(id).map(contact -> {
            contact.setName(updatedContact.getName());
            contact.setEmail(updatedContact.getEmail());
            contact.setPhoneNumber(updatedContact.getPhoneNumber());
            return contactRepository.save(contact);
        }).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void deleteContact(@PathVariable int id) {
        contactRepository.deleteById(id);
    }

    @PostMapping("/{contactId}/addresses")
    public Contact createAddress(@PathVariable int contactId, @RequestBody Address newAddress) {
        Contact contact = contactRepository.findById(contactId).orElse(null);
        if (contact == null) {
            return null;
        }
        newAddress.setContact(contact);
        contact.getAddresses().add(newAddress);
        return contactRepository.save(contact);

    }

    @DeleteMapping("/{contactId}/addresses/{addressId}")
    public void deleteAddress(@PathVariable int contactId, @PathVariable int addressId){
        Contact contact = contactRepository.findById(contactId).orElse(null);
        if (contact == null) {
            return ;
        }
        Address addressToDelete = null;
        for (Address address: contact.getAddresses()) {
            if (address.getId() == addressId) {
                addressToDelete = address;
                break;
            }
        }
        if (addressToDelete != null) {
            contact.getAddresses().remove(addressToDelete);
            contactRepository.save(contact);
        }

    }
}
