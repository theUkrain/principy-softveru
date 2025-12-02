package sk.uniba.fmph.dcs.terra_futura.process;


import sk.uniba.fmph.dcs.terra_futura.ProcessActionDeliver;
import sk.uniba.fmph.dcs.terra_futura.effects.AssistanceEffect;
import sk.uniba.fmph.dcs.terra_futura.effects.CopyableEffect;
import sk.uniba.fmph.dcs.terra_futura.effects.Effect;
import sk.uniba.fmph.dcs.terra_futura.effects.SetCardToEffect;
import sk.uniba.fmph.dcs.terra_futura.tiles.Card;
import sk.uniba.fmph.dcs.terra_futura.tiles.Grid;

public class ProcessActionAssistance<T extends Effect & CopyableEffect> extends ProcessAction {
    private final T effectAnotherPlayer;
    private ProcessActionDeliver deliver;
    private Grid grid;

    public ProcessActionAssistance(Effect effect, T effectAnotherPlayer, ProcessActionDeliver deliver, Grid grid) {
        super(effect);
        this.effectAnotherPlayer = effectAnotherPlayer;
        this.grid = grid;
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

        deliver.process(newEffect, grid);

        return 0;
    }
}
