package sk.uniba.fmph.dcs.terra_futura.effects;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;
import sk.uniba.fmph.dcs.terra_futura.Game;
import sk.uniba.fmph.dcs.terra_futura.ProcessActionDeliver;
import sk.uniba.fmph.dcs.terra_futura.tiles.Card;
import sk.uniba.fmph.dcs.terra_futura.tiles.Grid;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class Exchange extends SetCardToEffect {

    private Set<Set<Pair<Resource, Integer>>> simpleInputs;
    private Set<Set<Pair<Resource, Integer>>> complexInputs;

    private Set<Set<Pair<Resource, Integer>>> simpleOutputs;
    private Set<Set<Pair<Resource, Integer>>> complexOutputs;

    public Exchange(Set<Set<Pair<Resource, Integer>>> inputs, Set<Set<Pair<Resource, Integer>>> outputs) {

        simpleInputs = new HashSet<>();
        simpleOutputs = new HashSet<>();

        complexInputs = new HashSet<>();
        complexOutputs = new HashSet<>();

        for(Set<Pair<Resource, Integer>> input : inputs) {
            if(resourceAmountInInput(Resource.UNIVERSAL, input) > 0) complexInputs.add(input);
            else simpleInputs.add(input);
        }

        for(Set<Pair<Resource, Integer>> output : outputs) {
            if(resourceAmountInInput(Resource.UNIVERSAL, output) > 0) complexOutputs.add(output);
            else simpleOutputs.add(output);
        }

    }

    /**
     * @param input  <<Resource, Amount>, Taken from this card>.
     * @param output <Resource, Amount> expected to get.
     */

    public int execute(Map<Resource, List<Pair<Integer, Card>>> input, Set<Pair<Resource, Integer>> output) {

        for(Resource r : input.keySet()) if(r == Resource.UNIVERSAL) throw new IllegalArgumentException("Input/output of exchange effect should be specified directly without universal materials.");

        if(!(entryCanBeCovered(input, simpleInputs) || complexEntryCanBeCowered(input, complexInputs)))
            throw new IllegalArgumentException("Effect: \n" + this.toString() +
                    "\n doesn't support input: " + input.toString());

        if(!(entryCanBeCovered(output, simpleOutputs) || complexEntryCanBeCowered(output, complexOutputs)))
            throw new IllegalArgumentException("Effect: " + this.toString() +
                    "\n doesn't support output: " + output.toString());

        input.forEach( (r,d) -> {

            d.forEach( (p) -> {
                if(!(p.getValue().canGetResources(Map.of(r, p.getKey())))) throw new IllegalArgumentException("card: \n" + p.getValue() + "can't provide " + p.getKey() + " of " + r + "\n");
            });

        });

        input.forEach((r, d) -> {
            d.forEach(p -> {
                p.getValue().getResources(Map.of(r, p.getKey()));
            });
        });

        Map<Resource, Integer> resourcesToPut = new HashMap<>();

        for (Pair<Resource, Integer> resource : output) {

            if(resource.getKey() == Resource.POLLUTION) continue;

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

        for(Set<Pair<Resource, Integer>> complexEntry : coverage) {

            Map<Resource, Integer> entryToMap = entryToMap(complexEntry);

            Map<Resource, Integer> unCoveredByNonComplex = new HashMap<>(entryToMap(mergedInput));

            unCoveredByNonComplex.replaceAll( (r, a) -> a - entryToMap.getOrDefault(r, 0));

             AtomicReference<AtomicBoolean> hasNegativeEntry = new AtomicReference<>(new AtomicBoolean(false));

            unCoveredByNonComplex.values().forEach( a ->{
                if(a < 0) hasNegativeEntry.set(new AtomicBoolean(true));
            });

            if(hasNegativeEntry.get().get()) continue;

            unCoveredByNonComplex.entrySet().removeIf((e) -> e.getValue() == 0);

            Set<Resource> r = unCoveredByNonComplex.keySet();

            if(r.contains(Resource.GEAR) || r.contains(Resource.CAR) ||
            r.contains(Resource.BULB) || r.contains(Resource.POLLUTION) ||
                    r.contains(Resource.MONEY)) continue;

            int uncoveredResourcesLLeft = 0;

            for(int amount : unCoveredByNonComplex.values()) {
                uncoveredResourcesLLeft += amount;
            }

            if (uncoveredResourcesLLeft == resourceAmountInInput(Resource.UNIVERSAL, complexEntry)) return true;
        }

        return false;
    }

    private Map<Resource, Integer> entryToMap(Set<Pair<Resource, Integer>> entry) {

        Map<Resource, Integer> res = new HashMap<>();

        for(Pair<Resource, Integer> resourceIntegerPair : entry) {
            res.put(resourceIntegerPair.getKey(), resourceIntegerPair.getValue());
        }

        return res;

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
    public void apply(ProcessActionDeliver deliver, Grid grid) {
        deliver.process((Exchange) this, grid);
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
