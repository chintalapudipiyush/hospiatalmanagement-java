package com.learnJDBC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;
import java.sql.ResultSet;

public class Patient {
    private Connection con;
    private Scanner sc;

    public Patient(Connection con, Scanner sc) {
        this.con = con;
        this.sc = sc;
    }

		    public void addPatient() {
		        System.out.println("Enter patients name:");
		        String name = sc.next();
		        System.out.println("Enter patients age:");
		        int age = sc.nextInt();
		        System.out.println("Enter patients gender:");
		        String gender = sc.next();
		
		        try {
		            String query = "INSERT INTO patients(name, age, gender) VALUES (?, ?, ?)";
		            PreparedStatement preparedStatement = con.prepareStatement(query);  
		            preparedStatement.setString(1, name);
		            preparedStatement.setInt(2, age);
		            preparedStatement.setString(3, gender);
		
		            int affectedRows = preparedStatement.executeUpdate();
		            if (affectedRows > 0) {
		                System.out.println("Patient added successfully.");
		            }
		        } catch (SQLException e) {
		            System.out.println("Error adding patient: " + e.getMessage());
		        }
		       		      
		      }
		    
			    public void viewPatient() {
			    	String query="select * from patients";
			    	try {
			    		PreparedStatement preparedStatement=con.prepareStatement(query);
			    		ResultSet resultSet=preparedStatement.executeQuery();
			    		System.out.println("Patients: ");
			    		System.out.println("+--------------+--------------+--------------+--------------+");
			    		System.out.println("| Patient Id   | Name         | Age          |Gender        |");
			    		System.out.println("+--------------+--------------+--------------+--------------+");
			    		while(resultSet.next()) {
			    			int id=resultSet.getInt("id");
			    			String name=resultSet.getString("name");
			    			int age=resultSet.getInt("age");
			    			String gender=resultSet.getString("gender");
			    			System.out.printf(" | %-10s | %-18s | %-8s | %-10s |\n",id,name,age,gender);
			    			System.out.println("+--------------+--------------+--------------+--------------+");
			    			
			    			
			    		}
			    		
			    	}
			    	catch (SQLException e) {
			    		e.printStackTrace();
			    	}		    	
			    	
                     }
			    
			    
			  public boolean getPatientById(int id) {
				  String query="select * from Patients where id=?";
				  try {
					  PreparedStatement preparedStatement=con.prepareStatement(query);
					  preparedStatement.setInt(1,id);
					  ResultSet rs=preparedStatement.executeQuery();
					  if(rs.next()) {
						  return true;
					  }
					  
					  else {
						  return false;
					  }
					  
					  
				  }
				  catch(SQLException e) {
					  System.out.println("In catch");
				  }
				return false;
				  
			  }
			    
			    
			    
			    
			    
}
