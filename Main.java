import java.util.InputMismatchException;
import java.util.Scanner;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {

    private static final Scanner scan = new Scanner(System.in);

    public static void main(String[] args) {

        try {

            Class.forName("oracle.jdbc.driver.OracleDriver");

        } catch (ClassNotFoundException e) {

            System.out.println("Oracle JDBC Driver not found!");
            e.printStackTrace();
            return;

        }

        System.out.println("Oracle JDBC Driver Registered!");

        Connection connection = null;

        try {

            // STU31@"(DESCRIPTION= (ADDRESS=(PROTOCOL=TCP)(HOST=toldb.oulu.fi) (PORT=1521))(CONNECT_DATA=(SID=toldb11)))
            connection = DriverManager.getConnection(
                    "jdbc:oracle:thin:@toldb.oulu.fi:1521:toldb11", "STU31", "delrix123");

        } catch (SQLException e) {

            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return;

        }

        if (connection != null) {

            System.out.println("You made it, take control your database now!");
        } else {

            System.out.println("Failed to make connection!");
        }

        System.out.print("\n\n");
        System.out.println("1: Show items.");
        System.out.println("2: Show customers.");
        System.out.println("Make selection and press ENTER.");

        switch (getUserInput()) {

            case 1:
                showItems();
                break;

            case 2:
                showCustomers();
                break;
        }
    }

    private static int getUserInput() {

        int choice = 0;

        while (true) {

            try {

                choice = scan.nextInt();
                if (choice != 1 && choice != 2) {

                    scan.nextLine();
                    System.out.println("Please select 1 or 2.");
                } else {

                    scan.nextLine();
                    break;
                }
            } catch (InputMismatchException ime) {

                System.out.println("Please select 1 or 2.");
                scan.nextLine();
            }
        }

        return choice;
    }

    private static void showCustomers() {

    }

    private static void showItems() {

    }
}