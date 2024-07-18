import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

class Student {
    private int rollNumber;
    private String name;
    private String course;
    private Map<String, Integer> subjectMarks;
    private Map<String, Double> subjectGPAs;

    public Student(int rollNumber, String name, String course) {
        this.rollNumber = rollNumber;
        this.name = name;
        this.course = course;
        this.subjectMarks = new HashMap<>();
        this.subjectGPAs = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public int getRollNumber() {
        return rollNumber;
    }

    public String getCourse() {
        return course;
    }

    public Map<String, Integer> getSubjectMarks() {
        return subjectMarks;
    }

    public void addSubjectMarks(String subject, int marks) {
        subjectMarks.put(subject, marks);
    }

    public int getSubjectMarks(String subject) {
        return subjectMarks.getOrDefault(subject, 0);
    }

    public void addSubjectGPA(String subject, double gpa) {
        subjectGPAs.put(subject, gpa);
    }

    public double getSubjectGPA(String subject) {
        return subjectGPAs.getOrDefault(subject, 0.0);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCourse(String course) {
        this.course = course;
    }
}

public class StudentManagementSystem extends JFrame {
    private static final String DATA_FILE_PATH = "student.txt";
    private List<Student> students;
    private JTextField nameTextField;
    private JTextField rollNumberTextField;
    private JTextField courseTextField;
    private JTextField searchRollNumberTextField;
    private JTextField searchNameTextField;
    private JButton searchButton;
    private JComboBox<String> subjectComboBox;
    private JTextField marksTextField;
    private JButton addSubjectButton;
    private JTextArea resultTextArea;
    private JFrame viewFrame;

    public StudentManagementSystem() {
        // Set up the main frame
        
        setTitle("Student Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        students = new ArrayList<>();
        loadDataFromFile();
        // Create and add GUI components to the frame
        JPanel inputPanel = new JPanel(new GridLayout(7, 2));
        inputPanel.add(new JLabel("Name:"));
        nameTextField = new JTextField();
        inputPanel.add(nameTextField);
        inputPanel.add(new JLabel("Roll Number:"));
        rollNumberTextField = new JTextField();
        inputPanel.add(rollNumberTextField);
        inputPanel.add(new JLabel("Course:"));
        courseTextField = new JTextField();
        inputPanel.add(courseTextField);
        inputPanel.add(new JLabel("Search Roll Number:"));
        searchRollNumberTextField = new JTextField();
        inputPanel.add(searchRollNumberTextField);

        inputPanel.add(new JLabel("Search Name:"));
        searchNameTextField = new JTextField();
        inputPanel.add(searchNameTextField);

        searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchStudent();
            }
        });
        inputPanel.add(searchButton);
       

        JButton addButton = new JButton("Add Student");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addStudent();
            }
        });
        JButton searchButton = new JButton("Search Student");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchStudent();
            }
        });

        JButton viewButton = new JButton("View Students");
        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewStudents();
            }
        });

        JButton updateButton = new JButton("Update Student");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateStudent();
            }
        });

        JButton deleteButton = new JButton("Delete Student");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteStudent();
            }
        });

        JButton calculateButton = new JButton("Calculate Statistics");
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateStatistics();
            }
        });

        JButton sortButton = new JButton("Sort Students by Marks");
        sortButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sortStudentsByMarks();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(calculateButton);
        buttonPanel.add(sortButton);

        resultTextArea = new JTextArea();
        resultTextArea.setEditable(false);

        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(new JScrollPane(resultTextArea), BorderLayout.SOUTH);
    }

    private void addSubject() {
        String subject = (String) subjectComboBox.getSelectedItem();
        int marks = Integer.parseInt(marksTextField.getText());
          saveDataToFile();
        if (!subject.isEmpty() && marks >= 0) {
            resultTextArea.append("Subject: " + subject + " Marks: " + marks + "\n");
        } else {
            JOptionPane.showMessageDialog(this, "Invalid subject or marks!");
        }

    }

    private void searchStudent() {
        int rollNumber = -1;
        String name = null;

        String rollNumberText = searchRollNumberTextField.getText();
        if (!rollNumberText.isEmpty()) {
            try {
                rollNumber = Integer.parseInt(rollNumberText);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid roll number!");
                return;
            }
        }

        String nameText = searchNameTextField.getText();
        if (!nameText.isEmpty()) {
            name = nameText;
        }

        if (rollNumber == -1 && name == null) {
            JOptionPane.showMessageDialog(this, "Please enter roll number or name for searching!");
            return;
        }

        Student searchStudent = null;

        if (rollNumber != -1) {
            for (Student student : students) {
                if (student.getRollNumber() == rollNumber) {
                    searchStudent = student;
                    break;
                }
            }
        } else if (name != null) {
            for (Student student : students) {
                if (student.getName().equalsIgnoreCase(name)) {
                    searchStudent = student;
                    break;
                }
            }
        }

        if (searchStudent != null) {
            displayStudentDetails(searchStudent);
        } else {
            JOptionPane.showMessageDialog(this, "Student not found!");
        }
    }

    private void displayStudentDetails(Student student) {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(student.getName()).append("\n");
        sb.append("Roll Number: ").append(student.getRollNumber()).append("\n");
        sb.append("Course: ").append(student.getCourse()).append("\n");
         double overallGPA = calculateOverallGPA(student);
    sb.append("Overall GPA: ").append(overallGPA).append("\n");
        JOptionPane.showMessageDialog(this, sb.toString(), "Student Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void addStudent() {
        int rollNumber = Integer.parseInt(rollNumberTextField.getText());

        // Check if student with the same roll number already exists
        for (Student student : students) {
            if (student.getRollNumber() == rollNumber) {
                JOptionPane.showMessageDialog(this, "Student with the same roll number already exists.");
                return;
            }
        }

        String name = nameTextField.getText();
        String course = courseTextField.getText();

        Student student = new Student(rollNumber, name, course);

        // Prompt for subject-wise marks
        int subjectCount = 5; // Assuming 5 subjects
        for (int i = 1; i <= subjectCount; i++) {
            String subject = JOptionPane.showInputDialog("Enter subject " + i + " name:");
            int marks = Integer.parseInt(JOptionPane.showInputDialog("Enter marks for subject " + i + ":"));
            student.addSubjectMarks(subject, marks);
        }

        students.add(student);
        saveDataToFile();
        nameTextField.setText("");
        rollNumberTextField.setText("");
        courseTextField.setText("");

        resultTextArea.append("Student added: " + student.getName() + "\n");

    }

    private void viewStudents() {
        if (viewFrame != null) {
            viewFrame.dispose();
        }

        viewFrame = new JFrame("Student Details");
        viewFrame.setSize(800, 600);
        viewFrame.setLocationRelativeTo(null);
        viewFrame.setLayout(new BorderLayout());

        JPanel studentPanel = new JPanel(new GridLayout(students.size(), 1));

        for (Student student : students) {
            JPanel panel = new JPanel(new GridLayout(0, 3));

            panel.add(new JLabel("Name:"));
            panel.add(new JLabel(student.getName()));
            panel.add(new JLabel(""));

            panel.add(new JLabel("Roll Number:"));
            panel.add(new JLabel(String.valueOf(student.getRollNumber())));
            panel.add(new JLabel(""));

            panel.add(new JLabel("Course:"));
            panel.add(new JLabel(student.getCourse()));
            panel.add(new JLabel(""));

            panel.add(new JLabel("Subject"));
            panel.add(new JLabel("Marks"));
            panel.add(new JLabel("GPA"));

            Map<String, Integer> subjectMarks = student.getSubjectMarks();
            for (Map.Entry<String, Integer> entry : subjectMarks.entrySet()) {
                String subject = entry.getKey();
                int marks = entry.getValue();
                double gpa = calculateGPA(marks);
                student.addSubjectGPA(subject, gpa);

                panel.add(new JLabel(subject));
                panel.add(new JLabel(String.valueOf(marks)));
                panel.add(new JLabel(String.valueOf(gpa)));
            }

            panel.add(new JLabel("Overall GPA:"));
            panel.add(new JLabel(String.valueOf(calculateOverallGPA(student))));
            panel.add(new JLabel(""));

            panel.setBorder(BorderFactory.createEtchedBorder());
            studentPanel.add(panel);
        }

        JScrollPane scrollPane = new JScrollPane(studentPanel);
        viewFrame.add(scrollPane, BorderLayout.CENTER);
        viewFrame.setVisible(true);
    }

    private double calculateGPA(int marks) {
        if (marks >= 90) {
            return 4.0;
        } else if (marks >= 80) {
            return 3.5;
        } else if (marks >= 70) {
            return 3.0;
        } else if (marks >= 60) {
            return 2.5;
        } else if (marks >= 50) {
            return 2.0;
        } else {
            return 0.0;
        }
    }

    private double calculateOverallGPA(Student student) {
        Map<String, Integer> subjectMarks = student.getSubjectMarks();
        int totalMarks = 0;
        int totalSubjects = subjectMarks.size();

        for (int marks : subjectMarks.values()) {
            totalMarks += marks;
        }

        double averageMarks = (double) totalMarks / totalSubjects;
        return calculateGPA((int) averageMarks);
    }

    private void updateStudent() {
        int rollNumber = Integer.parseInt(rollNumberTextField.getText());
        Student studentToUpdate = null;

        for (Student student : students) {
            if (student.getRollNumber() == rollNumber) {
                studentToUpdate = student;
                break;
            }
        }
        saveDataToFile();
        if (studentToUpdate != null) {
            String name = nameTextField.getText();
            String course = courseTextField.getText();

            studentToUpdate.setName(name);
            studentToUpdate.setCourse(course);

            nameTextField.setText("");
            rollNumberTextField.setText("");
            courseTextField.setText("");

            resultTextArea.append("Student updated: " + studentToUpdate.getName() + "\n");
        } else {
            resultTextArea.append("Student not found\n");
        }
    }

    private void deleteStudent() {
        int rollNumber = Integer.parseInt(rollNumberTextField.getText());
        Student studentToDelete = null;

        for (Student student : students) {
            if (student.getRollNumber() == rollNumber) {
                studentToDelete = student;
                break;
            }
        }

        if (studentToDelete != null) {
            students.remove(studentToDelete);

            nameTextField.setText("");
            rollNumberTextField.setText("");
            courseTextField.setText("");

            resultTextArea.append("Student deleted: " + studentToDelete.getName() + "\n");
        } else {
            resultTextArea.append("Student not found\n");
        }
         saveDataToFile();
    }

  private void calculateStatistics() {
    int totalStudents = students.size();
    int totalMarks = 0;
    double highestGPA = Double.MIN_VALUE;
    double lowestGPA = Double.MAX_VALUE;
    
    for (Student student : students) {
        Map<String, Integer> subjectMarks = student.getSubjectMarks();
        int totalSubjectMarks = 0;
        for (int marks : subjectMarks.values()) {
            totalSubjectMarks += marks;
             if (marks > highestGPA) {
            highestGPA = marks;
        }
        if (marks < lowestGPA) {
            lowestGPA = marks;
        }
        }
        double gpa = (double) totalSubjectMarks / subjectMarks.size();
        totalMarks += totalSubjectMarks;
       
    }

    double averageGPA = (double) totalMarks / (totalStudents * 5);
    double passPercentage = ((double) totalStudents / students.size()) * 100;

    // Create a new JFrame for displaying the statistics
    JFrame statisticsFrame = new JFrame("Statistics");
    JTextArea statisticsTextArea = new JTextArea();
    statisticsTextArea.setEditable(false);
    statisticsFrame.getContentPane().add(statisticsTextArea);

    // Append the statistics to the JTextArea
    statisticsTextArea.append("Total Students: " + totalStudents + "\n");
    statisticsTextArea.append("Total Marks: " + totalMarks + "\n");
    statisticsTextArea.append("Average Marks: " + averageGPA + "\n");
    statisticsTextArea.append("Highest Marks: " + highestGPA + "\n");
    statisticsTextArea.append("Lowest Marks: " + lowestGPA + "\n");
    statisticsTextArea.append("Pass Percentage: " + passPercentage + "%\n");

    // Set the size and make the JFrame visible
    statisticsFrame.setSize(300, 200);
    statisticsFrame.setLocationRelativeTo(this);
    statisticsFrame.setVisible(true);
}

    private void sortStudentsByMarks() {
        Collections.sort(students, new Comparator<Student>() {
            @Override
            public int compare(Student student1, Student student2) {
                int totalMarks1 = calculateTotalMarks(student1);
                int totalMarks2 = calculateTotalMarks(student2);
                return Integer.compare(totalMarks2, totalMarks1);
            }
        });

        viewStudents();
    }

    private int calculateTotalMarks(Student student) {
        int totalMarks = 0;
        Map<String, Integer> subjectMarks = student.getSubjectMarks();
        for (int marks : subjectMarks.values()) {
            totalMarks += marks;
        }
        return totalMarks;
    }
    private void loadDataFromFile() {
    try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE_PATH))) {
        String line;
        int rollNumber;
        String name;
        String course;
        Student student = null;

        while ((line = reader.readLine()) != null) {
            if (line.isEmpty()) {
                if (student != null) {
                    students.add(student);
                }
                student = null;
            } else {
                if (student == null) {
                    // Start of a new student's data
                    rollNumber = Integer.parseInt(line);
                    name = reader.readLine();
                    course = reader.readLine();
                    student = new Student(rollNumber, name, course);
                } else {
                    // Subject marks data
                    String subject = line;
                    int marks = Integer.parseInt(reader.readLine());
                    student.addSubjectMarks(subject, marks);
                }
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Failed to load data from file.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    private void saveDataToFile() {
    try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_FILE_PATH))) {
        for (Student student : students) {
            writer.println(student.getRollNumber());
            writer.println(student.getName());
            writer.println(student.getCourse());

            Map<String, Integer> subjectMarks = student.getSubjectMarks();
            for (Map.Entry<String, Integer> entry : subjectMarks.entrySet()) {
                writer.println(entry.getKey());
                writer.println(entry.getValue());
            }
            writer.println(); // Add an empty line to separate each student's data
        }
    } catch (IOException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Failed to save data to file.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new StudentManagementSystem().setVisible(true);
            }
        });
    }
}
