package sk.uniba.fmph.dcs.terra_futura.effects;

import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;
import sk.uniba.fmph.dcs.terra_futura.ProcessActionDeliver;

import java.util.Map;

public class RawMaterialProducer extends SetCardToEffect {
    private final Resource guaranteedOutputs;

    public RawMaterialProducer(final Resource guaranteedOutputs) {
        this.guaranteedOutputs = guaranteedOutputs;
    }


    /**
     * method that puts corresponding material on user card
     */
    public void execute() {
        if (!card.canPutResources(Map.of(guaranteedOutputs, 1))) {
            throw new IllegalStateException("Can't put resources");
        }

        card.putResources(Map.of(guaranteedOutputs, 1));
    }

    /**
     *
     * @return can effect be executed via assistance effect
     */
    @Override
    public boolean canProvideAssistance() {
        return false;
    }

    /**
     *
     * @param deliver
     */
    @Override
    public void apply(ProcessActionDeliver deliver) {
        deliver.process((RawMaterialProducer) this);
    }

    /**
     *
     * @return string representation of effects
     */
    @Override
    public String toString() {
        return "Generates resource " + guaranteedOutputs;
    }

    /**
     *
     * @return resource that is produced by this producer
     */
    public Resource getGuaranteedOutputs(){
        return guaranteedOutputs;
    }

    /**
     *
     * @param obj
     * @return whether object is equal to this or not
     */
    @Override
    public boolean equals(Object obj){
        if(this == obj){
            return true;
        }
        RawMaterialProducer t = (RawMaterialProducer) obj;
        return this.guaranteedOutputs == t.getGuaranteedOutputs();
    }
}
