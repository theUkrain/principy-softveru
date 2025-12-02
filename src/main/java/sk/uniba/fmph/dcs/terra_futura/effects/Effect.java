package sk.uniba.fmph.dcs.terra_futura.effects;

import sk.uniba.fmph.dcs.terra_futura.ProcessActionDeliver;
import sk.uniba.fmph.dcs.terra_futura.tiles.Grid;

public interface Effect {
    boolean canProvideAssistance();
    void apply(ProcessActionDeliver deliver, Grid grid);
}
