public class Student {

    private String studentId;
    private String studentName;
    private double grade;

    // Constructor
    public Student(String studentId, String studentName, double grade) {
        setStudentId(studentId);
        setStudentName(studentName);
        setGrade(grade);
    }

    // Getters
    public String getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public double getGrade() {
        return grade;
    }

    // Setters with validation
    public void setStudentId(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID cannot be empty.");
        }
        this.studentId = studentId.trim();
    }

    public void setStudentName(String studentName) {
        if (studentName == null || studentName.trim().isEmpty()) {
            throw new IllegalArgumentException("Student name cannot be empty.");
        }
        this.studentName = studentName.trim();
    }

    public void setGrade(double grade) {
        if (grade < 0 || grade > 100) {
            throw new IllegalArgumentException("Grade must be between 0 and 100.");
        }
        this.grade = grade;
    }

    @Override
    public String toString() {
        return "ID: " + studentId + " | Name: " + studentName + " | Grade: " + grade;
    }
}
