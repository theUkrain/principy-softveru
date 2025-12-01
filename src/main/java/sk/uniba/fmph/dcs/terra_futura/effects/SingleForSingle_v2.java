package sk.uniba.fmph.dcs.terra_futura.effects;

import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;
import sk.uniba.fmph.dcs.terra_futura.tiles.Card;

import java.util.Map;

public class SingleForSingle_v2  {

    private Map<Resource, Integer> requiredInputs;

    private Map<Resource, Integer> guaranteedOutputs;

    private int generatedPollution;

    public SingleForSingle_v2 (Map<Resource, Integer> requiredInputs, Map<Resource, Integer> guaranteedOutputs, int generatedPollution) {
        this.requiredInputs = requiredInputs;
        this.guaranteedOutputs = guaranteedOutputs;
        this.generatedPollution = generatedPollution;
    }

    int execute(Resource given, Resource wanted, Card takeFrom) {
        if(takeFrom.canGetResources(Map.of(given))) {

        }
    }

}
