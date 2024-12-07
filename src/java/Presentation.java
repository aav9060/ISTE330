//  ISTE 330 Presentation Layer
//  Alex Vasilcoiu,  Noella Abraham, Sondos Sosak, Daniyah Wong, Jason Wu

import java.util.Scanner;
import java.sql.*;

public class Presentation {
    private static final Scanner scanner = new Scanner(System.in);
	MainApplication dl = new MainApplication();
	String userName = new String();
	String password = new String();

	public Presentation(){

        // Prompting for username
        System.out.println("Please enter MySQL information");
        System.out.print("Username -> ");
        userName = scanner.nextLine();

        // Prompting for password
        System.out.print("Password (Default is 'student') -> ");
        String temp_pass = scanner.nextLine();

        // Default password check
        if (temp_pass.equals("")) {
            password = "student";
        } else {
            password = temp_pass;
        }
		
        if(dl.connect(userName, password)){
            int choice;
            do {
                displayMainMenu();
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        System.out.print("\nEnter Your Email: ");
                        String email = scanner.nextLine();
                        System.out.print("Enter Your Password: ");
                        String password = scanner.nextLine();
                        dl.login(email, password);
                        break;
                    case 2:
                        System.out.println("\n--- Registration ---");
                        System.out.println("1 - Faculty");
                        System.out.println("2 - Student");
                        System.out.println("3 - Public User");
                        System.out.print("Choose user type: ");
                        int userType = scanner.nextInt();
                        scanner.nextLine(); // Consume newline
                        switch (userType) {
                            case 1:
                                System.out.print("\nEnter Your Full Name: ");
                                String name = scanner.nextLine();
                                System.out.print("Enter Your Department: ");
                                String department = scanner.nextLine();
                                System.out.print("Enter Your Building: ");
                                String building = scanner.nextLine();
                                System.out.print("Enter Your Office Number: ");
                                String office = scanner.nextLine();
                                System.out.print("Enter Your Faculty Email: ");
                                email = scanner.nextLine();
                                System.out.print("Enter Your Password: ");
                                password = scanner.nextLine();
                                System.out.print("Enter Your Abstract ID (press enter for no abstracts): ");
                                String abstractIdInput = scanner.nextLine();
                                Integer abstractId = abstractIdInput.isEmpty() ? null : Integer.parseInt(abstractIdInput);
                                dl.registerFaculty(name, department, building, office, email, password, abstractId);
                                break;
                            case 2:
                                System.out.print("\nEnter Your Full Name: ");
                                name = scanner.nextLine();
                                System.out.print("Enter Your Address: ");
                                String address = scanner.nextLine();
                                System.out.print("Enter Your Phone Number: ");
                                String phone = scanner.nextLine();
                                System.out.print("Enter Your Student Email: ");
                                email = scanner.nextLine();
                                System.out.print("Enter Your Password: ");
                                password = scanner.nextLine();
                                System.out.print("Enter Your Major: ");
                                String major = scanner.nextLine();
                                System.out.print("Enter Your Year: ");
                                String year = scanner.nextLine();
                                dl.registerStudent(name, address, phone, email, password, major, year);
                                break;
                            case 3:
                                System.out.print("\nEnter Your Name or Your Business's Name: ");
                                name = scanner.nextLine();
                                System.out.print("Enter Your Address: ");
                                address = scanner.nextLine();
                                System.out.print("Enter Your Email: ");
                                email = scanner.nextLine();
                                System.out.print("Enter Your Password: ");
                                password = scanner.nextLine();
                                dl.registerPublic(name, address, email, password);
                                break;
                            default:
                                System.out.println("Invalid user type. Returning to main menu.");
                        }
                        break;
                    case 3:
                        System.out.println("Exiting program. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } while (choice != 3);
            dl.close();
        }else{
            // Connection fails
            System.out.println("No DB Connection");

        }
    }

    private static void displayMainMenu() {
        System.out.println("\n--- Faculty Research Project ---");
        System.out.println("1 - Login");
        System.out.println("2 - Register");
        System.out.println("3 - Quit");
        System.out.print("Enter your choice: ");
    }

    public static void main(String [] args){
	    System.out.println("Authors: Alex Vasilcoiu, Noella Abraham, Sondos Sosak, Daniyah Wong, Jason Wu");
		new Presentation();
	}
}
