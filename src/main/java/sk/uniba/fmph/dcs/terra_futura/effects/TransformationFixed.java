package sk.uniba.fmph.dcs.terra_futura.effects;

import org.apache.commons.lang3.tuple.Pair;
import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;
import sk.uniba.fmph.dcs.terra_futura.tiles.Card;

import java.util.*;

public class TransformationFixed implements Effect {

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

    public void resourceRetrivier(){

    }

    /*add one more material, universal material(colored cube), and fix
    execute in way that I would be able to use any colored material via counter and one more condition, so
    I would be able to produce some output*/

    public int execute(Card card, Map<Resource, List<Pair<Card, Integer>>> cards) {//, Map<Resource, Integer> wantedResource) {
        if (!card.canPutResources(guaranteedOutputs)) {
            return 0;
        }

        Map<Resource, Integer> recievedResources = new HashMap<>();
        recievedResources.put(Resource.RED, 0);
        recievedResources.put(Resource.YELLOW, 0);
        recievedResources.put(Resource.GREEN, 0);

        for(Resource r: cards.keySet()){
            for(Pair<Card, Integer> p: cards.get(r)){
                if(p.getLeft().canGetResources(Map.of(r, p.getRight()))){
                    recievedResources.put(r, recievedResources.get(r) + p.getRight());
                }
            }
        }


        if (requiredInputs.containsKey(Resource.UNIVERSAL)) {
            int accumulatedResource = recievedResources.get(Resource.RED) + recievedResources.get(Resource.YELLOW) + recievedResources.get(Resource.GREEN);
            if (accumulatedResource >= requiredInputs.get(Resource.UNIVERSAL)) {

                card.putResources(guaranteedOutputs);
                return generatedPollution;
            }
        }

        for(Resource r: recievedResources.keySet()){
            if(recievedResources.get(r) < requiredInputs.get(r)){
                return 0;
            }
        }

        // boolean canProduce = true;
        // for (Resource r : cards.keySet()) {
        //     int acumulatedAmountOfResource = 0;
        //     for (Pair<Card, Integer> p : cards.get(r)) {
        //         acumulatedAmountOfResource += p.getRight();
        //         if (!p.getLeft().canGetResources(Map.of(r, p.getRight()))) {
        //             canProduce = false;
        //         }
        //     }
        //     if (acumulatedAmountOfResource < requiredInputs.get(r)) {
        //         canProduce = false;
        //     }
        // }
        //if (canProduce) {
            /*if(guaranteedOutputs.containsKey(Resource.UNIVERSAL)){
                int accumulated = 0;
                for(Resource r: wantedResource.keySet()){
                    if(r.equals(Resource.RED) || r.equals(Resource.YELLOW) || r.equals(Resource.GREEN)){
                        accumulated += wantedResource.get(r);
                    }
                }
                if(accumulated > guaranteedOutputs.get(Resource.UNIVERSAL)){
                    return 0;
                }
            }
            for (Resource r : cards.keySet()) {
                for (Pair<Card, Integer> p : cards.get(r)) {
                    p.getLeft().getResources(Map.of(r, p.getRight()));
                }
            }
            for(Resource r: wantedResource.keySet()){
                card.putResources(Map.of(r,wantedResource.get(r)));
            }
            return generatedPollution;*/
        //}
        return 0;
    }

    @Override
    public boolean canProvideAssistance() {
        return true;
    }

    @Override
    public boolean check(Card card, Map<Resource, List<Pair<Card, Integer>>> cards) {
//        if (!card.canPutResources(guaranteedOutputs)) {
//            return false;
//        }
//
//        if (requiredInputs.containsKey(Resource.UNIVERSAL)) {
//            int accumulatedResource = 0;
//            for (Resource r : cards.keySet()) {
//                for (Pair<Card, Integer> p : cards.get(r)) {
//                    if (card.canGetResources(Map.of(r, p.getRight()))
//                            && (r.equals(Resource.GREEN) || r.equals(Resource.RED) || r.equals(Resource.YELLOW))) {
//                        accumulatedResource += p.getRight();
//                    }
//                }
//            }
//            if (accumulatedResource >= requiredInputs.get(Resource.UNIVERSAL)) {
//                if (guaranteedOutputs.containsKey(Resource.UNIVERSAL)
//                        && (guaranteedOutputs.get(Resource.UNIVERSAL) >= wantedResource.get(Resource.RED) || guaranteedOutputs.get(Resource.UNIVERSAL) >= wantedResource.get(Resource.YELLOW) || guaranteedOutputs.get(Resource.UNIVERSAL) >= wantedResource.get(Resource.GREEN))
//                        && (wantedResource.containsKey(Resource.RED) || wantedResource.containsKey(Resource.GREEN) || wantedResource.containsKey(Resource.YELLOW))) {
//                    return false;
//                }
//
//            }
//        }
//
//        boolean canProduce = true;
//
//        for (Resource r : cards.keySet()) {
//            int acumulatedAmountOfResource = 0;
//            for (Pair<Card, Integer> p : cards.get(r)) {
//                if (!p.getLeft().canGetResources(Map.of(r, p.getRight()))) {
//                    canProduce = false;
//                }
//                acumulatedAmountOfResource += p.getRight();
//            }
//            if (acumulatedAmountOfResource < requiredInputs.get(r)) {
//                canProduce = false;
//            }
//        }
//        for (Resource r : wantedResource.keySet()) {
//            if (!guaranteedOutputs.containsKey(r) || guaranteedOutputs.get(r) < wantedResource.getOrDefault(r, 0)) {
//                canProduce = false;
//            }
//        }
//        return canProduce;
            return false;
    }

    @Override
    public String toString() {
        return "This effect for " + requiredInputs + " can generate "
                + guaranteedOutputs + "with" + generatedPollution + "amount of pollution";
    }
}
