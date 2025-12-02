package sk.uniba.fmph.dcs.terra_futura.effects;

import sk.uniba.fmph.dcs.terra_futura.Game;
import sk.uniba.fmph.dcs.terra_futura.ProcessActionDeliver;
import sk.uniba.fmph.dcs.terra_futura.tiles.Grid;

import java.util.Objects;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EffectOr extends SetCardToEffect {

    private List<Effect> effectList = new ArrayList<>();

    public EffectOr(SetCardToEffect e1, SetCardToEffect e2) {
        effectList.add(e1);
        effectList.add(e2);

        e1.setCard(card);
        e2.setCard(card);
    }

    public EffectOr(List<Effect> effectList) {
        this.effectList = effectList;
    }

    public List<Effect> getEffectList() {
        return Collections.unmodifiableList(this.effectList);
    }

    public Effect execute(int whatEffectToTrigger) {
        return effectList.get(whatEffectToTrigger);
    }

    @Override
    public boolean canProvideAssistance(){
        for (Effect effect : effectList) {
            if (effect.canProvideAssistance()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void apply(ProcessActionDeliver deliver) {
        deliver.process((EffectOr) this);
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("This composite effect is consist of ");
        for(Effect effect : effectList){
            sb.append(effect.toString());
        }

        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        EffectOr other = (EffectOr) obj;
        if(effectList.size() != other.effectList.size()){
            return false;
        }
        for(int i = 0; i < effectList.size(); i++){
            if(!effectList.get(i).equals(other.effectList.get(i))){
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(effectList);
    }
}
