package sk.uniba.fmph.dcs.terra_futura.process;

import sk.uniba.fmph.dcs.terra_futura.effects.Effect;

public class ProcessActionExchange extends ProcessAction{
    public ProcessActionExchange(Effect effect) {
        super(effect);
    }

    @Override
    public int activateCard() {
        return 0;
    }
}
