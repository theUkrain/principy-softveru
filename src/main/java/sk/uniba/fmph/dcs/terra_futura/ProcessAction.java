package sk.uniba.fmph.dcs.terra_futura;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Class to perform card activation action.
 * Handles resource transfers and pollution placement.
 */
public class ProcessAction {

    /**
     * Activate a card on the grid.
     *
     * @param card Card to activate
     * @param grid Grid containing the card
     * @param inputs List of input resources and their source positions
     * @param outputs List of output resources and their destination positions
     * @param pollution List of positions where pollution will be placed
     * @return true if activation successful, false otherwise
     */
    public boolean activateCard(Card card, Grid grid,
                                List<Pair<Resource, GridPosition>> inputs,
                                List<Pair<Resource, GridPosition>> outputs,
                                List<GridPosition> pollution) {

        // Validate that all input/output positions exist and have cards
        for (Pair<Resource, GridPosition> input : inputs) {
            Optional<Card> sourceCard = grid.getCard(input.getValue());
            if (sourceCard.isEmpty()) {
                return false;
            }
        }

        for (Pair<Resource, GridPosition> output : outputs) {
            Optional<Card> destCard = grid.getCard(output.getValue());
            if (destCard.isEmpty()) {
                return false;
            }
        }

        for (GridPosition pollutionPos : pollution) {
            Optional<Card> pollutionCard = grid.getCard(pollutionPos);
            if (pollutionCard.isEmpty()) {
                return false;
            }
        }

        // Extract input and output resources for effect checking
        List<Resource> inputResources = new ArrayList<>();
        for (Pair<Resource, GridPosition> input : inputs) {
            inputResources.add(input.getKey());
        }

        List<Resource> outputResources = new ArrayList<>();
        for (Pair<Resource, GridPosition> output : outputs) {
            outputResources.add(output.getKey());
        }

        // Check if transformation is valid
        if (!card.check(inputResources, outputResources, pollution.size())) {
            return false;
        }

        // Check if all input cards can provide resources
        for (Pair<Resource, GridPosition> input : inputs) {
            Card sourceCard = grid.getCard(input.getValue()).get();
            if (!sourceCard.canGetResources(List.of(input.getKey()))) {
                return false;
            }
        }

        // Check if all output cards can accept resources
        for (Pair<Resource, GridPosition> output : outputs) {
            Card destCard = grid.getCard(output.getValue()).get();
            if (!destCard.canPutResources(List.of(output.getKey()))) {
                return false;
            }
        }

        // Check if all pollution cards can accept pollution
        for (GridPosition pollutionPos : pollution) {
            Card pollutionCard = grid.getCard(pollutionPos).get();
            if (!pollutionCard.canPutResources(List.of(Resource.Polution))) {
                return false;
            }
        }

        // All checks passed, perform the actual transfers

        // Get resources from input cards
        for (Pair<Resource, GridPosition> input : inputs) {
            Card sourceCard = grid.getCard(input.getValue()).get();
            sourceCard.getResources(List.of(input.getKey()));
        }

        // Put resources on output cards
        for (Pair<Resource, GridPosition> output : outputs) {
            Card destCard = grid.getCard(output.getValue()).get();
            destCard.putResources(List.of(output.getKey()));
        }

        // Put pollution on cards
        for (GridPosition pollutionPos : pollution) {
            Card pollutionCard = grid.getCard(pollutionPos).get();
            pollutionCard.putResources(List.of(Resource .Polution));
        }

        return true;
    }
}