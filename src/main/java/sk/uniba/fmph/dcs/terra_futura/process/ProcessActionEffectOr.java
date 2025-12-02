package sk.uniba.fmph.dcs.terra_futura.process;

import sk.uniba.fmph.dcs.terra_futura.Game;
import sk.uniba.fmph.dcs.terra_futura.effects.Effect;
import sk.uniba.fmph.dcs.terra_futura.effects.EffectOr;

public class ProcessActionEffectOr extends ProcessAction {
    private final int whatEffectToTrigger;
    private final Game game;

    // TODO Game reference for casting inner effect
    public ProcessActionEffectOr(Effect effect, int whatEffectToTrigger, Game game) {
        super(effect);
        this.whatEffectToTrigger = whatEffectToTrigger;
        this.game = game;
    }

    @Override
    public int activateCard() {
        EffectOr effectCasted = (EffectOr) effect;
        Effect innerEffect = effectCasted.execute(whatEffectToTrigger);

        game.process(innerEffect);

        return 0;
    }
}
