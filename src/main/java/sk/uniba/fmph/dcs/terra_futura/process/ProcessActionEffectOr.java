package sk.uniba.fmph.dcs.terra_futura.process;

import sk.uniba.fmph.dcs.terra_futura.effects.Effect;
import sk.uniba.fmph.dcs.terra_futura.effects.EffectOr;

public class ProcessActionEffectOr extends ProcessAction {
    private int whatEffectToTrigger;

    // TODO Game reference for casting inner effect
    public ProcessActionEffectOr(Effect effect, int whatEffectToTrigger) {
        super(effect);
        this.whatEffectToTrigger = whatEffectToTrigger;
    }

    @Override
    public int activateCard() {
        EffectOr effectCasted = (EffectOr) effect;
        Effect innerEffect = effectCasted.execute(whatEffectToTrigger);

        return 0;
    }
}
