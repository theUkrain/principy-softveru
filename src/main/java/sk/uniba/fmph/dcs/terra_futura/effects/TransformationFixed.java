package sk.uniba.fmph.dcs.terra_futura.effects;

import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransformationFixed implements Effect {

    private List<Resource> requiredInputs;
    private List<Resource> guaranteedOutputs;
    private int generatedPollution;

    public TransformationFixed(List<Resource> requiredInputs, List<Resource> guaranteedOutputs, int generatedPollution) {
        this.requiredInputs = requiredInputs;
        this.guaranteedOutputs = guaranteedOutputs;
        this.generatedPollution = generatedPollution;
    }

    @Override
    public boolean check(List<Resource> input, List<Resource> output, int pollution) {
        Map<Resource, Integer> inputsCounts = count(input);
        Map<Resource, Integer> requiredCounts = count(requiredInputs);
        for (Resource r : requiredCounts.keySet()) {
            if (inputsCounts.getOrDefault(r, 0) < requiredCounts.get(r)) {
                return false;
            }
        }

        Map<Resource, Integer> outputCounts = count(output);
        Map<Resource, Integer> guaranteedCounts = count(guaranteedOutputs);
        for (Resource r : guaranteedCounts.keySet()) {
            if (!outputCounts.equals(guaranteedCounts)) {
                return false;
            }
        }

        return pollution >= generatedPollution;

    }

    @Override
    public boolean hasAssistance() {
        return false;
    }

    @Override
    public String state() {
        return "TransformationFixed: " + requiredInputs + " -> " + guaranteedOutputs + " (pollution: " + generatedPollution + ")";
    }

    private Map<Resource, Integer> count(List<Resource> input) {
        Map<Resource, Integer> counts = new HashMap<>();
        for (Resource r : input) {
            counts.put(r, counts.getOrDefault(r, 0) + 1);
        }
        return counts;
    }
}
