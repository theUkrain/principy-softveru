package sk.uniba.fmph.dcs.terra_futura;

import org.json.JSONArray;
import org.json.JSONObject;
import sk.uniba.fmph.dcs.terra_futura.tiles.GridPosition;

import java.util.ArrayList;
import java.util.Collection;


public class ActivationPattern {
    private final ArrayList<GridPosition> pattern;
    private boolean selected;
    private final InterfaceActivateGrid grid;

    public ActivationPattern(final InterfaceActivateGrid grid, final Collection<GridPosition> pattern) {

        for (GridPosition position : pattern)
            if (position.getX() > 1 || position.getY() > 1 || position.getX() < -1 || position.getY() < -1)
                throw new ArrayIndexOutOfBoundsException();

        this.grid = grid;
        this.pattern = new ArrayList<>(pattern);  // copy the pattern
        this.selected = false;

        for (GridPosition position : pattern) {
            if (position.getX() > 1 || position.getY() > 1 || position.getX() < -1 || position.getY() < -1) {
                throw new ArrayIndexOutOfBoundsException();
            }
        }
    }

    public void select() {
        if (this.selected) {
            throw new IllegalStateException("Pattern already selected");
        }
        this.grid.setActivationPattern(this.pattern);
        this.selected = true;
    }


    public boolean isSelected() {
        return this.selected;
    }

    @Override
    public String toString() {
        JSONArray patternList = new JSONArray();
        for (GridPosition position : pattern) {
            JSONObject pair = new JSONObject();
            pair.put("x", position.getX());
            pair.put("y", position.getY());
            patternList.put(pair);
        }
        JSONObject result = new JSONObject();
        result.put("selected", this.selected);
        result.put("activations", patternList);
        return result.toString();
    }
}
