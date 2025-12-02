package sk.uniba.fmph.dcs.terra_futura.effects;

import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;
import sk.uniba.fmph.dcs.terra_futura.Game;
import sk.uniba.fmph.dcs.terra_futura.ProcessActionDeliver;
import sk.uniba.fmph.dcs.terra_futura.tiles.Grid;

import java.util.Map;

public class RawMaterialProducer extends SetCardToEffect {
    private final Resource guaranteedOutputs;

    public RawMaterialProducer(final Resource guaranteedOutputs) {
        this.guaranteedOutputs = guaranteedOutputs;
    }

    public void execute() {
        if (!card.canPutResources(Map.of(guaranteedOutputs, 1))) {
            throw new IllegalStateException("Can't put resources");
        }

        card.putResources(Map.of(guaranteedOutputs, 1));
    }

    @Override
    public boolean canProvideAssistance() {
        return false;
    }

    @Override
    public void apply(ProcessActionDeliver deliver) {
        deliver.process((RawMaterialProducer) this);
    }

    @Override
    public String toString() {
        return "Generates resource " + guaranteedOutputs;
    }

    public Resource getGuaranteedOutputs(){
        return guaranteedOutputs;
    }

    @Override
    public boolean equals(Object obj){
        if(this == obj){
            return true;
        }
        RawMaterialProducer t = (RawMaterialProducer) obj;
        return this.guaranteedOutputs == t.getGuaranteedOutputs();
    }
}
