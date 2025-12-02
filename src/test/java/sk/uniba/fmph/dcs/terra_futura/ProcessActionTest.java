package sk.uniba.fmph.dcs.terra_futura;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;
import sk.uniba.fmph.dcs.terra_futura.effects.*;
import sk.uniba.fmph.dcs.terra_futura.process.ProcessActionEffectOr;
import sk.uniba.fmph.dcs.terra_futura.process.ProcessActionRawMaterialProducer;
import sk.uniba.fmph.dcs.terra_futura.process.ProcessActionTransformationFixed;
import sk.uniba.fmph.dcs.terra_futura.process.ProcessActionPollutionTransfer;
import sk.uniba.fmph.dcs.terra_futura.tiles.Card;
import sk.uniba.fmph.dcs.terra_futura.tiles.CardSource;
import sk.uniba.fmph.dcs.terra_futura.tiles.Grid;

import java.util.*;

public class ProcessActionTest {

    class TestCard implements Card {
        private boolean canPutPollutionResult = true;
        public int putPollutionCount = 0;
        public int receivedPollution = 0;

        private boolean canGetResourcesResult = true;
        public int getResourcesCallCount = 0;
        public Map<Resource, Integer> receivedResources = new HashMap<>();

        public void setCanPutPollution(boolean value) {
            this.canPutPollutionResult = value;
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
            return canGetResourcesResult;
        }

        @Override
        public void getResources(Map<Resource, Integer> resources) {
            getResourcesCallCount++;
        }

        @Override
        public boolean canPutResources(Map<Resource, Integer> resources) {
            return true;
        }

        @Override
        public void putResources(Map<Resource, Integer> resources) {
        }

        @Override
        public boolean isOverPolluted() {
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
            return null;
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
            super(System.in, null);
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
    @Test
    @DisplayName("ProcessActionRawMaterialProducer Test")
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
                "Deliver expected refference on Grid that given from ProcessActionEffectOr");
    }
}