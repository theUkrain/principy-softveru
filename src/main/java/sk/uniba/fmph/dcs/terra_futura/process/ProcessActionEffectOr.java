package sk.uniba.fmph.dcs.terra_futura.process;

import sk.uniba.fmph.dcs.terra_futura.ProcessActionDeliver;
import sk.uniba.fmph.dcs.terra_futura.effects.Effect;
import sk.uniba.fmph.dcs.terra_futura.effects.EffectOr;
import sk.uniba.fmph.dcs.terra_futura.tiles.Grid;

public class ProcessActionEffectOr extends ProcessAction {
    private final int whatEffectToTrigger;
    private final ProcessActionDeliver deliver;
    private Grid grid;

    public ProcessActionEffectOr(Effect effect, int whatEffectToTrigger, ProcessActionDeliver deliver, Grid grid) {
        super(effect);
        this.whatEffectToTrigger = whatEffectToTrigger;
        this.deliver = deliver;
        this.grid = grid;
    }

    @Override
    public int activateCard() {
        EffectOr effectCasted = (EffectOr) effect;
        Effect innerEffect = effectCasted.execute(whatEffectToTrigger);

        deliver.process(innerEffect, grid);

        return 0;
    }
}
