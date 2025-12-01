package sk.uniba.fmph.dcs.terra_futura;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Deck;
import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;
import sk.uniba.fmph.dcs.terra_futura.effects.*;
import sk.uniba.fmph.dcs.terra_futura.tiles.Card;
import sk.uniba.fmph.dcs.terra_futura.tiles.CardFactory;
import sk.uniba.fmph.dcs.terra_futura.tiles.CardSource;

import java.util.*;

public class EffectsTest {
    private void automatedTestPutTransformationFixed() {
        List<Pair<Map<Resource, Integer>, Map<Resource, Integer>>> tests = new ArrayList<>();
        tests.add(Pair.of(Map.of(Resource.RED, 2), Map.of(Resource.GEAR, 1)));
        tests.add(Pair.of(Map.of(Resource.YELLOW, 1), Map.of(Resource.BULB, 2)));
        tests.add(Pair.of(Map.of(Resource.RED, 4), Map.of(Resource.CAR, 1)));
        tests.add(Pair.of(Map.of(Resource.RED, 2, Resource.MONEY, 1), Map.of(Resource.CAR, 1)));
        tests.add(Pair.of(Map.of(Resource.GREEN, 1, Resource.MONEY, 4), Map.of(Resource.CAR, 1, Resource.YELLOW, 2)));

        Card card = CardFactory.card(1, null, null, new CardSource(0, Deck.II));

        List<Card> cards = new ArrayList<>();

        for (int i = 0; i < tests.size(); i++) {
            for (Map.Entry<Resource, Integer> resources : tests.get(i).getLeft().entrySet()) {
                Card cardForInput = CardFactory.card(1, null, null, new CardSource(0, Deck.II));
                cardForInput.putResources(Map.of(resources.getKey(), resources.getValue()));
                cards.add(cardForInput);
            }
        }

        for (Pair<Map<Resource, Integer>, Map<Resource, Integer>> test : tests) {
            Map<Resource, Integer> input = new HashMap<>(test.getLeft());
            Map<Resource, Integer> output = test.getRight();

            TransformationFixed effect = new TransformationFixed(input, output, 0);
            effect.setCard(card);

            Map<Resource, List<Pair<Card, Integer>>> inputCards = new HashMap<>();
            Map<Card, Pair<Resource, Integer>> toAssert = new HashMap<>();

            for (Card cardToLook : cards) {
                for (Iterator<Map.Entry<Resource, Integer>> iterator = input.entrySet().iterator(); iterator.hasNext(); ) {
                    Map.Entry<Resource, Integer> resources = iterator.next();

                    if (cardToLook.canGetResources(Map.of(resources.getKey(), resources.getValue()))) {
                        toAssert.put(cardToLook, Pair.of(resources.getKey(), resources.getValue()));
                        iterator.remove();
                        inputCards.put(resources.getKey(), List.of(Pair.of(cardToLook, resources.getValue())));
                    }
                }

                if (input.isEmpty()) {
                    break;
                }
            }

            effect.execute(inputCards);
            Assertions.assertTrue(card.canGetResources(output));

            for (Map.Entry<Card, Pair<Resource, Integer>> resources : toAssert.entrySet()) {
                Assertions.assertFalse(resources.getKey().canGetResources(Map.of(resources.getValue().getKey(), resources.getValue().getValue())));
            }
        }
    }

    @Test
    @DisplayName("TransformationFixed test")
    public void testTransformationFixed() {
        // automated checks
        automatedTestPutTransformationFixed();

        // special checks
        Map<Resource, Integer> input = Map.of(Resource.RED, 2);
        Map<Resource, Integer> output = Map.of(Resource.CAR, 2);

        Card card = CardFactory.card(1, null, null, new CardSource(0, Deck.II));
        TransformationFixed effect = new TransformationFixed(input, output, 0);
        effect.setCard(card);

        card.putResources(Map.of(Resource.RED, 1));

        Assertions.assertThrows(IllegalStateException.class, () -> {effect.execute(Map.of(Resource.RED, List.of(Pair.of(card, 2))));});
    }

    public void automatedTestPutRawMaterialProducer() {
        List<Resource> tests = new ArrayList<>();
        tests.add(Resource.YELLOW);
        tests.add(Resource.RED);
        tests.add(Resource.GREEN);
        tests.add(Resource.MONEY);
        tests.add(Resource.YELLOW);

        Card card = CardFactory.card(1, null, null, new CardSource(0, Deck.II));

        for (Resource test : tests) {
            RawMaterialProducer effect = new RawMaterialProducer(test);
            effect.setCard(card);
            effect.execute();
            Assertions.assertTrue(card.canGetResources(Map.of(test, 1)));
        }
    }

    @Test
    @DisplayName("RawMaterialProducer test")
    public void testRawMaterialProducer() {
        // check if card produces correct resources
        automatedTestPutRawMaterialProducer();

        // TODO check pollution

        // special checks
        Resource test = Resource.BULB;
        RawMaterialProducer effect = new RawMaterialProducer(test);
        Card card = CardFactory.card(1, null, null, new CardSource(0, Deck.II));

        effect.setCard(card);
        effect.execute();
        Assertions.assertTrue(card.canGetResources(Map.of(test, 1)));
        Assertions.assertFalse(card.canGetResources(Map.of(Resource.RED, 2)));
        Assertions.assertFalse(card.canGetResources(Map.of(Resource.GREEN, 3)));

        test = Resource.MONEY;
        effect = new RawMaterialProducer(test);

        effect.setCard(card);
        effect.execute();
        Assertions.assertTrue(card.canGetResources(Map.of(test, 1)));
        Assertions.assertTrue(card.canGetResources(Map.of(Resource.BULB, 1)));

        test = Resource.RED;
        effect.execute();
        Assertions.assertFalse(card.canGetResources(Map.of(test, 1)));
    }

    @Test
    @DisplayName("StartingCardEffect test")
    public void testStartingCardEffectTrigger() {
        StartingCardEffect effect = new StartingCardEffect();

        Effect trigger1 = effect.execute(0);

        Assertions.assertInstanceOf(EffectOr.class, trigger1,
                "execute(0) must return EffectOr");

        Effect trigger2 = effect.execute(1);

        Assertions.assertInstanceOf(AssistanceEffect.class, trigger2,
                "execute(1) must return AssistanceEffect, cuz the second effect in EffectOr");

        EffectOr expectedEffect1 = new EffectOr(
                new RawMaterialProducer(Resource.UNIVERSAL),
                new RawMaterialProducer(Resource.MONEY));

        Assertions.assertTrue(trigger1.equals(expectedEffect1));
    }


    private static class TestCard implements Card{
        private boolean canGet = true;

        public int getResourcesCount = 0;
        public int putResourcesCount = 0;
        public Map<Resource, Integer> recievedResources = new HashMap<>();
        public void setCanGet(boolean canGet) {
            this.canGet = canGet;
        }

        @Override
        public boolean canGetResources(Map<Resource, Integer> resources) {
            return canGet;
        }

        @Override
        public void getResources(Map<Resource, Integer> resources) { getResourcesCount++;}

        @Override
        public boolean canPutResources(Map<Resource, Integer> resources) {
            return true;
        }

        @Override
        public void putResources(Map<Resource, Integer> resources) {
            putResourcesCount++;
            recievedResources.putAll(resources);
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
        public void getPollution(int amount) {}

        @Override
        public boolean canPutPollution(int amount) {
            return true;
        }

        @Override
        public void putPollution(int amount) {}
    }

    private static class TestableExchange extends Exchange {
        public TestableExchange(Set<Set<Pair<Resource, Integer>>> inputs, Set<Set<Pair<Resource, Integer>>> outputs) {
            super(inputs, outputs);
        }

        public void setTargetCard(Card card) {
            this.card = card;
        }
    }

    //public int execute(Map<Resource, List<Pair<Integer, Card>>> input, Set<Pair<Resource, Integer>> output)
    @Test
    @DisplayName("Exchange test")
    public void testExchange() {
        List<Pair<Integer, Card>> expectedOutputList = new ArrayList<>();
        TestCard testCardA = new TestCard();
        TestCard testCardB = new TestCard();
        Card testCardC = CardFactory.card(0,);
        Resource request = Resource.YELLOW;

        testCard.setCanGet(false);

        Map<Pair<Resource,Integer>, Card> input = Map.of(request, testCard);
        Exchange exchange = new Exchange(Set.of(input.keySet()), Set.of(expectedOutput));
    }

}
