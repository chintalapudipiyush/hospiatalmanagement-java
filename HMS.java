package com.learnJDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class HMS {

    public static void main(String[] args) {
        String URL = "jdbc:mysql://localhost:3306/hospital";
        String Username = "root";
        String Password = "217r1a05n3";
        Connection connection = null;
        Scanner scanner = new Scanner(System.in);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, Username, Password);
            connection.setAutoCommit(false); // Manage transactions manually
            Patient patient = new Patient(connection, scanner);
            Doctor doctor = new Doctor(connection);

            while (true) {
                System.out.println("HOSPITAL MANAGEMENT SYSTEM");
                System.out.println("1. Add Patient");
                System.out.println("2. View Patients");
                System.out.println("3. View Doctors");
                System.out.println("4. Book Appointment");
                System.out.println("5. Exit");
                System.out.println("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline

                switch (choice) {
                    case 1:
                        patient.addPatient();
                        break;
                    case 2:
                        patient.viewPatient();
                        break;
                    case 3:
                        doctor.viewDoctor();
                        break;
                    case 4:
                        bookAppointment(patient, doctor, connection, scanner);
                       connection.commit(); // Commit transaction if all went well
                        break;
                    case 5:
                        System.out.println("Thank you for using the Hospital Management System!");
                        return; // Avoid closing scanner here
                    default:
                        System.out.println("Enter a valid choice!");
                        break;
                }
                System.out.println();
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Connection error or class not found!");
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
                if (scanner != null) {
                    scanner.close(); // Close scanner here at the end
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void bookAppointment(Patient patient, Doctor doctor, Connection connection, Scanner scanner) {
        System.out.print("Enter Patient Id: ");
        int patientId = scanner.nextInt();
        System.out.print("Enter Doctor Id: ");
        int doctorId = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        System.out.print("Enter appointment date (YYYY-MM-DD): ");
        String appointmentDate = scanner.nextLine();

        try {
            connection.setAutoCommit(false); // Start transaction
            if (patient.getPatientById(patientId) && doctor.getDoctorById(doctorId)) {
                if (checkDoctorAvailability(doctorId, appointmentDate, connection)) {
                    String appointmentQuery = "INSERT INTO appointments (patients_id, doctor_id, appointment_date) VALUES (?, ?, ?)";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery)) {
                        preparedStatement.setInt(1, patientId);
                        preparedStatement.setInt(2, doctorId);
                        preparedStatement.setString(3, appointmentDate);

                        int rowsAffected = preparedStatement.executeUpdate();
                        if (rowsAffected > 0) {
                            System.out.println("Appointment Booked!");
                            connection.commit(); // Commit only if everything is successful
                        } else {
                            System.out.println("Failed to Book Appointment!");
                            connection.rollback();
                        }
                    }
                } else {
                    System.out.println("Doctor not available on this date!");
                    connection.rollback();
                }
            } else {
                System.out.println("Either doctor or patient doesn't exist!!!");
                connection.rollback();
            }
        } catch (SQLException e) {
            System.out.println("SQL Error during appointment booking!");
            e.printStackTrace();
            try {
                connection.rollback(); 
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static boolean checkDoctorAvailability(int doctorId, String appointmentDate, Connection connection) throws SQLException {
        String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, doctorId);
            preparedStatement.setString(2, appointmentDate);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1) == 0;
            }
        }
        return false;
    }
}

