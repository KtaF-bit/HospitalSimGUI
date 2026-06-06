/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nz.ac.aut.comp603.hospitalsimgui.view;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author GGPC
 */
public class HospitalGUI extends JFrame {

    public HospitalGUI() {
        setTitle("Hospital Simulator");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        add(new HospitalPanel());
        
        setVisible(true);
    }   
}

class HospitalPanel extends JPanel {
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Waiting Room
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(50, 200, 200, 150);
        
        g.setColor(Color.BLACK);
        g.drawString ("Waiting Room", 90, 190);
        
        // 5 Hospital Rooms
        for (int i = 0; i < 5; i++) {
            int x = 300 + (i * 90);
            int y = 200;
            
            g.setColor(Color.WHITE);
            g.fillRect(x, y, 80, 80);
            
            g.setColor(Color.BLACK);
            g.drawRect(x, y, 80, 80);
            g.drawString("Room " + (i + 1), x + 10, y + 45);
        }
        
        // Hallway
        g.setColor(Color.GRAY);
        g.fillRect(250, 300, 500, 40);
    }
}