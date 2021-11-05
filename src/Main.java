import java.io.IOException;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException {

        System.out.println("Enter training dataset filename/path:");
        String filename = scanner.nextLine();

        // Initialize training dataset
        DataSet trainDataSet = new DataSet(filename);

        String check;
        do {
            System.out.println("Select an option:\n" +
                    "1. Check accuracy of test dataset.\n" +
                    "2. Enter email content to check spam.");

            String choice = scanner.nextLine();
            if (choice.equals("1")) {
                testAccuracy(trainDataSet);
            } else if (choice.equals("2")) {
                checkSpam(trainDataSet);
            } else {
                System.out.println("Invalid choice.");
            }

            System.out.println("Do you want to continue? [Y/N]");
            check = scanner.nextLine();
        } while (check.equals("Y") || check.equals("y"));
    }

    private static void checkSpam(DataSet trainDataSet) {
        System.out.println("Enter your email text:");
        String text = scanner.nextLine();
        Email email = new Email(text, false);
        boolean spam = trainDataSet.predictSpam(email);
        System.out.println(spam ? "Spam" : "Not Spam");
    }

    private static void testAccuracy(DataSet dataSet) throws IOException {
        System.out.println("Enter test dataset filename/path:");
        String filename = scanner.nextLine();

        // Initialize testing dataset
        DataSet testSet = new DataSet(filename);

        // Calculate accuracy
        double accurateCount = 0;
        for (Email email : testSet.emails) {
            boolean isSpam = dataSet.predictSpam(email);
            if (email.isSpam == isSpam) accurateCount++;
        }

        double accuracy = accurateCount / testSet.emails.size();
        System.out.println("Accuracy of test dataset: " + accuracy);
    }

}
