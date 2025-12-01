package sk.uniba.fmph.dcs.terra_futura.effects;

import org.apache.commons.lang3.tuple.Pair;
import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;
import sk.uniba.fmph.dcs.terra_futura.tiles.Card;

import java.util.*;

public class TransformationFixed extends SetCardToEffect {

    private final Map<Resource, Integer> requiredInputs;
    private final Map<Resource, Integer> guaranteedOutputs;
    private final int generatedPollution;

    public TransformationFixed(final Map<Resource, Integer> requiredInputs,
                               final Map<Resource, Integer> guaranteedOutputs,
                               final int generatedPollution) {
        this.requiredInputs = requiredInputs;
        this.guaranteedOutputs = guaranteedOutputs;
        this.generatedPollution = generatedPollution;
    }

    public void resourceRetriever(Map<Resource, List<Pair<Card, Integer>>> cards){
        for(Resource r: cards.keySet()){
            for(Pair<Card, Integer> p: cards.get(r)){
                p.getLeft().getResources(Map.of(r, p.getRight()));
            }
        }
    }

    /*add one more material, universal material(colored cube), and fix
    execute in way that I would be able to use any colored material via counter and one more condition, so
    I would be able to produce some output*/

    public int execute(Map<Resource, List<Pair<Card, Integer>>> cards) {
        if (!card.canPutResources(guaranteedOutputs)) {
            throw new IllegalStateException("Card unavailable");
        }

        Map<Resource, Integer> receivedResources = new HashMap<>();
        receivedResources.put(Resource.MONEY, 0);
        receivedResources.put(Resource.RED, 0);
        receivedResources.put(Resource.YELLOW, 0);
        receivedResources.put(Resource.GREEN, 0);

        for(Resource r: cards.keySet()){
            for(Pair<Card, Integer> p: cards.get(r)){
                if(p.getLeft().canGetResources(Map.of(r, p.getRight()))){
                    receivedResources.put(r, receivedResources.get(r) + p.getRight());
                }
            }
        }


        if (requiredInputs.containsKey(Resource.UNIVERSAL)) {
            int accumulatedResource = receivedResources.get(Resource.RED) + receivedResources.get(Resource.YELLOW) + receivedResources.get(Resource.GREEN);
            if (accumulatedResource >= requiredInputs.get(Resource.UNIVERSAL)) {
                resourceRetriever(cards);
                card.putResources(guaranteedOutputs);
                return generatedPollution;
            }
        }

        for(Resource r: receivedResources.keySet()){
            if(requiredInputs.containsKey(r) && receivedResources.get(r) < requiredInputs.get(r)) {
                throw new IllegalStateException("Insufficient resources");
            }
        }

        card.putResources(guaranteedOutputs);
        resourceRetriever(cards);
        return generatedPollution;
    }

    @Override
    public boolean canProvideAssistance() {
        return true;
    }

    @Override
    public String toString() {
        return "This effect for " + requiredInputs + " can generate "
                + guaranteedOutputs + "with" + generatedPollution + "amount of pollution";
    }
}
