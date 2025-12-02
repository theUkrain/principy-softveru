
package sk.uniba.fmph.dcs.terra_futura.effects;

import sk.uniba.fmph.dcs.terra_futura.Game;

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


    @Override
    public void apply(Game game) {
        game.process(this);
    }

    @Override
    public String toString() {
        return "This is assistance effect";
    }

    @Override
    public boolean equals(Object obj){
        if(this == obj){
            return true;
        }
        return obj instanceof AssistanceEffect;
    }
}
