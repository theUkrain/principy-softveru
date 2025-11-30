//package sk.uniba.fmph.dcs.terra_futura.effects;
//
//import sk.uniba.fmph.dcs.terra_futura.tiles.Card;
//import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;
//
//import java.util.List;
//
//public class ArbitraryBasic implements Effect {
//    private List<Resource> guaranteedOutputs;
//    private int generatedPollution;
//
//    public ArbitraryBasic(final List<Resource> guaranteedOutputs, final int generatedPollution) {
//        this.guaranteedOutputs = guaranteedOutputs;
//        this.generatedPollution = generatedPollution;
//    }
//
//    public int execute(Card card, Resource resource) {
//        if (card.canPutResources(guaranteedOutputs) && guaranteedOutputs.contains(resource)) {
//            card.putResources(List.of(resource));
//        }
//        return generatedPollution;
//    }
//
//    @Override
//    public boolean canProvideAssistance() {
//        return false;
//    }
//
//    @Override
//    public String toString() {
//        return "Generated resource/resources is " + generatedPollution;
//    }
//}
