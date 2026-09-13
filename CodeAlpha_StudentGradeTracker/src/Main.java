import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

    private static GradeTracker gradeTracker = new GradeTracker();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            printMenu();
            int choice = readMenuChoice();

            switch (choice) {
                case 1:
                    handleAddStudent();
                    break;
                case 2:
                    gradeTracker.viewAllStudents();
                    break;
                case 3:
                    handleSearchStudent();
                    break;
                case 4:
                    handleRemoveStudent();
                    break;
                case 5:
                    handleAverageGrade();
                    break;
                case 6:
                    handleHighestGrade();
                    break;
                case 7:
                    handleLowestGrade();
                    break;
                case 8:
                    gradeTracker.displaySummaryReport();
                    break;
                case 9:
                    System.out.println("Exiting application. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 9.");
            }
            System.out.println();
        }

        scanner.close();
    }

    // ---------------- Menu Display ----------------
    private static void printMenu() {
        System.out.println("======= Student Grade Tracker =======");
        System.out.println("1. Add Student");
        System.out.println("2. View All Students");
        System.out.println("3. Search Student");
        System.out.println("4. Remove Student");
        System.out.println("5. Calculate Average Grade");
        System.out.println("6. Find Highest Grade");
        System.out.println("7. Find Lowest Grade");
        System.out.println("8. Display Summary Report");
        System.out.println("9. Exit");
        System.out.print("Enter your choice: ");
    }

    // ---------------- Safe Menu Input ----------------
    private static int readMenuChoice() {
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            return choice;
        } catch (NumberFormatException e) {
            return -1; // triggers "invalid choice" in switch default
        }
    }

    // ---------------- Add Student ----------------
    private static void handleAddStudent() {
        try {
            System.out.print("Enter Student ID: ");
            String id = scanner.nextLine();

            System.out.print("Enter Student Name: ");
            String name = scanner.nextLine();

            System.out.print("Enter Grade (0-100): ");
            double grade = Double.parseDouble(scanner.nextLine().trim());

            gradeTracker.addStudent(id, name, grade);

        } catch (NumberFormatException e) {
            System.out.println("Error: Grade must be a valid number.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ---------------- Search Student ----------------
    private static void handleSearchStudent() {
        System.out.print("Enter Student ID to search: ");
        String id = scanner.nextLine();

        Student result = gradeTracker.searchStudent(id);
        if (result != null) {
            System.out.println("Student found: " + result);
        } else {
            System.out.println("No student found with ID: " + id);
        }
    }

    // ---------------- Remove Student ----------------
    private static void handleRemoveStudent() {
        System.out.print("Enter Student ID to remove: ");
        String id = scanner.nextLine();

        boolean removed = gradeTracker.removeStudent(id);
        if (removed) {
            System.out.println("Student with ID '" + id + "' removed successfully.");
        } else {
            System.out.println("No student found with ID: " + id);
        }
    }

    // ---------------- Average Grade ----------------
    private static void handleAverageGrade() {
        try {
            double average = gradeTracker.calculateAverageGrade();
            System.out.printf("Average Grade: %.2f%n", average);
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ---------------- Highest Grade ----------------
    private static void handleHighestGrade() {
        try {
            Student highest = gradeTracker.findHighestGrade();
            System.out.println("Highest Grade: " + highest.getStudentName() + " - " + highest.getGrade());
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ---------------- Lowest Grade ----------------
    private static void handleLowestGrade() {
        try {
            Student lowest = gradeTracker.findLowestGrade();
            System.out.println("Lowest Grade: " + lowest.getStudentName() + " - " + lowest.getGrade());
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
