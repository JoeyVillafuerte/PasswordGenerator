import java.io.*;
import java.net.*;

public class PasswordGeneratorClient {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java PasswordGeneratorClient <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        try {
            Socket socket = new Socket(hostName, portNumber);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

            String userInput;

            while (true) {
                System.out.print("Type 'generate' to request a password (or 'exit' to quit): ");
                userInput = stdIn.readLine();

                // Notify the server about the user disconnecting
                if (userInput.equalsIgnoreCase("exit")) {
                    out.println("exit");
                    break;
                } else if (userInput.equalsIgnoreCase("generate")) {
                    System.out.println("\nPlease fill in the following information to get your new password.");
                    System.out.print("Password Length (please enter a number in digits): ");
                    int length = Integer.parseInt(stdIn.readLine());
                    System.out.print("Include Uppercase (Y/N): ");
                    boolean includeUppercase = stdIn.readLine().equalsIgnoreCase("Y");
                    System.out.print("Include Lowercase (Y/N): ");
                    boolean includeLowercase = stdIn.readLine().equalsIgnoreCase("Y");
                    System.out.print("Include Numbers (Y/N): ");
                    boolean includeNumbers = stdIn.readLine().equalsIgnoreCase("Y");
                    System.out.print("Include Symbols (Y/N): ");
                    boolean includeSymbols = stdIn.readLine().equalsIgnoreCase("Y");

                    // Send the user's criteria to the server
                    out.println("generate " + length + " " + includeUppercase + " " + includeLowercase + " " + includeNumbers + " " + includeSymbols);

                    // Receives and displays the generated password and its strength from the server
                    String generatedPassword = in.readLine();
                    String passwordStrengthLabel = in.readLine();

                    // Outputs the new password and the calculated strength from the server
                    System.out.println("Generated Password: " + generatedPassword);
                    System.out.println("Your generated password strength is: " + passwordStrengthLabel + "\n");
                } else {
                    System.out.println("Unknown command. Type 'generate' to request a password or 'exit' to quit.");
                }
            }

            // Close the client socket
            socket.close();
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + hostName);
            System.exit(1);
        }
    }
}
