import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

class Student {
    String name;
    ArrayList<Double> marks = new ArrayList<>();
    double average;
    String grade;

    Student(String name, ArrayList<Double> marks) {
        this.name = name;
        this.marks = marks;
        calculateAverage();
        assignGrade();
    }

    void calculateAverage() {
        double total = 0;
        for (double m : marks) total += m;
        average = total / marks.size();
    }

    void assignGrade() {
        if (average >= 90) grade = "A";
        else if (average >= 80) grade = "B";
        else if (average >= 70) grade = "C";
        else if (average >= 60) grade = "D";
        else grade = "Fail";
    }
}

class Subject {
    String name;
    String code;

    Subject(String name, String code) {
        this.name = name;
        this.code = code;
    }
}

public class StudentGradeGUI extends JFrame {
    private JTextField nameField;
    private JPanel marksPanel;
    private JButton submitButton;
    private JTextArea outputArea;
    private ArrayList<Student> students = new ArrayList<>();
    private Student topper = null;
    private Student lowest = null;
    private ArrayList<Subject> subjects = new ArrayList<>();
    private int studentCount = 0;
    private int totalStudents;

    public StudentGradeGUI() {
        setTitle("Student Grade Tracker");
        setSize(600, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        totalStudents = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter total number of students:"));
        int totalSubjects = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter number of subjects:"));

        for (int i = 1; i <= totalSubjects; i++) {
            String subName = JOptionPane.showInputDialog(this, "Enter name of Subject " + i + ":");
            String subCode = JOptionPane.showInputDialog(this, "Enter code of Subject " + i + ":");
            subjects.add(new Subject(subName, subCode));
        }

        JPanel topPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        topPanel.setBorder(BorderFactory.createTitledBorder("Student Info"));
        topPanel.add(new JLabel("Student Name (only alphabets):"));
        nameField = new JTextField();
        topPanel.add(nameField);
        add(topPanel, BorderLayout.NORTH);

        marksPanel = new JPanel();
        marksPanel.setLayout(new BoxLayout(marksPanel, BoxLayout.Y_AXIS));
        for (Subject subject : subjects) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
            row.add(new JLabel(subject.name + " (" + subject.code + "): "));
            JTextField markField = new JTextField(5);
            row.add(markField);
            marksPanel.add(row);
        }

        JScrollPane scrollPane = new JScrollPane(marksPanel);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Enter Marks"));
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        submitButton = new JButton("Submit Student Data");
        bottomPanel.add(submitButton, BorderLayout.NORTH);
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        bottomPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        submitButton.addActionListener(e -> processStudent());
        setVisible(true);
    }

    private void processStudent() {
        String name = nameField.getText().trim();
        if (!name.matches("[a-zA-Z ]+")) {
            JOptionPane.showMessageDialog(this, "❌ Invalid name! Only alphabets allowed.");
            return;
        }

        ArrayList<Double> marks = new ArrayList<>();
        Component[] components = marksPanel.getComponents();
        for (Component c : components) {
            if (c instanceof JPanel) {
                Component[] fields = ((JPanel) c).getComponents();
                for (Component f : fields) {
                    if (f instanceof JTextField) {
                        try {
                            double m = Double.parseDouble(((JTextField) f).getText());
                            if (m < 0 || m > 100) {
                                JOptionPane.showMessageDialog(this, "❌ Marks must be between 0 and 100.");
                                return;
                            }
                            marks.add(m);
                        } catch (NumberFormatException e) {
                            JOptionPane.showMessageDialog(this, "❌ Enter valid numeric marks.");
                            return;
                        }
                    }
                }
            }
        }

        Student s = new Student(name, marks);
        students.add(s);
        if (topper == null || s.average > topper.average) topper = s;
        if (lowest == null || s.average < lowest.average) lowest = s;

        studentCount++;
        if (studentCount == totalStudents) {
            displayResults();
            submitButton.setEnabled(false);
        } else {
            clearFields();
        }
    }

    private void displayResults() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Student Report ===\n");
        for (Student s : students) {
            sb.append(String.format("%s - Avg: %.2f, Grade: %s\n", s.name, s.average, s.grade));
        }

        if (topper != null) {
            sb.append("\n\uD83C\uDFC6 Topper:\n");
            sb.append(String.format("%s - %.2f (%s)\n", topper.name, topper.average, topper.grade));
        }

        if (lowest != null) {
            sb.append("\n\uD83D\uDCC9 Lowest Scorer:\n");
            sb.append(String.format("%s - %.2f (%s)\n", lowest.name, lowest.average, lowest.grade));
        }

        outputArea.setText(sb.toString());
    }

    private void clearFields() {
        nameField.setText("");
        Component[] components = marksPanel.getComponents();
        for (Component c : components) {
            if (c instanceof JPanel) {
                Component[] fields = ((JPanel) c).getComponents();
                for (Component f : fields) {
                    if (f instanceof JTextField) {
                        ((JTextField) f).setText("");
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StudentGradeGUI::new);
    }
}
