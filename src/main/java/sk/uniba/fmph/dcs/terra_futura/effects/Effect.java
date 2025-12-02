package sk.uniba.fmph.dcs.terra_futura.effects;

import sk.uniba.fmph.dcs.terra_futura.ProcessActionDeliver;

public interface Effect {
    boolean canProvideAssistance();
    void apply(ProcessActionDeliver deliver);
}
