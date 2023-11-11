package com.sistema.venus.services;

import com.sistema.venus.controller.MedicationController;
import com.sistema.venus.domain.Medication;
import com.sistema.venus.domain.User;
import com.sistema.venus.repo.MedicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import com.sistema.venus.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    public Medication saveMedicine(Medication medicine) {
        // Validate the Medicine object
        User user = userRepository.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        medicine.setUserId(user);
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
}