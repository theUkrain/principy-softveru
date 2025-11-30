package sk.uniba.fmph.dcs.terra_futura.effects;

import org.apache.commons.lang3.tuple.Pair;
import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;
import sk.uniba.fmph.dcs.terra_futura.tiles.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TransformationFixed implements Effect {

    private Map<Resource, Integer> requiredInputs;
    private Map<Resource, Integer> guaranteedOutputs;
    private int generatedPollution;

    public TransformationFixed(final Map<Resource, Integer> requiredInputs,
                               final Map<Resource, Integer> guaranteedOutputs,
                               final int generatedPollution) {
        this.requiredInputs = requiredInputs;
        this.guaranteedOutputs = guaranteedOutputs;
        this.generatedPollution = generatedPollution;
    }

    public int execute(Card card, Map<Resource, List<Pair<Card, Integer>>> cards, Map<Resource, Integer> wantedResource) {

        if(!card.canPutResources(guaranteedOutputs)){
            return 0;
        }

        boolean canProduce = true;
        for(Resource r: cards.keySet()){
            int acumulatedAmountOfResource = 0;
            for(Pair<Card, Integer> p: cards.get(r)){
                acumulatedAmountOfResource +=p.getRight();
                if(!p.getLeft().canGetResources(Map.of(r,p.getRight()))){
                    canProduce = false;
                }
            }
            if(acumulatedAmountOfResource<requiredInputs.get(r)){
                canProduce = false;
            }
        }
        if(canProduce){
            for(Resource r: cards.keySet()){
                for(Pair<Card, Integer> p: cards.get(r)){
                    if(p.getLeft().canGetResources()){
                        p.getLeft().getResources(Map.of(r,p.getRight()));
                    }
                }
            }
            card.putResources(guaranteedOutputs);
            return generatedPollution;
        }
        return 0;
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
