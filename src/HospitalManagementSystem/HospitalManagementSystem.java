package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class HospitalManagementSystem {

    private static final String url = "jdbc:mysql://localhost:3306/hospital";
    private static final String username = "root";

    private static final String password = "Sumedha@24";

    public static void main(String[] args) {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");

        }catch (ClassNotFoundException e){
             e.printStackTrace();
        }
        Scanner scanner = new Scanner(System.in);

        try
        {
            Connection connection = DriverManager.getConnection(url,username,password);
            Patient patient = new Patient(connection,scanner);
            Doctor doctor = new Doctor( connection);
            while (true){
                System.out.println("Hospital Management System");
                System.out.println("1. Add Patient");
                System.out.println("2. View Patient");
                System.out.println("3. View Doctors");
                System.out.println("4. Book Appointment");
                System.out.println("5. Exit");
                System.out.println("Enter your Choice");
                int choice = scanner.nextInt();
                switch (choice){
                    case 1 :
                        patient.addPatient();
                        break;
                    case 2 :
                        patient.viewPatient();
                        break;
                    case 3:
                            doctor.viewDoctor();
                            break;
                    case 4:
                            bookAppointment(patient,doctor,connection,scanner);
                            break;
                    case 5:
                        return;
                    default:
                        System.out.println("Enter valid choice");
                        break;

                }


            }

        }catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public static void bookAppointment(Patient patient, Doctor doctor, Connection connection, Scanner scanner) {
        System.out.println("Enter Patient Id:");
        int patientId = scanner.nextInt();
        System.out.println("Enter Doctor Id:");
        int doctorId = scanner.nextInt();
        System.out.println("Enter appointment date (YYYY-MM-DD):");
        String appointmentDate = scanner.next();
        if (!isValidDateFormat(appointmentDate)) {
            System.out.println("Invalid date format. Please enter the date in 'YYYY-MM-DD' format.");
            return; // Exit the method or handle it accordingly
        }

        System.out.println("Debug: Patient ID: " + patientId + ", Doctor ID: " + doctorId + ", Appointment Date: " + appointmentDate);

        if (patient.getPatientById(patientId) && doctor.getDoctorsById(doctorId)) {
            if (checkDoctorAvailability(doctorId, appointmentDate, connection)) {
                String appointmentQuery = "INSERT INTO appointsments(patient_id, doctor_id, appointment_date) VALUES(?,?,?)";
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                    preparedStatement.setInt(1, patientId);
                    preparedStatement.setInt(2, doctorId);
                    preparedStatement.setString(3, appointmentDate);


                    int rowsAffected = preparedStatement.executeUpdate();


                    if (rowsAffected > 0) {
                        System.out.println("Appointment Booked");
                    } else {
                        System.out.println("Failed to book appointment");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Doctor is not Available");
            }
        } else {
            System.out.println("Either doctor or patient doesn't available");
        }
    }



    public static boolean checkDoctorAvailability(int doctorId , String appointmentDate, Connection connection){
        String Query = "SELECT COUNT(*) FROM appointsments WHERE doctor_id=? AND appointment_date=?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(Query);
            preparedStatement.setInt(1,doctorId);
            preparedStatement.setString(2,appointmentDate);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                int count =  resultSet.getInt(1);
                if (count==0){
                    return  true;
                }else {
                    return false;
                }
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
    private static boolean isValidDateFormat(String date) {
        try {
            java.sql.Date.valueOf(date);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }



}
