package sk.uniba.fmph.dcs.terra_futura.effects;

import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;

import java.util.Map;

public class RawMaterialProducer extends SetCardToEffect implements Effect {
    private final Resource guaranteedOutputs;

    public RawMaterialProducer(final Resource guaranteedOutputs) {
        this.guaranteedOutputs = guaranteedOutputs;
    }

    public void execute() {
        if (!card.canPutResources(Map.of(guaranteedOutputs, 1))) {
            throw new IllegalStateException("Cant put resources");
        }

        card.putResources(Map.of(guaranteedOutputs, 1));
    }

    @Override
    public boolean canProvideAssistance() {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }

    @Override
    public String toString() {
        return "Generates resource " + guaranteedOutputs;
    }
}
