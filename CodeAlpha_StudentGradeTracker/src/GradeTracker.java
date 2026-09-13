import java.util.ArrayList;

public class GradeTracker {

    private ArrayList<Student> studentList;

    public GradeTracker() {
        studentList = new ArrayList<>();
    }

    // ---------------- Add Student ----------------
    public void addStudent(String studentId, String studentName, double grade) {
        if (isStudentIdExists(studentId)) {
            throw new IllegalArgumentException("Student ID '" + studentId + "' already exists. ID must be unique.");
        }
        Student newStudent = new Student(studentId, studentName, grade);
        studentList.add(newStudent);
        System.out.println("Student added successfully: " + newStudent);
    }

    // Helper method to check duplicate IDs
    private boolean isStudentIdExists(String studentId) {
        for (Student s : studentList) {
            if (s.getStudentId().equalsIgnoreCase(studentId)) {
                return true;
            }
        }
        return false;
    }

    // ---------------- View All Students ----------------
    public void viewAllStudents() {
        if (studentList.isEmpty()) {
            System.out.println("No student records found.");
            return;
        }
        System.out.println("---------- All Students ----------");
        for (Student s : studentList) {
            System.out.println(s);
        }
        System.out.println("-----------------------------------");
    }

    // ---------------- Search Student ----------------
    public Student searchStudent(String studentId) {
        for (Student s : studentList) {
            if (s.getStudentId().equalsIgnoreCase(studentId)) {
                return s;
            }
        }
        return null;
    }

    // ---------------- Remove Student ----------------
    public boolean removeStudent(String studentId) {
        Student student = searchStudent(studentId);
        if (student != null) {
            studentList.remove(student);
            return true;
        }
        return false;
    }

    // ---------------- Average Grade ----------------
    public double calculateAverageGrade() {
        if (studentList.isEmpty()) {
            throw new IllegalStateException("No students available to calculate average.");
        }
        double total = 0;
        for (Student s : studentList) {
            total += s.getGrade();
        }
        return total / studentList.size();
    }

    // ---------------- Highest Grade ----------------
    public Student findHighestGrade() {
        if (studentList.isEmpty()) {
            throw new IllegalStateException("No students available to find highest grade.");
        }
        Student highest = studentList.get(0);
        for (Student s : studentList) {
            if (s.getGrade() > highest.getGrade()) {
                highest = s;
            }
        }
        return highest;
    }

    // ---------------- Lowest Grade ----------------
    public Student findLowestGrade() {
        if (studentList.isEmpty()) {
            throw new IllegalStateException("No students available to find lowest grade.");
        }
        Student lowest = studentList.get(0);
        for (Student s : studentList) {
            if (s.getGrade() < lowest.getGrade()) {
                lowest = s;
            }
        }
        return lowest;
    }

    // ---------------- Summary Report ----------------
    public void displaySummaryReport() {
        if (studentList.isEmpty()) {
            System.out.println("No student records found. Cannot generate summary report.");
            return;
        }

        double average = calculateAverageGrade();
        Student highest = findHighestGrade();
        Student lowest = findLowestGrade();

        System.out.println("========== Summary Report ==========");
        System.out.println("Total Students : " + studentList.size());
        System.out.printf("Average Grade  : %.2f%n", average);
        System.out.println("Highest Grade  : " + highest.getStudentName() + " - " + highest.getGrade());
        System.out.println("Lowest Grade   : " + lowest.getStudentName() + " - " + lowest.getGrade());
        System.out.println("=====================================");
    }

    // ---------------- Utility ----------------
    public int getTotalStudents() {
        return studentList.size();
    }
}
