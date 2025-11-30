package sk.uniba.fmph.dcs.terra_futura;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class PileTest {

    private static class MockCard extends Card {
        private final String id;

        public MockCard(String id) {
            super(new ArrayList<>(), 0);
            this.id = id;
        }

        @Override
        public String state() {
            return "Card[" + id + "]";
        }
    }

    private List<Card> createTestCards(int count) {
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            cards.add(new MockCard("Card" + i));
        }
        return cards;
    }

    @Test
    public void testPileInitialization() {
        List<Card> cards = createTestCards(10);
        Random random = new Random(42);
        Pile pile = new Pile(cards, random);

        // Should have 4 visible cards
        for (int i = 0; i < 4; i++) {
            assertTrue("Visible card " + i + " should be present",
                    pile.getCard(i).isPresent());
        }

        // Should have 6 hidden cards
        assertEquals("Should have 6 hidden cards", 6, pile.getHiddenCardsCount());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPileInitializationWithTooFewCards() {
        List<Card> cards = createTestCards(3);
        new Pile(cards);
    }

    @Test
    public void testGetCard() {
        List<Card> cards = createTestCards(10);
        Random random = new Random(42);
        Pile pile = new Pile(cards, random);

        // Valid indices
        for (int i = 0; i < 4; i++) {
            Optional<Card> card = pile.getCard(i);
            assertTrue("Card at index " + i + " should exist", card.isPresent());
        }

        // Invalid indices
        assertFalse("Negative index should return empty", pile.getCard(-1).isPresent());
        assertFalse("Out of bounds index should return empty", pile.getCard(4).isPresent());
    }

    @Test
    public void testTakeCard() {
        List<Card> cards = createTestCards(10);
        Random random = new Random(42);
        Pile pile = new Pile(cards, random);

        int initialHidden = pile.getHiddenCardsCount();

        // Take a valid card
        assertTrue("Should successfully take card", pile.takeCard(0));

        // Card should be refilled from hidden
        assertTrue("Card should be refilled", pile.getCard(0).isPresent());
        assertEquals("Hidden cards should decrease by 1",
                initialHidden - 1, pile.getHiddenCardsCount());
    }

    @Test
    public void testTakeCardInvalidIndex() {
        List<Card> cards = createTestCards(10);
        Pile pile = new Pile(cards);

        assertFalse("Should fail with negative index", pile.takeCard(-1));
        assertFalse("Should fail with out of bounds index", pile.takeCard(10));
    }

    @Test
    public void testRemoveLastCard() {
        List<Card> cards = createTestCards(10);
        Random random = new Random(42);
        Pile pile = new Pile(cards, random);

        int initialHidden = pile.getHiddenCardsCount();

        pile.removeLastCard();

        // Last card (index 3) should be refilled
        assertTrue("Last card should be refilled", pile.getCard(3).isPresent());
        assertEquals("Hidden cards should decrease",
                initialHidden - 1, pile.getHiddenCardsCount());
    }

    @Test
    public void testTakeAllCards() {
        List<Card> cards = createTestCards(10);
        Random random = new Random(42);
        Pile pile = new Pile(cards, random);

        // Take all cards
        for (int i = 0; i < 10; i++) {
            pile.takeCard(0);
        }

        assertEquals("Should have no hidden cards", 0, pile.getHiddenCardsCount());
        assertFalse("Should have no cards left", pile.hasCards());
    }

    @Test
    public void testHasCards() {
        List<Card> cards = createTestCards(5);
        Pile pile = new Pile(cards);

        assertTrue("Should have cards initially", pile.hasCards());

        // Take all cards
        for (int i = 0; i < 5; i++) {
            pile.takeCard(0);
        }

        assertFalse("Should have no cards after taking all", pile.hasCards());
    }

    @Test
    public void testState() {
        List<Card> cards = createTestCards(10);
        Pile pile = new Pile(cards);

        String state = pile.state();
        assertNotNull("State should not be null", state);
        assertTrue("State should contain 'Pile'", state.contains("Pile"));
        assertTrue("State should contain 'visible'", state.contains("visible"));
        assertTrue("State should contain 'hidden'", state.contains("hidden"));
    }

    @Test
    public void testRandomnessControlled() {
        List<Card> cards1 = createTestCards(10);
        List<Card> cards2 = createTestCards(10);

        Random random1 = new Random(42);
        Random random2 = new Random(42);

        Pile pile1 = new Pile(cards1, random1);
        Pile pile2 = new Pile(cards2, random2);

        // With same seed, should get same state
        assertEquals("Same seed should produce same state",
                pile1.state(), pile2.state());
    }

    @Test
    public void testRefillMaintainsVisibleCount() {
        List<Card> cards = createTestCards(10);
        Pile pile = new Pile(cards);

        // Take several cards
        pile.takeCard(0);
        pile.takeCard(1);
        pile.takeCard(2);

        // Should still have visible cards refilled
        int visibleCount = 0;
        for (int i = 0; i < 4; i++) {
            if (pile.getCard(i).isPresent()) {
                visibleCount++;
            }
        }

        assertEquals("Should maintain 4 visible cards", 4, visibleCount);
    }
}