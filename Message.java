import javax.swing.*;
import java.util.*;

public class Message {
    private String messageID;
    private String recipient;
    private String messageText;
    private String messageHash;

    // Static lists to hold different categories of messages
    public static ArrayList<Message> sentMessages = new ArrayList<>();
    public static ArrayList<Message> disregardedMessages = new ArrayList<>();
    public static ArrayList<Message> storedMessages = new ArrayList<>();
    public static ArrayList<String> messageHashes = new ArrayList<>();
    public static ArrayList<String> messageIDs = new ArrayList<>();

    // Constructor for creating a Message object
    public Message(String messageID, String recipient, String messageText) {
        this.messageID = messageID;
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageHash = createMessageHash();

        messageHashes.add(this.messageHash);
        messageIDs.add(this.messageID);
    }

    // Check if message ID is valid (format MSG followed by 3 digits)
    public boolean checkMessageID() {
        return messageID != null && messageID.matches("MSG\\d{3}");
    }

    // Check if recipient phone number is valid (South African +27 and max 13 chars)
    public boolean checkRecipientCell() {
        return recipient != null && recipient.length() <= 13 && recipient.startsWith("+27");
    }

    // Simulate sending a message (returns status text)
    public String sentMessage() {
        if (checkMessageID() && checkRecipientCell() && messageText != null && !messageText.isEmpty()) {
            sentMessages.add(this);
            return "Message sent";
        } else {
            disregardedMessages.add(this);
            return "Message disregarded due to invalid data";
        }
    }

    // Create a unique hash for the message
    public String createMessageHash() {
        String firstTwo = messageID.length() >= 2 ? messageID.substring(0, 2) : messageID;
        String[] words = messageText.trim().split("\\s+");
        String firstWord = words.length > 0 ? words[0] : "";
        String lastWord = words.length > 1 ? words[words.length - 1] : firstWord;
        return (firstTwo + ":" + recipient + "." + (firstWord + lastWord)).toUpperCase();
    }

    /*** METHODS EXPECTED BY UNIT TESTS ***/

    // Return list of sent messages (texts only)
    public static List<String> getSentMessagesList() {
        List<String> messages = new ArrayList<>();
        for (Message m : sentMessages) {
            messages.add(m.messageText);
        }
        return messages;
    }

    // Return the longest sent message text
    public static String getLongestMessage() {
        Message longest = null;
        for (Message m : sentMessages) {
            if (longest == null || m.messageText.length() > longest.messageText.length()) {
                longest = m;
            }
        }
        return longest != null ? longest.messageText : "";
    }

    // Search for a message by its ID
    public static String searchMessageByID(String id) {
        for (Message m : sentMessages) {
            if (m.messageID.equals(id)) {
                return m.messageText;
            }
        }
        return "Message ID not found.";
    }

    // Generate a basic hash from message text
    public static String generateHash(String message) {
        return Integer.toString(message.hashCode());
    }

    // Delete message from sentMessages by message hash
    public static String deleteMessageByHash(String hash) {
        Iterator<Message> it = sentMessages.iterator();
        while (it.hasNext()) {
            Message m = it.next();
            if (m.messageHash.equals(hash)) {
                it.remove();
                messageHashes.remove(m.messageHash);
                messageIDs.remove(m.messageID);
                return "Message successfully deleted.";
            }
        }
        return "Message with hash not found.";
    }

    // Display a report of all sent messages
    public static String displaySentMessageReport() {
        StringBuilder report = new StringBuilder("Sent Message Report:\n");
        for (Message m : sentMessages) {
            report.append("Recipient: ").append(m.recipient).append("\n")
                    .append("Message: ").append(m.messageText).append("\n")
                    .append("Hash: ").append(m.messageHash).append("\n\n");
        }
        return report.toString();
    }

    // Search all messages by recipient (sent + stored)
    public static List<String> searchMessagesByRecipient(String recipient) {
        List<String> results = new ArrayList<>();
        for (Message m : sentMessages) {
            if (m.recipient.equals(recipient)) {
                results.add(m.messageText);
            }
        }
        for (Message m : storedMessages) {
            if (m.recipient.equals(recipient)) {
                results.add(m.messageText);
            }
        }
        return results;
    }

    /*** GETTERS ***/
    public String getMessageID() {
        return messageID;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessageText() {
        return messageText;
    }

    public String getMessageHash() {
        return messageHash;
    }

    // Generate a message ID in format MSG###
    public static String generateMessageID() {
        Random rand = new Random();
        int number = rand.nextInt(900) + 100; // Ensures 100–999
        return "MSG" + number;
    }
}
