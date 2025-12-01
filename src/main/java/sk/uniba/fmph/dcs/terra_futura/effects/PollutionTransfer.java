package sk.uniba.fmph.dcs.terra_futura.effects;

import org.apache.commons.lang3.tuple.Pair;
import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;
import sk.uniba.fmph.dcs.terra_futura.tiles.Card;

import java.util.List;
import java.util.Map;

public class PollutionTransfer extends SetCardToEffect {

    public int execute(Card card, List<Pair<Card, Integer>> cards) {
        int commulatedPollution = 0;
        for (Pair<Card, Integer> p : cards) {
            commulatedPollution += p.getRight();
        }
        if (card.canPutResources(Map.of(Resource.POLLUTION, commulatedPollution))) {
            for (Pair<Card, Integer> p : cards) {
                p.getLeft().getResources(Map.of(Resource.POLLUTION, p.getRight()));
            }
            return -commulatedPollution;
        }
        return 0;
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
}
