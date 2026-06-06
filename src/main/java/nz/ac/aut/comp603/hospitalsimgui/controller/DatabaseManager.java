/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nz.ac.aut.comp603.hospitalsimgui.controller;

/**
 *
 * @author GGPC
 */
import java.sql.*;

public class DatabaseManager {

    private Connection conn;

    public DatabaseManager() {
        try {
            conn = DriverManager.getConnection("jdbc:derby:HospitalDB;create=true");
            createTable();
        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
        }
    }

    private void createTable() {
        try {
            Statement stmt = conn.createStatement();

            stmt.executeUpdate(
                "CREATE TABLE PatientStats ("
                + "id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, "
                + "level INT, "
                + "timeInHospital INT)"
            );

        } catch (SQLException e) {
            // table already exists → ignore
        }
    }

    public void insertPatientStat(int level, int time) {
        try {
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO PatientStats (level, timeInHospital) VALUES (?, ?)"
            );

            ps.setInt(1, level);
            ps.setInt(2, time);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
