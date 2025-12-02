
package sk.uniba.fmph.dcs.terra_futura.effects;

import sk.uniba.fmph.dcs.terra_futura.ProcessActionDeliver;

public class AssistanceEffect extends SetCardToEffect {

    /**
     *
     * @param effectOfAnoutherPlayer - effect of another player that will be executed according to games rules
     * @return - instance of effect, that will be processed by outer logic
     * @param <T> - generalized to make possible to use other player effect in outer logic
     */
    public <T extends Effect & CopyableEffect> T execute(T effectOfAnoutherPlayer) {
        if (effectOfAnoutherPlayer.canProvideAssistance()) {
            return effectOfAnoutherPlayer;
        }

        return null;
    }

    /**
     *
     * @return can effect be executed via assistance effect
     */
    @Override
    public boolean canProvideAssistance() {
        return false;
    }

    /**
     * used for visitor pattern in outer logic
     * @param deliver
     */
    @Override
    public void apply(ProcessActionDeliver deliver) {
        deliver.process((AssistanceEffect) this);
    }

    /**
     *
     * @return description of effect
     */
    @Override
    public String toString() {
        return "This is assistance effect";
    }

    /**
     *
     * @param obj
     * @return whether object is equal to this or not
     */
    @Override
    public boolean equals(Object obj){
        if(this == obj){
            return true;
        }
        return obj instanceof AssistanceEffect;
    }
}
