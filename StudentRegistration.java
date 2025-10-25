import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class StudentRegistration extends JFrame implements ActionListener {

    // GUI Components
    private JTextField nameField, ageField, emailField, courseField;
    private JButton submitBtn, viewBtn, clearBtn;
    private Connection con;

    public StudentRegistration() {
        setTitle("Student Registration Form");
        setSize(400, 350);
        setLayout(new GridLayout(6, 2, 10, 10));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // GUI Labels and Fields
        add(new JLabel("Name:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("Age:"));
        ageField = new JTextField();
        add(ageField);

        add(new JLabel("Email:"));
        emailField = new JTextField();
        add(emailField);

        add(new JLabel("Course:"));
        courseField = new JTextField();
        add(courseField);

        submitBtn = new JButton("Submit");
        submitBtn.addActionListener(this);
        add(submitBtn);

        viewBtn = new JButton("View Students");
        viewBtn.addActionListener(this);
        add(viewBtn);

        clearBtn = new JButton("Clear Fields");
        clearBtn.addActionListener(this);
        add(clearBtn);

        // Connect to database
        connectDB();

        setVisible(true);
    }

    // Database Connection Method
    private void connectDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/studentdb", 
                "root", 
                "lggm2006"
            );
            System.out.println("✅ Database connected successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "❌ Database Connection Failed!\n" + e.getMessage());
        }
    }

    // Handle Button Actions
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitBtn) {
            registerStudent();
        } else if (e.getSource() == viewBtn) {
            displayStudents();
        } else if (e.getSource() == clearBtn) {
            clearFields();
        }
    }

    // Register a new student
    private void registerStudent() {
        try {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String course = courseField.getText().trim();
            int age = Integer.parseInt(ageField.getText().trim());

            if (name.isEmpty() || email.isEmpty() || course.isEmpty()) {
                JOptionPane.showMessageDialog(this, "⚠️ Please fill all fields!");
                return;
            }

            String query = "INSERT INTO students (name, age, email, course) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, name);
            ps.setInt(2, age);
            ps.setString(3, email);
            ps.setString(4, course);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "✅ Student registered successfully!");
            clearFields();
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "⚠️ Age must be a number!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "❌ Error inserting record: " + ex.getMessage());
        }
    }

    // Display all students
    private void displayStudents() {
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM students");

            StringBuilder data = new StringBuilder("📋 Registered Students:\n\n");
            while (rs.next()) {
                data.append("ID: ").append(rs.getInt("id"))
                    .append(", Name: ").append(rs.getString("name"))
                    .append(", Age: ").append(rs.getInt("age"))
                    .append(", Email: ").append(rs.getString("email"))
                    .append(", Course: ").append(rs.getString("course"))
                    .append("\n");
            }

            JOptionPane.showMessageDialog(this, data.toString());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "❌ Error fetching data: " + ex.getMessage());
        }
    }

    // Clear form fields
    private void clearFields() {
        nameField.setText("");
        ageField.setText("");
        emailField.setText("");
        courseField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StudentRegistration::new);
    }
}
