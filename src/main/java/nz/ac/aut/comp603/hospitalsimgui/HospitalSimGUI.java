/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */

package nz.ac.aut.comp603.hospitalsimgui;


import nz.ac.aut.comp603.hospitalsimgui.controller.HospitalController;
import nz.ac.aut.comp603.hospitalsimgui.model.*;
import java.util.*;


/**
 *
 * @author GGPC
 */
public class HospitalSimGUI {

    
    public static void main(String[] args) {

        // Create rooms
        List<Room> rooms = new ArrayList<>();
        rooms.add(new Room(Set.of(1, 2)));
        rooms.add(new Room(Set.of(2, 3)));

        // Create doctors
        List<Doctor> doctors = new ArrayList<>();
        doctors.add(new Doctor(Set.of(1, 2)));
        doctors.add(new Doctor(Set.of(2, 3)));

        // Create controller
        HospitalController controller = new HospitalController(rooms, doctors);

        // Add some patients
        controller.addPatient(1);
        controller.addPatient(3);
        controller.addPatient(2);

        // Run a few ticks
        for (int i = 0; i < 5; i++) {
            System.out.println("Tick " + i);
            controller.nextTick();
        }
    }

}
