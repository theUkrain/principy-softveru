package sk.uniba.fmph.dcs.terra_futura;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Deck;
import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;
import sk.uniba.fmph.dcs.terra_futura.effects.Effect;
import sk.uniba.fmph.dcs.terra_futura.effects.RawMaterialProducer;
import sk.uniba.fmph.dcs.terra_futura.effects.StartingCardEffect;
import sk.uniba.fmph.dcs.terra_futura.effects.TransformationFixed;
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
        List<Pair<Resource, Integer>> tests = new ArrayList<>();
        tests.add(Pair.of(Resource.YELLOW, 1));
        tests.add(Pair.of(Resource.RED, 2));
        tests.add(Pair.of(Resource.GREEN, 3));
        tests.add(Pair.of(Resource.MONEY, 1));
        tests.add(Pair.of(Resource.YELLOW, 4));

        Card card = CardFactory.card(1, null, null, new CardSource(0, Deck.II));

        for (Pair<Resource, Integer> test : tests) {
            RawMaterialProducer effect = new RawMaterialProducer(test, 0);
            effect.setCard(card);
            effect.execute();
            Assertions.assertTrue(card.canGetResources(Map.of(test.getLeft(), test.getRight())));
        }
    }

    @Test
    @DisplayName("RawMaterialProducer test")
    public void testRawMaterialProducer() {
        // check if card produces correct resources
        automatedTestPutRawMaterialProducer();

        // special checks
        Pair<Resource, Integer> test = Pair.of(Resource.BULB, 1);
        RawMaterialProducer effect = new RawMaterialProducer(test, 0);
        Card card = CardFactory.card(1, null, null, new CardSource(0, Deck.II));

        effect.setCard(card);
        effect.execute();
        Assertions.assertTrue(card.canGetResources(Map.of(test.getLeft(), test.getRight())));
        Assertions.assertFalse(card.canGetResources(Map.of(Resource.RED, 2)));
        Assertions.assertFalse(card.canGetResources(Map.of(Resource.GREEN, 3)));

        test = Pair.of(Resource.MONEY, 2);
        effect = new RawMaterialProducer(test, 0);

        effect.setCard(card);
        effect.execute();
        Assertions.assertTrue(card.canGetResources(Map.of(test.getLeft(), test.getRight())));
        Assertions.assertTrue(card.canGetResources(Map.of(Resource.BULB, 1)));

        test = Pair.of(Resource.RED, 1);
        effect.execute();
        Assertions.assertFalse(card.canGetResources(Map.of(test.getLeft(), test.getRight())));
    }

    @Test
    @DisplayName("StartingCardEffect test")
    public void testStartingCardEffectTriger() {
        StartingCardEffect effect = new StartingCardEffect();
        Effect trigger = effect.execute(0);

        Assertions.assertTrue(trigger instanceof RawMaterialProducer);

        Effect triger2 = effect.execute(1);
        Assertions.assertTrue(triger2 instanceof RawMaterialProducer);
    }
}
