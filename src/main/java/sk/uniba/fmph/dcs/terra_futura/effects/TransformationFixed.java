package sk.uniba.fmph.dcs.terra_futura.effects;

import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;
import sk.uniba.fmph.dcs.terra_futura.tiles.Card;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransformationFixed implements Effect {

    private final List<Resource> requiredInputs;
    private final List<Resource> guaranteedOutputs;

    public TransformationFixed(final List<Resource> requiredInputs,final List<Resource> guaranteedOutputs) {
        this.requiredInputs = requiredInputs;
        this.guaranteedOutputs = guaranteedOutputs;
    }

    @Override
    public boolean activate(Card card) {
        if (card.canGetResources(requiredInputs) && card.canPutResources()) {
            card.putResources(guaranteedOutputs);
            card.getResources(requiredInputs);

            return true;
        }

        return false;
    }

    @Override
    public boolean hasAssistance() {
        return false;
    }

    @Override
    public String state() {
        return "TransformationFixed: " + requiredInputs + " -> " + guaranteedOutputs + " (pollution: " + generatedPollution + ")";
    }

    private Map<Resource, Integer> count(final List<Resource> input) {
        Map<Resource, Integer> counts = new HashMap<>();

        for (Resource r : input) {
            counts.put(r, counts.getOrDefault(r, 0) + 1);
        }

        return counts;
    }
}
