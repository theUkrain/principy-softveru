package sk.uniba.fmph.dcs.terra_futura.effects;

import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;

public class StartingCardEffect extends SetCardToEffect {

    EffectOr effect = new EffectOr(
            new EffectOr(
                    new RawMaterialProducer(Resource.UNIVERSAL),
                    new RawMaterialProducer(Resource.MONEY)),
            new AssistanceEffect());

    public Effect execute(int whatEffectToTrigger) {
        return effect.execute(whatEffectToTrigger);
    }

    @Override
    public boolean canProvideAssistance() {
        return false;
    }

    @Override
    public boolean equals(Object obj){
        if(this == obj){
            return true;
        }
        StartingCardEffect t = (StartingCardEffect) obj;
        return this.effect.equals(t.effect);
    }
}