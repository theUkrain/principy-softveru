package sk.uniba.fmph.dcs.terra_futura.effects;

import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;
import sk.uniba.fmph.dcs.terra_futura.tiles.Card;

import java.util.Map;

public class SingleForSingle_v2 extends SetCardToEffect {

    private Map<Resource, Integer> requiredInputs;

    private Map<Resource, Integer> guaranteedOutputs;

    private int generatedPollution;

    public int execute (Map<Resource, Integer> requiredInputs, Map<Resource, Integer> guaranteedOutputs, int generatedPollution) {
        return 0;
    }

    int execute(Resource given, Resource wanted, Card takeFrom) {
        return 0;
    }

    @Override
    public boolean canProvideAssistance() {
        return false;
    }
}
