package nz.ac.aut.comp603.hospitalsimgui.controller;

/**
 *
 * @author Kobe Fabrello (22157634)
 */
import java.sql.*;
import java.util.List;
import java.util.ArrayList;

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
    
    public List<int[]> getAllPatientStats() {
        List<int[]> stats = new ArrayList<>();

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT level, timeInHospital FROM PatientStats");

            while (rs.next()) {
                int level = rs.getInt("level");
                int time = rs.getInt("timeInHospital");

                stats.add(new int[]{level, time});
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return stats;
    }
}
