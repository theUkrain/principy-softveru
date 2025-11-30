package sk.uniba.fmph.dcs.terra_futura;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GridTest {

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

    private Card createMockCard(String id) {
        return new MockCard(id);
    }

    @Test
    public void testGridInitialization() {
        Card startingCard = createMockCard("Start");
        Grid grid = new Grid(startingCard);

        GridPosition center = new GridPosition(0, 0);
        Optional<Card> card = grid.getCard(center);

        assertTrue("Starting card should be at center", card.isPresent());
        assertEquals("Should be the starting card", startingCard, card.get());
    }

    @Test
    public void testGetCardValidPosition() {
        Card startingCard = createMockCard("Start");
        Grid grid = new Grid(startingCard);

        // Center should have card
        assertTrue("Center should have card",
                grid.getCard(new GridPosition(0, 0)).isPresent());

        // Adjacent positions should be empty
        assertFalse("Adjacent position should be empty",
                grid.getCard(new GridPosition(1, 0)).isPresent());
    }

    @Test
    public void testGetCardInvalidPosition() {
        Card startingCard = createMockCard("Start");
        Grid grid = new Grid(startingCard);

        // Out of bounds positions - should throw in GridPosition constructor
        // or return empty if position validation happens in getCard
        assertFalse("Far position should return empty",
                grid.getCard(new GridPosition(-2, -2)).isPresent());
    }

    @Test
    public void testCanPutCardAdjacentToCenter() {
        Card startingCard = createMockCard("Start");
        Grid grid = new Grid(startingCard);

        // Adjacent positions to center should be valid
        assertTrue("Right of center should be valid",
                grid.canPutCard(new GridPosition(1, 0)));
        assertTrue("Left of center should be valid",
                grid.canPutCard(new GridPosition(-1, 0)));
        assertTrue("Above center should be valid",
                grid.canPutCard(new GridPosition(0, 1)));
        assertTrue("Below center should be valid",
                grid.canPutCard(new GridPosition(0, -1)));
    }

    @Test
    public void testCanPutCardNotAdjacent() {
        Card startingCard = createMockCard("Start");
        Grid grid = new Grid(startingCard);

        // Non-adjacent positions should be invalid
        assertFalse("Diagonal should be invalid",
                grid.canPutCard(new GridPosition(1, 1)));
        assertFalse("Far position should be invalid",
                grid.canPutCard(new GridPosition(2, 0)));
    }

    @Test
    public void testCanPutCardOccupiedPosition() {
        Card startingCard = createMockCard("Start");
        Grid grid = new Grid(startingCard);

        // Center is occupied
        assertFalse("Occupied position should be invalid",
                grid.canPutCard(new GridPosition(0, 0)));
    }

    @Test
    public void testPutCard() {
        Card startingCard = createMockCard("Start");
        Card newCard = createMockCard("New");
        Grid grid = new Grid(startingCard);

        GridPosition pos = new GridPosition(1, 0);
        grid.putCard(pos, newCard);

        Optional<Card> retrievedCard = grid.getCard(pos);
        assertTrue("Card should be present", retrievedCard.isPresent());
        assertEquals("Should be the new card", newCard, retrievedCard.get());
    }

    @Test(expected = IllegalStateException.class)
    public void testPutCardInvalidPosition() {
        Card startingCard = createMockCard("Start");
        Card newCard = createMockCard("New");
        Grid grid = new Grid(startingCard);

        // Try to put card at non-adjacent position
        grid.putCard(new GridPosition(2, 0), newCard);
    }

    @Test
    public void testPutCardExpandsGrid() {
        Card startingCard = createMockCard("Start");
        Grid grid = new Grid(startingCard);

        // Place cards to expand grid
        Card card1 = createMockCard("Card1");
        Card card2 = createMockCard("Card2");

        grid.putCard(new GridPosition(1, 0), card1);

        // Now (2, 0) should be valid because it's adjacent to (1, 0)
        assertTrue("Should be valid after expansion",
                grid.canPutCard(new GridPosition(2, 0)));

        grid.putCard(new GridPosition(2, 0), card2);
        assertTrue("Card should be placed",
                grid.getCard(new GridPosition(2, 0)).isPresent());
    }

    @Test
    public void testCanBeActivated() {
        Card startingCard = createMockCard("Start");
        Grid grid = new Grid(startingCard);

        GridPosition center = new GridPosition(0, 0);

        // Should be able to activate initially
        assertTrue("Should be able to activate", grid.canBeActivated(center));

        // After activation, should not be able to activate again
        grid.setActivated(center);
        assertFalse("Should not be able to activate twice",
                grid.canBeActivated(center));
    }

    @Test
    public void testCanBeActivatedNonExistentCard() {
        Card startingCard = createMockCard("Start");
        Grid grid = new Grid(startingCard);

        assertFalse("Non-existent card should not be activatable",
                grid.canBeActivated(new GridPosition(1, 0)));
    }

    @Test(expected = IllegalStateException.class)
    public void testSetActivatedInvalidCard() {
        Card startingCard = createMockCard("Start");
        Grid grid = new Grid(startingCard);

        // Try to activate non-existent card
        grid.setActivated(new GridPosition(1, 0));
    }

    @Test(expected = IllegalStateException.class)
    public void testSetActivatedTwice() {
        Card startingCard = createMockCard("Start");
        Grid grid = new Grid(startingCard);

        GridPosition center = new GridPosition(0, 0);
        grid.setActivated(center);
        grid.setActivated(center); // Should throw
    }

    @Test
    public void testEndTurn() {
        Card startingCard = createMockCard("Start");
        Grid grid = new Grid(startingCard);

        GridPosition center = new GridPosition(0, 0);

        // Activate card
        grid.setActivated(center);
        assertFalse("Should not be activatable after activation",
                grid.canBeActivated(center));

        // End turn
        grid.endTurn();

        // Should be activatable again
        assertTrue("Should be activatable after end turn",
                grid.canBeActivated(center));
    }

    @Test
    public void testSetActivationPattern() {
        Card startingCard = createMockCard("Start");
        Grid grid = new Grid(startingCard);

        List<GridPosition> pattern = List.of(
                new GridPosition(0, 0),
                new GridPosition(1, 0),
                new GridPosition(0, 1)
        );

        grid.setActivationPattern(pattern);

        List<GridPosition> retrieved = grid.getActivationPattern();
        assertEquals("Pattern should match", pattern, retrieved);
    }

    @Test
    public void testGetAllCards() {
        Card startingCard = createMockCard("Start");
        Card card1 = createMockCard("Card1");
        Grid grid = new Grid(startingCard);

        grid.putCard(new GridPosition(1, 0), card1);

        Map<GridPosition, Card> allCards = grid.getAllCards();
        assertEquals("Should have 2 cards", 2, allCards.size());
        assertTrue("Should contain center card",
                allCards.containsKey(new GridPosition(0, 0)));
        assertTrue("Should contain new card",
                allCards.containsKey(new GridPosition(1, 0)));
    }

    @Test
    public void testState() {
        Card startingCard = createMockCard("Start");
        Grid grid = new Grid(startingCard);

        String state = grid.state();
        assertNotNull("State should not be null", state);
        assertTrue("State should contain 'Grid'", state.contains("Grid"));
    }

    @Test
    public void testComplexGridBuilding() {
        Card startingCard = createMockCard("Start");
        Grid grid = new Grid(startingCard);

        // Build a cross pattern
        grid.putCard(new GridPosition(1, 0), createMockCard("Right"));
        grid.putCard(new GridPosition(-1, 0), createMockCard("Left"));
        grid.putCard(new GridPosition(0, 1), createMockCard("Top"));
        grid.putCard(new GridPosition(0, -1), createMockCard("Bottom"));

        assertEquals("Should have 5 cards", 5, grid.getAllCards().size());

        // Now corners should be valid
        assertTrue("Corner should be valid",
                grid.canPutCard(new GridPosition(1, 1)));
    }
}