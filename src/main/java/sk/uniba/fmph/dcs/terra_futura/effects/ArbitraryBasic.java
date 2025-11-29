package sk.uniba.fmph.dcs.terra_futura.effects;

import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;
import sk.uniba.fmph.dcs.terra_futura.tiles.Card;

import java.util.List;

public class ArbitraryBasic implements Effect {
    private final int requiredInputs;
    private final List<Resource> guaranteedOutputs;

    public ArbitraryBasic(final int requiredInputs, final List<Resource> guaranteedOutputs) {
        this.requiredInputs = requiredInputs;
        this.guaranteedOutputs = guaranteedOutputs;
    }

    @Override
    public boolean activate(Card card) {
        if (card.canPutResources()) {
            card.putResources(guaranteedOutputs);

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
        return "ArbitraryBasic: required input " + requiredInputs + " to generate " + guaranteedOutputs + " (pollution: \" + generatedPollution + \")";
    }
}
