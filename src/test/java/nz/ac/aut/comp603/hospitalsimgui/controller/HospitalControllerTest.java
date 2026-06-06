package nz.ac.aut.comp603.hospitalsimgui.controller;

/**
 *
 * @author Kobe Fabrello (22157634)
 */

import nz.ac.aut.comp603.hospitalsimgui.model.Room;
import nz.ac.aut.comp603.hospitalsimgui.model.Doctor;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HospitalControllerTest {

    // HELPER METHOD to create controller correctly
    private HospitalController createController() {
        List<Room> rooms = new ArrayList<>();
        List<Doctor> doctors = new ArrayList<>();

        // Level set (can treat all levels)
        java.util.Set<Integer> levels = new java.util.HashSet<>();
        levels.add(1);
        levels.add(2);
        levels.add(3);

        // create rooms
        for (int i = 0; i < 3; i++) {
            rooms.add(new Room(levels));
        }

        // create doctors
        for (int i = 0; i < 3; i++) {
            doctors.add(new Doctor(levels));
        }

        return new HospitalController(rooms, doctors);
        }

    // TEST 1: Level 3 goes to priority queue
    @Test
    public void testLevel3GoesToPriorityQueue() {
        HospitalController controller = createController();

        controller.addPatient(3);

        assertEquals(1, controller.getPriorityQueueSize());
        assertEquals(0, controller.getNormalQueueSize());
    }

    // TEST 2: Normal patient goes to normal queue
    @Test
    public void testNormalPatientQueue() {
        HospitalController controller = createController();

        controller.addPatient(1);

        assertEquals(1, controller.getNormalQueueSize());
    }

    // TEST 3: Waiting room capacity (max 7)
    @Test
    public void testWaitingRoomCapacity() {
        HospitalController controller = createController();

        for (int i = 0; i < 20; i++) {
            controller.addPatient(1);
        }

        assertTrue(controller.getWaitingRoomSize() <= 7);
    }

    // TEST 4: Patients move into rooms
    @Test
    public void testPatientMovesToRoom() {
        HospitalController controller = createController();

        controller.addPatient(2);
        controller.nextTick();

        boolean found = false;

        for (Room room : controller.getRooms()) {
            if (room.getPatient() != null) {
                found = true;
            }
        }

        assertTrue(found);
    }

    // TEST 5: Doctor gets assigned
    @Test
    public void testDoctorAssigned() {
        HospitalController controller = createController();

        controller.addPatient(2);
        controller.nextTick();

        boolean doctorAssigned = false;

        for (Room room : controller.getRooms()) {
            if (room.getDoctor() != null) {
                doctorAssigned = true;
            }
        }

        assertTrue(doctorAssigned);
    }

    // TEST 6: Patients get discharged eventually
    @Test
    public void testPatientDischarge() {
        HospitalController controller = createController();

        controller.addPatient(1);

        // run many ticks
        for (int i = 0; i < 50; i++) {
            controller.nextTick();
        }

        boolean allEmpty = true;

        for (Room room : controller.getRooms()) {
            if (room.getPatient() != null) {
                allEmpty = false;
            }
        }

        assertTrue(allEmpty);
    }

    // TEST 7: Stats increase after treatment
    @Test
    public void testStatsIncreaseAfterTreatment() {
        HospitalController controller = createController();

        controller.addPatient(1);

        for (int i = 0; i < 50; i++) {
            controller.nextTick();
        }

        assertTrue(controller.getTotalPatientsTreated() > 0);
    }
    
    // TEST 8: Database stores patient
    @Test
    public void testDatabaseStoresPatient() {
        HospitalController controller = createController();

        controller.addPatient(1);

        // run enough ticks for discharge
        for (int i = 0; i < 50; i++) {
            controller.nextTick();
        }

        // DB should now have stored at least 1 patient
        assertTrue(controller.getTotalPatientsFromDB() > 0);
    }
    
    // TEST 9: Database matches treatments
    @Test
    public void testDatabaseMatchesTreatedCount() {
        HospitalController controller = createController();

        controller.addPatient(1);
        controller.addPatient(2);

        for (int i = 0; i < 50; i++) {
            controller.nextTick();
        }

        int treated = controller.getTotalPatientsTreated();
        int stored = controller.getTotalPatientsFromDB();

        // DB should store at least as many completed treatments
        assertTrue(stored >= treated);
    }
    
    // TEST 10: Database persists across controller instances
    @Test
    public void testDatabasePersistsData() {
        HospitalController controller1 = createController();

        controller1.addPatient(1);

        for (int i = 0; i < 50; i++) {
            controller1.nextTick();
        }

        int storedBefore = controller1.getTotalPatientsFromDB();

        // Create new controller (simulates restart)
        HospitalController controller2 = createController();

        int storedAfter = controller2.getTotalPatientsFromDB();

        // Data should persist
        assertTrue(storedAfter >= storedBefore);
    }
}