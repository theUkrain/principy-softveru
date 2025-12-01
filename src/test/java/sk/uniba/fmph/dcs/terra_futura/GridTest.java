package sk.uniba.fmph.dcs.terra_futura;

import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import sk.uniba.fmph.dcs.terra_futura.ActivationPattern;
import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Deck;
import sk.uniba.fmph.dcs.terra_futura.Samostatne.Grid;
import sk.uniba.fmph.dcs.terra_futura.tiles.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class GridTest {
    @Test
    @DisplayName("Test method: canPutCard & putCard")
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

        // put
        grid.putCard(new GridPosition(-2, -2), CardFactory.card(0, null, null, new CardSource(0, Deck.I)));

        Assertions.assertFalse(grid.canPutCard(new GridPosition(-2, -2)));
        Assertions.assertFalse(grid.canPutCard(new GridPosition(2, 2)));

        grid.putCard(new GridPosition(-1, -2), CardFactory.card(0, null, null, new CardSource(0, Deck.I)));

        Assertions.assertFalse(grid.canPutCard(new GridPosition(-1, -2)));
        Assertions.assertTrue(grid.canPutCard(new GridPosition(-1, -1)));

        Assertions.assertThrows(IllegalArgumentException.class, () -> grid.putCard(new GridPosition(0, 1), CardFactory.card(0, null, null, new CardSource(0, Deck.I))));

        Assertions.assertFalse(grid.canPutCard(new GridPosition(0, 1)));
        Assertions.assertTrue(grid.canPutCard(new GridPosition(0, -2)));

        Assertions.assertThrows(IllegalArgumentException.class, () -> grid.putCard(new GridPosition(-2, 2), CardFactory.card(0, null, null, new CardSource(0, Deck.I))));

        grid.putCard(new GridPosition(-1, -1), CardFactory.card(0, null, null, new CardSource(0, Deck.I)));

        Assertions.assertFalse(grid.canPutCard(new GridPosition(-1, -1)));
        Assertions.assertTrue(grid.canPutCard(new GridPosition(-1, 0)));
    }

    @Test
    @DisplayName("Test method: setActivationPattern & getActivatedCards")
    public void testGetActivatedCards() {
        Grid grid = new Grid();

        List<GridPosition> pattern = new ArrayList<>();
        pattern.add(new GridPosition(-1, -1));
        pattern.add(new GridPosition(0, 0));
        pattern.add(new GridPosition(1, 1));

        ActivationPattern activationPattern = new ActivationPattern(grid, pattern);
        activationPattern.select();

        // *##
        // **#
        // *#*

        List<Card> inputCards = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            inputCards.add(CardFactory.card(0, null, null, new CardSource(0, Deck.I)));
        }

        grid.putCard(new GridPosition(1, 1), inputCards.get(0));
        grid.putCard(new GridPosition(2, 2), inputCards.get(1));
        grid.putCard(new GridPosition(0, 2), inputCards.get(2));
        grid.putCard(new GridPosition(0, 1), inputCards.get(3));

        Collection<Card> cards = grid.getActivatedCards();

        Assertions.assertTrue(cards.contains(inputCards.get(0)));
        Assertions.assertTrue(cards.contains(inputCards.get(1)));
        Assertions.assertFalse(cards.contains(inputCards.get(2)));
        Assertions.assertFalse(cards.contains(inputCards.get(3)));

        Assertions.assertTrue(cards.size() == 3);
    }
}
