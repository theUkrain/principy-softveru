package sk.uniba.fmph.dcs.terra_futura.process;

import sk.uniba.fmph.dcs.terra_futura.effects.Effect;

public abstract class ProcessAction {
    protected Effect effect;

    public ProcessAction(Effect effect) {
        this.effect = effect;
    }

    public int activateCard() {
        return 0;
    }
}
