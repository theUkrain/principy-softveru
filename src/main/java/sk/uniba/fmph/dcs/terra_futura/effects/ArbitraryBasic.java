package sk.uniba.fmph.dcs.terra_futura.effects;

import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;

import java.util.List;

public class ArbitraryBasic implements Effect {
    private int requiredInputs;
    private List<Resource> guaranteedOutputs;
    private int generatedPollution;

    public ArbitraryBasic(int requiredInputs, List<Resource> guaranteedOutputs, int generatedPollution) {
        this.requiredInputs = requiredInputs;
        this.guaranteedOutputs = guaranteedOutputs;
        this.generatedPollution = generatedPollution;
    }

    @Override
    public boolean check(List<Resource> input, List<Resource> output, int pollution) {

        if (input.size() < requiredInputs) {
            return false;
        }

        if (output.size() != 1) {
            return false;
        }

        if (!guaranteedOutputs.contains(output.getFirst())) {
            return false;
        }

        return generatedPollution <= pollution;
    }

    @Override
    public boolean hasAssistance() {
        return false;
    }

    @Override
    public String state() {
        return "ArbitraryBasic: required input " + requiredInputs + " to generate " + guaranteedOutputs + " (pollution: " + generatedPollution + ")";
    }
}
