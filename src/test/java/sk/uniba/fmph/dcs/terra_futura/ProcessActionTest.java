package sk.uniba.fmph.dcs.terra_futura;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;
import sk.uniba.fmph.dcs.terra_futura.effects.*;
import sk.uniba.fmph.dcs.terra_futura.process.*;
import sk.uniba.fmph.dcs.terra_futura.tiles.Card;
import sk.uniba.fmph.dcs.terra_futura.tiles.CardSource;
import sk.uniba.fmph.dcs.terra_futura.tiles.Grid;

import java.util.*;

public class ProcessActionTest {

    class TestCard implements Card {
        private boolean canPutPollutionResult = true;
        public int putPollutionCount = 0;
        public int receivedPollution = 0;
        private Map<Resource, Integer> currentResources = new HashMap<>();

        private boolean canGetResourcesResult = true;
        public int getResourcesCallCount = 0;
        public Map<Resource, Integer> receivedResources = new HashMap<>();

        private final int pollutionSpaces = 10;
        private int curPollution = 0;
        private boolean isOverPollutedResult = false;

        public void setCanPutPollution(boolean value) {
            this.canPutPollutionResult = value;
        }

        public void setOverPolluted(boolean value) {
            this.isOverPollutedResult = value;
        }

        public void setInitialResources(Map<Resource, Integer> initialResources) {
            this.currentResources.putAll(initialResources);
        }

        public Map<Resource, Integer> takeResources() {
            return currentResources;
        }


        @Override
        public boolean canPutPollution(int amount) {
            return canPutPollutionResult;
        }

        @Override
        public void putPollution(int amount) {
            putPollutionCount++;
            receivedPollution += amount;
        }

        @Override
        public boolean canGetResources(Map<Resource, Integer> resources) {
            if (isOverPolluted()) return false;
            for (Map.Entry<Resource, Integer> entry : resources.entrySet()) {
                if (currentResources.getOrDefault(entry.getKey(), 0) < entry.getValue()) return false;
            }
            return canGetResourcesResult;
        }

        @Override
        public void getResources(Map<Resource, Integer> resources) {
            getResourcesCallCount++;
            if (!canGetResources(resources)) throw new IllegalArgumentException("Cannot get resources.");
            for (Map.Entry<Resource, Integer> entry : resources.entrySet()) {
                currentResources.put(entry.getKey(), currentResources.get(entry.getKey()) - entry.getValue());
            }
            currentResources.entrySet().removeIf(e -> e.getValue() == 0);
        }

        @Override
        public boolean canPutResources(Map<Resource, Integer> resources) {
            return true;
        }

        @Override
        public void putResources(Map<Resource, Integer> resources) {
            for (Map.Entry<Resource, Integer> entry : resources.entrySet()) {
                currentResources.merge(entry.getKey(), entry.getValue(), Integer::sum);
            }
        }

        @Override
        public boolean isOverPolluted() {
            return isOverPollutedResult || curPollution >= pollutionSpaces;
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
        public CardSource getCardSource() {
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
        public Map<Resource, Integer> getCurResources() {
            return currentResources;
        }

        @Override
        public boolean hasAssistance() {
            return false;
        }
    }
    class TestRawMaterialProducer extends RawMaterialProducer {
        public boolean wasCalled = false;

        public TestRawMaterialProducer(Resource guaranteedOutputs) {
            super(guaranteedOutputs);
        }

        @Override
        public void execute() {
            wasCalled = true;
        }

        @Override
        public void setCard(Card card) {
            super.setCard(card);
        }
    }

    class TestTransformationFixed extends TransformationFixed {
        public boolean wasCalled = false;
        private final int pollutionToReturn;

        public TestTransformationFixed(int pollutionToReturn) {
            super(Map.of(Resource.RED, 1), Map.of(Resource.MONEY, 1), pollutionToReturn);
            this.pollutionToReturn = pollutionToReturn;
        }

        @Override
        public int execute(Map<Resource, List<Pair<Card, Integer>>> cards) {
            wasCalled = true;
            return pollutionToReturn;
        }
    }

    class TestPollutionTransfer extends PollutionTransfer {
        public boolean wasCalled = false;
        public List<Pair<Card, Integer>> receivedCards = null;

        @Override
        public void execute(List<Pair<Card, Integer>> cards) {
            wasCalled = true;
            receivedCards = cards;
        }

        @Override
        public void setCard(Card card) {
            super.setCard(card);
        }
    }

    class TestProcessActionDeliver extends ProcessActionDeliver {
        public Effect processedEffect = null;
        public Grid processedGrid = null;

        public TestProcessActionDeliver() {
            super(System.in);
        }

        public void process(Effect effect, Grid grid) {
            this.processedEffect = effect;
            this.processedGrid = grid;
        }

        @Override public void process(RawMaterialProducer effect){}
        @Override public void process(TransformationFixed effect){}
        @Override public void process(Exchange effect){}
        @Override public void process(EffectOr effect){}
        @Override public void process(AssistanceEffect effect){}
        @Override public void process(PollutionTransfer effect){}
    }

    class TestExchange extends Exchange {
        public boolean wasCalled = false;
        public Map<Resource, List<Pair<Integer, Card>>> receivedInput = null;
        public Set<Pair<Resource, Integer>> receivedOutput = null;
        private final int pollutionToReturn;

        public TestExchange(int pollutionToReturn) {
            super(Set.of(Set.of(Pair.of(Resource.RED, 1))), Set.of(Set.of(Pair.of(Resource.MONEY, 1))));
            this.pollutionToReturn = pollutionToReturn;
        }

        @Override
        public int execute(Map<Resource, List<Pair<Integer, Card>>> input, Set<Pair<Resource, Integer>> output) {
            wasCalled = true;
            receivedInput = input;
            receivedOutput = output;
            return pollutionToReturn;
        }

        @Override
        public void setCard(Card card) {
            super.setCard(card);
        }
    }
    @Test
    @DisplayName("PocessActionRawMaterialProducer Test")
    public void rawMaterialProducerTest() {
        Resource testResource = Resource.GREEN;
        TestRawMaterialProducer testProducer = new TestRawMaterialProducer(testResource);

        testProducer.setCard(new TestCard());

        ProcessActionRawMaterialProducer action = new ProcessActionRawMaterialProducer(testProducer);

        int pollution = action.activateCard();

        Assertions.assertTrue(testProducer.wasCalled,
                "testProducer was not called");

        Assertions.assertEquals(0, pollution,
                "activateCard() expected 0, yours: " + pollution);
    }

    @Test
    @DisplayName("ProcessActionTransformationFixed Test")
    public void transformationFixedTest() {
        int expectedPollution = 5;
        TestTransformationFixed testEffect = new TestTransformationFixed(expectedPollution);

        Map<Resource, List<Pair<Card, Integer>>> testCard = Map.of(
                Resource.RED, List.of(Pair.of(new TestCard(), 1))
        );

        testEffect.setCard(new TestCard());

        ProcessActionTransformationFixed action = new ProcessActionTransformationFixed(testEffect, testCard);

        int actualPollution = action.activateCard();

        Assertions.assertTrue(testEffect.wasCalled,
                "execute() on TransformationFixed expected be called");
        Assertions.assertEquals(expectedPollution, actualPollution,
                "activateCard() expected return pollution, from execute()");
    }

    @Test
    @DisplayName("ProcessActionPollutionTransfer Test")
    public void pollutionTransferTest() {
        TestCard card1 = new TestCard();
        TestCard card2 = new TestCard();

        List<Pair<Card, Integer>> cardsToProcess = List.of(
                Pair.of(card1, 4),
                Pair.of(card2, 1)
        );

        TestPollutionTransfer testEffect = new TestPollutionTransfer();

        testEffect.setCard(new TestCard());

        ProcessActionPollutionTransfer action = new ProcessActionPollutionTransfer(testEffect, cardsToProcess);

        int pollution = action.activateCard();

        Assertions.assertTrue(testEffect.wasCalled,
                "execute() on PollutionTransfer expected be called");

        Assertions.assertSame(cardsToProcess, testEffect.receivedCards,
                "CardList, in execute(), expected be saved as input list");

        Assertions.assertEquals(0, pollution,
                "activateCard() expected return 0");
    }

    @Test
    @DisplayName("ProcessActionDeliver placePollution Test")
    public void placePollutionDeliverTest() {
        TestCard testCard1 = new TestCard();
        TestCard testCard2 = new TestCard();
        List<Pair<Card, Integer>> placement = List.of(Pair.of(testCard1, 2), Pair.of(testCard2, 3));

        ProcessActionDeliver deliver = new ProcessActionDeliver(System.in);

        deliver.placePollution(placement);

        Assertions.assertEquals(1, testCard1.putPollutionCount, "put expected be evoked in testCard1");
        Assertions.assertEquals(2, testCard1.receivedPollution, "expected pollution in testCard1: 2, your: " + testCard1.receivedPollution);
        Assertions.assertEquals(1, testCard2.putPollutionCount, "put expected be evoked in testCard2");
        Assertions.assertEquals(3, testCard2.receivedPollution, "expected pollution in testCard1: 3, your: " + testCard2.receivedPollution);
    }

    @Test
    @DisplayName("ProcessActionEffectOr Test")
    public void effectOrProcessActionTest() {
        SetCardToEffect testEffect1 = new TestRawMaterialProducer(Resource.UNIVERSAL);
        SetCardToEffect testEffect2 = new TestRawMaterialProducer(Resource.MONEY);

        EffectOr testEffectOr = new EffectOr(testEffect1, testEffect2);

        TestProcessActionDeliver testDeliver = new TestProcessActionDeliver();
        Grid testGrid = new Grid();

        int selectedIndex = 1;
        ProcessActionEffectOr action =
                new ProcessActionEffectOr(testEffectOr, selectedIndex, testDeliver, testGrid);

        int pollution = action.activateCard();

        Assertions.assertEquals(0, pollution,
                "activateCard() expected return 0");

        Assertions.assertSame(testEffect2, testDeliver.processedEffect,
                "Deliver expected second effect (index 1)");

        Assertions.assertSame(testGrid, testDeliver.processedGrid,
                "Deliver expected reference on Grid that given from ProcessActionEffectOr");
    }

    @Test
    @DisplayName("ProcessActionExchange Test")
    public void exchangeTest() {
        Set<Set<Pair<Resource, Integer>>> allowedInputs = Set.of(Set.of(Pair.of(Resource.RED, 2)));
        Set<Set<Pair<Resource, Integer>>> allowedOutputs = Set.of(Set.of(Pair.of(Resource.MONEY, 5), Pair.of(Resource.POLLUTION, 3)));

        Exchange exchangeEffect = new Exchange(allowedInputs, allowedOutputs);
        TestCard outputCard = new TestCard();
        outputCard.setInitialResources(Map.of(Resource.GREEN, 1));
        exchangeEffect.setCard(outputCard);

        TestCard inputCard1 = new TestCard();
        inputCard1.setInitialResources(Map.of(Resource.RED, 5));
        TestCard inputCard2 = new TestCard();
        inputCard2.setInitialResources(Map.of(Resource.RED, 1));

        int expectedPollution = 3;

        Map<Resource, List<Pair<Integer, Card>>> processActionInput = Map.of(
                Resource.RED, List.of(Pair.of(1, inputCard1), Pair.of(1, inputCard2))
        );

        Set<Pair<Resource, Integer>> processActionOutput = Set.of(
                Pair.of(Resource.MONEY, 5),
                Pair.of(Resource.POLLUTION, 3)
        );

        ProcessActionExchange action = new ProcessActionExchange(exchangeEffect, processActionInput, processActionOutput);

        int actualPollution = action.activateCard();

        Assertions.assertEquals(expectedPollution, actualPollution,
                "activateCard() expected Pollution from execute()");

        Assertions.assertEquals(4, inputCard1.getCurResources().get(Resource.RED),
                "Card1 expected 4 RED (5 - 1)");
        Assertions.assertFalse(inputCard2.getCurResources().containsKey(Resource.RED),
                "Card2 expected 0 RED (1 - 1)");

        Assertions.assertEquals(5, outputCard.getCurResources().get(Resource.MONEY),
                "Output Card expected 5 MONEY");
        Assertions.assertEquals(1, outputCard.getCurResources().get(Resource.GREEN),
                "Output Card expected 1 GREEN (exist)");

        TestCard cardGreen = new TestCard();
        cardGreen.setInitialResources(Map.of(Resource.GREEN, 1));

        Map<Resource, List<Pair<Integer, Card>>> invalidInput = Map.of(
                Resource.RED, List.of(Pair.of(1, inputCard1)),
                Resource.GREEN, List.of(Pair.of(1, cardGreen))
        );
        Set<Pair<Resource, Integer>> validOutput = Set.of(Pair.of(Resource.MONEY, 5));

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            exchangeEffect.execute(invalidInput, validOutput);
        }, "Expected IllegalArgumentException, input is not allowed");

        inputCard1.setInitialResources(Map.of(Resource.RED, 2));

        Map<Resource, List<Pair<Integer, Card>>> validInput = Map.of(
                Resource.RED, List.of(Pair.of(2, inputCard1))
        );

        Set<Pair<Resource, Integer>> invalidOutput = Set.of(Pair.of(Resource.MONEY, 2));

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            exchangeEffect.execute(validInput, invalidOutput);
        }, "Expected IllegalArgumentException, output is not allowed");
    }

    @Test
    @DisplayName("ProcessAction Failure Test")
    public void processActionFailureTest() {
        TestCard card = new TestCard();
        card.setInitialResources(Map.of(Resource.RED, 1));

        Map<Resource, Integer> request = Map.of(Resource.RED, 2);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            card.getResources(request);
        }, "getResources expected exception not enough resources");

        TestCard overPollutedCard = new TestCard();
        overPollutedCard.setInitialResources(Map.of(Resource.RED, 5));
        overPollutedCard.setOverPolluted(true);

        Map<Resource, Integer> requestPolluted = Map.of(Resource.RED, 1);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            overPollutedCard.getResources(requestPolluted);
        }, "getResources expected exception, card is over-polluted");

        TestPollutionTransfer testEffectTransfer = new TestPollutionTransfer() {
            @Override
            public void execute(List<Pair<Card, Integer>> cards) {
                cards.forEach(pair -> {
                    if (!pair.getKey().canPutPollution(pair.getValue())) {
                        throw new IllegalArgumentException("Target card cannot accept pollution");
                    }
                    pair.getKey().putPollution(pair.getValue());
                });
            }
        };
        testEffectTransfer.setCard(new TestCard());

        TestCard targetCard = new TestCard();
        targetCard.setCanPutPollution(false);

        List<Pair<Card, Integer>> cardsToProcess = List.of(
                Pair.of(targetCard, 4)
        );

        ProcessActionPollutionTransfer actionTransfer = new ProcessActionPollutionTransfer(testEffectTransfer, cardsToProcess);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            actionTransfer.activateCard();
        }, "PollutionTransfer expected exception, target card cannot accept pollution");
    }
}