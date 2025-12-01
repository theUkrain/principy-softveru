package sk.uniba.fmph.dcs.terra_futura.effects;

import org.apache.commons.lang3.tuple.Pair;
import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;
import sk.uniba.fmph.dcs.terra_futura.tiles.Card;

import java.util.List;
import java.util.Map;

public class SingleForSingle implements Effect {
    private Map<Resource, Integer> requiredInputs;

    private Map<Resource, Integer> guaranteedOutputs;

    private int generatedPollution;


    public SingleForSingle(Map<Resource, Integer> requiredInputs, Map<Resource, Integer> guaranteedOutputs, int generatedPollution) {
        this.requiredInputs = requiredInputs;
        this.guaranteedOutputs = guaranteedOutputs;
        this.generatedPollution = generatedPollution;
    }

    /*I need here Map<Resource, Integer> wantedResource
    so user tell me which one of suggested material he would want to get.
    Finish implementing execute*/
    public int execute(Card card, Map<Resource, List<Pair<Card, Integer>>> cards, Map<Resource, Integer> wantedResource) {
        if (!card.canPutResources(guaranteedOutputs)) {
            return 0;
        }

        if (requiredInputs.containsKey(Resource.UNIVERSAL)) {
            int accumulatedResource = 0;
            for (Resource r : cards.keySet()) {
                for (Pair<Card, Integer> p : cards.get(r)) {
                    if (card.canGetResources(Map.of(r, p.getRight()))
                            && (r.equals(Resource.GREEN) || r.equals(Resource.RED) || r.equals(Resource.YELLOW))) {
                        accumulatedResource += p.getRight();
                    }
                }
            }
            if (accumulatedResource >= requiredInputs.get(Resource.UNIVERSAL)) {
                if (guaranteedOutputs.containsKey(Resource.UNIVERSAL)
                        && (guaranteedOutputs.get(Resource.UNIVERSAL) >= wantedResource.get(Resource.RED) || guaranteedOutputs.get(Resource.UNIVERSAL) >= wantedResource.get(Resource.YELLOW) || guaranteedOutputs.get(Resource.UNIVERSAL) >= wantedResource.get(Resource.GREEN))
                        && (wantedResource.containsKey(Resource.RED) || wantedResource.containsKey(Resource.GREEN) || wantedResource.containsKey(Resource.YELLOW))) {
                    card.putResources(wantedResource);
                    return generatedPollution;
                }

            }
        }

        boolean canProduce = true;

        for (Resource r : cards.keySet()) {
            int acumulatedAmountOfResource = 0;
            for (Pair<Card, Integer> p : cards.get(r)) {
                if (!p.getLeft().canGetResources(Map.of(r, p.getRight()))) {
                    canProduce = false;
                }
                acumulatedAmountOfResource += p.getRight();
            }
            if (acumulatedAmountOfResource < requiredInputs.get(r)) {
                canProduce = false;
            }
        }
        for (Resource r : wantedResource.keySet()) {
            if (guaranteedOutputs.get(r) < wantedResource.getOrDefault(r, 0)) {
                canProduce = false;
            }
        }
        if (canProduce) {
            for (Resource r : cards.keySet()) {
                for (Pair<Card, Integer> p : cards.get(r)) {
                    p.getLeft().getResources(Map.of(r, p.getRight()));
                }
            }
            for (Resource r : wantedResource.keySet()) {
                if (guaranteedOutputs.containsKey(r)) {
                    card.putResources(Map.of(r, guaranteedOutputs.get(r)));
                }
            }
            return generatedPollution;
        }
        return 0;
    }

    @Override
    public boolean canProvideAssistance() {
        return true;
    }

//    @Override
//    public boolean check(Card card, Map<Resource, List<Pair<Card, Integer>>> cards) {
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
//     return true;
// }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("This effect in exchange for: ");
        for (Resource r : requiredInputs.keySet()) {
            out.append(r);
            out.append(" with quantity of ");
            out.append(requiredInputs.get(r));
            out.append(" or ");
        }
        out.append("can provide you with ");
        for (Resource r : guaranteedOutputs.keySet()) {
            out.append(r);
            out.append(" with quantity of ");
            out.append(guaranteedOutputs.get(r));
            out.append(" or ");
        }
        out.append(" and generate ");
        out.append(generatedPollution);
        out.append(" amount of pollution");
        return out.toString();
    }
}
