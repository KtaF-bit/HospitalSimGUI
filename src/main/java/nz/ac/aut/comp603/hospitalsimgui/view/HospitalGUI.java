/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nz.ac.aut.comp603.hospitalsimgui.view;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.*;
import java.awt.Graphics;
import nz.ac.aut.comp603.hospitalsimgui.controller.HospitalController;
import nz.ac.aut.comp603.hospitalsimgui.model.*;
import java.util.*;

/**
 *
 * @author GGPC
 */
public class HospitalGUI extends JFrame {

    private final HospitalController controller;
    
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
            controller.nextTick();  // ✅ update system
            panel.repaint();        // ✅ redraw GUI
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

        add(buttonPanel, "South");
        
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
        }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // ✅ WAITING ROOM
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(50, 200, 200, 150);

        g.setColor(Color.BLACK);
        g.drawString("Waiting Room", 80, 190);

        // ✅ Show number of patients
        g.drawString(
            "(" + controller.getWaitingRoomSize() + " patients)", 
            70, 220
        );

        // ✅ ROOMS
        List<Room> rooms = controller.getRooms();

        for (int i = 0; i < rooms.size(); i++) {

            Room room = rooms.get(i);

            int x = 300 + (i * 90);
            int y = 200;

            g.setColor(Color.WHITE);
            g.fillRect(x, y, 80, 80);

            g.setColor(Color.BLACK);
            g.drawRect(x, y, 80, 80);

            // ✅ Room label
            g.drawString("Room " + (i + 1), x + 10, y + 20);

            // ✅ Show status
            if (room.getPatient() == null) {
                g.drawString("Empty", x + 10, y + 45);
            } else {
                int level = room.getPatient().getSicknessLevel();
                g.drawString("L" + level, x + 10, y + 45);
            }
        }

        // ✅ HALLWAY
        g.setColor(Color.GRAY);
        g.fillRect(250, 300, 500, 40);
        
        g.setColor(Color.BLACK);

        int prioritySize = controller.getPriorityQueueSize();
        int normalSize = controller.getNormalQueueSize();

        // Bottom-left corner text
        g.drawString("Priority Queue: " + prioritySize, 20, getHeight() - 40);
        g.drawString("Normal Queue: " + normalSize, 20, getHeight() - 20);
    }
}