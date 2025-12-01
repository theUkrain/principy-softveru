package sk.uniba.fmph.dcs.terra_futura.effects;

import org.apache.commons.lang3.tuple.Pair;
import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;
import sk.uniba.fmph.dcs.terra_futura.tiles.Card;

import java.util.*;

public class Exchange extends SetCardToEffect {

    private final Map<Set<Pair<Resource, Integer>>, Set<Pair<Resource, Integer>>> instructions;

    public Exchange(Map<Set<Pair<Resource, Integer>>, Set<Pair<Resource, Integer>>> instructions) {
        this.instructions = new HashMap<>(instructions);
    }

    public int execute(Map<Resource, List<Pair<Card, Integer>>> input, Set<Pair<Resource, Integer>> output) {
        // Convert input to Set<Pair<Resource, Integer>> for comparison with instructions
        Set<Pair<Resource, Integer>> inputSet = convertInputToSet(input);

        // Validate that this exchange rule exists
        if (!instructions.containsKey(inputSet)) {
            throw new UnsupportedOperationException(
                    "Effect doesn't support exchange from: " + inputSet +
                            " to: " + output +
                            "\nAvailable exchanges: " + instructions
            );
        }

        // Validate that output matches the rule
        Set<Pair<Resource, Integer>> expectedOutput = instructions.get(inputSet);
        if (!expectedOutput.equals(output)) {
            throw new UnsupportedOperationException(
                    "Effect with input: " + inputSet +
                            " should produce: " + expectedOutput +
                            " but got: " + output
            );
        }

        // Validate all cards have required resources
        validateCardsHaveResources(input);

        // Take resources from input cards
        takeResourcesFromCards(input);

        // Calculate pollution and put output resources on this card
        int pollution = putOutputResources(output);

        return pollution;
    }

    /**
     * Converts input map to set for comparison with instructions
     */
    private Set<Pair<Resource, Integer>> convertInputToSet(Map<Resource, List<Pair<Card, Integer>>> input) {
        Set<Pair<Resource, Integer>> result = new HashSet<>();

        for (Map.Entry<Resource, List<Pair<Card, Integer>>> entry : input.entrySet()) {
            Resource resource = entry.getKey();
            int totalAmount = 0;

            for (Pair<Card, Integer> cardAmount : entry.getValue()) {
                totalAmount += cardAmount.getRight();
            }

            result.add(Pair.of(resource, totalAmount));
        }

        return result;
    }

    /**
     * Validates that all cards have required resources
     */
    private void validateCardsHaveResources(Map<Resource, List<Pair<Card, Integer>>> input) {
        for (Map.Entry<Resource, List<Pair<Card, Integer>>> entry : input.entrySet()) {
            Resource resource = entry.getKey();

            for (Pair<Card, Integer> cardAmount : entry.getValue()) {
                Card card = cardAmount.getLeft();
                int amount = cardAmount.getRight();

                if (!card.canGetResources(Map.of(resource, amount))) {
                    throw new IllegalArgumentException(
                            "Card " + card + " can't provide " + resource + " x" + amount
                    );
                }
            }
        }
    }

    /**
     * Takes resources from all input cards.
     */
    private void takeResourcesFromCards(Map<Resource, List<Pair<Card, Integer>>> input) {
        for (Map.Entry<Resource, List<Pair<Card, Integer>>> entry : input.entrySet()) {
            Resource resource = entry.getKey();

            for (Pair<Card, Integer> cardAmount : entry.getValue()) {
                Card card = cardAmount.getLeft();
                int amount = cardAmount.getRight();

                card.getResources(Map.of(resource, amount));
            }
        }
    }

    /**
     * Puts output resources on this card and returns pollution amount
     */
    private int putOutputResources(Set<Pair<Resource, Integer>> output) {
        int pollution = 0;
        Map<Resource, Integer> resourcesToAdd = new HashMap<>();

        for (Pair<Resource, Integer> resourceAmount : output) {
            Resource resource = resourceAmount.getLeft();
            int amount = resourceAmount.getRight();

            if (resource == Resource.POLLUTION) {
                pollution = amount;
            } else {
                resourcesToAdd.put(resource, amount);
            }
        }

        if (!resourcesToAdd.isEmpty()) {
            this.card.putResources(resourcesToAdd);
        }

        if (pollution > 0) {
            this.card.putPollution(pollution);
        }

        return pollution;
    }

    @Override
    public boolean canProvideAssistance() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Exchange)) return false;
        Exchange exchange = (Exchange) o;
        return instructions.equals(exchange.instructions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instructions);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Exchange effect:\n");
        for (Map.Entry<Set<Pair<Resource, Integer>>, Set<Pair<Resource, Integer>>> entry : instructions.entrySet()) {
            sb.append("  ").append(formatResourceSet(entry.getKey()))
                    .append(" -> ")
                    .append(formatResourceSet(entry.getValue()))
                    .append("\n");
        }
        return sb.toString();
    }

    private String formatResourceSet(Set<Pair<Resource, Integer>> resources) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Pair<Resource, Integer> pair : resources) {
            if (!first) sb.append(", ");
            sb.append(pair.getLeft()).append(" x").append(pair.getRight());
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }
}