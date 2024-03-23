import java.util.Scanner;

public class HalfPrecisionConverterCLI {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            // Prompt for input
            System.out.print("Enter a value (or type 'exit' to quit): ");
            String input = scanner.nextLine();

            // Check if user wants to exit
            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Exiting...");
                break;
            }

            // Call convertInput method of HalfPrecisionConverter
            String result = HalfPrecisionConverter.convertInput(input);

            // Print the result
            System.out.println("Result: " + result);
        }

        scanner.close();
    }
}