package sk.uniba.fmph.dcs.terra_futura.effects;

import org.apache.commons.lang3.tuple.Pair;
import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;
import sk.uniba.fmph.dcs.terra_futura.tiles.Card;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Exchange extends SetCardToEffect {

    private Set<Set<Pair<Resource, Integer>>> inputs;

    private Set<Set<Pair<Resource, Integer>>> outputs;

    private int generatedPollution;

    public Exchange(Set<Set<Pair<Resource, Integer>>> inputs, Set<Set<Pair<Resource, Integer>>> outputs) {
        this.inputs = inputs;
        this.outputs = outputs;
    }

    /**
     *
     * @param input <<Resource, Amount>, Taken from this card>.
     * @param output <Resource, Amount> expected to get.
     */

    public int execute (Map<Pair<Resource, Integer>, Card> input, Set<Pair<Resource, Integer>> output) {

        if(outputs.contains(output)) {
            throw new UnsupportedOperationException("Effect with possible outputs: \n" + outputs.toString() +
                    "\n doesn't support output: " + output.toString());
        }

        if(inputs.contains(inputs)) {
            throw new UnsupportedOperationException("Effect with possible inputs: \n" + inputs.toString() +
                    "\n doesn't support input: " + input.toString());
        }



        for(Pair<Resource, Integer> resourcesRequest : input.keySet()) {

            Map<Resource, Integer> requestDetails = Map.of(resourcesRequest.getKey(), resourcesRequest.getValue());

            if(!(input.get(resourcesRequest).canGetResources(requestDetails)))
                throw new IllegalArgumentException
                        ("Card: \n " + input.get(resourcesRequest).toString() + "can't provide \n" + resourcesRequest);
        }


        for(Pair<Resource, Integer> resourcesRequest : input.keySet()) {

            Map<Resource, Integer> requestDetails = Map.of(resourcesRequest.getKey(), resourcesRequest.getValue());

            input.get(resourcesRequest).getResources(requestDetails);
        }



        Map<Resource, Integer> resourcesToPut = new HashMap<>();

        for(Pair<Resource, Integer> resource : output) {
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
        if(!(o instanceof Exchange)) return false;
        return (((Exchange) o).inputs == this.inputs && ((Exchange) o).outputs == this.outputs);
    }

    @Override
    public String toString() {
        return "Exchange effect can provide exchange of one of following inputs to one of outputs: \n Inputs:\n" + inputs.toString() +
                "\n Outputs: " + outputs.toString();
    }
}
