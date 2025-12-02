package sk.uniba.fmph.dcs.terra_futura.process;

import sk.uniba.fmph.dcs.terra_futura.effects.Effect;
import sk.uniba.fmph.dcs.terra_futura.effects.RawMaterialProducer;

public class ProcessActionRawMaterialProducer extends ProcessAction {
    public ProcessActionRawMaterialProducer(Effect effect) {
        super(effect);
    }

    /**
     *
     * @return activates card and make corresponding actions
     */
    @Override
    public int activateCard() {
        RawMaterialProducer effectCasted = ((RawMaterialProducer)effect);
        effectCasted.execute();

        return 0;
    }
}
