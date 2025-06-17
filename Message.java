import javax.swing.*;
import java.util.*;

public class Message {
    private String messageID;
    private String recipient;
    private String messageText;
    private String messageHash;
    private String flag;

    // Flags to track status
    private boolean isSent;
    private boolean isReceived;
    private boolean isRead;

    // Static lists to manage messages
    public static ArrayList<Message> sentMessages = new ArrayList<>();
    public static ArrayList<Message> disregardedMessages = new ArrayList<>();
    public static ArrayList<Message> storedMessages = new ArrayList<>();
    public static ArrayList<String> messageHashes = new ArrayList<>();
    public static ArrayList<String> messageIDs = new ArrayList<>();

    // Constructor
    public Message(String messageID, String recipient, String messageText) {
        this.messageID = messageID;
        this.recipient = recipient;
        this.messageText = messageText;
        this.isSent = false;
        this.isReceived = false;
        this.isRead = false;
        this.messageHash = createMessageHash();
        this.flag = "";

        messageHashes.add(this.messageHash);
        messageIDs.add(this.messageID);
    }

    // Validate message ID format
    public boolean checkMessageID() {
        return messageID != null && messageID.matches("MSG\\d{3}");
    }

    // Validate recipient number format
    public boolean checkRecipientCell() {
        return recipient != null && recipient.length() <= 13 && recipient.startsWith("+27");
    }

    // Send the message and set the flag
    public String sentMessage() {
        if (checkMessageID() && checkRecipientCell() && messageText != null && !messageText.isEmpty()) {
            sentMessages.add(this);
            this.isSent = true;
            return "Message sent";
        } else {
            disregardedMessages.add(this);
            this.isSent = false;
            return "Message disregarded due to invalid data";
        }
    }

    // Generate a hash for the message
    public String createMessageHash() {
        String firstTwo = messageID.length() >= 2 ? messageID.substring(0, 2) : messageID;
        String[] words = messageText.trim().split("\\s+");
        String firstWord = words.length > 0 ? words[0] : "";
        String lastWord = words.length > 1 ? words[words.length - 1] : firstWord;
        return (firstTwo + ":" + recipient + "." + (firstWord + lastWord)).toUpperCase();
    }

    // Show flag status as "Flag: Sent Received Read"
    public String displayFlags() {
        StringBuilder flagDisplay = new StringBuilder("Flag: ");
        if (isSent) flagDisplay.append("Sent ");
        if (isReceived) flagDisplay.append("Received ");
        if (isRead) flagDisplay.append("Read ");
        if (!isSent && !isReceived && !isRead) flagDisplay.append("None");
        return flagDisplay.toString().trim();
    }


    // Return list of sent message texts
    public static List<String> getSentMessagesList() {
        List<String> messages = new ArrayList<>();
        for (Message m : sentMessages) {
            messages.add(m.messageText);
        }
        return messages;
    }

    // Return the longest sent message
    public static String getLongestMessage() {
        Message longest = null;
        for (Message m : sentMessages) {
            if (longest == null || m.messageText.length() > longest.messageText.length()) {
                longest = m;
            }
        }
        return longest != null ? longest.messageText : "";
    }

    // Search message by ID and return message
    public static String searchMessageByID(String id) {
        for (Message msg : sentMessages) {
            if (msg.getMessageID().equals(id)) {
                return msg.getMessageText();
            }
        }
        return "Message ID not found";
    }

    // Search messages sent to a specific recipient
    public static List<String> searchMessagesByRecipient(String recipient) {
        List<String> results = new ArrayList<>();
        for (Message m : sentMessages) {
            if (m.recipient.equalsIgnoreCase(recipient)) {
                results.add(m.messageText);
            }
        }
        return results;
    }

    // Delete sent message by its hash
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

    // Generate simple hash for testing
    public static String generateHash(String message) {
        return Integer.toString(message.hashCode());
    }

    // Show all sent messages as a report
    public static String displaySentMessageReport() {
        StringBuilder report = new StringBuilder("Sent Message Report:\n");
        for (Message msg : sentMessages) {
            report.append("Recipient: ").append(msg.getRecipient()).append("\n");
            report.append("Message: ").append(msg.getMessageText()).append("\n");
            report.append("Hash: ").append(msg.getMessageHash()).append("\n");
            report.append(msg.displayFlags()).append("\n");


            report.append("\n");
        }
        return report.toString();
    }


    // Generate message ID in format MSG###
    public static String generateMessageID() {
        Random rand = new Random();
        int number = rand.nextInt(900) + 100; // 100–999
        return "MSG" + number;
    }

    // Getters and Setters
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

    public String getFlag() {
        return flag;
    }

    public void setSent(boolean sent) {
        this.isSent = sent;
        updateFlag();
    }

    public void setReceived(boolean received) {
        this.isReceived = received;
        updateFlag();
    }

    public void setRead(boolean read) {
        this.isRead = read;
        updateFlag();
    }

    // Utility to keep the flag string in sync
    private void updateFlag() {
        StringBuilder sb = new StringBuilder();
        if (isSent) sb.append("Sent ");
        if (isReceived) sb.append("Received ");
        if (isRead) sb.append("Read ");
        this.flag = sb.length() > 0 ? sb.toString().trim() : "None";
    }
}
