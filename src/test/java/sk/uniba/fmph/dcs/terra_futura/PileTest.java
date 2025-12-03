package sk.uniba.fmph.dcs.terra_futura;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;
import sk.uniba.fmph.dcs.terra_futura.effects.Effect;
import sk.uniba.fmph.dcs.terra_futura.tiles.Card;
import sk.uniba.fmph.dcs.terra_futura.tiles.CardSource;
import sk.uniba.fmph.dcs.terra_futura.tiles.Pile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class PileTest {
    private List<Card> generateInput(int n) {
        List<Card> input = new ArrayList<>();
        for (int i = 0; i < n; ++i) {
            input.add(new TestCard());
        }
        return input;
    }

    @Test
    @DisplayName("Test method: getCard")
    public void testGetCard() {
        List<Card> input = generateInput(20);
        Pile tpile = new Pile(input);

        for (int i = 0; i < 20; ++i) {
            Optional<Card> tcard = tpile.getCard(i);
            assertTrue(tcard.isPresent());
        }


        input = generateInput(20);
        final Pile pile2 = new Pile(input);
        for (int i = 0; i < 20; ++i) {
            pile2.getCard(i);
        }
        Assertions.assertThrows(IllegalArgumentException.class, () -> pile2.getCard(0));

        input = generateInput(3);
        Pile pile3 = new Pile(input);
        Card c1 = (pile3.getCard(0)).get();
        Card c2 = (pile3.getCard(0)).get();
        assertTrue(c1 != c2);

        input = generateInput(50);
        Pile pile4 = new Pile(input);
        int extracted = 0;
        for (int i = 0; i < 50; ++i) {
            if (i % 2 == 0) {
                ++extracted;
                pile4.getCard(0);
            } else {
                pile4.discardCard();
            }
        }
        assertEquals(25, extracted);
        Optional<Card> ct = pile4.getCard(0);
        assertTrue(ct.isPresent());

        input = generateInput(25);
        Pile pile5 = new Pile(input);
        for (int i = 0; i < 25; ++i) {
            assertDoesNotThrow(() -> pile5.discardCard());
        }
    }

    private class TestCard implements Card {

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
        public boolean isOverPolluted() {
            return false;
        }

        @Override
        public CardSource getCardSource() {
            return null;
        }

        @Override
        public boolean hasAssistance() {
            return false;
        }

        @Override
        public Effect getUpper() {
            return null;
        }

        @Override
        public Effect getLower() {
            return null;
        }

        @Override
        public Map<Resource, Integer> getCurResources() {
            return null;
        }

        @Override
        public boolean canGetPollution(int amount) {
            return true;
        }

        @Override
        public void getPollution(int amount) {
        }

        @Override
        public boolean canPutPollution(int amount) {
            return true;
        }

        @Override
        public void putPollution(int amount) {
        }
    }
}