package sk.uniba.fmph.dcs.terra_futura.process;

import org.apache.commons.lang3.tuple.Pair;
import sk.uniba.fmph.dcs.terra_futura.effects.Effect;
import sk.uniba.fmph.dcs.terra_futura.tiles.Card;

import java.util.List;

public abstract class ProcessAction {
    protected Effect effect;

    public ProcessAction(Effect effect) {
        this.effect = effect;
    }

    public int activateCard() {
        return 0;
    }
}
