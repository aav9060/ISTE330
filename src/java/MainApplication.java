//  ISTE 330 Deliverable 2 Data Layer
//  Meetings + Planning + Development Began 11/8/2024
//  Alex Vasilcoiu,  Noella Abraham, Sondos Sosak, Daniyah Wong, Jason Wu

import java.sql.*;
import java.util.Scanner;

public class MainApplication {

    private static final Scanner scanner = new Scanner(System.in);



    /* 
      NO USER LOGGED IN 
      OPENING MENU : APPLICATION OPTIONS 
    */
    
    
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
                        searchFacultyAbstract(connection);
                        break;
                        
                    case 4:
                        System.out.println("Exiting program. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } while (choice != 4);

        } catch (SQLException e) {
            System.out.println("Error: Unable to establish a database connection.");
            e.printStackTrace();
        }
    }
    
    

    private static void displayMainMenu() {
        System.out.println("\n--- Faculty Research Project ---");
        System.out.println("1 - Login");
        System.out.println("2 - Register");
        System.out.println("3 - Search Faculty Abstract Test");
        System.out.println("4 - Quit");
        System.out.print("Enter your choice: ");
    }
    
    
    
    
    /* 
      OPENING MENU
      OPTION 1 LOGIN 
    */
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
                } else if ("Faculty".equalsIgnoreCase(userType)) {
                    facultyMenu(connection, email);
                }else {
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
    
    
    
    
    
    /* 
      OPENING MENU
      OPTION 2 ADD
      SUB MENU : ADD A NEW USER 
      CHOOSE TYPE OF USER TO ADD
    */
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
                registerFaculty(connection);
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
    
    
    
    
    /* 
      OPENING MENU
      OPTION 2 ADD
      SUB MENU : ADD A NEW USER 
      OPTION 2 REGISTER STUDENT
    */
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
    
    
    /* 
      STUDENT LOGGED IN 
      USER MENU : STUDENT OPTIONS 
    */
        
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
    
    
    private static void studentMenu(Connection connection, String email) {
        int choice;
        do {
            displayStudentMenu();
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    searchFacultyInterests(connection, email);
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
    

    
    
    
    
    
    /* 
      STUDENT 
      VIEW OWN INTERESTS 
    */
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
    
    
    
    /* 
      STUDENT 
      ADD TO OWN INTERESTS 
    */
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
    
    
    
    /* 
      STUDENT 
      DELETE OWN INTERESTS 
    */
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



    /* 
      STUDENT 
      UPDATE OWN INTERESTS 
    */
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
    
    
    
    
    /* 
      PUBLIC LOGGED IN 
      USER MENU : PUBLIC OPTIONS 
    */

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
        } while (choice != 5);
    }
    
    
    
    
    
    
    /* 
      OPENING MENU
      OPTION 2 ADD
      SUB MENU : ADD A NEW USER 
      OPTION 3 REGISTER PUBLIC
    */
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






    /* 
      PUBLIC 
      VIEW OWN INTERESTS 
    */
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



    /* 
      PUBLIC 
      UPDATE OWN INTERESTS 
    */
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



    /* 
      PUBLIC 
      DELETE OWN INTERESTS 
    */
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


    
    
    
    /* 
      OPENING MENU
      OPTION 2 ADD
      SUB MENU : ADD A NEW USER 
      OPTION 1 REGISTER FACULTY MEMBER
    */
    private static void registerFaculty(Connection connection) {
        System.out.print("\nEnter Your Full Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Your Department: ");
        String department = scanner.nextLine();
        System.out.print("Enter Your Building: ");
        String building = scanner.nextLine();
        System.out.print("Enter Your Office Number: ");
        String office = scanner.nextLine();
        System.out.print("Enter Your Faculty Email: ");
        String email = scanner.nextLine();
        System.out.print("Enter Your Password: ");
        String password = scanner.nextLine();
        System.out.print("Enter Your Abstract ID (press enter for no abstracts): ");
        String abstractIdInput = scanner.nextLine();
        Integer abstractId = abstractIdInput.isEmpty() ? null : Integer.parseInt(abstractIdInput);
    
        try {
            connection.setAutoCommit(false);
    
            // Insert into account table
            String insertAccount = "INSERT INTO account (email, password, type) VALUES (?, ?, 'Faculty')";
            try (PreparedStatement pstmtAccount = connection.prepareStatement(insertAccount)) {
                pstmtAccount.setString(1, email);
                pstmtAccount.setString(2, password);
                pstmtAccount.executeUpdate();
            }
    
            // Insert into faculty table
            String insertFaculty = "INSERT INTO faculty (name, abstract_id, department, building, office, email, password) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmtFaculty = connection.prepareStatement(insertFaculty)) {
                pstmtFaculty.setString(1, name);
                pstmtFaculty.setObject(2, abstractId, java.sql.Types.INTEGER); // Handles null correctly
                pstmtFaculty.setString(3, department);
                pstmtFaculty.setString(4, building);
                pstmtFaculty.setString(5, office);
                pstmtFaculty.setString(6, email);
                pstmtFaculty.setString(7, password);
                pstmtFaculty.executeUpdate();
            }
    
            connection.commit();
            System.out.println("Faculty registration successful!");
        } catch (SQLException e) {
            System.out.println("Failed to register faculty. Error: " + e.getMessage());
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                System.out.println("Failed to rollback transaction. Error: " + rollbackEx.getMessage());
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                System.out.println("Failed to set auto commit. Error: " + ex.getMessage());
            }
        }
    }    
    
    
    
    
    /* 
      FACULTY MEMBER LOGGED IN 
      USER MENU : FACULTY MEMBER OPTIONS 
    */
    private static void displayFacultyMenu() {
        System.out.println("\n--- Faculty Menu ---");
        System.out.println("1 - Search Student Interests");
        System.out.println("2 - Insert Abstracts or Interests");
        System.out.println("3 - Update Abstracts or Interests");
        System.out.println("4 - Delete Abstracts or Interests");
        System.out.println("5 - See Own Interests");
        System.out.println("6 - See Own Abstracts");
        System.out.println("7 - Quit");
    }
    
    public static void facultyMenu(Connection connection, String email) {
        int choice;
        do {
            displayFacultyMenu();
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    searchStudentInterests(connection);
                    break;
                case 2:
                    insertFacultyAbstractsOrInterests(connection);
                    break;
                case 3:
                    updateFacultyAbstractsOrInterests(connection);
                    break;
                case 4:
                    deleteFacultyAbstractsOrInterests(connection);
                    break;
                case 5:
                    seeFacultyInterests(connection, email);
                    break;
                case 6:
                    seeFacultyAbstracts(connection, email);
                    break;                                      
                case 7:
                    System.out.println("Logging out and returning to main menu...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        } while (choice != 7);
    }





    /* 
      FACULTY MENU
      SUB MENU : INSERT ABSTRACTS OR INTERESTS MENU
    */
    private static void insertFacultyAbstractsOrInterests(Connection connection) {
        System.out.println("\nChoose an option:");
        System.out.println("1 - Insert an Abstract");
        System.out.println("2 - Insert an Interest");
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        switch (choice) {
            case 1:
                insertFacultyAbstract(connection);
                break;
            case 2:
                insertFacultyInterest(connection);
                break;
            default:
                System.out.println("Invalid option. Please try again.");
                break;
        }
    }
    
    
    
    /* 
      FACULTY MEMBER
      FACULTY MENU : OPTION 1
      SUB MENU : INSERT ABSTRACTS OR INTERESTS 
      OPTION 1 INSERT ABSTRACTS
    */
    private static void insertFacultyAbstract(Connection connection) {
        System.out.print("Enter title: ");
        String title = scanner.nextLine();
        System.out.print("Enter abstract: ");
        String abstractText = scanner.nextLine();

        String sql = "INSERT INTO faculty_abstract (title, abstract) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, title);
            statement.setString(2, abstractText);
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("A new abstract was inserted successfully!");
            }
        } catch (SQLException e) {
            System.out.println("Failed to insert the abstract.");
            e.printStackTrace();
        }
    }



    /* 
      FACULTY MEMBER
      FACULTY MENU : OPTION 1
      SUB MENU : INSERT ABSTRACTS OR INTERESTS 
      OPTION 2 INSERT INTERESTS
    */
    private static void insertFacultyInterest(Connection connection) {
        System.out.print("Enter interest: ");
        String interest = scanner.nextLine();

        String sql = "INSERT INTO interests (interest) VALUES (?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, interest);
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("A new interest was inserted successfully!");
            }
        } catch (SQLException e) {
            System.out.println("Failed to insert the interest.");
            e.printStackTrace();
        }
    }


    /* 
      FACULTY MEMBER
      FACULTY MENU : OPTION 1
      SUB MENU : UPDATE ABSTRACTS OR INTERESTS 
    */
    private static void updateFacultyAbstractsOrInterests(Connection connection) {
        System.out.println("\nChoose an option:");
        System.out.println("1 - Update an Abstract");
        System.out.println("2 - Update an Interest");
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        switch (choice) {
            case 1:
                updateFacultyAbstract(connection);
                break;
            case 2:
                updateFacultyInterest(connection);
                break;
            default:
                System.out.println("Invalid option. Please try again.");
                break;
        }
    }


    /* 
      FACULTY MEMBER
      FACULTY MENU : OPTION 2
      SUB MENU : UPDATE ABSTRACTS OR INTERESTS 
      OPTION 1 UPDATE ABSTRACTS
    */
    private static void updateFacultyAbstract(Connection connection) {
        System.out.print("Enter Abstract ID: ");
        int abstractId = scanner.nextInt();
        scanner.nextLine(); // consume newline
        System.out.print("Enter new title: ");
        String title = scanner.nextLine();
        System.out.print("Enter new abstract: ");
        String abstractText = scanner.nextLine();

        String sql = "UPDATE faculty_abstract SET title = ?, abstract = ? WHERE abstract_ID = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, title);
            statement.setString(2, abstractText);
            statement.setInt(3, abstractId);
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Abstract updated successfully!");
            } else {
                System.out.println("No abstract found with the specified ID.");
            }
        } catch (SQLException e) {
            System.out.println("Failed to update the abstract.");
            e.printStackTrace();
        }
    }



    /* 
      FACULTY MEMBER
      FACULTY MENU : OPTION 2
      SUB MENU : UPDATE ABSTRACTS OR INTERESTS 
      OPTION 2 UPDATE INTERESTS
    */
    private static void updateFacultyInterest(Connection connection) {
        System.out.print("Enter Interest ID: ");
        int interestId = scanner.nextInt();
        scanner.nextLine(); // consume newline
        System.out.print("Enter new interest: ");
        String interest = scanner.nextLine();

        String sql = "UPDATE interests SET interest = ? WHERE interest_ID = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, interest);
            statement.setInt(2, interestId);
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Interest updated successfully!");
            } else {
                System.out.println("No interest found with the specified ID.");
            }
        } catch (SQLException e) {
            System.out.println("Failed to update the interest.");
            e.printStackTrace();
        }
    }


    /* 
      FACULTY MEMBER
      FACULTY MENU : OPTION 3
      SUB MENU : DELETE ABSTRACTS OR INTERESTS
    */
    private static void deleteFacultyAbstractsOrInterests(Connection connection) {
        System.out.println("\nChoose an option:");
        System.out.println("1 - Delete an Abstract");
        System.out.println("2 - Delete an Interest");
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        switch (choice) {
            case 1:
                deleteFacultyAbstract(connection);
                break;
            case 2:
                deleteFacultyInterest(connection);
                break;
            default:
                System.out.println("Invalid option. Please try again.");
                break;
        }
    }


    /* 
      FACULTY MEMBER
      FACULTY MENU : OPTION 3
      SUB MENU : DELETE ABSTRACTS OR INTERESTS 
      OPTION 1 DELETE ABSTRACTS
    */
    private static void deleteFacultyAbstract(Connection connection) {
        System.out.print("Enter Abstract ID: ");
        int abstractId = scanner.nextInt();

        String sql = "DELETE FROM faculty_abstract WHERE abstract_ID = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, abstractId);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Abstract deleted successfully!");
            } else {
                System.out.println("No abstract found with the specified ID.");
            }
        } catch (SQLException e) {
            System.out.println("Failed to delete the abstract.");
            e.printStackTrace();
        }
    }



    /* 
      FACULTY MEMBER
      FACULTY MENU : OPTION 3
      SUB MENU : DELETE ABSTRACTS OR INTERESTS 
      OPTION 2 DELETE INTERESTS
    */
    private static void deleteFacultyInterest(Connection connection) {
        System.out.print("Enter Interest ID: ");
        int interestId = scanner.nextInt();

        String sql = "DELETE FROM interests WHERE interest_ID = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, interestId);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Interest deleted successfully!");
            } else {
                System.out.println("No interest found with the specified ID.");
            }
        } catch (SQLException e) {
            System.out.println("Failed to delete the interest.");
            e.printStackTrace();
        }
    }




    /* 
      FACULTY MEMBER
      FACULTY MENU : OPTION 4
      VIEW OWN INTERESTS
    */
    private static void seeFacultyInterests(Connection connection, String email) {
        String sql = "SELECT GROUP_CONCAT(interests.interest SEPARATOR ' | ') AS interests_list " +
                     "FROM interests " +
                     "JOIN faculty_interests ON interests.interest_ID = faculty_interests.interest_ID " +
                     "JOIN faculty ON faculty.faculty_id = faculty_interests.faculty_ID " +
                     "WHERE faculty.email = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String interestsList = resultSet.getString("interests_list");
                if (interestsList == null || interestsList.isEmpty()) {
                    System.out.println("No interests found for this faculty member.");
                } else {
                    System.out.println("Interests: " + interestsList);
                }
            } else {
                System.out.println("No interests found for this faculty member.");
            }
        } catch (SQLException e) {
            System.out.println("Failed to retrieve interests.");
            e.printStackTrace();
        }
    }



    /* 
      FACULTY MEMBER
      FACULTY MENU : OPTION 5
      VIEW OWN ABSTRACTS
    */
    private static void seeFacultyAbstracts(Connection connection, String email) {
        System.out.println("\n--- View Your Abstracts ---");
        String sql = "SELECT faculty_abstract.abstract_ID, title, abstract " +
                     "FROM faculty_abstract " +
                     "JOIN faculty ON faculty.abstract_id = faculty_abstract.abstract_ID " +
                     "WHERE faculty.email = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.isBeforeFirst()) {
                System.out.println("No abstracts found.");
            } else {
                while (resultSet.next()) {
                    int abstractId = resultSet.getInt("abstract_ID");
                    String title = resultSet.getString("title");
                    String abstractText = resultSet.getString("abstract");
                    System.out.println("Abstract ID: " + abstractId);
                    System.out.println("Title: " + title);
                    System.out.println("Abstract: " + abstractText);
                    System.out.println("-------------------------------");
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to retrieve abstracts: " + e.getMessage());
            e.printStackTrace();
        }
    }                 
}





   /* END OF USER MENUS, START OF SEARCH MATCH INTERESTS FUNCTIONALITY */




    /* 
      PUBLIC 
      SEARCH FOR FACULTY MEMBERS WITH SPECIFIC INTERESTS
      INPUT INTERESTS OUTPUT FACULTY MEMBERS LIST [NAME, EMAIL]
    */
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




    /* 
      STUDENT 
      SEARCH FOR FACULTY MEMBERS WITH SPECIFIC INTERESTS
      INPUT INTERESTS OUTPUT FACULTY MEMBERS LIST [NAME, BUILDING, OFFICE, EMAIL, COMMON INTERESTS]
    */
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




    /* 
      OPENING MENU OPTION 3
      SEARCH FACULTY ABSTRACTS WITH TERMS
      INPUT SEARCH TERM OUTPUT FACULTY MEMBERS WHO HAVE RELATED ABSTRACT(S) LIST [NAME]
    */
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





    /* 
      FACULTY 
      SEARCH FOR STUDENTS WITH SPECIFIC INTERESTS
      INPUT INTERESTS OUTPUT STUDENTS LIST [NAME, EMAIL, PHONE]
    */
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





    /* 
      DISPLAY ALL INTERESTS
    */
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
