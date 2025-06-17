import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;
import org.junit.Before;
import java.util.Arrays;

public class MessageTest {

    private Message msg1, msg2, msg3, msg4;

    @Before
    public void setUp() {
        // Clear old data before each test
        Message.sentMessages.clear();
        Message.storedMessages.clear();
        Message.disregardedMessages.clear();
        Message.messageHashes.clear();
        Message.messageIDs.clear();

        // Create example messages
        msg1 = new Message("MSG001", "+27821234567", "Did you get the cake?");
        msg2 = new Message("MSG002", "+27821234567", "It is dinner time!");
        msg3 = new Message("MSG003", "+27711223344", "Where are you? You are late! I asked you to be on time.");
        msg4 = new Message("MSG004", "+27711223344", "Ok, I am leaving without you.");

        // Add them to the messages sent list
        Message.sentMessages.addAll(Arrays.asList(msg1, msg2, msg3, msg4));
    }

    @Test
    public void testSentMessagesPopulated() {
        // Check that sent messages were added correctly
        List<String> expected = Arrays.asList(
                "Did you get the cake?",
                "It is dinner time!",
                "Where are you? You are late! I asked you to be on time.",
                "Ok, I am leaving without you."
        );
        assertEquals(expected, Message.getSentMessagesList());
    }

    @Test
    public void testLongestMessage() {
        // Check that the system finds the longest message
        assertEquals("Where are you? You are late! I asked you to be on time.", Message.getLongestMessage());
    }

    @Test
    public void testDisplaySentMessageReport_Flags() {
        // Set flag values on each message
        msg1.setSent(true); // Sent
        msg2.setSent(true); msg2.setReceived(true); // Sent + Received
        msg3.setSent(true); msg3.setReceived(true); msg3.setRead(true); // All flags

        // Generate report
        String report = Message.displaySentMessageReport();

        // Check that the report includes all expected flags
        assertTrue(report.contains("Flag: Sent"));
        assertTrue(report.contains("Flag: Received"));
        assertTrue(report.contains("Flag: Read"));
    }


    @Test
    public void testSearchByMessageID() {
        // Search by message ID and check that correct message is returned
        String result = Message.searchMessageByID("MSG002");
        System.out.println("Actual returned: " + result);
        assertNotNull("Message with ID MSG002 should not be null", result);
        assertEquals("It is dinner time!", result);
    }

    @Test
    public void testSearchByRecipient() {
        // Find messages sent to a specific number
        List<String> expected = Arrays.asList(
                "Where are you? You are late! I asked you to be on time.",
                "Ok, I am leaving without you."
        );
        assertEquals(expected, Message.searchMessagesByRecipient("+27711223344"));
    }

    @Test
    public void testDeleteByMessageHash() {
        // Delete a message using its hash
        String knownHash = msg1.getMessageHash();
        String result = Message.deleteMessageByHash(knownHash);

        // Check that the message was removed successfully
        assertEquals("Message successfully deleted.", result);
        assertFalse(Message.messageHashes.contains(knownHash));
    }

    @Test
    public void testDisplaySentMessageReport_Content() {
        // Set flag on the message so the report includes it
        msg1.setSent(true);

        // Check that the report includes recipient, message, hash, and flag
        String report = Message.displaySentMessageReport().trim();
        assertTrue(report.contains("Recipient: +27821234567"));
        assertTrue(report.contains("Message: Did you get the cake?"));
        assertTrue(report.contains("Hash:"));
        assertTrue(report.contains("Flag: Sent"));
    }
}
