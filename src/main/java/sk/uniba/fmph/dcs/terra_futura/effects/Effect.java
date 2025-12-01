package sk.uniba.fmph.dcs.terra_futura.effects;

import sk.uniba.fmph.dcs.terra_futura.Game;

public interface Effect {
    boolean canProvideAssistance();
    void apply(Game game);
}
