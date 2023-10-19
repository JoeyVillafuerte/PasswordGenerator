import java.io.*;
import java.net.*;
import java.security.SecureRandom;

public class PasswordGeneratorServer {
    private static SecureRandom secureRandom = new SecureRandom();

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java PasswordGeneratorServer <port number>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            System.out.println("Password Generator Server is running on port " + portNumber);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("A user has connected.");

                // Create a new thread to handle each client
                Thread clientThread = new Thread(new ClientHandler(clientSocket));
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String request;
                while ((request = in.readLine()) != null) {
                    if (request.equalsIgnoreCase("exit")) {
                        System.out.println("A user has disconnected.");
                        break; // Exit the loop and terminate the client
                    } else if (request.equalsIgnoreCase("generate")) {
                        out.println("Type 'generate' to request a password (or 'exit' to quit).");
                    } else if (request.startsWith("generate")) {
                        String[] parts = request.split(" ");
                        if (parts.length >= 6) {
                            int length = Integer.parseInt(parts[1]);
                            boolean includeUppercase = Boolean.parseBoolean(parts[2]);
                            boolean includeLowercase = Boolean.parseBoolean(parts[3]);
                            boolean includeNumbers = Boolean.parseBoolean(parts[4]);
                            boolean includeSymbols = Boolean.parseBoolean(parts[5]);

                            // Generate the password based on user criteria
                            String generatedPassword = generatePassword(length, includeUppercase, includeLowercase, includeNumbers, includeSymbols);
                            String passwordStrengthLabel = assessPasswordStrength(generatedPassword);

                            // Send the generated password to the client
                            out.println(generatedPassword);

                            // Send the password strength label to the client
                            out.println(passwordStrengthLabel);
                        } else {
                            out.println("Invalid request format. Please specify password criteria.");
                        }
                    }
                }
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String generatePassword(int length, boolean includeUppercase, boolean includeLowercase, boolean includeNumbers, boolean includeSymbols) {
        // Building blocks of the password generation
        // Modified depending on the criteria the user defines
        String charset = "";
        if (includeUppercase) charset += "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        if (includeLowercase) charset += "abcdefghijklmnopqrstuvwxyz";
        if (includeNumbers) charset += "0123456789";
        if (includeSymbols) charset += "!@#$%^&*()_-+=<>?";

        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(charset.length());
            password.append(charset.charAt(index));
        }

        return password.toString();
    }

    private static String assessPasswordStrength(String password) {
        // Default to weak
        int strength = 1;

        // Sorta arbitrary number to define password strength
        // Has to be minimum 4 to have one each of uppercase, lowercase, number, symbol
        if (password.length() >= 8) {
            strength++;
        }
        // Checks for uppercase letters
        if (password.matches(".*[A-Z].*")) {
            strength++;
        }

        // Checks for lowercase letters
        if (password.matches(".*[a-z].*")) {
            strength++;
        }

        // Checks for digits
        if (password.matches(".*\\d.*")) {
            strength++;
        }

        // Checks for symbols
        if (password.matches(".*[^A-Za-z0-9].*")) {
            strength++;
        }

        if (strength <= 2) {
            return "Weak";
        } else if (strength <= 4) {
            return "Medium";
        } else {
            return "Strong";
        }
    }
}
