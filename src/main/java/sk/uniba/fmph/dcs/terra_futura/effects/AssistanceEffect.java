
package sk.uniba.fmph.dcs.terra_futura.effects;

public class AssistanceEffect extends SetCardToEffect {

    public Effect execute(Effect effectOfAnoutherPlayer) {
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
    public String toString() {
        return "This is assistance effect";
    }

    @Override
    public boolean equals(Object obj){
        if(obj instanceof AssistanceEffect) {
            return true;
        }
        return false;
    }
}
