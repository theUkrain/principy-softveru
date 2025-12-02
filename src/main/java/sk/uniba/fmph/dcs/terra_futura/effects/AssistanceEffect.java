
package sk.uniba.fmph.dcs.terra_futura.effects;

import sk.uniba.fmph.dcs.terra_futura.Game;
import sk.uniba.fmph.dcs.terra_futura.ProcessActionDeliver;
import sk.uniba.fmph.dcs.terra_futura.tiles.Grid;

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
    public void apply(ProcessActionDeliver deliver, Grid grid) {
        deliver.process((AssistanceEffect) this, grid);
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
