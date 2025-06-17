import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.*;
import java.util.List;
import org.junit.Before;
import java.util.Arrays;

public class MessageTest {

    private Message msg1, msg2, msg3, msg4;

    @Before
    public void setUp() {
        // Clear previous test data
        Message.sentMessages.clear();
        Message.storedMessages.clear();
        Message.disregardedMessages.clear();
        Message.messageHashes.clear();
        Message.messageIDs.clear();

        // Create and add test messages
        msg1 = new Message("MSG001", "+27821234567", "Did you get the cake?");
        msg2 = new Message("MSG002", "+27821234567", "It is dinner time!");
        msg3 = new Message("MSG003", "+27711223344", "Where are you? You are late! I asked you to be on time.");
        msg4 = new Message("MSG004", "+27711223344", "Ok, I am leaving without you.");

        Message.sentMessages.addAll(Arrays.asList(msg1, msg2, msg3, msg4));
    }

    @Test
    public void testSentMessagesPopulated() {
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
        assertEquals("Where are you? You are late! I asked you to be on time.", Message.getLongestMessage());
    }

    @Test
    public void testSearchByMessageID() {
        assertEquals("It is dinner time!", Message.searchMessageByID("MSG002"));
    }

    @Test
    public void testSearchByRecipient() {
        List<String> expected = Arrays.asList(
                "Where are you? You are late! I asked you to be on time.",
                "Ok, I am leaving without you."
        );
        assertEquals(expected, Message.searchMessagesByRecipient("+27711223344"));
    }

    @Test
    public void testDeleteByMessageHash() {
        String knownHash = msg1.getMessageHash();
        String result = Message.deleteMessageByHash(knownHash);
        assertEquals("Message successfully deleted.", result);
        assertFalse(Message.messageHashes.contains(knownHash));
    }

    @Test
    public void testDisplaySentMessageReport() {
        String report = Message.displaySentMessageReport().trim();
        assertTrue(report.contains("Recipient: +27821234567"));
        assertTrue(report.contains("Message: Did you get the cake?"));
        assertTrue(report.contains("Hash:"));
    }
}
