package sk.uniba.fmph.dcs.terra_futura.effects;

import java.util.ArrayList;
import java.util.List;

public class EffectOr extends SetCardToEffect implements Effect {

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

    public String toString(){
        return "This composite effect is consist of" + effectPair.getFirst()
                + " and " + effectPair.getLast() + " they will do ongoing effects \n First: "
                + effectPair.getFirst().toString() + " Second: " + effectPair.getLast().toString();
    }

}
