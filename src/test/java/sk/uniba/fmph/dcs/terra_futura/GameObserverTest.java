package sk.uniba.fmph.dcs.terra_futura;

import org.junit.Before;
import org.junit.Test;
import sk.uniba.fmph.dcs.terra_futura.observer.GameObserver;
import sk.uniba.fmph.dcs.terra_futura.observer.TerraFuturaObserverInterface;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

public class GameObserverTest {

    /**
     * Mock observer for testing.
     * Implements TerraFuturaObserverInterface and tracks notifications.
     */
    private static class MockObserver implements TerraFuturaObserverInterface {
        private String lastNotification;
        private int notificationCount;

        @Override
        public void notify(String gameState) {
            this.lastNotification = gameState;
            this.notificationCount++;
        }

        @Override
        public String read() {
            return "";
        }

        @Override
        public void write(String message) {

        }

        public String getLastNotification() {
            return lastNotification;
        }

        public int getNotificationCount() {
            return notificationCount;
        }

        public void reset() {
            lastNotification = null;
            notificationCount = 0;
        }
    }

    private GameObserver gameObserver;
    private MockObserver observer1;
    private MockObserver observer2;

    @Before
    public void setUp() {
        gameObserver = new GameObserver();
        observer1 = new MockObserver();
        observer2 = new MockObserver();
    }

    @Test
    public void testRegisterObserver() {
        gameObserver.registerObserver(1, observer1);

        assertTrue("Observer should be registered", gameObserver.hasObserver(1));
        assertEquals("Should have 1 observer", 1, gameObserver.getObserverCount());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterNullObserver() {
        gameObserver.registerObserver(1, null);
    }

    @Test
    public void testRegisterMultipleObservers() {
        gameObserver.registerObserver(1, observer1);
        gameObserver.registerObserver(2, observer2);

        assertTrue("Observer 1 should be registered", gameObserver.hasObserver(1));
        assertTrue("Observer 2 should be registered", gameObserver.hasObserver(2));
        assertEquals("Should have 2 observers", 2, gameObserver.getObserverCount());
    }

    @Test
    public void testUnregisterObserver() {
        gameObserver.registerObserver(1, observer1);
        gameObserver.registerObserver(2, observer2);

        gameObserver.unregisterObserver(1);

        assertFalse("Observer 1 should be unregistered", gameObserver.hasObserver(1));
        assertTrue("Observer 2 should still be registered", gameObserver.hasObserver(2));
        assertEquals("Should have 1 observer", 1, gameObserver.getObserverCount());
    }

    @Test
    public void testUnregisterNonExistentObserver() {
        // Should not throw exception
        gameObserver.unregisterObserver(999);
        assertEquals("Should have 0 observers", 0, gameObserver.getObserverCount());
    }

    @Test
    public void testNotifyPlayer() {
        gameObserver.registerObserver(1, observer1);

        String testState = "Test game state";
        gameObserver.notifyPlayer(1, testState);

        assertEquals("Observer should receive notification",
                testState, observer1.getLastNotification());
        assertEquals("Observer should be notified once",
                1, observer1.getNotificationCount());
    }

    @Test
    public void testNotifyPlayerNotRegistered() {
        gameObserver.registerObserver(1, observer1);

        // Try to notify unregistered player - should not throw exception
        gameObserver.notifyPlayer(2, "Test state");

        assertNull("Observer 1 should not receive notification",
                observer1.getLastNotification());
        assertEquals("Observer 1 should not be notified",
                0, observer1.getNotificationCount());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNotifyPlayerWithNullState() {
        gameObserver.registerObserver(1, observer1);
        gameObserver.notifyPlayer(1, null);
    }

    @Test
    public void testNotifyAll() {
        gameObserver.registerObserver(1, observer1);
        gameObserver.registerObserver(2, observer2);

        Map<Integer, String> states = new HashMap<>();
        states.put(1, "State for player 1");
        states.put(2, "State for player 2");

        gameObserver.notifyAll(states);

        assertEquals("Observer 1 should receive state 1",
                "State for player 1", observer1.getLastNotification());
        assertEquals("Observer 2 should receive state 2",
                "State for player 2", observer2.getLastNotification());
        assertEquals("Observer 1 should be notified once",
                1, observer1.getNotificationCount());
        assertEquals("Observer 2 should be notified once",
                1, observer2.getNotificationCount());
    }

    @Test
    public void testNotifyAllWithMissingPlayer() {
        gameObserver.registerObserver(1, observer1);
        // Observer 2 not registered

        Map<Integer, String> states = new HashMap<>();
        states.put(1, "State for player 1");
        states.put(2, "State for player 2");

        gameObserver.notifyAll(states);

        assertEquals("Observer 1 should receive notification",
                "State for player 1", observer1.getLastNotification());
        assertNull("Observer 2 should not receive notification",
                observer2.getLastNotification());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNotifyAllWithNullMap() {
        gameObserver.registerObserver(1, observer1);
        gameObserver.notifyAll(null);
    }

    @Test
    public void testMultipleNotifications() {
        gameObserver.registerObserver(1, observer1);

        gameObserver.notifyPlayer(1, "First state");
        gameObserver.notifyPlayer(1, "Second state");
        gameObserver.notifyPlayer(1, "Third state");

        assertEquals("Observer should receive last notification",
                "Third state", observer1.getLastNotification());
        assertEquals("Observer should be notified 3 times",
                3, observer1.getNotificationCount());
    }

    @Test
    public void testHasObserverEmptyObservers() {
        assertFalse("Should not have any observers", gameObserver.hasObserver(1));
    }

    @Test
    public void testGetObserverCountEmpty() {
        assertEquals("Should have 0 observers", 0, gameObserver.getObserverCount());
    }

    @Test
    public void testRegisterSamePlayerTwice() {
        MockObserver firstObserver = new MockObserver();
        MockObserver secondObserver = new MockObserver();

        gameObserver.registerObserver(1, firstObserver);
        gameObserver.registerObserver(1, secondObserver);

        assertEquals("Should still have 1 observer (replaced)",
                1, gameObserver.getObserverCount());

        gameObserver.notifyPlayer(1, "Test");

        assertNull("First observer should not receive notification",
                firstObserver.getLastNotification());
        assertEquals("Second observer should receive notification",
                "Test", secondObserver.getLastNotification());
    }

    @Test
    public void testNotifyAllEmptyStates() {
        gameObserver.registerObserver(1, observer1);

        Map<Integer, String> emptyStates = new HashMap<>();
        gameObserver.notifyAll(emptyStates);

        assertNull("Observer should not be notified",
                observer1.getLastNotification());
        assertEquals("Notification count should be 0",
                0, observer1.getNotificationCount());
    }

    @Test
    public void testClearAllObservers() {
        gameObserver.registerObserver(1, observer1);
        gameObserver.registerObserver(2, observer2);

        assertEquals("Should have 2 observers", 2, gameObserver.getObserverCount());

        gameObserver.clearAllObservers();

        assertEquals("Should have 0 observers after clear", 0, gameObserver.getObserverCount());
        assertFalse("Observer 1 should not be registered", gameObserver.hasObserver(1));
        assertFalse("Observer 2 should not be registered", gameObserver.hasObserver(2));
    }

    @Test
    public void testObserverException() {
        // Observer that throws exception
        TerraFuturaObserverInterface faultyObserver = new TerraFuturaObserverInterface() {
            @Override
            public void notify(String gameState) {
                throw new RuntimeException("Test exception");
            }
        };

        gameObserver.registerObserver(1, faultyObserver);
        gameObserver.registerObserver(2, observer2);

        Map<Integer, String> states = new HashMap<>();
        states.put(1, "State 1");
        states.put(2, "State 2");

        // Should not throw - exception is caught internally
        gameObserver.notifyAll(states);

        // Observer 2 should still receive notification despite observer 1 failing
        assertEquals("Observer 2 should receive notification",
                "State 2", observer2.getLastNotification());
    }

    @Test
    public void testMultiplePlayersIndependentNotifications() {
        gameObserver.registerObserver(1, observer1);
        gameObserver.registerObserver(2, observer2);

        // Notify only player 1
        gameObserver.notifyPlayer(1, "Player 1 message");

        assertEquals("Observer 1 should receive message",
                "Player 1 message", observer1.getLastNotification());
        assertNull("Observer 2 should not receive message",
                observer2.getLastNotification());

        // Now notify only player 2
        gameObserver.notifyPlayer(2, "Player 2 message");

        assertEquals("Observer 2 should receive message",
                "Player 2 message", observer2.getLastNotification());
        assertEquals("Observer 1 should keep old message",
                "Player 1 message", observer1.getLastNotification());
    }

    @Test
    public void testObserverReceivesOnlyOwnState() {
        gameObserver.registerObserver(1, observer1);
        gameObserver.registerObserver(2, observer2);

        Map<Integer, String> states = new HashMap<>();
        states.put(1, "Secret state for player 1");
        states.put(2, "Secret state for player 2");

        gameObserver.notifyAll(states);

        // Each observer should receive only their own state
        assertEquals("Observer 1 should receive only player 1 state",
                "Secret state for player 1", observer1.getLastNotification());
        assertEquals("Observer 2 should receive only player 2 state",
                "Secret state for player 2", observer2.getLastNotification());
    }
}