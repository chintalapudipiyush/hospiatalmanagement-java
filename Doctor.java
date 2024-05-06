package com.learnJDBC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Doctor {
    private Connection con;

    public Doctor(Connection con) {
        this.con = con;
    }

    public void viewDoctor() {
        String query = "select * from doctors";
        try (
            PreparedStatement preparedStatement = con.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
        ) {
            System.out.println("Doctors:");
            System.out.println("+--------------+--------------+----------------+");
            System.out.println("| Doctor Id    | Name         | Specialization |");
            System.out.println("+--------------+--------------+----------------+");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String  specilization = resultSet.getString("specilization");
                System.out.printf("| %-12d | %-12s | %-14s |\n", id, name, specilization);
                System.out.println("+--------------+--------------+----------------+");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean getDoctorById(int id) {
        String query = "select * from doctors where id=?";
        try (
            PreparedStatement preparedStatement = con.prepareStatement(query);
        ) {
            preparedStatement.setInt(1, id);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Error occurred while fetching doctor by ID:");
            e.printStackTrace();
        }
        return false;
    }
}
