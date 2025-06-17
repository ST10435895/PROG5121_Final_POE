import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Login {

    // User details
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String phoneNumber;

    // Lists to hold messages by status
    private static ArrayList<Message> sentMessages = new ArrayList<>();
    private static ArrayList<Message> storedMessages = new ArrayList<>();
    private static ArrayList<Message> disregardedMessages = new ArrayList<>();

    // Constructor to set user details
    public Login(String firstName, String lastName, String username, String password, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }

    // Check if username contains underscore and max length 5
    public boolean checkUserName() {
        return username.contains("_") && username.length() <= 5;
    }

    // Check password complexity rules
    public boolean checkPasswordComplexity() {
        boolean hasUppercase = !password.equals(password.toLowerCase());
        boolean hasNumber = password.matches(".*\\d.*");
        boolean hasSpecialChar = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");
        boolean isLongEnough = password.length() >= 8;
        return hasUppercase && hasNumber && hasSpecialChar && isLongEnough;
    }

    // Validate phone number format +27 followed by 9 digits
    public boolean checkCellPhoneNumber() {
        String regex = "\\+27\\d{9}";
        return phoneNumber.matches(regex);
    }

    // Register user if all validations pass, else return error messages
    public String registerUser() {
        if (!checkUserName()) {
            return "Username is not correctly formatted. It must contain an underscore and be no more than 5 characters.";
        }
        if (!checkPasswordComplexity()) {
            return "Password is not correctly formatted. It must contain at least 8 characters, a capital letter, a number, and a special character.";
        }
        if (!checkCellPhoneNumber()) {
            return "Cell phone number incorrectly formatted. It must start with +27 and have 9 digits after.";
        }
        return "Registration successful.";
    }

    // Check login credentials against stored user data
    public boolean loginUser(String enteredUsername, String enteredPassword, String enteredPhoneNumber) {
        return this.username.equals(enteredUsername) &&
                this.password.equals(enteredPassword) &&
                this.phoneNumber.equals(enteredPhoneNumber);
    }

    public static void main(String[] args) {
        // Register new user with input dialogs
        Login user = getUserRegistration();
        if (user == null) {
            JOptionPane.showMessageDialog(null, "Registration failed or cancelled.");
            System.exit(0);
        }

        // Allow max 3 login attempts
        int loginAttempts = 0;
        boolean loggedIn = false;

        while (loginAttempts < 3 && !loggedIn) {
            String loginUsername = JOptionPane.showInputDialog("Login - Enter username:");
            String loginPassword = JOptionPane.showInputDialog("Login - Enter password:");
            String loginPhone = JOptionPane.showInputDialog("Login - Enter phone number:");

            // Validate login credentials
            if (user.loginUser(loginUsername, loginPassword, loginPhone)) {
                JOptionPane.showMessageDialog(null, "Welcome " + user.firstName + " " + user.lastName + "! It is great to see you again.");
                loggedIn = true;
            } else {
                loginAttempts++;
                if (loginAttempts < 3)
                    JOptionPane.showMessageDialog(null, "Incorrect credentials. " + (3 - loginAttempts) + " attempt(s) left.");
            }
        }

        // If login fails after 3 attempts, ask to restart registration or quit
        if (!loggedIn) {
            int option = JOptionPane.showConfirmDialog(null, "Too many failed attempts. Restart registration?", "Login Failed", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                main(null); // Restart program
            } else {
                JOptionPane.showMessageDialog(null, "Exiting. Goodbye!");
                System.exit(0);
            }
        }

        // Display welcome message after successful login
        JOptionPane.showMessageDialog(null, "Welcome to QuickChat.");

        // Ask how many messages user wants to send
        int messagesToSend = getPositiveIntInput("How many messages would you like to send?");
        int messagesSentCount = 0; // Counter for sent messages

        // Main menu loop
        while (true) {
            String menu = "Select an option:\n"
                    + "1) Send Messages\n"
                    + "2) Show recently sent messages\n"
                    + "3) Search messages by recipient\n"
                    + "4) Delete message by hash\n"
                    + "5) Display sent message report\n"
                    + "6) Show longest sent message\n"
                    + "7) Search message by Message ID\n"
                    + "8) Search all messages sent to a recipient\n"
                    + "9) Quit";

            String option = JOptionPane.showInputDialog(menu);
            if (option == null) break;

            switch (option) {
                case "1":
                    // Sending messages loop up to number user specified
                    for (int i = 0; i < messagesToSend; i++) {
                        String recipient = getRecipientInput();
                        if (recipient == null) break;

                        String messageText = getMessageTextInput();
                        if (messageText == null) break;

                        String messageID = Message.generateMessageID();

                        Message msg = new Message(messageID, recipient, messageText);

                        // Validate generated message ID and recipient number
                        if (!msg.checkMessageID()) {
                            JOptionPane.showMessageDialog(null, "Invalid message ID generated. Try again.");
                            i--;
                            continue;
                        }
                        if (!msg.checkRecipientCell()) {
                            JOptionPane.showMessageDialog(null, "Recipient cell number invalid.");
                            i--;
                            continue;
                        }

                        // Get user's choice on what to do with the message
                        String userChoice = getUserMessageChoice();

                        // Handle user choice for sending, storing, or disregarding
                        switch (userChoice) {
                            case "1": // Send message
                                sentMessages.add(msg);
                                messagesSentCount++;
                                JOptionPane.showMessageDialog(null, getMessageDetails(msg));
                                break;
                            case "2": // Store message
                                storedMessages.add(msg);
                                JOptionPane.showMessageDialog(null, "Message stored for later.");
                                break;
                            case "3": // Disregard message
                                disregardedMessages.add(msg);
                                JOptionPane.showMessageDialog(null, "Message disregarded.");
                                break;
                            default:
                                JOptionPane.showMessageDialog(null, "No valid option selected, message disregarded.");
                                disregardedMessages.add(msg);
                                break;
                        }
                    }
                    JOptionPane.showMessageDialog(null, "Total messages sent: " + messagesSentCount);
                    break;

                case "2": // Show recently sent messages
                    if (sentMessages.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "No sent messages to show.");
                    } else {
                        StringBuilder sentList = new StringBuilder("Recently sent messages:\n");
                        for (Message m : sentMessages) {
                            sentList.append("- ").append(m.getMessageText()).append("\n");
                        }
                        JOptionPane.showMessageDialog(null, sentList.toString());
                    }
                    break;

                case "3": // Search messages by recipient
                    String searchRecipient = JOptionPane.showInputDialog("Enter recipient to search for:");
                    if (searchRecipient != null) {
                        List<String> foundMessages = new ArrayList<>();
                        for (Message m : sentMessages) {
                            if (m.getRecipient().equals(searchRecipient)) {
                                foundMessages.add(m.getMessageText());
                            }
                        }
                        if (foundMessages.isEmpty()) {
                            JOptionPane.showMessageDialog(null, "No messages found for recipient: " + searchRecipient);
                        } else {
                            StringBuilder results = new StringBuilder("Messages for " + searchRecipient + ":\n");
                            for (String msgText : foundMessages) {
                                results.append("- ").append(msgText).append("\n");
                            }
                            JOptionPane.showMessageDialog(null, results.toString());
                        }
                    }
                    break;

                case "4": // Delete message by hash
                    String hashToDelete = JOptionPane.showInputDialog("Enter the message hash to delete:");
                    if (hashToDelete != null) {
                        boolean deleted = false;
                        for (int i = 0; i < sentMessages.size(); i++) {
                            if (sentMessages.get(i).getMessageHash().equalsIgnoreCase(hashToDelete)) {
                                sentMessages.remove(i);
                                deleted = true;
                                break;
                            }
                        }
                        JOptionPane.showMessageDialog(null, deleted ? "Message deleted." : "Message with that hash not found.");
                    }
                    break;

                case "5": // Display sent message report
                    if (sentMessages.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "No sent messages to report.");
                    } else {
                        StringBuilder report = new StringBuilder("Sent Message Report:\n");
                        for (Message m : sentMessages) {
                            report.append("Recipient: ").append(m.getRecipient()).append("\n")
                                    .append("Message: ").append(m.getMessageText()).append("\n")
                                    .append("Hash: ").append(m.getMessageHash()).append("\n")
                                    .append("Flag: ").append(m.displayFlags()).append("\n\n");
                        }
                        JOptionPane.showMessageDialog(null, report.toString());
                    }
                    break;

                case "6": // Show longest sent message
                    if (sentMessages.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "No sent messages to analyze.");
                    } else {
                        // Find message with longest length
                        Message longest = sentMessages.get(0);
                        for (Message m : sentMessages) {
                            if (m.getMessageText().length() > longest.getMessageText().length()) {
                                longest = m;
                            }
                        }
                        JOptionPane.showMessageDialog(null, "Longest Message Details:\n" + getMessageDetails(longest));
                    }
                    break;

                case "7": // Search message by Message ID
                    String searchID = JOptionPane.showInputDialog("Enter Message ID to search for:");
                    if (searchID != null) {
                        Message foundMsg = null;
                        for (Message m : sentMessages) {
                            if (m.getMessageID().equalsIgnoreCase(searchID)) {
                                foundMsg = m;
                                break;
                            }
                        }
                        if (foundMsg == null) {
                            JOptionPane.showMessageDialog(null, "No message found with ID: " + searchID);
                        } else {
                            JOptionPane.showMessageDialog(null, "Message found:\nRecipient: " + foundMsg.getRecipient() + "\nMessage: " + foundMsg.getMessageText());
                        }
                    }
                    break;

                case "8": // Search all messages sent to a recipient
                    String recipientToSearch = JOptionPane.showInputDialog("Enter recipient to search messages for:");
                    if (recipientToSearch != null) {
                        List<Message> messagesForRecipient = new ArrayList<>();
                        for (Message m : sentMessages) {
                            if (m.getRecipient().equalsIgnoreCase(recipientToSearch)) {
                                messagesForRecipient.add(m);
                            }
                        }
                        if (messagesForRecipient.isEmpty()) {
                            JOptionPane.showMessageDialog(null, "No messages found for recipient: " + recipientToSearch);
                        } else {
                            StringBuilder messagesList = new StringBuilder("Messages for " + recipientToSearch + ":\n");
                            for (Message m : messagesForRecipient) {
                                messagesList.append("- ").append(m.getMessageText()).append("\n");
                            }
                            JOptionPane.showMessageDialog(null, messagesList.toString());
                        }
                    }
                    break;

                case "9": // Quit the program
                    JOptionPane.showMessageDialog(null, "Goodbye!");
                    System.exit(0);
                    break;

                default: // Invalid menu input
                    JOptionPane.showMessageDialog(null, "Invalid option. Please enter a number between 1 and 9.");
            }
        }
    }

    // Helper method to get user registration inputs and validate
    private static Login getUserRegistration() {
        String firstName = JOptionPane.showInputDialog("Enter your first name:");
        if (firstName == null) return null;
        String lastName = JOptionPane.showInputDialog("Enter your last name:");
        if (lastName == null) return null;

        // Loop until valid username entered
        String username;
        while (true) {
            username = JOptionPane.showInputDialog("Enter username (must contain '_' and be <= 5 characters):");
            if (username == null) return null;
            Login tempUser = new Login(firstName, lastName, username, "", "");
            if (tempUser.checkUserName()) break;
            else JOptionPane.showMessageDialog(null, "Username must contain an underscore and be max 5 characters.");
        }

        // Loop until valid password entered
        String password;
        while (true) {
            password = JOptionPane.showInputDialog("Enter password (8+ chars, uppercase, number, special char):");
            if (password == null) return null;
            Login tempUser = new Login(firstName, lastName, username, password, "");
            if (tempUser.checkPasswordComplexity()) break;
            else JOptionPane.showMessageDialog(null, "Password must be at least 8 characters, contain uppercase, number and special character.");
        }

        // Loop until valid phone number entered
        String phoneNumber;
        while (true) {
            phoneNumber = JOptionPane.showInputDialog("Enter phone number (e.g. +27838968976):");
            if (phoneNumber == null) return null;
            Login tempUser = new Login(firstName, lastName, username, password, phoneNumber);
            if (tempUser.checkCellPhoneNumber()) break;
            else JOptionPane.showMessageDialog(null, "Phone number must start with +27 and have 9 digits after.");
        }

        Login user = new Login(firstName, lastName, username, password, phoneNumber);
        String registrationMessage = user.registerUser();
        JOptionPane.showMessageDialog(null, registrationMessage);

        // Return null if registration failed
        if (!registrationMessage.equals("Registration successful.")) {
            return null;
        }
        return user;
    }

    // Helper method to get positive integer input with validation
    private static int getPositiveIntInput(String prompt) {
        int number = -1;
        while (number <= 0) {
            String input = JOptionPane.showInputDialog(prompt);
            if (input == null) {
                System.exit(0); // Exit if user cancels
            }
            try {
                number = Integer.parseInt(input);
                if (number <= 0)
                    JOptionPane.showMessageDialog(null, "Please enter a positive number.");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a number.");
            }
        }
        return number;
    }

    // Helper method to get valid recipient number input
    private static String getRecipientInput() {
        while (true) {
            String recipient = JOptionPane.showInputDialog("Enter recipient cell number (max: 12 characters, starts with '+27'):");
            if (recipient == null) return null;
            if (recipient.length() <= 12 && recipient.startsWith("+27")) return recipient;
            JOptionPane.showMessageDialog(null, "Recipient number invalid. Must be max 12 digits and start with '+27'.");
        }
    }

    // Helper method to get valid message text input
    private static String getMessageTextInput() {
        while (true) {
            String messageText = JOptionPane.showInputDialog("Enter message (max 50 characters):");
            if (messageText == null) return null;
            if (messageText.length() <= 50) return messageText;
            JOptionPane.showMessageDialog(null, "Please enter a message of less than 50 characters.");
        }
    }

    // Helper method to get user's choice for message action
    private static String getUserMessageChoice() {
        String choice = JOptionPane.showInputDialog(
                "Choose an option:\n1) Send message\n2) Store message\n3) Disregard message");
        if (choice == null) return "3"; // Default to disregard if cancel
        if (!choice.equals("1") && !choice.equals("2") && !choice.equals("3")) {
            JOptionPane.showMessageDialog(null, "Invalid choice, message will be disregarded.");
            return "3";
        }
        return choice;
    }

    // Format message details for display
    private static String getMessageDetails(Message msg) {
        return "Message ID: " + msg.getMessageID() + "\n"
                + "Message Hash: " + msg.getMessageHash() + "\n"
                + "Recipient: " + msg.getRecipient() + "\n"
                + "Message: " + msg.getMessageText() + "\n"
                + "Flag: " + msg.getFlag();
    }
}
