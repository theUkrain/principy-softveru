package sk.uniba.fmph.dcs.terra_futura;

import java.util.Collection;

import sk.uniba.fmph.dcs.terra_futura.tiles.GridPosition;

public interface InterfaceActivateGrid {
    void setActivationPattern(Collection<GridPosition> pattern);
}
