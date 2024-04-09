import java.sql.*;
import java.util.Scanner;

class User {
    protected String name;
    protected String id;

    public User(String name, String id) {
        this.name = name;
        this.id = id;
    }
}

class Admin {
    protected String name;
    protected String id;

    public Admin(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public GatePass issueGatePass(User user, String purpose) {
        return new GatePass(user, purpose);
    }
}

class Employee extends User {
    public Employee(String name, String id) {
        super(name, id);
    }
}

class Student extends User {
    private String course;
    private int year;
    private String section; // New field for section

    public Student(String name, String id, int year, String course, String section) {
        super(name, id);
        this.course = course;
        this.year = year;
        this.section = section;
    }

    // Getter and setter for the section field (optional, but recommended)
    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    @Override
    public String toString() {
        return "Name: " + name + "\n" +
                "ID: " + id + "\n" +
                "Course: " + course + "\n" +
                "Year: " + year + "\n" +
                "Section: " + section + "\n"; // Include the section in the toString() method
    }
}

class GatePass {
    private User user;
    private String purpose;

    public GatePass(User user, String purpose) {
        this.user = user;
        this.purpose = purpose;
    }

    @Override
    public String toString() {
        return "Gate Pass Details:\n" +
                "User: " + user.name + " (ID: " + user.id + ")\n" +
                "Purpose: " + purpose + "\n";
    }
}

public class GatePassApplication {
    private static Connection con;

    // Method to establish database connection
    private static void connectDB() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/project";
        String user = "root";
        String password = "Abhilesh13";
        con = DriverManager.getConnection(url, user, password);
    }

    // Method to retrieve user details from the database based on the user ID
    private static User getUserDetails(String userId, String type) throws SQLException {
        String query;
        if (type.equals("Student")) {
            query = "SELECT * FROM Student WHERE id=?";
        } else {
            query = "SELECT * FROM EMP WHERE id=?";
        }
        PreparedStatement preparedStatement = con.prepareStatement(query);
        preparedStatement.setString(1, userId);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            String name = resultSet.getString("name");
            String id = resultSet.getString("id");
            if (type.equals("Student")) {
                int year = resultSet.getInt("year");
                String course = resultSet.getString("course");
                String section = resultSet.getString("section"); // Retrieve section from the database
                return new Student(name, id, year, course, section);
            }
            return new Employee(name, id);
        } else {
            return null;
        }
    }
    private static void addStudentToDB(String id, String name, int year, String course, String section) throws SQLException {
        String query = "INSERT INTO Student (id, name, year, course, section) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = con.prepareStatement(query);
        preparedStatement.setString(1, id);
        preparedStatement.setString(2, name);
        preparedStatement.setInt(3, year);
        preparedStatement.setString(4, course);
        preparedStatement.setString(5, section);
        preparedStatement.executeUpdate();
        System.out.println("User details added to the database.");
    }
    
    private static void addEmpToDB(String id, String name) throws SQLException {
        String query = "INSERT INTO Emp (id, name) VALUES (?, ?)";
        PreparedStatement preparedStatement = con.prepareStatement(query);
        preparedStatement.setString(1, id);
        preparedStatement.setString(2, name);
        preparedStatement.executeUpdate();
        System.out.println("User details added to the database.");
    }

    private static void deleteStudent(String id) throws SQLException {
        String query = "DELETE FROM Student WHERE id=?";
        PreparedStatement preparedStatement = con.prepareStatement(query);
        preparedStatement.setString(1, id);
        int rowsAffected = preparedStatement.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println("Student with ID " + id + " deleted from the database.");
        } else {
            System.out.println("Student with ID " + id + " not found in the database.");
        }
    }

    private static void deleteEmp(String id) throws SQLException {
        String query = "DELETE FROM Emp WHERE id=?";
        PreparedStatement preparedStatement = con.prepareStatement(query);
        preparedStatement.setString(1, id);
        int rowsAffected = preparedStatement.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println("Employee with ID " + id + " deleted from the database.");
        } else {
            System.out.println("Employee with ID " + id + " not found in the database.");
        }
    }

    private static void updateStudentCourse(String id, String newCourse) throws SQLException {
        String query = "UPDATE Student SET course=? WHERE id=?";
        PreparedStatement preparedStatement = con.prepareStatement(query);
        preparedStatement.setString(1, newCourse);
        preparedStatement.setString(2, id);
        int rowsAffected = preparedStatement.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println("Student with ID " + id + " course updated in the database.");
        } else {
            System.out.println("Student with ID " + id + " not found in the database.");
        }
    }

    private static void updateEmpName(String id, String newName) throws SQLException {
        String query = "UPDATE Emp SET name=? WHERE id=?";
        PreparedStatement preparedStatement = con.prepareStatement(query);
        preparedStatement.setString(1, newName);
        preparedStatement.setString(2, id);
        int rowsAffected = preparedStatement.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println("Employee with ID " + id + " name updated in the database.");
        } else {
            System.out.println("Employee with ID " + id + " not found in the database.");
        }
    }

    public static void main(String[] args) throws SQLException {
        try {
            connectDB(); // Establish database connection
        } catch (SQLException e) {
            System.out.println("Failed to connect to the database. Exiting...");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        //System.out.println("Welcome to GatePass Application!");
        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1. Issue Gate Pass");
            System.out.println("2. Create a new Student");
            System.out.println("3. Update Student Course");
            System.out.println("4. Create a new Employee");
            System.out.println("5. Update Employee Name");
            System.out.println("6. Delete Student");
            System.out.println("7. Delete Employee");
            System.out.println("0. Exit");

            System.out.println("Enter your choice:");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Clear the input buffer

            switch (choice) {
                case 1:
                System.out.println("\nEnter your ID:");
                String id = scanner.next();
                System.out.println("\nEnter your role:\n1. Student\n2. Employee\n");
                int role = scanner.nextInt();

                User user;
                try {
                    user = getUserDetails(id, (role == 1) ? "Student" : "Emp"); // Corrected the table name for Employee
                } catch (SQLException e) {
                    System.out.println("Error fetching user details. Please try again.");
                    e.printStackTrace();
                    continue;
                }

                if (user == null) {
                    System.out.println("Invalid user ID. Try again.");
                    continue;
                }

                scanner.nextLine(); // Clear the input buffer
                System.out.println("Enter the purpose of the gate pass:");
                String purpose = scanner.nextLine();

                Admin admin = new Admin("Admin Name", "1");
                GatePass gatePass = admin.issueGatePass(user, purpose);
                System.out.println("\nGate pass issued successfully:");
                System.out.println(gatePass);

                if (user instanceof Student) {
                    System.out.println("Student Details:");
                    System.out.println(user);
                }
                break;


                case 2:
    System.out.println("\nEnter Student ID:");
    String studentId = scanner.next();
    System.out.println("Enter Student Name:");
    String studentName = scanner.next();
    System.out.println("Enter Student Year:");
    int studentYear = scanner.nextInt();
    scanner.nextLine(); // Clear the input buffer
    System.out.println("Enter Student Course:");
    String studentCourse = scanner.nextLine();
    System.out.println("Enter Student Section:");
    String studentSection = scanner.nextLine(); // Capture the section input

    addStudentToDB(studentId, studentName, studentYear, studentCourse, studentSection); // Include section in the method call
    break;
                case 3:
                    System.out.println("\nEnter Student ID:");
                    String studentIdToUpdate = scanner.next();
                    scanner.nextLine(); // Clear the input buffer
                    System.out.println("Enter New Student Course:");
                    String newStudentCourse = scanner.nextLine();

                    updateStudentCourse(studentIdToUpdate, newStudentCourse);
                    break;

                case 4:
                    System.out.println("\nEnter Employee ID:");
                    String empId = scanner.next();
                    scanner.nextLine(); // Clear the input buffer
                    System.out.println("Enter Employee Name:");
                    String empName = scanner.nextLine();

                    addEmpToDB(empId, empName);
                    break;

                case 5:
                    System.out.println("\nEnter Employee ID:");
                    String empIdToUpdate = scanner.next();
                    scanner.nextLine(); // Clear the input buffer
                    System.out.println("Enter New Employee Name:");
                    String newEmpName = scanner.nextLine();

                    updateEmpName(empIdToUpdate, newEmpName);
                    break;

                case 6:
                    System.out.println("\nEnter Student ID to delete:");
                    String studentIdToDelete = scanner.next();

                    deleteStudent(studentIdToDelete);
                    break;

                case 7:
                    System.out.println("\nEnter Employee ID to delete:");
                    String empIdToDelete = scanner.next();

                    deleteEmp(empIdToDelete);
                    break;

                case 0:
                    // Exit the program
                    System.out.println("Exiting...");
                    try {
                        if (con != null) {
                            con.close(); // Close the database connection
                        }
                    } catch (SQLException e) {
                        System.out.println("Error closing the database connection.");
                    }
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
