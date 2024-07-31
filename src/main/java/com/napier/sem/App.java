package com.napier.sem;

import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class App {
    public static void main(String[] args) {
        // Initializing Credentials to login to database
        String jdbcURL = "jdbc:mysql://localhost:3306/world"; //path to the database
        String username = "root"; // This is the username to be used to login
        String password = "example"; // The password to use : this is example as I started setting it to example when setting up the database as it
                                    // took many tries


        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); //prepares the driver
            System.out.println("MySQL JDBC Driver Registered!"); //success message
        } catch (ClassNotFoundException e) {
            System.out.println("Driver Error"); //failure message
            return;
        }

        // connect to the database
        try (Connection connection = DriverManager.getConnection(jdbcURL, username, password)) { //Connection object is inbuilt to JDBC
            System.out.println("Connected to the database"); //success message


            menu(connection);// Call menu to begin user interaction and pass the connection to database

        } catch (SQLException e) {
            System.out.println("Error connecting to Database: " + e.getMessage()); //if database connection fails, show error message
        }
    }

    public static void menu(Connection connection) {
        System.out.println("What data would you like to display? \nFor Country press 1, for City press 2, for Capital City press 3, for Population press 4 ");
        Scanner scanner = new Scanner(System.in); //prepare scanner
        int input = scanner.nextInt(); //take user input
        boolean validInput = false; //set validInput to false by default

        do {
            if (input == 1) {
                System.out.println("Displaying Country report...");
                printAllCountries(connection); // pass connection to the method
                validInput = true;
            } else if (input == 2) {
                System.out.println("Displaying City report...");
                printAllCities(connection); // Pass connection to the method
                validInput = true;
            } else if (input == 3) {
                System.out.println("Displaying Capital City report...");
                validInput = true;
            } else if (input == 4) {
                System.out.println("Displaying Population report...");

                validInput = true;
            } else {
                System.out.println("Invalid Input");
            }
        } while (!validInput);
    }

    // Method to print all countries
    public static void printAllCountries(Connection con) {
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Code, Name, Continent, Region, Population FROM country")) {
            while (rs.next()) {
                System.out.println("Country Code: " + rs.getString("Code") +
                        ", Name: " + rs.getString("Name") +
                        ", Continent: " + rs.getString("Continent") +
                        ", Region: " + rs.getString("Region") +
                        ", Population: " + rs.getInt("Population"));
            }
        } catch (SQLException e) {
            System.out.println("Failed to retrieve countries: " + e.getMessage());
        }
    }


    public static void printAllCities(Connection con) {
        try (Statement stmt = con.createStatement(); //statement object is inbuilt to JDBC
             ResultSet rs = stmt.executeQuery("SELECT ID, Name FROM city")) { //Resultset object is inbuilt to JDBC
            while (rs.next()) {
                System.out.println("City ID: " + rs.getInt("ID") + ", Name: " + rs.getString("Name"));
            }
        } catch (SQLException e) {
            System.out.println("Failed to retrieve cities: " + e.getMessage());
        }
    }
}
