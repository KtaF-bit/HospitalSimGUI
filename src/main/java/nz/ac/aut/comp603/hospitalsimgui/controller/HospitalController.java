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
    private final Queue<Patient> waitingRoom;
    private final int WAITING_ROOM_CAPACITY = 7;
    private final Queue<Patient> priorityQueue;
    private final Queue<Patient> normalQueue;

    private int tick;

    // Constructor
    public HospitalController(List<Room> rooms, List<Doctor> doctors) {
        this.rooms = rooms;
        this.doctors = doctors;
        this.waitingRoom = new LinkedList<>();
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
            room.incrementWaitTime();
            room.progressTreatment();
        }
    }

    private void moveWaitingRoomToRooms() {

        for (int i = 0; i < rooms.size(); i++) {

            Room room = rooms.get(i);

            if (room.isFree()) {

                Patient selected = null;

                for (Patient p : waitingRoom) {
                    if (room.canTreat(p.getSicknessLevel())) {
                        selected = p;
                        break;
                    }
                }

                if (selected != null) {

                    waitingRoom.remove(selected);
                    room.assignPatient(selected);

                    System.out.println(
                        "Patient (Level " + selected.getSicknessLevel() +
                        ") moved from Waiting Room to Room " + (i + 1)
                    );
                }
            }
        }
    }
    
    
    private void moveOutsidePatients() {

    while (true) {

        Patient p = null;

        // Pick next patient (priority first)
        if (!priorityQueue.isEmpty()) {
            p = priorityQueue.peek();
        } else if (!normalQueue.isEmpty()) {
            p = normalQueue.peek();
        }

        // No more patients outside
        if (p == null) break;

        boolean moved = false;

        // ✅ Try to move into a room
        for (Room room : rooms) {
            if (room.isFree() && room.canTreat(p.getSicknessLevel())) {

                if (p.getSicknessLevel() == Patient.SICKNESS_3) {
                    priorityQueue.poll();
                } else {
                    normalQueue.poll();
                }

                room.assignPatient(p);

                System.out.println("Patient (Level " 
                    + p.getSicknessLevel() + ") moved directly to Room");

                moved = true;
                break;
            }
        }

        // ✅ If not moved → try waiting room
        if (!moved) {
            if (waitingRoom.size() < WAITING_ROOM_CAPACITY) {

                if (p.getSicknessLevel() == Patient.SICKNESS_3) {
                    priorityQueue.poll();
                } else {
                    normalQueue.poll();
                }

                addToWaitingRoom(p);

                System.out.println("Patient (Level " 
                    + p.getSicknessLevel() + ") moved to Waiting Room");

                moved = true;
            }
        }

        // ✅ If couldn't move anywhere → stop
        if (!moved) break;
    }
}
    
    private void addToWaitingRoom(Patient p) {
        
        if (p.getSicknessLevel() == Patient.SICKNESS_3) {
            
            LinkedList<Patient> list = (LinkedList<Patient>) waitingRoom;
            
            int index = 0;
        
            for (Patient existing : list) {
                if (existing.getSicknessLevel() == Patient.SICKNESS_3) {
                    break;
                }
                index++;
            }
            
            list.add(index, p);
        } else {
            waitingRoom.add(p);
        }
    }
    
//    private void movePatientsToRooms() {
//        System.out.println("→ MOVING PATIENTS TO ROOM");
//        for (Room room : rooms) {
//
//           // Only work with free rooms
//           if (room.isFree()) {
//
//               Patient p = null;
//
//               // Try priority queue first
//               if (!priorityQueue.isEmpty() && room.canTreat(priorityQueue.peek().getSicknessLevel())) {
//                   p = priorityQueue.poll();
//               }
//               
//               // Otherwise try normal queue
//               else if (!normalQueue.isEmpty() && room.canTreat(normalQueue.peek().getSicknessLevel())) {
//                   p = normalQueue.poll();
//               }
//
//               // If we found a suitable patient, assign them
//               if (p != null) {
//                   room.assignPatient(p);
//               }
//           }
//       }
//    }

private void assignDoctorsToRooms() {

    System.out.println("→ ASSIGNING DOCTORS");

    for (Doctor doctor : doctors) {

        if (doctor.isBusy()) continue;

        Room bestRoom = null;

        // ✅ 1. First: look for LEVEL 3 patients
        for (Room room : rooms) {
            if (room.getPatient() != null &&
                room.getDoctor() == null &&
                room.getPatient().getSicknessLevel() == Patient.SICKNESS_3 &&
                doctor.canTreat(Patient.SICKNESS_3)) {

                bestRoom = room;
                break; // level 3 found → highest priority
            }
        }

        // ✅ 2. If no level 3, find longest waiting patient
        if (bestRoom == null) {

            int maxWait = -1;

            for (Room room : rooms) {
                if (room.getPatient() != null &&
                    room.getDoctor() == null &&
                    doctor.canTreat(room.getPatient().getSicknessLevel())) {

                    if (room.getWaitTime() > maxWait) {
                        maxWait = room.getWaitTime();
                        bestRoom = room;
                    }
                }
            }
        }

        // ✅ Assign doctor if found a room
        if (bestRoom != null) {
            bestRoom.assignDoctor(doctor);

            System.out.println("Doctor assigned to Room for Patient (Level "
                + bestRoom.getPatient().getSicknessLevel()
                + ", Wait=" + bestRoom.getWaitTime() + ")");
        }
    }
}

    public void nextTick() {
        tick++;

        progressRooms();
        moveWaitingRoomToRooms();
        moveOutsidePatients();
        assignDoctorsToRooms();
    }
}

