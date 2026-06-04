/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nz.ac.aut.comp603.hospitalsimgui.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import nz.ac.aut.comp603.hospitalsimgui.model.Doctor;
import nz.ac.aut.comp603.hospitalsimgui.model.Patient;
import nz.ac.aut.comp603.hospitalsimgui.model.Room;

/**
 *
 * @author GGPC
 */

public class HospitalController {
    private final List<Room> rooms;
    private final List<Doctor> doctors;

    private final Queue<Patient> priorityQueue;
    private final Queue<Patient> normalQueue;

    private int tick;

    // Constructor
    public HospitalController(List<Room> rooms, List<Doctor> doctors) {
        this.rooms = rooms;
        this.doctors = doctors;

        this.priorityQueue = new LinkedList<>();
        this.normalQueue = new LinkedList<>();

        this.tick = 0;
    }

    public void addPatient(int sicknessLevel) {
        Patient p = new Patient(sicknessLevel);

        if (sicknessLevel == Patient.SICKNESS_3) {
            priorityQueue.add(p);
        } else {
            normalQueue.add(p);
        }
    }
    
    private void progressRooms() {
        System.out.println("→ TREATING PATIENTS");
        for (Room room : rooms) {
            room.progressTreatment();
        }
    }

    private void movePatientsToRooms() {
        System.out.println("→ MOVING PATIENTS TO ROOM");
        for (Room room : rooms) {

           // Only work with free rooms
           if (room.isFree()) {

               Patient p = null;

               // Try priority queue first
               if (!priorityQueue.isEmpty() && room.canTreat(priorityQueue.peek().getSicknessLevel())) {
                   p = priorityQueue.poll();
               }
               
               // Otherwise try normal queue
               else if (!normalQueue.isEmpty() && room.canTreat(normalQueue.peek().getSicknessLevel())) {
                   p = normalQueue.poll();
               }

               // If we found a suitable patient, assign them
               if (p != null) {
                   room.assignPatient(p);
               }
           }
       }
    }

    private void assignDoctorsToRooms() {
        System.out.println("→ ASSIGNING DOCTORS");
        for (Room room : rooms) {

            // Room must have a patient but no doctor
            if (room.getPatient() != null && room.getDoctor() == null) {

                for (Doctor doctor : doctors) {

                    // Doctor must be free and able to treat the patient
                    if (!doctor.isBusy() && doctor.canTreat(room.getPatient().getSicknessLevel())) {
                        room.assignDoctor(doctor);
                        break; // Move to next room once assigned
                    }
                }
            }
        }
    }

    public void nextTick() {
        tick++;

        progressRooms();
        movePatientsToRooms();
        assignDoctorsToRooms();
    }
}

