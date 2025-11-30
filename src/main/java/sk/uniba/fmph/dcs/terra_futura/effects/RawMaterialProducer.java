package sk.uniba.fmph.dcs.terra_futura.effects;

import org.apache.commons.lang3.tuple.Pair;
import sk.uniba.fmph.dcs.terra_futura.tiles.Card;
import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;

import java.util.List;
import java.util.Map;

public class RawMaterialProducer implements Effect {
    private Map<Resource, Integer> guaranteedOutputs;
    private Resource resource;
    private int generatedPollution;


    public RawMaterialProducer(final Map<Resource, Integer> guaranteedOutputs, final int generatedPollution) {
        this.guaranteedOutputs = guaranteedOutputs;
        this.generatedPollution = generatedPollution;
        if (guaranteedOutputs.keySet().size() > 1) {
            throw new RuntimeException("To many materials");
        }
        for (Resource r : guaranteedOutputs.keySet()) {
            resource = r;
        }
    }

    public int execute(Card card, Resource resource) {
        if (!card.canPutResources(guaranteedOutputs)) {
            return 0;
        }
        if (guaranteedOutputs.containsKey(Resource.UNIVERSAL) && (resource.equals(Resource.RED) || resource.equals(Resource.YELLOW) || resource.equals(Resource.GREEN))) {
            card.putResources(Map.of(resource, 1));
        }
        if (guaranteedOutputs.containsKey(resource)) {
            card.putResources(Map.of(resource, 1));
        }
        return generatedPollution;
    }

    @Override
    public boolean canProvideAssistance() {
        return false;
    }

    @Override
    public boolean check(Card card, Map<Resource, List<Pair<Card, Integer>>> cards, Map<Resource, Integer> wantedResource) {
        boolean canExecute = true;
        if (!(card.canPutResources(guaranteedOutputs) && wantedResource.keySet().size() > 1)) {
            canExecute = false;
        }
        if (!(guaranteedOutputs.containsKey(Resource.UNIVERSAL) && (resource.equals(Resource.RED) || resource.equals(Resource.YELLOW) || resource.equals(Resource.GREEN)))) {
            canExecute = false;
        }
        if (!guaranteedOutputs.containsKey(resource)) {
            canExecute = false;
        }
        return canExecute;
    }

    @Override
    public String toString() {
        return "Generated resource/resources is " + generatedPollution;
    }
}
