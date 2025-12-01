package sk.uniba.fmph.dcs.terra_futura.effects;

import org.apache.commons.lang3.tuple.Pair;
import sk.uniba.fmph.dcs.terra_futura.tiles.Card;

import java.util.List;

public class PollutionTransfer extends SetCardToEffect implements Effect {

    public void execute(List<Pair<Card, Integer>> cards) {
        int commulatedPollution = 0;
        for (Pair<Card, Integer> p : cards) {
            commulatedPollution += p.getRight();
        }
        if(card.canPutPollution(commulatedPollution)){
            for (Pair<Card, Integer> p : cards) {
                p.getLeft().takePollution(p.getRight());
            }
            card.putPollution(commulatedPollution);
        }
    }

    @Override
    public boolean canProvideAssistance() {
        return true;
    }

    @Override
    public String toString() {
        return "This effect will get up to 4 pollutions " +
                "from other cards considering amount of pollution " +
                "on card that is being under this effect";
    }

    @Override
    public boolean equals(Object obj){
        return true;
    }
}
