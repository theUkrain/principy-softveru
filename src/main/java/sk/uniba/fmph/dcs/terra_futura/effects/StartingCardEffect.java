package sk.uniba.fmph.dcs.terra_futura.effects;

import org.apache.commons.lang3.tuple.Pair;
import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;
import sk.uniba.fmph.dcs.terra_futura.tiles.Card;

import java.util.List;
import java.util.Map;

public class StartingCardEffect implements Effect {

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

}