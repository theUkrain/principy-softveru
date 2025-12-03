package sk.uniba.fmph.dcs.terra_futura.effects;

import sk.uniba.fmph.dcs.terra_futura.ProcessActionDeliver;

public class AssistanceEffect extends SetCardToEffect {

    /**
     * @param effectOfAnotherPlayer - effect of another player that will be executed according to games rules
     * @param <T>                   - generalized to make possible to use other player effect in outer logic
     * @return - instance of effect, that will be processed by outer logic
     */
    public <T extends Effect & CopyableEffect> T execute(T effectOfAnotherPlayer) {
        if (effectOfAnotherPlayer.canProvideAssistance()) {
            return effectOfAnotherPlayer;
        }

        return null;
    }

    /**
     * @return can effect be executed via assistance effect
     */
    @Override
    public boolean canProvideAssistance() {
        return false;
    }


    @Override
    public void apply(ProcessActionDeliver deliver) {
        deliver.process(this);
    }

    @Override
    public String toString() {
        return "This is assistance effect";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return obj instanceof AssistanceEffect;
    }
}
