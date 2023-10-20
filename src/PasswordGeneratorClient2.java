import java.io.*;
import java.net.*;

public class PasswordGeneratorClient2 {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java PasswordGeneratorClient <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        try (Socket socket = new Socket(hostName, portNumber);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in)) ) {

            String userInput;

            while (true) {
                System.out.print("Type 'generate' to request a password (or 'exit' to quit): ");
                userInput = stdIn.readLine();
                out.println(userInput);

                if (userInput.equalsIgnoreCase("exit")) {
                    // Exit the loop and terminate the client
                    System.out.println("User has disconnected.");
                    break;
                } else if (userInput.equalsIgnoreCase("generate")) {
                    System.out.println("\nPlease fill in the following information to get your new password.");
                    System.out.print("Password Length (please enter a number in digits): ");
                    int length;
                    try {
                        length = Integer.parseInt(stdIn.readLine());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a valid number for password length.");
                        continue;
                    }

                    boolean includeUppercase, includeLowercase, includeNumbers, includeSymbols;

                    // Ask the user for password criteria
                    System.out.print("Include Uppercase (Y/N): ");
                    includeUppercase = stdIn.readLine().equalsIgnoreCase("Y");
                    System.out.print("Include Lowercase (Y/N): ");
                    includeLowercase = stdIn.readLine().equalsIgnoreCase("Y");
                    System.out.print("Include Numbers (Y/N): ");
                    includeNumbers = stdIn.readLine().equalsIgnoreCase("Y");
                    System.out.print("Include Symbols (Y/N): ");
                    includeSymbols = stdIn.readLine().equalsIgnoreCase("Y");

                    if (!includeUppercase && !includeLowercase && !includeNumbers && !includeSymbols) {
                        System.out.println("At least one option must be selected for password generation.");
                        continue;
                    }

                    // Send the user's criteria to the server
                    out.println("generate " + length + " " + includeUppercase + " " + includeLowercase + " " + includeNumbers + " " + includeSymbols);

                    // Receive and display the generated password and its strength from the server
                    String generatedPassword = in.readLine();
                    String passwordStrengthLabel = in.readLine();

                    // Outputs the new password and the calculated strength from the server
                    System.out.println("\nGenerated Password: " + generatedPassword);
                    System.out.println("Your generated password strength is: " + passwordStrengthLabel);
                } else {
                    System.out.println("Unknown command. Type 'generate' to request a password or 'exit' to quit.");
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + hostName);
            System.exit(1);
        }
    }
}
