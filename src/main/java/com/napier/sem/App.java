package com.napier.sem;

//As I had a lot of problems getting the method from the Labs to work, I had to find alternatives. To do this, I used
//The following sources
//https://www.baeldung.com/java-jdbc



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
            Class.forName("com.mysql.cj.jdbc.Driver"); //loads the driver class
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
        System.out.println("What data would you like to display? \n1. Country \n2. City \n3. Capital City \n4. Other \n5. Exit");
        Scanner scanner = new Scanner(System.in); //prepare scanner
        int input = scanner.nextInt(); //take user input
        boolean validInput = false; //set validInput to false by default

        do {
            if (input == 1) {
                System.out.println("Displaying Country menu...");
                System.out.println("1. Full report \n2. Top N populated countries");
                int countryInput = scanner.nextInt();
                do {
                    if (countryInput == 1) {
                        System.out.println("Displaying Country report...");
                        printAllCountries(connection); // pass connection to the method
                        validInput = true;
                    } else if (countryInput == 2) {
                        System.out.println("How many countries would you like to view?");
                        int num = scanner.nextInt();

                        System.out.println("Displaying top " + num + " populated countries...");
                        topPopulatedCountry(connection, num);
                    }
                }while (!validInput);
            } else if (input == 2) {
                System.out.println("Displaying City menu...");
                System.out.println("1. Full report \n2. Top N populated cities");
                int cityInput = scanner.nextInt();
                do {
                    if (cityInput == 1) {
                        System.out.println("Displaying City report...");
                        printAllCities(connection); // Pass connection to the method
                        validInput = true;
                    } else if (cityInput == 2) {
                        System.out.println("How many cities would you like to view?");
                        int num = scanner.nextInt();

                        System.out.println("Displaying top " + num + " populated cities...");
                        topPopulatedCity(connection, num);
                    }
                }while (!validInput);
            } else if (input == 3) {
                System.out.println("Displaying Capital City report...");
                printAllCapitals(connection);
                validInput = true;
            } else if (input == 4) {
                System.out.println("1. Languages Report");
                int languageInput = scanner.nextInt();
                do {
                    if (languageInput == 1) {
                        System.out.println("Displaying Language report...");
                        printLanguages(connection); // Pass connection to the method
                        validInput = true;
                    }
                }while (!validInput);
                validInput = true;
            } else if (input == 5) {
                System.out.println("Exiting...");
                System.exit(0);

            } else {
                System.out.println("Invalid Input");
            }
        } while (!validInput);
    }

    public static void printLanguages(Connection con) {
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT ")) {
            while (rs.next()) {
                System.out.println("");
            }
        } catch (SQLException e) {
            System.out.println("Failed to retrieve languages: " + e.getMessage());
        }
    }

    public static void topPopulatedCountry(Connection con, int num){
        int counter = 0;
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Code, Name, Population FROM country ORDER BY Population desc ")) {
            while (rs.next() && counter <= num-1) {
                System.out.println("Country Code: " + rs.getString("Code") + ", Name: " + rs.getString("Name") + ", Population: " + rs.getString("Population"));
                counter++;
            }
        } catch (SQLException e) {
            System.out.println("Failed to retrieve countries: " + e.getMessage());
        }
        menu(con);
    }

    public static void topPopulatedCity(Connection con, int num){
        int counter = 0;
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Name, Population FROM city ORDER BY Population desc ")) {
            while (rs.next() && counter <= num-1) {
                System.out.println("Name: " + rs.getString("Name") + ", Population: " + rs.getString("Population"));
                counter++;
            }
        } catch (SQLException e) {
            System.out.println("Failed to retrieve cities: " + e.getMessage());
        }
    }




    // Method to print all countries
    public static void printAllCountries(Connection con) {
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Code, Name, Continent, Region, Population, Capital FROM country ORDER BY Population desc ")) {
            while (rs.next()) {
                System.out.println("Country Code: " + rs.getString("Code") + ", Name: " + rs.getString("Name") + ", Continent: " + rs.getString("Continent") + ", Region: " + rs.getString("Region") + ", Population: " + rs.getString("Population"));
            }
        } catch (SQLException e) {
            System.out.println("Failed to retrieve countries: " + e.getMessage());
        }
    }

    //citis organised by population
    public static void printAllCities(Connection con) {
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT city.Name AS CityName, country.Name AS CountryName, city.District, city.Population FROM city INNER JOIN country ON city.CountryCode=country.Code ORDER BY city.Population desc")) {
            while (rs.next()) {
                System.out.println("Name: " + rs.getString("CityName") + ", Country: " + rs.getString("CountryName") + ", District: " + rs.getString("District") + ", Population: " + rs.getString("Population"));
            }
        } catch (SQLException e) {
            System.out.println("Failed to retrieve countries: " + e.getMessage());
        }
    }


    public static void printAllCapitals(Connection con) {
        int problemCount = 0;  // Counter for problematic records. Added as some fields seemed to have syntax errors which would throw exceptions

        try (Statement stmt = con.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT Capital AS Name, Name AS Country FROM country");

            while (rs.next()) {
                try {
                    String countryCode = rs.getString("Code");
                    String capital = rs.getString("Capital");
                    System.out.println("Country Code: " + countryCode + ", Capital: " + capital);
                } catch (SQLException e) {
                    problemCount++;  // Increment the problem counter
                    System.out.println("Problem with record: " + e.getMessage());
                }
            }

            if (problemCount > 0) {
                System.out.println("Number of problematic records: " + problemCount);
            }

        } catch (SQLException e) {
            System.out.println("Failed to retrieve capitals: " + e.getMessage());
        }
    }

}