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
        result = effect.execute(new RawMaterialProducer(Resource.RED));
        Assertions.assertNull(result);
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


    @Test
    @DisplayName("Simple exchange: 2 RED -> 1 GEAR")
    public void testSimpleExchange() {
        // Create exchange rule: 2 RED -> 1 GEAR
        Map<Set<Pair<Resource, Integer>>, Set<Pair<Resource, Integer>>> instructions = new HashMap<>();
        instructions.put(
                Set.of(Pair.of(Resource.RED, 2)),
                Set.of(Pair.of(Resource.GEAR, 1))
        );

        Exchange exchange = new Exchange(instructions);
        Card exchangeCard = CardFactory.card(2, exchange, null, new CardSource(0, Deck.II));

        // Create input card with 2 RED
        Card inputCard = CardFactory.card(2, null, null, new CardSource(1, Deck.II));
        inputCard.putResources(Map.of(Resource.RED, 2));

        // Execute exchange
        Map<Resource, List<Pair<Card, Integer>>> input = Map.of(
                Resource.RED, List.of(Pair.of(inputCard, 2))
        );
        Set<Pair<Resource, Integer>> output = Set.of(Pair.of(Resource.GEAR, 1));

        int pollution = exchange.execute(input, output);

        // Verify
        Assertions.assertEquals(0, pollution, "Should not generate pollution");
        Assertions.assertTrue(exchangeCard.canGetResources(Map.of(Resource.GEAR, 1)),
                "Exchange card should have 1 GEAR");
        Assertions.assertFalse(inputCard.canGetResources(Map.of(Resource.RED, 1)),
                "Input card should have no RED left");
    }

    @Test
    @DisplayName("Complex exchange: 2 RED + 1 MONEY -> 1 CAR")
    public void testComplexExchange() {
        // Create exchange rule
        Map<Set<Pair<Resource, Integer>>, Set<Pair<Resource, Integer>>> instructions = new HashMap<>();
        instructions.put(
                Set.of(Pair.of(Resource.RED, 2), Pair.of(Resource.MONEY, 1)),
                Set.of(Pair.of(Resource.CAR, 1))
        );

        Exchange exchange = new Exchange(instructions);
        Card exchangeCard = CardFactory.card(2, exchange, null, new CardSource(0, Deck.II));

        // Create input cards
        Card redCard = CardFactory.card(2, null, null, new CardSource(1, Deck.II));
        redCard.putResources(Map.of(Resource.RED, 2));

        Card moneyCard = CardFactory.card(2, null, null, new CardSource(2, Deck.II));
        moneyCard.putResources(Map.of(Resource.MONEY, 1));

        // Execute exchange
        Map<Resource, List<Pair<Card, Integer>>> input = Map.of(
                Resource.RED, List.of(Pair.of(redCard, 2)),
                Resource.MONEY, List.of(Pair.of(moneyCard, 1))
        );
        Set<Pair<Resource, Integer>> output = Set.of(Pair.of(Resource.CAR, 1));

        int pollution = exchange.execute(input, output);

        // Verify
        Assertions.assertEquals(0, pollution);
        Assertions.assertTrue(exchangeCard.canGetResources(Map.of(Resource.CAR, 1)));
        Assertions.assertFalse(redCard.canGetResources(Map.of(Resource.RED, 1)));
        Assertions.assertFalse(moneyCard.canGetResources(Map.of(Resource.MONEY, 1)));
    }

    @Test
    @DisplayName("Exchange with pollution: 1 GREEN -> 1 YELLOW + 1 POLLUTION")
    public void testExchangeWithPollution() {
        // Create exchange rule that generates pollution
        Map<Set<Pair<Resource, Integer>>, Set<Pair<Resource, Integer>>> instructions = new HashMap<>();
        instructions.put(
                Set.of(Pair.of(Resource.GREEN, 1)),
                Set.of(Pair.of(Resource.YELLOW, 1), Pair.of(Resource.POLLUTION, 1))
        );

        Exchange exchange = new Exchange(instructions);
        Card exchangeCard = CardFactory.card(3, exchange, null, new CardSource(0, Deck.II));

        Card inputCard = CardFactory.card(2, null, null, new CardSource(1, Deck.II));
        inputCard.putResources(Map.of(Resource.GREEN, 1));

        // Execute
        Map<Resource, List<Pair<Card, Integer>>> input = Map.of(
                Resource.GREEN, List.of(Pair.of(inputCard, 1))
        );
        Set<Pair<Resource, Integer>> output = Set.of(
                Pair.of(Resource.YELLOW, 1),
                Pair.of(Resource.POLLUTION, 1)
        );

        int pollution = exchange.execute(input, output);

        // Verify
        Assertions.assertEquals(1, pollution, "Should generate 1 pollution");
        Assertions.assertTrue(exchangeCard.canGetResources(Map.of(Resource.YELLOW, 1)));
        Assertions.assertTrue(exchangeCard.canGetPollution(1),
                "Exchange card should have 1 pollution");
    }

    @Test
    @DisplayName("Multiple input cards for same resource")
    public void testMultipleInputCards() {
        // 3 RED -> 1 GEAR
        Map<Set<Pair<Resource, Integer>>, Set<Pair<Resource, Integer>>> instructions = new HashMap<>();
        instructions.put(
                Set.of(Pair.of(Resource.RED, 3)),
                Set.of(Pair.of(Resource.GEAR, 1))
        );

        Exchange exchange = new Exchange(instructions);
        Card exchangeCard = CardFactory.card(2, exchange, null, new CardSource(0, Deck.II));

        // Two cards with RED
        Card redCard1 = CardFactory.card(2, null, null, new CardSource(1, Deck.II));
        redCard1.putResources(Map.of(Resource.RED, 2));

        Card redCard2 = CardFactory.card(2, null, null, new CardSource(2, Deck.II));
        redCard2.putResources(Map.of(Resource.RED, 1));

        // Execute - take 2 RED from card1 and 1 RED from card2
        Map<Resource, List<Pair<Card, Integer>>> input = Map.of(
                Resource.RED, List.of(Pair.of(redCard1, 2), Pair.of(redCard2, 1))
        );
        Set<Pair<Resource, Integer>> output = Set.of(Pair.of(Resource.GEAR, 1));

        exchange.execute(input, output);

        // Verify
        Assertions.assertTrue(exchangeCard.canGetResources(Map.of(Resource.GEAR, 1)));
        Assertions.assertFalse(redCard1.canGetResources(Map.of(Resource.RED, 1)));
        Assertions.assertFalse(redCard2.canGetResources(Map.of(Resource.RED, 1)));
    }

    @Test
    @DisplayName("Exception: unsupported input")
    public void testUnsupportedInput() {
        // Only supports: 2 RED -> 1 GEAR
        Map<Set<Pair<Resource, Integer>>, Set<Pair<Resource, Integer>>> instructions = new HashMap<>();
        instructions.put(
                Set.of(Pair.of(Resource.RED, 2)),
                Set.of(Pair.of(Resource.GEAR, 1))
        );

        Exchange exchange = new Exchange(instructions);
        Card exchangeCard = CardFactory.card(2, exchange, null, new CardSource(0, Deck.II));

        Card inputCard = CardFactory.card(2, null, null, new CardSource(1, Deck.II));
        inputCard.putResources(Map.of(Resource.YELLOW, 2));

        // Try to exchange 2 YELLOW (not supported)
        Map<Resource, List<Pair<Card, Integer>>> input = Map.of(
                Resource.YELLOW, List.of(Pair.of(inputCard, 2))
        );
        Set<Pair<Resource, Integer>> output = Set.of(Pair.of(Resource.GEAR, 1));

        Assertions.assertThrows(UnsupportedOperationException.class, () -> {
            exchange.execute(input, output);
        }, "Should throw exception for unsupported input");
    }

    @Test
    @DisplayName("Exception: wrong output for given input")
    public void testWrongOutput() {
        // 2 RED -> 1 GEAR
        Map<Set<Pair<Resource, Integer>>, Set<Pair<Resource, Integer>>> instructions = new HashMap<>();
        instructions.put(
                Set.of(Pair.of(Resource.RED, 2)),
                Set.of(Pair.of(Resource.GEAR, 1))
        );

        Exchange exchange = new Exchange(instructions);
        Card exchangeCard = CardFactory.card(2, exchange, null, new CardSource(0, Deck.II));

        Card inputCard = CardFactory.card(2, null, null, new CardSource(1, Deck.II));
        inputCard.putResources(Map.of(Resource.RED, 2));

        // Try to get CAR instead of GEAR
        Map<Resource, List<Pair<Card, Integer>>> input = Map.of(
                Resource.RED, List.of(Pair.of(inputCard, 2))
        );
        Set<Pair<Resource, Integer>> output = Set.of(Pair.of(Resource.CAR, 1));

        Assertions.assertThrows(UnsupportedOperationException.class, () -> {
            exchange.execute(input, output);
        }, "Should throw exception for wrong output");
    }

    @Test
    @DisplayName("Exception: insufficient resources on input card")
    public void testInsufficientResources() {
        // 2 RED -> 1 GEAR
        Map<Set<Pair<Resource, Integer>>, Set<Pair<Resource, Integer>>> instructions = new HashMap<>();
        instructions.put(
                Set.of(Pair.of(Resource.RED, 2)),
                Set.of(Pair.of(Resource.GEAR, 1))
        );

        Exchange exchange = new Exchange(instructions);
        Card exchangeCard = CardFactory.card(2, exchange, null, new CardSource(0, Deck.II));

        // Card only has 1 RED, but exchange needs 2
        Card inputCard = CardFactory.card(2, null, null, new CardSource(1, Deck.II));
        inputCard.putResources(Map.of(Resource.RED, 1));

        Map<Resource, List<Pair<Card, Integer>>> input = Map.of(
                Resource.RED, List.of(Pair.of(inputCard, 2))
        );
        Set<Pair<Resource, Integer>> output = Set.of(Pair.of(Resource.GEAR, 1));

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            exchange.execute(input, output);
        }, "Should throw exception when card doesn't have enough resources");
    }

    @Test
    @DisplayName("Multiple exchange rules supported")
    public void testMultipleRules() {
        // Support two different exchanges
        Map<Set<Pair<Resource, Integer>>, Set<Pair<Resource, Integer>>> instructions = new HashMap<>();
        instructions.put(
                Set.of(Pair.of(Resource.RED, 2)),
                Set.of(Pair.of(Resource.GEAR, 1))
        );
        instructions.put(
                Set.of(Pair.of(Resource.YELLOW, 1)),
                Set.of(Pair.of(Resource.BULB, 2))
        );

        Exchange exchange = new Exchange(instructions);
        Card exchangeCard = CardFactory.card(3, exchange, null, new CardSource(0, Deck.II));

        // First exchange: 2 RED -> 1 GEAR
        Card redCard = CardFactory.card(2, null, null, new CardSource(1, Deck.II));
        redCard.putResources(Map.of(Resource.RED, 2));

        exchange.execute(
                Map.of(Resource.RED, List.of(Pair.of(redCard, 2))),
                Set.of(Pair.of(Resource.GEAR, 1))
        );

        Assertions.assertTrue(exchangeCard.canGetResources(Map.of(Resource.GEAR, 1)));

        // Second exchange: 1 YELLOW -> 2 BULB
        Card yellowCard = CardFactory.card(2, null, null, new CardSource(2, Deck.II));
        yellowCard.putResources(Map.of(Resource.YELLOW, 1));

        exchange.execute(
                Map.of(Resource.YELLOW, List.of(Pair.of(yellowCard, 1))),
                Set.of(Pair.of(Resource.BULB, 2))
        );

        Assertions.assertTrue(exchangeCard.canGetResources(Map.of(Resource.BULB, 2)));
        Assertions.assertTrue(exchangeCard.canGetResources(Map.of(Resource.GEAR, 1)));
    }

    @Test
    @DisplayName("canProvideAssistance returns true")
    public void testCanProvideAssistance() {
        Map<Set<Pair<Resource, Integer>>, Set<Pair<Resource, Integer>>> instructions = new HashMap<>();
        instructions.put(
                Set.of(Pair.of(Resource.RED, 1)),
                Set.of(Pair.of(Resource.GEAR, 1))
        );

        Exchange exchange = new Exchange(instructions);
        Assertions.assertTrue(exchange.canProvideAssistance(),
                "Exchange should be able to provide assistance");
    }

    @Test
    @DisplayName("equals and hashCode work correctly")
    public void testEqualsAndHashCode() {
        Map<Set<Pair<Resource, Integer>>, Set<Pair<Resource, Integer>>> instructions1 = new HashMap<>();
        instructions1.put(
                Set.of(Pair.of(Resource.RED, 2)),
                Set.of(Pair.of(Resource.GEAR, 1))
        );

        Map<Set<Pair<Resource, Integer>>, Set<Pair<Resource, Integer>>> instructions2 = new HashMap<>();
        instructions2.put(
                Set.of(Pair.of(Resource.RED, 2)),
                Set.of(Pair.of(Resource.GEAR, 1))
        );

        Exchange exchange1 = new Exchange(instructions1);
        Exchange exchange2 = new Exchange(instructions2);

        Assertions.assertEquals(exchange1, exchange2, "Exchanges with same instructions should be equal");
        Assertions.assertEquals(exchange1.hashCode(), exchange2.hashCode(),
                "Equal exchanges should have same hash code");
    }

    @Test
    @DisplayName("Exception: overpolluted input card")
    public void testOverpollutedInputCard() {
        Map<Set<Pair<Resource, Integer>>, Set<Pair<Resource, Integer>>> instructions = new HashMap<>();
        instructions.put(
                Set.of(Pair.of(Resource.RED, 1)),
                Set.of(Pair.of(Resource.GEAR, 1))
        );

        Exchange exchange = new Exchange(instructions);
        Card exchangeCard = CardFactory.card(2, exchange, null, new CardSource(0, Deck.II));

        // Create overpolluted input card
        Card inputCard = CardFactory.card(2, null, null, new CardSource(1, Deck.II));
        inputCard.putResources(Map.of(Resource.RED, 1));
        inputCard.putPollution(2); // Overpolluted!

        Map<Resource, List<Pair<Card, Integer>>> input = Map.of(
                Resource.RED, List.of(Pair.of(inputCard, 1))
        );
        Set<Pair<Resource, Integer>> output = Set.of(Pair.of(Resource.GEAR, 1));

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            exchange.execute(input, output);
        }, "Should throw exception when input card is overpolluted");
    }
}
