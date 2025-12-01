
package sk.uniba.fmph.dcs.terra_futura.effects;

import org.apache.commons.lang3.tuple.Pair;
import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;
import sk.uniba.fmph.dcs.terra_futura.tiles.Card;

import java.util.List;
import java.util.Map;

public class AssistanceEffect implements Effect {

    public Effect execute(Card cardWithAssist, Effect effectOfAnoutherPlayer,
                          Map<Resource, List<Pair<Card, Integer>>> cards,
                          Map<Resource, Integer> wantedResource) {
        if (effectOfAnoutherPlayer.canProvideAssistance() && effectOfAnoutherPlayer.check(cardWithAssist, cards)) {
            return effectOfAnoutherPlayer;
        }
        return null;
    }

    @Override
    public boolean canProvideAssistance() {
        return false;
    }

    @Override
    public boolean check(Card card, Map<Resource, List<Pair<Card, Integer>>> cards) {
        return false;
    }

    @Override
    public String toString() {
        return "This is assistance effect";
    }
}
