package nz.ac.aut.comp603.hospitalsimgui.view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JOptionPane;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.*;
import java.awt.Graphics;
import nz.ac.aut.comp603.hospitalsimgui.controller.HospitalController;
import nz.ac.aut.comp603.hospitalsimgui.model.*;
import java.util.*;

/**
 *
 * @author Kobe Fabrello (22157634)
 */
public class HospitalGUI extends JFrame {

    private final HospitalController controller;
    private JLabel totalPatientsLabel;
    private JLabel totalPatientsDBLabel;
    
    public HospitalGUI() {
        setTitle("Hospital Simulator");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create Rooms
        List<Room> rooms = new ArrayList<>();
        rooms.add(new Room(Set.of(1)));
        rooms.add(new Room(Set.of(1,2)));
        rooms.add(new Room(Set.of(1,2)));
        rooms.add(new Room(Set.of(2,3)));
        rooms.add(new Room(Set.of(3)));
        
        // Create Doctors
        List<Doctor> doctors = new ArrayList<>();
        doctors.add(new Doctor(Set.of(1,2)));
        doctors.add(new Doctor(Set.of(1,2)));
        doctors.add(new Doctor(Set.of(2,3)));
        
        controller = new HospitalController(rooms, doctors);
        
        HospitalPanel panel = new HospitalPanel(controller);
        add(panel);
        JButton nextTickButton = new JButton("Next Tick");
        nextTickButton.addActionListener(e -> {
            controller.nextTick();
            panel.repaint();

            // update label
            
            totalPatientsDBLabel.setText(
                "Total Patients (DB): " + controller.getTotalPatientsFromDB()
            );

            totalPatientsLabel.setText(
                "Total Patients Treated: " + controller.getTotalPatientsTreated()
            );
        });
        
        JPanel buttonPanel = new JPanel();
        JButton addPatientButton = new JButton("Add Patient");
        
        addPatientButton.addActionListener(e -> {
            openAddPatientDialog(panel);
        });
        
        JButton statsButton = new JButton("Hospital Stats");
        
        statsButton.addActionListener(e -> {

            String statsText = getStatsText();

            JOptionPane.showMessageDialog(
                this,
                statsText,
                "Hospital Statistics",
                JOptionPane.INFORMATION_MESSAGE
            );
        });
        
        buttonPanel.add(addPatientButton);
        buttonPanel.add(nextTickButton);
        buttonPanel.add(statsButton);

        // Add total patients label
        totalPatientsLabel = new JLabel("Total Patients Treated: 0");
        buttonPanel.add(totalPatientsLabel);
        totalPatientsDBLabel = new JLabel("Total Patients (DB): " + controller.getTotalPatientsFromDB());
        buttonPanel.add(totalPatientsDBLabel);

        add(buttonPanel, BorderLayout.SOUTH);
        setVisible(true);
    }   
    
    private void openAddPatientDialog(HospitalPanel panel) {

        JDialog dialog = new JDialog(this, "Add Patients", false);
        dialog.setSize(300, 150);
        dialog.setLayout(new BorderLayout());
        
        JPanel buttonPanel = new JPanel();

        JButton level1Btn = new JButton("Level 1");
        JButton level2Btn = new JButton("Level 2");
        JButton level3Btn = new JButton("Level 3");
        JButton randomBtn = new JButton("Random");
        
        level1Btn.addActionListener(e -> {
            controller.addPatient(1);
            System.out.println("Added Patient Level 1");
            panel.repaint();
        });

        level2Btn.addActionListener(e -> {
            controller.addPatient(2);
            System.out.println("Added Patient Level 2");
            panel.repaint();
        });

        level3Btn.addActionListener(e -> {
            controller.addPatient(3);
            System.out.println("Added Patient Level 3");
            panel.repaint();
        });

        randomBtn.addActionListener(e -> {
            int level = new Random().nextInt(3) + 1;
            controller.addPatient(level);
            System.out.println("Added Patient Level " + level);
            panel.repaint();
            totalPatientsDBLabel.setText("Total Patients (DB): " + controller.getTotalPatientsFromDB());
            totalPatientsLabel.setText("Total Patients Treated: " + controller.getTotalPatientsTreated());
        });
        buttonPanel.add(level1Btn);
        buttonPanel.add(level2Btn);
        buttonPanel.add(level3Btn);
        buttonPanel.add(randomBtn);
        
        JButton doneBtn = new JButton("Done");
        doneBtn.addActionListener(e -> dialog.dispose());
        
        dialog.add(buttonPanel, BorderLayout.CENTER);
        dialog.add(doneBtn, BorderLayout.SOUTH);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private String getStatsText() {

        return "=== Hospital Statistics ===\n\n"
                
            + "Total Patients (DB): " + controller.getTotalPatientsFromDB() + "\n\n"

            + "Total Patients Treated: " + controller.getTotalPatientsTreated() + "\n\n"

            + "--- Patients Per Level ---\n"
            + "Level 1: " + controller.getTreatedLevel1() + "\n"
            + "Level 2: " + controller.getTreatedLevel2() + "\n"
            + "Level 3: " + controller.getTreatedLevel3() + "\n\n"

            + "--- Average Time in Hospital ---\n"
            + "Overall: " + String.format("%.2f", controller.getAverageTime()) + "\n"
            + "Level 1: " + String.format("%.2f", controller.getAverageTimeLevel1()) + "\n"
            + "Level 2: " + String.format("%.2f", controller.getAverageTimeLevel2()) + "\n"
            + "Level 3: " + String.format("%.2f", controller.getAverageTimeLevel3()) + "\n\n"

            + "--- Doctor Work ---\n"
            + "Total Work Time: " + controller.getTotalDoctorWorkTime();
    }

}

class HospitalPanel extends JPanel {

    private final HospitalController controller;

    public HospitalPanel(HospitalController controller) {
        this.controller = controller;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }
        });
    }

    // HANDLE CLICKS
    private void handleClick(int mouseX, int mouseY) {

        List<Room> rooms = controller.getRooms();

        // ROOM [i] clicks
        for (int i = 0; i < rooms.size(); i++) {

            int x = 300 + (i * 90);
            int y = 200;

            int ix = x + 60;
            int iy = y;
            int size = 15;

            if (mouseX >= ix && mouseX <= ix + size &&
                mouseY >= iy && mouseY <= iy + size) {

                showRoomInfo(rooms.get(i), i + 1);
                return;
            }
        }

        // WAITING ROOM [i]
        int wx = 230;
        int wy = 200;
        int size = 15;

        if (mouseX >= wx && mouseX <= wx + size &&
            mouseY >= wy && mouseY <= wy + size) {

            showWaitingRoomInfo();
        }
    }

    // ROOM INFO POPUP
    private void showRoomInfo(Room room, int roomNumber) {

        if (room.getPatient() == null) {
            JOptionPane.showMessageDialog(this,
                "Room " + roomNumber + "\nEmpty",
                "Room Info",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Patient p = room.getPatient();

        String info =
            "Room " + roomNumber + "\n\n"
            + "Patient Level: " + p.getSicknessLevel() + "\n"
            + "Time in Hospital: " + p.getTimeInHospital() + "\n"
            + "Treatment Time Left: " + p.getTreatmentTime() + "\n"
            + "Wait Time: " + room.getWaitTime() + "\n"
            + "Doctor Assigned: " + (room.getDoctor() != null ? "Yes" : "No");

        JOptionPane.showMessageDialog(this, info, "Room Info", JOptionPane.INFORMATION_MESSAGE);
    }

    // WAITING ROOM INFO POPUP
    private void showWaitingRoomInfo() {

        int total = controller.getWaitingRoomSize();
        int l1 = 0, l2 = 0, l3 = 0;

        StringBuilder queueOrder = new StringBuilder();

        for (Patient p : controller.getWaitingRoom()) {

            int level = p.getSicknessLevel();

            switch (level) {
                case 1:
                    l1++;
                    break;
                case 2:
                    l2++;
                    break;
                case 3:
                    l3++;
                    break;
                default:
                    break;
            }

            queueOrder.append(level).append(" → ");
        }

        String info =
            "Waiting Room\n\n"
            + "Total Patients: " + total + "\n"
            + "Level 1: " + l1 + "\n"
            + "Level 2: " + l2 + "\n"
            + "Level 3: " + l3 + "\n\n"
            + "Queue Order:\n"
            + queueOrder;

        JOptionPane.showMessageDialog(this, info, "Waiting Room Info", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // WAITING ROOM
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(50, 200, 200, 150);

        // WAITING ROOM [i]
        g.setColor(new Color(173, 216, 230));
        g.fillRect(230, 200, 15, 15);

        g.setColor(Color.BLACK);
        g.drawRect(230, 200, 15, 15);
        g.drawString("i", 235, 212);

        g.drawString("Waiting Room", 80, 190);

        g.drawString("(" + controller.getWaitingRoomSize() + " patients)", 70, 220);

        // STATUS LEGEND (colored dots)

        // Green
        g.setColor(Color.GREEN);
        g.fillOval(300, 135, 10, 10);
        g.setColor(Color.BLACK);
        g.drawString("= Being Treated", 315, 145);

        // Red
        g.setColor(Color.RED);
        g.fillOval(300, 150, 10, 10);
        g.setColor(Color.BLACK);
        g.drawString("= Waiting for Doctor", 315, 160);

        // ROOMS
        List<Room> rooms = controller.getRooms();

        for (int i = 0; i < rooms.size(); i++) {

            Room room = rooms.get(i);

            int x = 300 + (i * 90);
            int y = 200;

            // Room box
            g.setColor(Color.WHITE);
            g.fillRect(x, y, 80, 80);

            g.setColor(Color.BLACK);
            g.drawRect(x, y, 80, 80);

            g.drawString("Room " + (i + 1), x + 10, y + 20);

            if (room.getPatient() == null) {
                g.drawString("Empty", x + 10, y + 45);
            } else {
                g.drawString("L" + room.getPatient().getSicknessLevel(), x + 10, y + 45);
            }

            // STATUS INDICATOR
            Color indicatorColor;

            if (room.getPatient() == null) {
                indicatorColor = Color.GRAY;
            } else if (room.getDoctor() == null) {
                indicatorColor = Color.RED;
            } else {
                indicatorColor = Color.GREEN;
            }

            g.setColor(indicatorColor);
            g.fillOval(x + 5, y - 20, 10, 10);

            g.setColor(Color.BLACK);
            g.drawOval(x + 5, y - 20, 10, 10);

            // [i]
            g.setColor(new Color(173, 216, 230));
            g.fillRect(x + 60, y, 15, 15);

            g.setColor(Color.BLACK);
            g.drawRect(x + 60, y, 15, 15);
            g.drawString("i", x + 65, y + 12);
        }

        // HALLWAY
        g.setColor(Color.GRAY);
        g.fillRect(250, 300, 500, 40);

        g.setColor(Color.BLACK);

        g.drawString("Priority Queue: " + controller.getPriorityQueueSize(), 20, getHeight() - 40);
        g.drawString("Normal Queue: " + controller.getNormalQueueSize(), 20, getHeight() - 20);
    }
}