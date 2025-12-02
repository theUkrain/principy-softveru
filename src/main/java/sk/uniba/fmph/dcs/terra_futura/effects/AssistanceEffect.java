
package sk.uniba.fmph.dcs.terra_futura.effects;

import sk.uniba.fmph.dcs.terra_futura.ProcessActionDeliver;

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
    public void apply(ProcessActionDeliver deliver) {
        deliver.process((AssistanceEffect) this);
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
