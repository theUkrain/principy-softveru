package sk.uniba.fmph.dcs.terra_futura;

import sk.uniba.fmph.dcs.terra_futura.tiles.GridPosition;

import java.util.Collection;
import java.util.AbstractMap.SimpleEntry;
public interface InterfaceActivateGrid {
    void setActivationPattern(Collection<GridPosition> pattern);
}
