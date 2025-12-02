
package sk.uniba.fmph.dcs.terra_futura.effects;

import sk.uniba.fmph.dcs.terra_futura.Game;

public class AssistanceEffect extends SetCardToEffect {

    public <T extends Effect & CopyableEffect> T execute(T effectOfAnoutherPlayer) {
        if (effectOfAnoutherPlayer.canProvideAssistance()) {
            return effectOfAnoutherPlayer;
        }

        return null;
    }

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
