package sk.uniba.fmph.dcs.terra_futura.effects;

import org.apache.commons.lang3.tuple.Pair;
import sk.uniba.fmph.dcs.terra_futura.tiles.Card;
import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;

import java.util.List;
import java.util.Map;

public class RawMaterialProducer extends SetCardToEffect implements Effect {
    private final Pair<Resource, Integer> guaranteedOutputs;
    private final int generatedPollution;

    public RawMaterialProducer(final Pair<Resource, Integer> guaranteedOutputs, final int generatedPollution) {
        this.guaranteedOutputs = Pair.of(guaranteedOutputs.getLeft(), guaranteedOutputs.getRight());
        this.generatedPollution = generatedPollution;
    }

    public int execute() {
        if (!check(null)) {
            throw new IllegalStateException("Cant put resources");
        }

        card.putResources(Map.of(guaranteedOutputs.getLeft(), guaranteedOutputs.getRight()));
        return generatedPollution;
    }

    @Override
    public boolean canProvideAssistance() {
        return false;
    }

    @Override
    public boolean check(Map<Resource, List<Pair<Card, Integer>>> cards) {
        return card.canPutResources(Map.of(guaranteedOutputs.getLeft(), guaranteedOutputs.getRight()));
    }

    @Override
    public String toString() {
        return "Generated resource/resources is " + generatedPollution;
    }
}
