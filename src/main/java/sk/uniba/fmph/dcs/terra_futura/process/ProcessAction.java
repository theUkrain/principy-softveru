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

    public void placePollution(List<Pair<Card, Integer>> placements) {
        for (Pair<Card, Integer> info: placements) {
            Card card = info.getLeft();

            if (!card.canPutPollution(info.getRight())) {
                throw new IllegalArgumentException("Card " + card + " doesnt have place to put pollution");
            }

            card.putPollution(info.getRight());
        }
    }
}
