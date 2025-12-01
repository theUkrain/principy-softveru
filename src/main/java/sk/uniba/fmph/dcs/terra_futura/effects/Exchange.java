package sk.uniba.fmph.dcs.terra_futura.effects;

import org.apache.commons.lang3.tuple.Pair;
import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;
import sk.uniba.fmph.dcs.terra_futura.tiles.Card;

import java.util.HashMap;
import java.util.HashSet;
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
    public int execute (Map<Resource,Pair<Card, Integer>> input, Set<Pair<Resource, Integer>> output) {
        Set<Pair<Resource, Integer>> kostily = new HashSet();

        for (Map.Entry<Resource, Pair<Card, Integer>> entry : input.entrySet()) {
            kostily.add(Pair.of(entry.getKey(), entry.getValue().getRight()));
        }

        if(this.outputs.contains(output)) {
            throw new UnsupportedOperationException("Effect with possible outputs: \n" + this.outputs.toString() +
                    "\n doesn't support output: " + output.toString());
        }

       if(this.inputs.contains(kostily)) {
            throw new UnsupportedOperationException("Effect with possible inputs: \n" + this.inputs.toString() +
                    "\n doesn't support input: " + kostily.toString());
       }

       for (Map.Entry<Resource, Pair<Card, Integer>> entry : input.entrySet()) {
            Resource resourceToTake = entry.getKey();
            Card card = entry.getValue().getLeft();
            Integer amount = entry.getValue().getRight();

            Map<Resource, Integer> requestDetails = Map.of(resourceToTake, amount);

            if (!(card.canGetResources(requestDetails))) {
                throw new IllegalArgumentException(
                        "Card: \n " + card.toString() + " can't provide \n" + resourceToTake + " x" + amount);
            }
        }
       for (Map.Entry<Resource, Pair<Card, Integer>> entry : input.entrySet()) {
           Resource resourceToTake = entry.getKey();
           Card card = entry.getValue().getLeft();
           Integer amount = entry.getValue().getRight();

           Map<Resource, Integer> request = Map.of(resourceToTake, amount);

           card.getResources(request);
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
        return "Exchange effect can provide exchange of one of following inputs to one of outputs: \n Inputs:\n" + this.inputs.toString() +
                "\n Outputs: " + this.outputs.toString();
    }
}
