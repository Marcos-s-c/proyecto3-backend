package com.sistema.venus.services;

import com.sistema.venus.domain.Medication;
import com.sistema.venus.domain.User;
import com.sistema.venus.repo.MedicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.sistema.venus.repo.UserRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
public class MedicationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MedicationRepository medicationRepository;
    public List<Medication> getMedicationByUser(){
        User user = userRepository.findUserByEmail((SecurityContextHolder.getContext().getAuthentication().getName()));
        return medicationRepository.getMedicationByUserId(Long.parseLong(user.getUser_id().toString()));
    }

    public List<Medication> getAllFiltered(){
        User user = userRepository.findUserByEmail((SecurityContextHolder.getContext().getAuthentication().getName()));
        return medicationRepository.findAll().stream()
                .filter(item -> item.getUser().getUser_id().equals(user.getUser_id()))
                .collect(Collectors.toList());
    }

    public Medication saveMedicine(Medication medicine) {
        User user = userRepository.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        medicine.setUser(user);
        return medicationRepository.save(medicine);
    }

    public Medication deleteMedicine(Long medicineId) {
        // Check if the medication exists
        Medication existingMedication = medicationRepository.findById(medicineId)
                .orElseThrow(() -> new EntityNotFoundException("Medication not found with id: " + medicineId));

        // Check if the medication belongs to the authenticated user
        User authenticatedUser = userRepository.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        if (authenticatedUser == null) {
            throw new AccessDeniedException("You do not have permission to delete this medication.");
        }

        // Delete the medication
        medicationRepository.delete(existingMedication);

        return existingMedication;
    }

    public Medication modificarMedicina(Medication med, Integer id) {
        Optional<Medication> medActual = medicationRepository.findById(Long.parseLong(String.valueOf(id)));
        medActual.get().setName(med.getName());
        medActual.get().setDosis(med.getDosis());
        medActual.get().setFrecuencia(med.getFrecuencia());

        return medicationRepository.save(medActual.get());
    }
}