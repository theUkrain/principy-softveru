package sk.uniba.fmph.dcs.terra_futura;

import java.util.*;

/**
 * Demo/Main class to demonstrate Terra Futura game functionality.
 * This class can be run to test the game implementation.
 */
public class TerraFuturaDemo {

    /**
     * Simple console observer for demo purposes.
     */
    private static class ConsoleObserver implements TerraFuturaObserverInterface {
        private final int playerId;

        public ConsoleObserver(int playerId) {
            this.playerId = playerId;
        }

        @Override
        public void notify(String gameState) {
            System.out.println("=== Player " + playerId + " Notification ===");
            System.out.println(gameState);
            System.out.println();
        }
    }

    /**
     * Create a simple card with basic resources for demo.
     */
    private static Card createSimpleCard(String id, Resource... resources) {
        List<Resource> resourceList = Arrays.asList(resources);
        Card card = new Card(resourceList, 2); // 2 pollution spaces

        // Add a simple transformation effect
        SimpleTransformationEffect effect = new SimpleTransformationEffect(
                List.of(Resource.Green),
                List.of(Resource.Red),
                0
        );
        card.setUpperEffect(effect);

        return card;
    }

    /**
     * Simple transformation effect for demo purposes.
     */
    private static class SimpleTransformationEffect implements Effect {
        private final List<Resource> from;
        private final List<Resource> to;
        private final int pollution;

        public SimpleTransformationEffect(List<Resource> from, List<Resource> to, int pollution) {
            this.from = from;
            this.to = to;
            this.pollution = pollution;
        }

        @Override
        public boolean check(List<Resource> input, List<Resource> output, int pollutionAmount) {
            return input.equals(from) && output.equals(to) && pollutionAmount == pollution;
        }

        @Override
        public boolean hasAssistance() {
            return false;
        }

        @Override
        public String state() {
            return "Transform[" + from + " -> " + to + ", pollution: " + pollution + "]";
        }
    }

    public static void main(String[] args) {
        System.out.println("=================================");
        System.out.println("Terra Futura Game Demo");
        System.out.println("=================================\n");

        // Setup players
        int[] players = {1, 2};
        int startingPlayer = 1;

        // Create starting cards for each player
        Card startCard1 = createSimpleCard("Start1", Resource.Green, Resource.Red);
        Card startCard2 = createSimpleCard("Start2", Resource.Yellow, Resource.Bulb);

        // Create grids
        Map<Integer, Grid> grids = new HashMap<>();
        grids.put(1, new Grid(startCard1));
        grids.put(2, new Grid(startCard2));

        // Create piles
        List<Card> deckICards = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            deckICards.add(createSimpleCard("DeckI-" + i, Resource.Green, Resource.Yellow));
        }

        List<Card> deckIICards = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            deckIICards.add(createSimpleCard("DeckII-" + i, Resource.Red, Resource.Bulb));
        }

        Map<Deck, Pile> piles = new HashMap<>();
        piles.put(Deck.I, new Pile(deckICards));
        piles.put(Deck.II, new Pile(deckIICards));

        // Create component
        TerraFuturaComponent component = new TerraFuturaComponent(players, startingPlayer, grids, piles);

        // Register observers
        component.registerObserver(1, new ConsoleObserver(1));
        component.registerObserver(2, new ConsoleObserver(2));

        System.out.println("Game initialized!");
        System.out.println("Starting player: " + startingPlayer);
        System.out.println("Game state: " + component.getGameState());
        System.out.println("Turn number: " + component.getTurnNumber());
        System.out.println("\n=================================\n");

        // Demonstrate game actions

        // Turn 1: Player 1 takes a card
        System.out.println(">>> Player 1: Taking card from Deck I, index 0");
        CardSource source1 = new CardSource(Deck.I, 0);
        GridPosition dest1 = new GridPosition(1, 0);
        boolean success = component.takeCard(1, source1, dest1);
        System.out.println("Success: " + success);
        System.out.println("Current state: " + component.getGameState());
        System.out.println("\n---------------------------------\n");

        // Turn 1: Player 1 finishes turn
        System.out.println(">>> Player 1: Finishing turn");
        success = component.turnFinished(1);
        System.out.println("Success: " + success);
        System.out.println("Current state: " + component.getGameState());
        System.out.println("Player on turn: " + component.getPlayerOnTurn());
        System.out.println("\n---------------------------------\n");

        // Turn 2: Player 2 takes a card
        System.out.println(">>> Player 2: Taking card from Deck II, index 1");
        CardSource source2 = new CardSource(Deck.II, 1);
        GridPosition dest2 = new GridPosition(0, 1);
        success = component.takeCard(2, source2, dest2);
        System.out.println("Success: " + success);
        System.out.println("Current state: " + component.getGameState());
        System.out.println("\n---------------------------------\n");

        // Turn 2: Player 2 finishes turn
        System.out.println(">>> Player 2: Finishing turn");
        success = component.turnFinished(2);
        System.out.println("Success: " + success);
        System.out.println("Current state: " + component.getGameState());
        System.out.println("Turn number: " + component.getTurnNumber());
        System.out.println("Player on turn: " + component.getPlayerOnTurn());
        System.out.println("\n=================================");
        System.out.println("Demo completed successfully!");
        System.out.println("=================================");
    }
}