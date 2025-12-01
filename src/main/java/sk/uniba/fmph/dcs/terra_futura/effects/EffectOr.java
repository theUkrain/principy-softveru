package sk.uniba.fmph.dcs.terra_futura.effects;

import java.util.List;

public class EffectOr implements Effect {

    List<Effect> effectList;

    public EffectOr(Effect e1, Effect e2) {
        effectList.add(e1);
        effectList.add(e2);
    }

    public Effect execute(int whatEffectToTrigger) {
        return effectList.get(whatEffectToTrigger);
    }

    public boolean canProvideAssistance(){
        return true;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("This composite effect is consist of ");
        for(Effect effect : effectList){
            sb.append(effect.toString());
        }

        return sb.toString();
    }
}
