package sk.uniba.fmph.dcs.terra_futura.effects;

import org.apache.commons.lang3.tuple.Pair;
import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;
import sk.uniba.fmph.dcs.terra_futura.tiles.Card;

import java.util.List;
import java.util.Map;

public class StartingCardEffect implements Effect {

    EffectOr upperEffect = new EffectOr(new RawMaterialProducer(Map.of(Resource.UNIVERSAL, 1), 0),
            new RawMaterialProducer(Map.of(Resource.MONEY, 1), 0));
    Effect lowerEffect = new AssistanceEffect();

    public Effect executeUpper(int whatEffectToTrigger) {
        return upperEffect.execute(whatEffectToTrigger);
    }

    public Effect executeLower() {
        return lowerEffect;
    }

    @Override
    public boolean canProvideAssistance() {
        return false;
    }

    @Override
    public boolean check(Card card, Map<Resource, List<Pair<Card, Integer>>> cards, Map<Resource, Integer> wantedResource) {
        return lowerEffect.check(card, cards, wantedResource) && upperEffect.check(card, cards, wantedResource);
    }
}
