/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nz.ac.aut.comp603.hospitalsimgui.model;

import java.util.Set;

/**
 *
 * @author GGPC
 */

public class Room {

    private Patient patient;
    private Doctor doctor;
    private int waitTime;

    private final Set<Integer> treatableLevels;

    // ✅ Constructor
    public Room(Set<Integer> treatableLevels) {
        this.treatableLevels = treatableLevels;
        this.patient = null;
        this.doctor = null;
        this.waitTime = 0;
    }

    // ✅ Check if room is free
    public boolean isFree() {
        return patient == null;
    }

    // ✅ Check if room can treat sickness level
    public boolean canTreat(int sicknessLevel) {
        return treatableLevels.contains(sicknessLevel);
    }

    // ✅ Assign patient to room
    public void assignPatient(Patient p) {
        this.patient = p;
        p.stopTreatment(); // waits until doctor assigned
        this.waitTime = 0;
    }

    // ✅ Remove patient
    public void removePatient() {
        this.patient = null;
        this.waitTime = 0;
    }

    public Patient getPatient() {
        return patient;
    }

    // ✅ Assign doctor
    public void assignDoctor(Doctor d) {
        if (patient != null && doctor == null) {
            this.doctor = d;
            d.startTreatment();
            patient.startTreatment();
        }
    }

    // ✅ Remove doctor
    public void removeDoctor() {
        if (patient != null) {
            patient.stopTreatment();
        }
        if (doctor != null) {
            doctor.stopTreatment();
        }
        this.doctor = null;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public Set<Integer> getTreatableLevels() {
        return treatableLevels;
    }

    // ✅ Wait time tracking
    public void incrementWaitTime() {
        if (patient != null && doctor == null) {
            waitTime++;
        }
    }

    public int getWaitTime() {
        return waitTime;
    }

    // ✅ Progress treatment (important)
    public void progressTreatment() {
        if (patient != null && doctor != null) {
            patient.reduceTreatmentTime();

            if (patient.isTreated()) {
                removeDoctor();
                removePatient();
            }
        }
    }
}
