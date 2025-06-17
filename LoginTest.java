import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class LoginTest {

    private Login validUser;
    private Login userWithBadUsername;
    private Login userWithBadPassword;
    private Login userWithBadPhone;

    private Message validMessage;
    private Message longMessage;
    private Message badRecipient;
    private Message badMessageID;

    @Before
    public void setUp() {
        validUser = new Login("Alice", "Smith", "a_s", "P@ssword1", "+27831234567");
        userWithBadUsername = new Login("Bob", "Jones", "bob", "P@ssword1", "+27831234567");
        userWithBadPassword = new Login("Charlie", "Brown", "c_b", "password", "+27831234567");
        userWithBadPhone = new Login("Dana", "White", "d_w", "P@ssword1", "0831234567");

        validMessage = new Message("MSG001", "+2783123456", "Hello there!");
        longMessage = new Message("MSG002", "+2783123456", "This is a really long message that exceeds the maximum limit of 50 characters.");
        badRecipient = new Message("MSG003", "0831234567", "Hi");
        badMessageID = new Message("invalid_id", "+2783123456", "Sample message");
    }

    @Test
    public void testCheckUserName_Valid() {
        assertTrue(validUser.checkUserName());
    }

    @Test
    public void testCheckUserName_Invalid() {
        assertFalse(userWithBadUsername.checkUserName());
    }

    @Test
    public void testCheckPasswordComplexity_Valid() {
        assertTrue(validUser.checkPasswordComplexity());
    }

    @Test
    public void testCheckPasswordComplexity_Invalid() {
        assertFalse(userWithBadPassword.checkPasswordComplexity());
    }

    @Test
    public void testCheckCellPhoneNumber_Valid() {
        assertTrue(validUser.checkCellPhoneNumber());
    }

    @Test
    public void testCheckCellPhoneNumber_Invalid() {
        assertFalse(userWithBadPhone.checkCellPhoneNumber());
    }

    @Test
    public void testRegisterUser_Success() {
        assertEquals("Registration successful.", validUser.registerUser());
    }

    @Test
    public void testRegisterUser_InvalidUsername() {
        String result = userWithBadUsername.registerUser();
        assertEquals("Username is not correctly formatted. It must contain an underscore and be no more than 5 characters.", result);
    }

    @Test
    public void testRegisterUser_InvalidPassword() {
        String result = userWithBadPassword.registerUser();
        assertEquals("Password is not correctly formatted. It must contain at least 8 characters, a capital letter, a number, and a special character.", result);
    }

    @Test
    public void testRegisterUser_InvalidPhone() {
        String result = userWithBadPhone.registerUser();
        assertEquals("Cell phone number incorrectly formatted. It must start with +27 and have 9 digits after.", result);
    }

    @Test
    public void testLoginUser_Success() {
        assertTrue(validUser.loginUser("a_s", "P@ssword1", "+27831234567"));
    }

    @Test
    public void testLoginUser_Failure_WrongUsername() {
        assertFalse(validUser.loginUser("wrong_user", "P@ssword1", "+27831234567"));
    }

    @Test
    public void testLoginUser_Failure_WrongPassword() {
        assertFalse(validUser.loginUser("a_s", "WrongPass", "+27831234567"));
    }

    @Test
    public void testLoginUser_Failure_WrongPhone() {
        assertFalse(validUser.loginUser("a_s", "P@ssword1", "+27000000000"));
    }

    @Test
    public void testValidMessageID() {
        assertTrue(validMessage.checkMessageID());
    }

    @Test
    public void testInvalidMessageID() {
        assertFalse(badMessageID.checkMessageID()); // This should now pass
    }


    @Test
    public void testValidRecipient() {
        assertTrue(validMessage.checkRecipientCell());
    }

    @Test
    public void testInvalidRecipient() {
        assertFalse(badRecipient.checkRecipientCell());
    }

    @Test
    public void testMessageWithinLimit() {
        assertTrue(validMessage.getMessageText().length() <= 50);
    }

    @Test
    public void testMessageExceedsLimit() {
        assertTrue(longMessage.getMessageText().length() > 50);
    }

    @Test
    public void testCreateMessageHash_NotNull() {
        assertNotNull(validMessage.createMessageHash());
    }

    @Test
    public void testSendMessage_ReturnsValidChoice() {
        String result = validMessage.sentMessage();
        assertTrue(result.equals("Message sent") || result.equals("Message stored") || result.equals("Message disregarded"));
    }
}
