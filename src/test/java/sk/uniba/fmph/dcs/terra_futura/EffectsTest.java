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
            Card card = CardFactory.card(1, effect, null, new CardSource(0, Deck.II));

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

        TransformationFixed effect = new TransformationFixed(input, output, 0);
        Card card = CardFactory.card(1, effect, null, new CardSource(0, Deck.II));

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

        for (Resource test : tests) {
            RawMaterialProducer effect = new RawMaterialProducer(test);
            Card card = CardFactory.card(1, effect, null, new CardSource(0, Deck.II));
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
        Card card = CardFactory.card(1, effect, null, new CardSource(0, Deck.II));

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
    @DisplayName("EffectOr test")
    public void testEffectOr() {
        Effect e1 = new TransformationFixed(Map.of(), Map.of(), 0);
        Effect e2 = new RawMaterialProducer(Resource.UNIVERSAL);

        EffectOr effect = new EffectOr(e1, e2);

        Assertions.assertEquals(effect.execute(0), e1);
        Assertions.assertEquals(effect.execute(1), e2);

        e2 = new EffectOr(e1, e2);
        effect = new EffectOr(e2, e1);

        Assertions.assertEquals(effect.execute(0), e2);
    }

    @Test
    @DisplayName("AssistanceEffect test")
    public void testAssistanceEffect() {
        AssistanceEffect effect = new AssistanceEffect();
        Effect result =  effect.execute(new TransformationFixed(Map.of(), Map.of(), 0));
        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName("PollutionTransfer test")
    public void testPollutionTransfer() {
        Card toTransfer = CardFactory.pollutionTransferCard(new CardSource(0, Deck.II));

        Card card = CardFactory.card(2, null, null, new CardSource(0, Deck.II));
        card.putPollution(2);

        ((PollutionTransfer)toTransfer.getUpper()).execute(List.of(Pair.of(card, 2)));

        Assertions.assertTrue(toTransfer.canGetPollution(2));
        Assertions.assertFalse(card.canGetPollution(2));

        card = CardFactory.card(2, null, null, new CardSource(0, Deck.II));
        Card card2 = CardFactory.card(2, null, null, new CardSource(0, Deck.II));
        card.putPollution(2);
        card2.putPollution(2);

        toTransfer.getPollution(2);

        ((PollutionTransfer)toTransfer.getUpper()).execute(List.of(Pair.of(card, 2), Pair.of(card2, 1)));

        Assertions.assertTrue(toTransfer.canGetPollution(3));
        Assertions.assertFalse(toTransfer.canGetPollution(4));
        Assertions.assertFalse(card.canGetPollution(2));
        Assertions.assertFalse(card2.canGetPollution(2));
        Assertions.assertTrue(card2.canGetPollution(1));

        final Card card3 = CardFactory.card(2, null, null, new CardSource(0, Deck.II));
        card3.putPollution(2);

        Assertions.assertThrows(IllegalArgumentException.class, () -> ((PollutionTransfer)toTransfer.getUpper()).execute(List.of(Pair.of(card3, 2))));
    }
}
