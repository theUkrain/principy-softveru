package sk.uniba.fmph.dcs.terra_futura;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Deck;
import sk.uniba.fmph.dcs.terra_futura.tiles.*;
import sk.uniba.fmph.dcs.terra_futura.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GridTest {
    @Test
    public void occupiedLotException() {
        Grid grid = new Grid();
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            grid.putCard(new GridPosition(0, 0), null);
        });
    }

    @Test
    public void boundariesExceptionTest0() {
        Grid grid = new Grid();
        Assertions.assertThrows(IllegalArgumentException.class, () ->
        {
            grid.putCard(new GridPosition(2, 0), null);
            grid.putCard(new GridPosition(-2, 0), null);
        });
    }

    @Test
    public void boundariesExceptionTest1() {
        Grid grid = new Grid();
        Assertions.assertThrows(IllegalArgumentException.class, () ->
        {
            grid.putCard(new GridPosition(2, 2), null);
            grid.putCard(new GridPosition(-2, -2), null);
        });
    }

    @Test
    public void activationPatternTest0() {
        Grid grid = new Grid();
        grid.putCard(new GridPosition(2, -2), new TestingCard("1"));
        grid.putCard(new GridPosition(2, -1), new TestingCard("2"));
        grid.putCard(new GridPosition(1, -1), new TestingCard("3"));
        ActivationPattern activationPattern = new ActivationPattern(grid, Set.of(new GridPosition(1, 1), new GridPosition(1, -1),
                new GridPosition(-1, -1), new GridPosition(-1, 1)));

        activationPattern.select();

        grid.getActivatedCards().equals(Set.of(new TestingCard("1"), new TestingCard("2")));

    }

    @Test
    public void testCanPutCard() {
        Grid grid = new Grid();

        // can put on start card
        Assertions.assertFalse(grid.canPutCard(new GridPosition(0, 0)));

        // can put on side
        Assertions.assertTrue(grid.canPutCard(new GridPosition(-1, 0)));
        Assertions.assertTrue(grid.canPutCard(new GridPosition(1, 0)));

        Assertions.assertTrue(grid.canPutCard(new GridPosition(0, -1)));
        Assertions.assertTrue(grid.canPutCard(new GridPosition(0, 1)));

        Assertions.assertTrue(grid.canPutCard(new GridPosition(-1, 1)));
        Assertions.assertTrue(grid.canPutCard(new GridPosition(1, 1)));

        Assertions.assertTrue(grid.canPutCard(new GridPosition(-2, 2)));
        Assertions.assertTrue(grid.canPutCard(new GridPosition(-2, -2)));

        List<Card> cards = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            cards.add(CardFactory.card(0, null, null, new CardSource(0, Deck.I)));
        }

        // put
        grid.putCard(new GridPosition(-2, -2), cards.get(0));

        Assertions.assertFalse(grid.canPutCard(new GridPosition(-2, -2)));
        Assertions.assertFalse(grid.canPutCard(new GridPosition(2, 2)));

        grid.putCard(new GridPosition(-1, -2), cards.get(1));

        Assertions.assertFalse(grid.canPutCard(new GridPosition(-1, -2)));
        Assertions.assertTrue(grid.canPutCard(new GridPosition(-1, -1)));

        Assertions.assertThrows(IllegalArgumentException.class, () -> grid.putCard(new GridPosition(0, 1), CardFactory.card(0, null, null, new CardSource(0, Deck.I))));

        Assertions.assertFalse(grid.canPutCard(new GridPosition(0, 1)));
        Assertions.assertTrue(grid.canPutCard(new GridPosition(0, -2)));

        Assertions.assertThrows(IllegalArgumentException.class, () -> grid.putCard(new GridPosition(-2, 2), CardFactory.card(0, null, null, new CardSource(0, Deck.I))));

        Set<Card> activatedCards = grid.putCard(new GridPosition(-1, -1), cards.get(2));

        Assertions.assertFalse(grid.canPutCard(new GridPosition(-1, -1)));
        Assertions.assertTrue(grid.canPutCard(new GridPosition(-1, 0)));

        // process activation
        Assertions.assertEquals(2, activatedCards.size());
        Assertions.assertTrue(activatedCards.contains(cards.get(1)));
        Assertions.assertTrue(activatedCards.contains(cards.get(2)));
        Assertions.assertFalse(activatedCards.contains(cards.get(0)));

        activatedCards = grid.putCard(new GridPosition(-2, -1), cards.get(3));

        Assertions.assertEquals(3, activatedCards.size());
        Assertions.assertTrue(activatedCards.contains(cards.get(0)));
        Assertions.assertTrue(activatedCards.contains(cards.get(2)));
        Assertions.assertFalse(activatedCards.contains(cards.get(1)));
    }
}
