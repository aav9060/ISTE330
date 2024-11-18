// Created on 11/17/2024

import java.sql.*;
import java.util.Scanner;

public class MainApplication {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/faculty_research_group5", "root", "password")) {

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
                        searchFacultyAbstract(connection, "Jython");
                        break;
                    case 5:
                        searchStudentInterests(connection);
                        break;
                    case 6:
                        System.out.println("Exiting program. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } while (choice != 6);

        } catch (SQLException e) {
            System.out.println("Error: Unable to establish a database connection.");
            e.printStackTrace();
        }
    }
    private static void displayMainMenu() {
        System.out.println("\n--- Faculty Research Project ---");
        System.out.println("1 - Login");
        System.out.println("2 - Register");
        System.out.println("3 - Search Faculty Interests Test");
        System.out.println("4 - Search Faculty Abstract Test");
        System.out.println("5 - Search Student Interests");
        System.out.println("6 - Quit");
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
                } else if ("Public".equalsIgnoreCase(userType)) {
                    publicMenu(connection, email);
                } else {
                    System.out.println("Other user type is not yet implemented.");
                }
            } else {
                System.out.println("Invalid email or password. Please try again.");
            }
        } catch (SQLException e) {
            System.out.println("Error during login.");
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
                registerPublic(connection);
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
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.println("Common Faculty Interests with Students is not yet implemented.");
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
            System.out.println("Error fetching student interests.");
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
                    System.out.println("Error adding interest.");
                    e.printStackTrace();
                    break;
                }
            }
        }
    }
    private static void deleteStudentInterest(Connection connection, String email) {
        System.out.print("Enter the Interest ID to Delete: ");
        int interestId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

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
            System.out.println("Error deleting interest.");
            e.printStackTrace();
        }
    }

    private static void updateStudentInterest(Connection connection, String email) {
        System.out.print("Enter the Old Interest ID to Update: ");
        int oldInterestId = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        System.out.print("Enter the New Interest ID: ");
        int newInterestId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

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
            System.out.println("Error updating interest.");
            e.printStackTrace();
        }
    }

    private static void displayPublicMenu() {
        System.out.println("\n--- Public Menu ---");
        System.out.println("1 - Search for Experts on Interest");
        System.out.println("2 - View Own Interests");
        System.out.println("3 - Update Interest");
        System.out.println("4 - Delete Interest");
        System.out.println("5 - Quit");
        System.out.print("Enter your choice: ");
    }

    private static void publicMenu(Connection connection, String email) {
        int choice;
        do {
            displayPublicMenu();
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    searchForInterest(connection);
                    break;
                case 2:
                    viewSelfPublicInterest(connection, email);
                    break;
                case 3:
                    updatePublicInterest(connection, email);
                    break;
                case 4:
                    deletePublicInterest(connection, email);
                    break;
                case 5:
                    System.out.println("Returning to main menu...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 4);
    }

    private static void registerPublic(Connection connection) {
        System.out.print("\nEnter Your Name or Your Business's Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Your Address: ");
        String address = scanner.nextLine();
        System.out.print("Enter Your Email: ");
        String email = scanner.nextLine();
        System.out.print("Enter Your Password: ");
        String password = scanner.nextLine();

        try {
            connection.setAutoCommit(false);

            String insertAccount = "INSERT INTO account (email, password, type) VALUES (?, ?, 'Public')";
            try (PreparedStatement pstmt = connection.prepareStatement(insertAccount)) {
                pstmt.setString(1, email);
                pstmt.setString(2, password);
                pstmt.executeUpdate();
            }

            String insertPublic = "INSERT INTO public (name, address, email) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(insertPublic)) {
                pstmt.setString(1, name);
                pstmt.setString(2, address);
                pstmt.setString(3, email);
                pstmt.executeUpdate();
            }

            connection.commit();
            System.out.println("Public user registration successful!");
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            System.out.println("Error during registration.");
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void viewSelfPublicInterest(Connection connection, String email) {
        String sql = "SELECT i.interest FROM public p "
                + "JOIN interests i ON p.interest_ID = i.interest_ID "
                + "WHERE p.public_id = (SELECT public_id FROM public WHERE email = ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            System.out.print("\nYour Interest: ");
            boolean hasInterests = false;
            while (rs.next()) {
                if (hasInterests) {
                    System.out.print(", ");
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
            System.out.println("Error fetching public interests.");
            e.printStackTrace();
        }
    }

    private static void updatePublicInterest(Connection connection, String email) {
        System.out.print("Enter new Interest ID or '?' to see the list of interests: ");
        String interestId = scanner.nextLine();

        if ("?".equals(interestId)) {
            displayAllInterests(connection);
        } else {
            try {
                int id = Integer.parseInt(interestId);
                String sql = "UPDATE public SET interest_ID = ? "
                        + "WHERE public_id = (SELECT public_id FROM public WHERE email = ?)";
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setInt(1, id);
                    pstmt.setString(2, email);
                    int rowsAffected = pstmt.executeUpdate();

                    if (rowsAffected > 0) {
                        System.out.println("Interest updated successfully!");
                    } else {
                        System.out.println("No matching interest found to update.");
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid Interest ID.");
            } catch (SQLException e) {
                System.out.println("Error updating public interest.");
                e.printStackTrace();
            }
        }
    }

    private static void deletePublicInterest(Connection connection, String email) {
        String sql = "UPDATE public SET interest_ID = NULL "
                + "WHERE public_id = (SELECT public_id FROM public WHERE email = ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Interest deleted successfully!");
            } else {
                System.out.println("No matching interest found to delete.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting public interest.");
            e.printStackTrace();
        }
    }


    private static void searchForInterest(Connection connection) {
        displayAllInterests(connection);
        System.out.print("Enter the Interest ID to search: ");
        int interestId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        String sql = "SELECT f.name, f.email FROM faculty_interests fi "
                + "JOIN faculty f ON fi.faculty_id = f.faculty_id "
                + "WHERE fi.interest_ID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, interestId);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\nExperts on this Interest:");
            boolean found = false;
            while (rs.next()) {
                String name = rs.getString("name");
                String email = rs.getString("email");
                System.out.println("Name: " + name + ", Email: " + email);
                found = true;
            }
            if (!found) {
                System.out.println("No experts found for this interest.");
            }
        } catch (SQLException e) {
            System.out.println("Error searching for experts.");
            e.printStackTrace();
        }
    }

    private static void searchFacultyInterests(Connection connection, String email) {
        String sql = 
            "SELECT " +
            "f.name AS faculty_name, " +
            "f.building AS building_number, " +
            "f.office AS office_number, " +
            "f.email AS faculty_email, " +
            "GROUP_CONCAT(i.interest ORDER BY i.interest) AS common_interests " +
            "FROM faculty f " +
            "JOIN faculty_interests fi USING (faculty_id) " +
            "JOIN interests i USING (interest_id) " +
            "JOIN faculty_abstract fa USING (abstract_id) " +
            "JOIN student_interests si USING (interest_id) " +
            "JOIN students s USING (student_id) " +
            "WHERE s.email = ? AND fa.abstract LIKE CONCAT('%', i.interest, '%') " +
            "GROUP BY f.faculty_id, f.name, f.building, f.office, f.email " +
            "ORDER BY f.name";

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
            }
        } catch (SQLException e) {
            System.out.println("Error fetching faculty interests.");
            e.printStackTrace();
        }
    }

    private static void searchFacultyAbstract(Connection connection, String searchTerm) {
        String sql = 
            "SELECT " +
            "f.name AS faculty_name " +
            "FROM faculty f " +
            "JOIN faculty_abstract fa USING (abstract_id) " +
            "WHERE fa.abstract LIKE ? " +
            "ORDER BY f.name";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + searchTerm + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("No faculty abstracts found matching the search term.");
                    return;
                }
                System.out.println("\nFaculty Members Matching Abstract Search Term:");
                do {
                    String facultyName = rs.getString("faculty_name");
                    System.out.println("Faculty Name: " + facultyName);
                } while (rs.next());
            }
        } catch (SQLException e) {
            System.out.println("Error searching faculty abstracts.");
            e.printStackTrace();
        }
    }

    private static void searchStudentInterests(Connection connection) {
        String sql = "SELECT interest_ID, interest FROM interests";
        try (PreparedStatement pstmt = connection.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            System.out.println("\nAvailable Interests:");
            while (rs.next()) {
                int id = rs.getInt("interest_ID");
                String interest = rs.getString("interest");
                System.out.println(id + ": " + interest);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching interests.");
            e.printStackTrace();
        }

        System.out.print("Enter Interest ID to search for students: ");
        int interestId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        String sql1 = 
            "SELECT s.name, s.email, s.phone " +
            "FROM students s " +
            "JOIN student_interests si ON s.student_id = si.student_id " +
            "WHERE si.interest_ID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql1)) {
            pstmt.setInt(1, interestId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("No students found with the selected interest.");
                    return;
                }
                System.out.println("\nStudents Matching Interest:");
                do {
                    String name = rs.getString("name");
                    String email = rs.getString("email");
                    String phone = rs.getString("phone");
                    System.out.println("Name: " + name + ", Email: " + email + ", Phone: " + phone);
                } while (rs.next());
            }
        } catch (SQLException e) {
            System.out.println("Error fetching students by interest.");
            e.printStackTrace();
        }
    }

    private static void displayAllInterests(Connection connection) {
        String sql = "SELECT interest_ID, interest FROM interests";
        try (PreparedStatement pstmt = connection.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            System.out.println("\nAvailable Interests:");
            while (rs.next()) {
                int id = rs.getInt("interest_ID");
                String interest = rs.getString("interest");
                System.out.println(id + ": " + interest);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching interests.");
            e.printStackTrace();
        }
    }

}
