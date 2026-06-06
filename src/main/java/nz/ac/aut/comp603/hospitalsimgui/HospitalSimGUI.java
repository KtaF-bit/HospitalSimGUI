package nz.ac.aut.comp603.hospitalsimgui;


import nz.ac.aut.comp603.hospitalsimgui.controller.HospitalController;
import nz.ac.aut.comp603.hospitalsimgui.model.*;
import nz.ac.aut.comp603.hospitalsimgui.view.*;
import java.util.*;
import javax.swing.SwingUtilities;


/**
 *
 * @author Kobe Fabrello (22157634)
 */
public class HospitalSimGUI {

    
    public static void main(String[] args) {
        // Create rooms
        List<Room> rooms = new ArrayList<>();
        rooms.add(new Room(Set.of(1,2))); // Room 1
        rooms.add(new Room(Set.of(1,2))); // Room 2
        rooms.add(new Room(Set.of(1)));   // Room 3
        rooms.add(new Room(Set.of(2,3))); // Room 4
        rooms.add(new Room(Set.of(3)));   // Room 5

        // Create doctors
        List<Doctor> doctors = new ArrayList<>();
        doctors.add(new Doctor(Set.of(1,2))); // Doctor 1
        doctors.add(new Doctor(Set.of(1,2))); // Doctor 2
        doctors.add(new Doctor(Set.of(2,3))); // Doctor 3

        HospitalController controller = new HospitalController(rooms, doctors);

        SwingUtilities.invokeLater(() -> {
            new HospitalGUI();
        });
        
    }

}
