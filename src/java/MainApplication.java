// Created on 11/17/2024

import java.sql.*;
import java.util.Scanner;

public class MainApplication {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/faculty_research_group5", "root", "student")) {

            int choice;
            do {
                displayMainMenu();
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        login(connection);
                        break;
                    case 2:
                        register(connection);
                        break;
                    case 3:
                        searchFacultyInterests(connection, "cleclerc@rit.edu");
                        break;
                    case 4:
                        System.out.println("Exiting program. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } while (choice != 4);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void displayMainMenu() {
        System.out.println("\n--- Faculty Research Project ---");
        System.out.println("1 - Login");
        System.out.println("2 - Register");
        System.out.println("3 - Search Faculty Interests Test");
        System.out.println("4 - Quit");
        System.out.print("Enter your choice: ");
    }

    private static void login(Connection connection) {
        System.out.print("\nEnter Your Email: ");
        String email = scanner.nextLine();
        System.out.print("Enter Your Password: ");
        String password = scanner.nextLine();

        String sql = "SELECT * FROM account WHERE email = ? AND password = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String userType = rs.getString("type");
                System.out.println("Login successful! Welcome, " + userType + ".");

                if ("Student".equalsIgnoreCase(userType)) {
                    studentMenu(connection, email);
                } else {
                    System.out.println("Other user types are not yet implemented.");
                }
            } else {
                System.out.println("Invalid email or password. Please try again.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void register(Connection connection) {
        System.out.println("\n--- Registration ---");
        System.out.println("1 - Faculty");
        System.out.println("2 - Student");
        System.out.println("3 - Public User");
        System.out.print("Choose user type: ");
        int userType = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (userType) {
            case 1:
                System.out.println("Faculty registration is not yet implemented.");
                break;
            case 2:
                registerStudent(connection);
                break;
            case 3:
                System.out.println("Public user registration is not yet implemented.");
                break;
            default:
                System.out.println("Invalid user type. Returning to main menu.");
        }
    }

    private static void registerStudent(Connection connection) {
        System.out.print("\nEnter Your Full Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Your Address: ");
        String address = scanner.nextLine();
        System.out.print("Enter Your Phone Number: ");
        String phone = scanner.nextLine();
        System.out.print("Enter Your Student Email: ");
        String email = scanner.nextLine();
        System.out.print("Enter Your Password: ");
        String password = scanner.nextLine();
        System.out.print("Enter Your Major: ");
        String major = scanner.nextLine();
        System.out.print("Enter Your Year: ");
        String year = scanner.nextLine();

        try {
            connection.setAutoCommit(false);

            String insertAccount = "INSERT INTO account (email, password, type) VALUES (?, ?, 'Student')";
            try (PreparedStatement pstmt = connection.prepareStatement(insertAccount)) {
                pstmt.setString(1, email);
                pstmt.setString(2, password);
                pstmt.executeUpdate();
            }

            String insertStudent = "INSERT INTO students (name, address, phone, email, major, year) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(insertStudent)) {
                pstmt.setString(1, name);
                pstmt.setString(2, address);
                pstmt.setString(3, phone);
                pstmt.setString(4, email);
                pstmt.setString(5, major);
                pstmt.setString(6, year);
                pstmt.executeUpdate();
            }

            connection.commit();
            System.out.println("Student registration successful!");
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void studentMenu(Connection connection, String email) {
        int choice;
        do {
            displayStudentMenu();
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("Common Faculty Interests With Students Has Not Been Implemented Yet");
                    break;
                case 2:
                    viewOwnStudentInterests(connection, email);
                    break;
                case 3:
                    addStudentInterest(connection, email);
                    break;
                case 4:
                    deleteStudentInterest(connection, email);
                    break;
                case 5:
                    updateStudentInterest(connection, email);
                    break;
                case 6:
                    System.out.println("Returning to main menu...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 6);
    }

    private static void displayStudentMenu() {
        System.out.println("\n--- Student Menu ---");
        System.out.println("1 - Search Faculty Interests");
        System.out.println("2 - View Own Interests");
        System.out.println("3 - Add Interests");
        System.out.println("4 - Delete Interests");
        System.out.println("5 - Update Interests");
        System.out.println("6 - Quit");
        System.out.print("Enter your choice: ");
    }

    private static void viewOwnStudentInterests(Connection connection, String email) {
        String sql = "SELECT i.interest FROM student_interests si "
                + "JOIN interests i ON si.interest_ID = i.interest_ID "
                + "WHERE si.student_id = (SELECT student_id FROM students WHERE email = ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            System.out.print("\nYour Interests: ");
            boolean hasInterests = false;
            while (rs.next()) {
                if (hasInterests) {
                    System.out.print(" | ");
                }
                System.out.print(rs.getString("interest"));
                hasInterests = true;
            }
            if (!hasInterests) {
                System.out.println("No interests found.");
            } else {
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void addStudentInterest(Connection connection, String email) {
        while (true) {
            System.out.print("Enter the Interest ID to Add (or type '?' to see available interests): ");
            String input = scanner.nextLine();

            if ("?".equals(input)) {
                displayAllInterests(connection);
            } else {
                try {
                    int interestId = Integer.parseInt(input);
                    String sql = "INSERT INTO student_interests (student_id, interest_ID) "
                            + "VALUES ((SELECT student_id FROM students WHERE email = ?), ?)";
                    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                        pstmt.setString(1, email);
                        pstmt.setInt(2, interestId);
                        int rowsAffected = pstmt.executeUpdate();

                        if (rowsAffected > 0) {
                            System.out.println("Interest added successfully!");
                        } else {
                            System.out.println("Failed to add interest. Please try again.");
                        }
                    }
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a valid Interest ID.");
                } catch (SQLException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

    private static void deleteStudentInterest(Connection connection, String email) {
        System.out.print("Enter the Interest ID to Delete: ");
        int interestId = scanner.nextInt();
        scanner.nextLine();

        String sql = "DELETE FROM student_interests "
                + "WHERE student_id = (SELECT student_id FROM students WHERE email = ?) AND interest_ID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setInt(2, interestId);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Interest deleted successfully!");
            } else {
                System.out.println("No matching interest found to delete.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateStudentInterest(Connection connection, String email) {
        System.out.print("Enter the Old Interest ID to Update: ");
        int oldInterestId = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter the New Interest ID: ");
        int newInterestId = scanner.nextInt();
        scanner.nextLine();

        String sql = "UPDATE student_interests SET interest_ID = ? "
                + "WHERE student_id = (SELECT student_id FROM students WHERE email = ?) AND interest_ID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, newInterestId);
            pstmt.setString(2, email);
            pstmt.setInt(3, oldInterestId);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Interest updated successfully!");
            } else {
                System.out.println("No matching interest found to update.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void displayAllInterests(Connection connection) {
        String sql = "SELECT interest_ID, interest FROM interests";
        try (PreparedStatement pstmt = connection.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

            System.out.print("\nAvailable Interests: ");
            boolean hasInterests = false;
            while (rs.next()) {
                if (hasInterests) {
                    System.out.print(" | ");
                }
                System.out.print(rs.getInt("interest_ID") + ": " + rs.getString("interest"));
                hasInterests = true;
            }
            if (!hasInterests) {
                System.out.println("No interests found.");
            } else {
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void searchFacultyInterests(Connection connection, String email){
        String sql = """
            SELECT 
                f.name AS faculty_name,
                f.building AS building_number,
                f.office AS office_number,
                f.email AS faculty_email,
            GROUP_CONCAT(i.interest ORDER BY i.interest) AS common_interests
            FROM faculty f
            JOIN faculty_interests fi USING (faculty_id)
            JOIN interests i USING (interest_id)
            JOIN faculty_abstract fa USING (abstract_id)
            JOIN student_interests si USING (interest_id)
            JOIN students s USING (student_id)
            WHERE s.email = ? AND fa.abstract LIKE CONCAT('%', i.interest, '%')
            GROUP BY f.faculty_id, f.name, f.building, f.office, f.email
            ORDER BY f.name;
            """;
                    

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);  
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("No faculty found with common interests for this student.");
                    return;
                }
                do {
                    String facultyName = rs.getString("faculty_name");
                    String buildingNumber = rs.getString("building_number");
                    String officeNumber = rs.getString("office_number");
                    String facultyEmail = rs.getString("faculty_email");
                    String commonInterests = rs.getString("common_interests");
                    
                    System.out.println("Faculty Name: " + facultyName);
                    System.out.println("Building Number: " + buildingNumber);
                    System.out.println("Office Number: " + officeNumber);
                    System.out.println("Faculty Email: " + facultyEmail);
                    System.out.println("Common Interests: " + commonInterests);
                    System.out.println("----------------------------------------");
                } while (rs.next());
                
            } catch (SQLException e) {
                System.out.println("Error executing query: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("Error preparing or executing the statement: " + e.getMessage());
        }
    }
}

