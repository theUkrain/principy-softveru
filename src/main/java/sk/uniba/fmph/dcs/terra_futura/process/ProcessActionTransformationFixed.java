package sk.uniba.fmph.dcs.terra_futura.process;

import org.apache.commons.lang3.tuple.Pair;
import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;
import sk.uniba.fmph.dcs.terra_futura.effects.Effect;
import sk.uniba.fmph.dcs.terra_futura.effects.TransformationFixed;
import sk.uniba.fmph.dcs.terra_futura.tiles.Card;

import java.util.List;
import java.util.Map;

public class ProcessActionTransformationFixed extends ProcessAction {
    private Map<Resource, List<Pair<Card, Integer>>> cards;

    public ProcessActionTransformationFixed(Effect effect, Map<Resource, List<Pair<Card, Integer>>> cards) {
        super(effect);
        this.cards = cards;
    }

    @Override
    public int activateCard() {
        TransformationFixed effectCasted = ((TransformationFixed)effect);
        return effectCasted.execute(this.cards);
    }
}
