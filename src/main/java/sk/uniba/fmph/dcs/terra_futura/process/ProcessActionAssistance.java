package sk.uniba.fmph.dcs.terra_futura.process;


import sk.uniba.fmph.dcs.terra_futura.Game;
import sk.uniba.fmph.dcs.terra_futura.effects.AssistanceEffect;
import sk.uniba.fmph.dcs.terra_futura.effects.CopyableEffect;
import sk.uniba.fmph.dcs.terra_futura.effects.Effect;
import sk.uniba.fmph.dcs.terra_futura.effects.SetCardToEffect;
import sk.uniba.fmph.dcs.terra_futura.tiles.Card;

public class ProcessActionAssistance<T extends Effect & CopyableEffect> extends ProcessAction {
    private T effectAnotherPlayer;
    private Game game;

    public ProcessActionAssistance(Effect effect, T effectAnotherPlayer, Game game) {
        super(effect);
        this.effectAnotherPlayer = effectAnotherPlayer;
    }

    @Override
    public int activateCard() {
        AssistanceEffect effectCasted = (AssistanceEffect) effect;
        T result = effectCasted.execute(effectAnotherPlayer);

        if (result == null) {
            throw new IllegalArgumentException("You cant activate assistance effect on that card");
        }

        Card card = effectCasted.getCard();
        SetCardToEffect newEffect = (SetCardToEffect) result.copy();
        newEffect.setCard(card);

        game.process((Effect) newEffect);

        return 0;
    }
}
