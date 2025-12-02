package sk.uniba.fmph.dcs.terra_futura.effects;

import org.apache.commons.lang3.tuple.Pair;
import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;
import sk.uniba.fmph.dcs.terra_futura.tiles.Card;

import java.util.*;

public class Exchange extends SetCardToEffect {

    private Set<Set<Pair<Resource, Integer>>> inputs;

    private Set<Set<Pair<Resource, Integer>>> inputsContainingUniversal;

    private Set<Set<Pair<Resource, Integer>>> outputs;

    public Exchange(Set<Set<Pair<Resource, Integer>>> inputs, Set<Set<Pair<Resource, Integer>>> outputs) {
        this.inputs = new HashSet<>(inputs);

        for(Set<Pair<Resource, Integer>> input : inputs) {
            for(Pair<Resource, Integer> resourceQuantity : input) {
                if(resourceQuantity.getKey() == Resource.UNIVERSAL) inputsContainingUniversal.add(input);
                else this.inputs.add(input);
            }
        }

        this.outputs = new HashSet<>(outputs);
    }

    /**
     * @param input  <<Resource, Amount>, Taken from this card>.
     * @param output <Resource, Amount> expected to get.
     */

    public int execute(Map<Resource, List<Pair<Integer, Card>>> input, Set<Pair<Resource, Integer>> output) {

        if (!outputs.contains(output)) {

            for (Set<Pair<Resource, Integer>> possibleInput : inputs) {
                for (Pair<Resource, Integer> resourceRequired : possibleInput) {
                    if () {

                    }
                }
            }

            throw new UnsupportedOperationException("Effect with possible outputs: \n" + outputs.toString() +
                    "\n doesn't support output: " + output.toString());
        }

        if (!inputs.contains(input)) {
            throw new UnsupportedOperationException("Effect with possible inputs: \n" + inputs.toString() +
                    "\n doesn't support input: " + input.toString());
        }


        for (List<Pair<Integer, Card>> resourcesRequest : input.keySet()) {

            Map<Resource, Integer> requestDetails = Map.of(resourcesRequest.getKey(), resourcesRequest.getValue());

            if (!(input.get(resourcesRequest).canGetResources(requestDetails)))
                throw new IllegalArgumentException
                        ("Card: \n " + input.get(resourcesRequest).toString() + "can't provide \n" + resourcesRequest);
        }


        for (Pair<Resource, Integer> resourcesRequest : input.keySet()) {

            Map<Resource, Integer> requestDetails = Map.of(resourcesRequest.getKey(), resourcesRequest.getValue());

            input.get(resourcesRequest).getResources(requestDetails);
        }


        Map<Resource, Integer> resourcesToPut = new HashMap<>();

        int generatedPollution = 0;

        for (Pair<Resource, Integer> resource : output) {

            if (resource.getKey() == Resource.POLLUTION) generatedPollution = resource.getValue();

            resourcesToPut.put(resource.getKey(), resource.getValue());

        }

        this.card.putResources(resourcesToPut);

        return generatedPollution;
    }

    @Override
    public boolean canProvideAssistance() {
        return true;
    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Exchange)) return false;
        return (((Exchange) o).inputs == this.inputs && ((Exchange) o).outputs == this.outputs);
    }

    @Override
    public String toString() {
        return "Exchange effect can provide exchange of one of following inputs to one of outputs: \n Inputs:\n" + inputs.toString() +
                "\n Outputs: " + outputs.toString();
    }
}
