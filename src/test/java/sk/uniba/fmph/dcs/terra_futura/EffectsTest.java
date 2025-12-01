package sk.uniba.fmph.dcs.terra_futura;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Deck;
import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;
import sk.uniba.fmph.dcs.terra_futura.effects.*;
import sk.uniba.fmph.dcs.terra_futura.tiles.Card;
import sk.uniba.fmph.dcs.terra_futura.tiles.CardFactory;
import sk.uniba.fmph.dcs.terra_futura.tiles.CardSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EffectsTest {
    @Test
    @DisplayName("TransformationFixed test")
    public void testTransformationFixed() {
        Map<Resource, Integer> requiredInputs = new HashMap<>();
        requiredInputs.put(Resource.RED, 2);

        Map<Resource, Integer> guaranteedOutputs = new HashMap<>();
        guaranteedOutputs.put(Resource.YELLOW, 1);

        int guaranteedPollution = 0;

        TransformationFixed effect = new TransformationFixed(requiredInputs, guaranteedOutputs, guaranteedPollution);

        Card card = CardFactory.card(0, null, null, new CardSource(0, Deck.II));
        card.putResources(Map.of(Resource.RED, 2));

        Map<Resource, List<Pair<Card, Integer>>> cards = new HashMap<>();
        cards.put(Resource.RED, List.of(Pair.of(card, 2)));

        // effect.check(card, cards, )
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
            effect.execute(card);
            Assertions.assertTrue(card.canGetResources(Map.of(test.getLeft(), test.getRight())));
        }
    }

    @Test
    @DisplayName("RawMaterialProducer test")
    public void testRawMaterialProducer() {
        // check if card produces correct resources
        automatedTestPutRawMaterialProducer();

        // TODO check pollution

        // special checks
        Pair<Resource, Integer> test = Pair.of(Resource.BULB, 1);
        RawMaterialProducer effect = new RawMaterialProducer(test, 0);
        Card card = CardFactory.card(1, null, null, new CardSource(0, Deck.II));

        effect.execute(card);
        Assertions.assertTrue(card.canGetResources(Map.of(test.getLeft(), test.getRight())));
        Assertions.assertFalse(card.canGetResources(Map.of(Resource.RED, 2)));
        Assertions.assertFalse(card.canGetResources(Map.of(Resource.GREEN, 3)));

        test = Pair.of(Resource.MONEY, 2);
        effect = new RawMaterialProducer(test, 0);

        effect.execute(card);
        Assertions.assertTrue(card.canGetResources(Map.of(test.getLeft(), test.getRight())));
        Assertions.assertTrue(card.canGetResources(Map.of(Resource.BULB, 1)));
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


    @Test
    @DisplayName("StartingCardEffect test")
    public void testStartingCardEffectCheck() {}

}
