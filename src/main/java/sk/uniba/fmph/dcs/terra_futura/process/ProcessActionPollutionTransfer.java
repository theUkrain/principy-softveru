package sk.uniba.fmph.dcs.terra_futura.process;

import org.apache.commons.lang3.tuple.Pair;
import sk.uniba.fmph.dcs.terra_futura.effects.Effect;
import sk.uniba.fmph.dcs.terra_futura.effects.PollutionTransfer;
import sk.uniba.fmph.dcs.terra_futura.tiles.Card;

import java.util.List;

public class ProcessActionPollutionTransfer extends ProcessAction {
    private final List<Pair<Card, Integer>> cards;

    public ProcessActionPollutionTransfer(Effect effect, List<Pair<Card, Integer>> cards) {
        super(effect);
        this.cards = cards;
    }
    /**
     *
     * @return activates card and make corresponding actions
     */
    @Override
    public int activateCard() {
        PollutionTransfer effectCasted = (PollutionTransfer) effect;
        effectCasted.execute(cards);

        return 0;
    }
}
