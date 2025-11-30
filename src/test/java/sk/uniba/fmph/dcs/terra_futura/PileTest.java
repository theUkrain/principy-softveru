package sk.uniba.fmph.dcs.terra_futura;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Assertions;
import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;
import sk.uniba.fmph.dcs.terra_futura.tiles.Card;
import sk.uniba.fmph.dcs.terra_futura.tiles.CardSource;
import sk.uniba.fmph.dcs.terra_futura.tiles.Pile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PileTest {
    private class TestCard implements Card {
        @Override
        public boolean isOverPolluted() {
            return false;
        }

        @Override
        public boolean canGetResources(Map<Resource, Integer> resources) {
            return false;
        }

        @Override
        public void getResources(Map<Resource, Integer> resources) {

        }

        @Override
        public boolean canPutResources(Map<Resource, Integer> resources) {
            return false;
        }

        @Override
        public void putResources(Map<Resource, Integer> resources) {

        }

        @Override
        public CardSource getCardSource() {
            return null;
        }

        @Override
        public boolean hasAssistance() {
            return false;
        }
    }

    @Test
    @DisplayName("Test method: getCard")
    public void testGetCard() {
        // Data set
        List<Card> input = new ArrayList<Card>();

        for (int i = 0; i < 20; i++) {
            input.add(new TestCard());
        }

        // Test for exceptions
        Pile pile = new Pile(input);

        for (int i = 0; i < 20; i++) {
            Optional<Card> card = pile.getCard(i);
            Assertions.assertTrue(card.isPresent());
            System.out.println(card.get());
        }

        Assertions.assertThrows(ArrayIndexOutOfBoundsException.class, () -> pile.getCard(0));
    }

    @Test
    @DisplayName("Test method: discardCard")
    public void testDiscardCard() {
        // Data set
        List<Card> input = new ArrayList<Card>();

        for (int i = 0; i < 20; i++) {
            input.add(new TestCard());
        }

        // Test for exceptions
        Pile pile = new Pile(input);

        for (int i = 0; i < 20; i++) {
            pile.discardCard();
        }

        for (int i = 0; i < 20; i++) {
            pile.getCard(0);
        }

        Assertions.assertThrows(ArrayIndexOutOfBoundsException.class, () -> pile.discardCard());
    }
}
