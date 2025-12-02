package sk.uniba.fmph.dcs.terra_futura.effects;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;
import sk.uniba.fmph.dcs.terra_futura.Game;
import sk.uniba.fmph.dcs.terra_futura.tiles.Card;

import java.util.*;

public class Exchange extends SetCardToEffect {

    private Set<Set<Pair<Resource, Integer>>> simpleInputs;
    private Set<Set<Pair<Resource, Integer>>> complexInputs;

    private Set<Set<Pair<Resource, Integer>>> simpleOutputs;
    private Set<Set<Pair<Resource, Integer>>> complexOutputs;

    public Exchange(Set<Set<Pair<Resource, Integer>>> inputs, Set<Set<Pair<Resource, Integer>>> outputs) {

        for(Set<Pair<Resource, Integer>> input : inputs) {
            for(Pair<Resource, Integer> resourceQuantity : input) {
                if(resourceQuantity.getKey() == Resource.UNIVERSAL) complexInputs.add(input);
                else this.simpleInputs.add(input);
            }
        }

        for(Set<Pair<Resource, Integer>> output : outputs) {
            for(Pair<Resource, Integer> resourceQuantity : output) {
                if(resourceQuantity.getKey() == Resource.UNIVERSAL) complexOutputs.add(output);
                else this.simpleOutputs.add(output);
            }
        }

    }

    /**
     * @param input  <<Resource, Amount>, Taken from this card>.
     * @param output <Resource, Amount> expected to get.
     */

    public int execute(Map<Resource, List<Pair<Integer, Card>>> input, Set<Pair<Resource, Integer>> output) {

        if(!((resourceAmountInInput(Resource.UNIVERSAL, mergedInput(input)) > 0 &&
        complexEntryCanBeCowered(input, complexInputs)) || entryCanBeCovered(input, simpleInputs)))
            throw new IllegalArgumentException("Effect: \n" + this.toString() +
                    "\n doesn't support input: " + input.toString());

        if(!((resourceAmountInInput(Resource.UNIVERSAL, output) > 0 &&
                complexEntryCanBeCowered(output, complexOutputs)) || entryCanBeCovered(output, complexOutputs)))
            throw new IllegalArgumentException("Effect: " + this.toString() +
                    "\n doesn't support output: " + output.toString());

        for (Resource r : input.keySet()) {

            for(Pair<Integer, Card> resourcesRequested : input.get(r)) {
                if(!resourcesRequested.getValue().canGetResources(Map.of(r, resourcesRequested.getKey())))
                    throw new IllegalArgumentException("card: \n" + resourcesRequested.getValue() + "can't provide "
                            + resourcesRequested.getKey() + " of " + r + "\n" );
            }

        }

        for (Resource r : input.keySet()) {

            for(Pair<Integer, Card> resourcesRequested :input.get(r)) {
                resourcesRequested.getValue().getResources(Map.of(r, resourcesRequested.getKey()));
            }

        }

        Map<Resource, Integer> resourcesToPut = new HashMap<>();

        for (Pair<Resource, Integer> resource : output) {

            resourcesToPut.put(resource.getKey(), resource.getValue());

        }

        this.card.putResources(resourcesToPut);

        return resourceAmountInInput(Resource.POLLUTION, output);
    }

    private boolean entryCanBeCovered(Map<Resource, List<Pair<Integer, Card>>> entry, Set<Set<Pair<Resource, Integer>>> coverage) {

        Set<Pair<Resource, Integer>> mergedInput = mergedInput(entry);

        return entryCanBeCovered(mergedInput, coverage);
    }

    private boolean entryCanBeCovered(Set<Pair<Resource, Integer>> mergedInput, Set<Set<Pair<Resource, Integer>>> coverage) {
        return coverage.contains(mergedInput);
    }

    private boolean complexEntryCanBeCowered(Map<Resource, List<Pair<Integer, Card>>> entry,  Set<Set<Pair<Resource, Integer>>> coverage) {

        Set<Pair<Resource, Integer>> mergedInput = mergedInput(entry);

       return complexEntryCanBeCowered(mergedInput, coverage);
    }

    private boolean complexEntryCanBeCowered(Set<Pair<Resource, Integer>> mergedInput, Set<Set<Pair<Resource, Integer>>> coverage) {
        for(Set<Pair<Resource, Integer>> complexInput : coverage) {

            Set<Pair<Resource, Integer>> unCoveredByNonComplex = new HashSet<>();

            for(Pair<Resource, Integer> resourceIntegerPair : complexInput ) {
                unCoveredByNonComplex.add(new ImmutablePair<>(resourceIntegerPair.getKey(),
                        resourceAmountInInput(resourceIntegerPair.getKey(),mergedInput) -
                                resourceAmountInInput(resourceIntegerPair.getKey(),complexInput)));
            }


            boolean hasNonPrimitiveResources = false;

            for(Pair<Resource, Integer> resourceIntegerPair : unCoveredByNonComplex) {
                if(resourceIntegerPair.getKey() == Resource.GEAR ||
                        resourceIntegerPair.getKey() == Resource.CAR ||
                        resourceIntegerPair.getKey() == Resource.BULB ||
                        resourceIntegerPair.getKey() == Resource.POLLUTION) hasNonPrimitiveResources = true;

                if(hasNonPrimitiveResources) break;

            }

            if(hasNonPrimitiveResources) continue;

            if (unCoveredByNonComplex.size()  <= resourceAmountInInput(Resource.UNIVERSAL, complexInput)) return true;

        }
        return false;
    }

    private int resourceAmountInInput(Resource resource, Set<Pair<Resource, Integer>> input) {
        for(Pair<Resource, Integer> r : input) {
            if(r.getKey() == resource) return r.getValue();
        }
        return 0;
    }

    private Set<Pair<Resource, Integer>> mergedInput(Map<Resource, List<Pair<Integer, Card>>> input) {

        Set<Pair<Resource, Integer>> mergedInput = new HashSet<>();

        for(Resource r : input.keySet()) {
            int requiredR = 0;
            for(Pair<Integer, Card> resourceAmountFromCard : input.get(r)) {
                requiredR += resourceAmountFromCard.getKey();
            }
            if(requiredR == 0) continue;
            mergedInput.add(new MutablePair<>(r, requiredR));
        }
        return mergedInput;
    }


    @Override
    public boolean canProvideAssistance() {
        return true;
    }

    @Override
    public void apply(Game game) {
        game.process(this);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Exchange)) return false;
        return (((Exchange) o).simpleInputs == this.simpleInputs && ((Exchange) o).simpleOutputs == this.simpleOutputs)
                && (((Exchange) o).complexInputs == this.complexInputs && ((Exchange) o).complexOutputs == this.complexOutputs);
    }

    @Override
    public String toString() {
        return "Exchange effect can provide exchange of one of following inputs to one of outputs: \n Inputs:\n" + simpleInputs.toString() + complexInputs.toString() +
                "\n Outputs: " + simpleOutputs.toString() + complexOutputs.toString();
    }
}
