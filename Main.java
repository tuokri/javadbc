import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

    private static final Scanner scan = new Scanner(System.in);

    public static void main(String[] args) {

        System.out.println("1: Show items.");
        System.out.println("2: Show customers.");
        System.out.println("Make selection and press ENTER.");

        int choice = getUserInput();

        switch (choice) {

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