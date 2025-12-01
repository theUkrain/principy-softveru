package sk.uniba.fmph.dcs.terra_futura.effects;

import org.apache.commons.lang3.tuple.Pair;
import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;
import sk.uniba.fmph.dcs.terra_futura.tiles.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EffectOr implements Effect {

    List<Effect> effectPair = new ArrayList<>();

    public EffectOr(Effect e1, Effect e2) {
        effectPair.add(e1);
        effectPair.add(e2);
    }

    public Effect execute(int whatEffectToTrigger) {
        return effectPair.get(whatEffectToTrigger);
    }

    public boolean canProvideAssistance(){
        return true;
    }

    @Override
    public boolean check(Card card, Map<Resource, List<Pair<Card, Integer>>> cards) {
        return effectPair.getFirst().check(card, cards) &&
                effectPair.getLast().check(card,cards);
    }

    public String toString(){
        return "This composite effect is consist of" + effectPair.getFirst()
                + " and " + effectPair.getLast() + " they will do ongoing effects \n First: "
                + effectPair.getFirst().toString() + " Second: " + effectPair.getLast().toString();
    }

}
