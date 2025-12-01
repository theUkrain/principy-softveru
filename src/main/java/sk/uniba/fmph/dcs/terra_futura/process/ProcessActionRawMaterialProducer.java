package sk.uniba.fmph.dcs.terra_futura.process;

import sk.uniba.fmph.dcs.terra_futura.effects.Effect;
import sk.uniba.fmph.dcs.terra_futura.effects.RawMaterialProducer;

public class ProcessActionRawMaterialProducer extends ProcessAction {
    public ProcessActionRawMaterialProducer(Effect effect) {
        super(effect);
    }

    @Override
    public int activateCard() {
        RawMaterialProducer effectCasted = ((RawMaterialProducer)effect);
        effectCasted.execute();

        return 0;
    }
}
