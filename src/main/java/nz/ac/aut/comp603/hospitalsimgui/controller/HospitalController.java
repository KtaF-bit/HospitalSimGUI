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
    
    // Stat keeping PATIENTS
    private int totalPatientsTreated = 0;
    private int treatedLevel1 = 0;
    private int treatedLevel2 = 0;
    private int treatedLevel3 = 0;
    private int totalTimeInHospital = 0;
    private int totalTimeLevel1 = 0;
    private int totalTimeLevel2 = 0;
    private int totalTimeLevel3 = 0;
    
    // Stat keeping DOCTORS
    private int totalDoctorWorkTime = 0;
    private int workTimeLevel1 = 0;
    private int workTimeLevel2 = 0;
    private int workTimeLevel3 = 0;
    private int totalTreatmentsDone = 0;
    private int treatmentsLevel1 = 0;
    private int treatmentsLevel2 = 0;
    private int treatmentsLevel3 = 0;

    private DatabaseManager db;
    private int tick;

    // Constructor
    public HospitalController(List<Room> rooms, List<Doctor> doctors) {
        this.rooms = rooms;
        this.doctors = doctors;
        this.waitingRoom = new LinkedList<>();
        this.priorityQueue = new LinkedList<>();
        this.normalQueue = new LinkedList<>();
        db = new DatabaseManager();
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
        // System.out.println("→ TREATING PATIENTS");
        for (Room room : rooms) {
            room.incrementWaitTime();
            Patient finished = room.progressTreatment();

            if (finished != null) {
                recordPatientStats(finished);
            }
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
                        ") moved from Waiting Room to Room " + (i + 1));
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

    private void assignDoctorsToRooms() {

        System.out.println("→ ASSIGNING DOCTORS");

        for (Doctor doctor : doctors) {

            if (doctor.isBusy()) continue;

            Room bestRoom = null;
            int maxWait = -1;

            // ✅ STEP 1: find BEST Level 3 room (longest wait)
            for (Room room : rooms) {
                if (room.getPatient() != null &&
                    room.getDoctor() == null &&
                    room.getPatient().getSicknessLevel() == Patient.SICKNESS_3 &&
                    doctor.canTreat(Patient.SICKNESS_3)) {

                    if (room.getWaitTime() > maxWait) {
                        maxWait = room.getWaitTime();
                        bestRoom = room;
                    }
                }
            }

            // ✅ STEP 2: if no Level 3 found → use general longest wait
            if (bestRoom == null) {

                maxWait = -1;

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

            // ✅ ASSIGN
            if (bestRoom != null) {
                bestRoom.assignDoctor(doctor);

                System.out.println(
                    "Doctor assigned to Room for Patient (Level "
                    + bestRoom.getPatient().getSicknessLevel()
                    + ", Wait=" + bestRoom.getWaitTime() + ")"
                );
            }
        }
    }
    
    public void recordPatientStats(Patient p) {
        System.out.println("Recording stats for patient Level " + p.getSicknessLevel());
        db.insertPatientStat(p.getSicknessLevel(), p.getTimeInHospital());

        totalPatientsTreated++;
        totalTimeInHospital += p.getTimeInHospital();

        int level = p.getSicknessLevel();

        switch (level) {
            case Patient.SICKNESS_1:
                treatedLevel1++;
                totalTimeLevel1 += p.getTimeInHospital();
                treatmentsLevel1++;
                break;
            case Patient.SICKNESS_2:
                treatedLevel2++;
                totalTimeLevel2 += p.getTimeInHospital();
                treatmentsLevel2++;
                break;
            case Patient.SICKNESS_3:
                treatedLevel3++;
                totalTimeLevel3 += p.getTimeInHospital();
                treatmentsLevel3++;
                break;
            default:
                break;
        }

        totalTreatmentsDone++;
    }

    public void nextTick() {
        tick++;
        
        // ✅ Waiting room time
        for (Patient p : waitingRoom) {
            p.incrementTimeInHospital();
        }

        // ✅ Rooms time
        for (Room room : rooms) {
            if (room.getPatient() != null) {
                room.getPatient().incrementTimeInHospital();
            }
        }

        progressRooms();
        moveWaitingRoomToRooms();
        moveOutsidePatients();
        assignDoctorsToRooms();

        // ✅ ADD THIS HERE
        for (Room room : rooms) {
            if (room.getPatient() != null && room.getDoctor() != null) {

                totalDoctorWorkTime++;

                int level = room.getPatient().getSicknessLevel();

                switch (level) {
                    case Patient.SICKNESS_1:
                        workTimeLevel1++;
                        break;
                    case Patient.SICKNESS_2:
                        workTimeLevel2++;
                        break;
                    case Patient.SICKNESS_3:
                        workTimeLevel3++;
                        break;
                    default:
                        break;
                }
            }
        }
    }
    
    public List<Room> getRooms() {
        return rooms;
    }

    public int getWaitingRoomSize() {
        return waitingRoom.size();
    }

    public int getPriorityQueueSize() {
        return priorityQueue.size();
    }

    public int getNormalQueueSize() {
        return normalQueue.size();
    }
    
    public double getAverageTime() {
        if (totalPatientsTreated == 0) return 0;
        return (double) totalTimeInHospital / totalPatientsTreated;
    }
    
    public double getAverageTimeLevel1() {
        if (treatedLevel1 == 0) return 0;
        return (double) totalTimeLevel1 / treatedLevel1;
    }

    public double getAverageTimeLevel2() {
        if (treatedLevel2 == 0) return 0;
        return (double) totalTimeLevel2 / treatedLevel2;
    }

    public double getAverageTimeLevel3() {
        if (treatedLevel3 == 0) return 0;
        return (double) totalTimeLevel3 / treatedLevel3;
    }
    
    public double getAverageDoctorWorkTime() {
        if (totalTreatmentsDone == 0) return 0;
        return (double) totalDoctorWorkTime / totalTreatmentsDone;
    }
    
    public int getTotalPatientsTreated() {
        return totalPatientsTreated;
    }

    public int getTreatedLevel1() {
        return treatedLevel1;
    }

    public int getTreatedLevel2() {
        return treatedLevel2;
    }

    public int getTreatedLevel3() {
        return treatedLevel3;
    }

    public int getTotalDoctorWorkTime() {
        return totalDoctorWorkTime;
    }
    
    public Queue<Patient> getWaitingRoom() {
        return waitingRoom;
    }
}

