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
            System.exit(0);

        }

        System.out.println("Oracle JDBC Driver registered!");

        Connection connection = null;

        try {

            connection = DriverManager.getConnection(
                    "jdbc:oracle:thin:@toldb.oulu.fi:1521:toldb11", "STU31", "delrix123");

        } catch (SQLException e) {

            System.out.println("Connection failed!");
            e.printStackTrace();
            System.exit(0);

        }

        if (connection != null) {

            System.out.println("Connection to database successful!");

        } else {

            System.out.println("Failed to make connection!");
            System.exit(0);

        }

        System.out.print("\n\n");
        System.out.println("1: Show items.");
        System.out.println("2: Show customers.");
        System.out.println("3: Exit.");
        System.out.println("Make selection and press ENTER.");

        switch (getUserInput()) {

            case 1:
                showItems();
                break;

            case 2:
                showCustomers();
                break;

            case 3:
                try {

                    connection.close();
                    System.exit(0);

                } catch (SQLException e) {

                    System.out.println("Error while closing connection!");
                    e.printStackTrace();

                }

            default:
                System.out.println("Something went wrong!");
                System.exit(1);
        }

        try {

            connection.close();
            System.exit(0);

        } catch (SQLException e) {

            System.out.println("Error while closing connection!");
            e.printStackTrace();

        }
    }

    private static int getUserInput() {

        int choice = 0;

        while (true) {

            try {

                choice = scan.nextInt();

                if (choice != 1 && choice != 2 && choice != 3) {

                    scan.nextLine();
                    System.out.println("Please select 1, 2 or 3.");

                } else {

                    scan.nextLine();
                    break;

                }

            } catch (InputMismatchException ime) {

                System.out.println("Please select 1, 2 or 3.");
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