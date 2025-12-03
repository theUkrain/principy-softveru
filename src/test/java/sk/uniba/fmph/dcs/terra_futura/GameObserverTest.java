package sk.uniba.fmph.dcs.terra_futura;

import org.junit.Before;
import org.junit.Test;
import sk.uniba.fmph.dcs.terra_futura.observer.GameObserver;
import sk.uniba.fmph.dcs.terra_futura.tiles.Grid;
import sk.uniba.fmph.dcs.terra_futura.tiles.GridPosition;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.*;

public class GameObserverTest {

    private Player testPlayer;
    private Grid testGrid;

    @Before
    public void setUp() {
        testGrid = new Grid();

        ActivationPattern pattern1 = createTestActivationPattern();
        ActivationPattern pattern2 = createTestActivationPattern();
        ScoringMethod scoring1 = createTestScoringMethod();
        ScoringMethod scoring2 = createTestScoringMethod();

        testPlayer = new Player("TestPlayer", testGrid, pattern1, pattern2, scoring1, scoring2);
    }

    private ActivationPattern createTestActivationPattern() {
        return new ActivationPattern(new TestActivateGrid(), Collections.emptyList()) {
            private boolean selected = false;

            @Override
            public void select() {
                selected = true;
            }

            @Override
            public boolean isSelected() {
                return selected;
            }
        };
    }

    private ScoringMethod createTestScoringMethod() {
        return new ScoringMethod(testGrid, Collections.emptyList(), 0) {
            @Override
            public int selectThisMethodAndCalculate() {
                return 0;
            }
        };
    }

    private GameObserver createObserverWithInput(String input, Player player) {
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        return new GameObserver(inputStream, outputStream, player);
    }

    @Test
    public void testConstructor() {
        String input = "";
        GameObserver observer = createObserverWithInput(input, testPlayer);

        assertNotNull("Observer should be created", observer);
        assertNotNull("Player should not be null", observer.getPlayer());
        assertEquals("Player should match", testPlayer, observer.getPlayer());
    }

    @Test
    public void testGetPlayer() {
        GameObserver observer = createObserverWithInput("", testPlayer);

        Player player = observer.getPlayer();
        assertNotNull("Player should not be null", player);
        assertEquals("Player name should match", "TestPlayer", player.getName());
        assertSame("Grid should match", testGrid, player.getGrid());
    }

    @Test
    public void testReadSingleLine() {
        String input = "test command\n";
        GameObserver observer = createObserverWithInput(input, testPlayer);

        String result = observer.read();
        assertEquals("Should read the input line", "test command", result);
    }

    @Test
    public void testReadMultipleLines() {
        String input = "line1\nline2\nline3\n";
        GameObserver observer = createObserverWithInput(input, testPlayer);

        assertEquals("First line", "line1", observer.read());
        assertEquals("Second line", "line2", observer.read());
        assertEquals("Third line", "line3", observer.read());
    }

    @Test
    public void testReadEmptyLine() {
        String input = "\n";
        GameObserver observer = createObserverWithInput(input, testPlayer);

        String result = observer.read();
        assertEquals("Should read empty string", "", result);
    }

    @Test
    public void testReadWithWhitespace() {
        String input = "  command with spaces  \n";
        GameObserver observer = createObserverWithInput(input, testPlayer);

        String result = observer.read();
        assertEquals("Should preserve whitespace", "  command with spaces  ", result);
    }

    @Test
    public void testReadReturnsNullOnEOF() {
        String input = ""; // Empty input = EOF
        GameObserver observer = createObserverWithInput(input, testPlayer);

        String result = observer.read();
        assertNull("Should return null on EOF", result);
    }

    @Test
    public void testWrite() {
        InputStream input = new ByteArrayInputStream("".getBytes());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        GameObserver observer = new GameObserver(input, output, testPlayer);

        observer.write("test message");

        String result = output.toString();
        assertTrue("Output should contain message", result.contains("test message"));
    }

    @Test
    public void testWriteMultipleMessages() {
        InputStream input = new ByteArrayInputStream("".getBytes());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        GameObserver observer = new GameObserver(input, output, testPlayer);

        observer.write("message 1");
        observer.write("message 2");
        observer.write("message 3");

        String result = output.toString();
        assertTrue("Should contain first message", result.contains("message 1"));
        assertTrue("Should contain second message", result.contains("message 2"));
        assertTrue("Should contain third message", result.contains("message 3"));
    }

    @Test
    public void testWriteWithNewlines() {
        InputStream input = new ByteArrayInputStream("".getBytes());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        GameObserver observer = new GameObserver(input, output, testPlayer);

        observer.write("line 1");

        String result = output.toString();
        assertTrue("Should end with newline", result.endsWith(System.lineSeparator()));
    }

    @Test
    public void testNotify() {
        InputStream input = new ByteArrayInputStream("".getBytes());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        GameObserver observer = new GameObserver(input, output, testPlayer);

        observer.notify("game state update");

        String result = output.toString();
        assertTrue("Should contain notification", result.contains("game state update"));
    }

    @Test
    public void testNotifyMultipleTimes() {
        InputStream input = new ByteArrayInputStream("".getBytes());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        GameObserver observer = new GameObserver(input, output, testPlayer);

        observer.notify("state 1");
        observer.notify("state 2");

        String result = output.toString();
        assertTrue("Should contain first state", result.contains("state 1"));
        assertTrue("Should contain second state", result.contains("state 2"));
    }

    @Test
    public void testNotifyUsesWrite() {
        InputStream input = new ByteArrayInputStream("".getBytes());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        GameObserver observer = new GameObserver(input, output, testPlayer);

        String testMessage = "test notification";
        observer.notify(testMessage);

        String result = output.toString();
        assertTrue("notify should write to output", result.contains(testMessage));
    }

    @Test
    public void testReadAndWrite() {
        String input = "player command\n";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        GameObserver observer = new GameObserver(inputStream, outputStream, testPlayer);

        observer.write("Enter command:");
        String command = observer.read();
        assertEquals("Should read command", "player command", command);

        observer.write("Command received: " + command);

        String output = outputStream.toString();
        assertTrue("Should contain prompt", output.contains("Enter command:"));
        assertTrue("Should contain response", output.contains("Command received: player command"));
    }

    @Test
    public void testMultipleReadWriteCycles() {
        String input = "cmd1\ncmd2\ncmd3\n";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        GameObserver observer = new GameObserver(inputStream, outputStream, testPlayer);

        for (int i = 1; i <= 3; i++) {
            observer.write("Enter command " + i + ":");
            String command = observer.read();
            assertEquals("Should read command " + i, "cmd" + i, command);
            observer.write("Received: " + command);
        }

        String output = outputStream.toString();
        assertTrue("Should contain all prompts and responses",
                output.contains("Enter command 1:") &&
                        output.contains("Received: cmd1") &&
                        output.contains("Enter command 2:") &&
                        output.contains("Received: cmd2"));
    }

    @Test
    public void testWriteEmptyString() {
        InputStream input = new ByteArrayInputStream("".getBytes());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        GameObserver observer = new GameObserver(input, output, testPlayer);

        observer.write("");

        String result = output.toString();
        assertEquals("Should write newline for empty string",
                System.lineSeparator(), result);
    }

    @Test
    public void testWriteNull() {
        InputStream input = new ByteArrayInputStream("".getBytes());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        GameObserver observer = new GameObserver(input, output, testPlayer);

        observer.write(null);

        String result = output.toString();
        assertTrue("Should write 'null'", result.contains("null"));
    }

    @Test
    public void testWriteSpecialCharacters() {
        InputStream input = new ByteArrayInputStream("".getBytes());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        GameObserver observer = new GameObserver(input, output, testPlayer);

        observer.write("Special: !@#$%^&*()");
        observer.write("Unicode: ñ é ü ❤");

        String result = output.toString();
        assertTrue("Should handle special characters", result.contains("!@#$%^&*()"));
        assertTrue("Should handle unicode", result.contains("ñ é ü"));
    }

    @Test
    public void testReadSpecialCharacters() {
        String input = "Special: !@#$%^&*()\nUnicode: ñ é ü\n";
        GameObserver observer = createObserverWithInput(input, testPlayer);

        String line1 = observer.read();
        String line2 = observer.read();

        assertEquals("Should read special characters", "Special: !@#$%^&*()", line1);
        assertTrue("Should read unicode", line2.contains("ñ é ü"));
    }

    @Test
    public void testClose() {
        InputStream input = new ByteArrayInputStream("test\n".getBytes());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        GameObserver observer = new GameObserver(input, output, testPlayer);

        observer.close();
    }

    @Test
    public void testCloseMultipleTimes() {
        InputStream input = new ByteArrayInputStream("".getBytes());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        GameObserver observer = new GameObserver(input, output, testPlayer);

        observer.close();
        observer.close();
        observer.close();
    }

    @Test
    public void testDifferentPlayers() {
        Grid grid1 = new Grid();
        Grid grid2 = new Grid();

        ActivationPattern pattern1 = createTestActivationPattern();
        ActivationPattern pattern2 = createTestActivationPattern();
        ScoringMethod scoring1 = createTestScoringMethod();
        ScoringMethod scoring2 = createTestScoringMethod();

        Player player1 = new Player("Alice", grid1, pattern1, pattern2, scoring1, scoring2);
        Player player2 = new Player("Bob", grid2, pattern1, pattern2, scoring1, scoring2);

        GameObserver observer1 = new GameObserver(
                new ByteArrayInputStream("".getBytes()),
                new ByteArrayOutputStream(),
                player1
        );

        GameObserver observer2 = new GameObserver(
                new ByteArrayInputStream("".getBytes()),
                new ByteArrayOutputStream(),
                player2
        );

        assertEquals("Observer 1 should have player 1", player1, observer1.getPlayer());
        assertEquals("Observer 2 should have player 2", player2, observer2.getPlayer());
        assertNotEquals("Players should be different",
                observer1.getPlayer(), observer2.getPlayer());
    }

    @Test
    public void testConcurrentReadWrite() {
        String input = "concurrent command\n";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        GameObserver observer = new GameObserver(inputStream, outputStream, testPlayer);

        observer.write("Prompt");
        String command = observer.read();
        observer.write("Response: " + command);

        String output = outputStream.toString();
        assertTrue("Should contain prompt", output.contains("Prompt"));
        assertTrue("Should contain response", output.contains("Response: concurrent command"));
    }

    @Test
    public void testLongInput() {
        StringBuilder longInput = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longInput.append("word").append(i).append(" ");
        }
        longInput.append("\n");

        GameObserver observer = createObserverWithInput(longInput.toString(), testPlayer);

        String result = observer.read();
        assertNotNull("Should read long input", result);
        assertTrue("Should contain input data", result.contains("word0"));
        assertTrue("Should contain end data", result.contains("word999"));
    }

    @Test
    public void testLongOutput() {
        InputStream input = new ByteArrayInputStream("".getBytes());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        GameObserver observer = new GameObserver(input, output, testPlayer);

        StringBuilder longMessage = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longMessage.append("line ").append(i).append(" ");
        }

        observer.write(longMessage.toString());

        String result = output.toString();
        assertTrue("Should write long output", result.contains("line 0"));
        assertTrue("Should write end of output", result.contains("line 999"));
    }

    @Test
    public void testGameTurnSimulation() {
        String input = "take card 0\nactivate card\nend turn\n";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        GameObserver observer = new GameObserver(inputStream, outputStream, testPlayer);

        observer.notify("Your turn!");
        observer.write("Available actions: take card, activate card, end turn");

        String action1 = observer.read();
        assertEquals("Should read first action", "take card 0", action1);
        observer.write("Card taken successfully");

        String action2 = observer.read();
        assertEquals("Should read second action", "activate card", action2);
        observer.write("Card activated");

        String action3 = observer.read();
        assertEquals("Should read third action", "end turn", action3);
        observer.write("Turn ended");

        String output = outputStream.toString();
        assertTrue("Should contain all messages",
                output.contains("Your turn!") &&
                        output.contains("Card taken successfully") &&
                        output.contains("Card activated") &&
                        output.contains("Turn ended"));
    }

    @Test
    public void testPlayerHasGrid() {
        GameObserver observer = createObserverWithInput("", testPlayer);

        Player player = observer.getPlayer();
        assertNotNull("Player should have grid", player.getGrid());
        assertSame("Grid should match test grid", testGrid, player.getGrid());
    }

    @Test
    public void testPlayerHasActivationPatterns() {
        GameObserver observer = createObserverWithInput("", testPlayer);

        Player player = observer.getPlayer();
        assertNotNull("Player should have first activation pattern",
                player.getFirstActivationPattern());
        assertNotNull("Player should have second activation pattern",
                player.getSecondActivationPattern());
    }

    @Test
    public void testPlayerHasScoringMethods() {
        GameObserver observer = createObserverWithInput("", testPlayer);

        Player player = observer.getPlayer();
        assertNotNull("Player should have first scoring method",
                player.getFirstScoringMethod());
        assertNotNull("Player should have second scoring method",
                player.getSecondScoringMethod());
    }

    private class TestActivateGrid implements InterfaceActivateGrid {

        @Override
        public void setActivationPattern(Collection<GridPosition> pattern) {

        }
    }
}