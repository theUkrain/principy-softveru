package sk.uniba.fmph.dcs.terra_futura;

import org.json.JSONObject;
import org.json.JSONArray;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import sk.uniba.fmph.dcs.terra_futura.tiles.GridPosition;

import static org.junit.Assert.*;

class ActivateGridFake implements InterfaceActivateGrid {
    ArrayList<GridPosition> activations;

    public ActivateGridFake() {
        this.activations = new ArrayList<>();
    }

    @Override
    public void setActivationPattern(Collection<GridPosition> pattern) {
        activations = new ArrayList<>(pattern);
    }
}

public class ActivationPatternTest {

    private ActivateGridFake grid;
    private ActivationPattern activationPattern;
    private ArrayList<GridPosition> patternEntries;

    @Before
    public void setUp() {
        grid = new ActivateGridFake();
        patternEntries = new ArrayList<>();
        patternEntries.add(new GridPosition(0, 0));
        patternEntries.add(new GridPosition(0, 0));
        patternEntries.add(new GridPosition(-1, 1));
        activationPattern = new ActivationPattern(grid, patternEntries);
    }

    private void checkStateString(String expectedList, boolean expectedActivated) {
        System.out.println("Nas JSON:\n");
        System.out.println(activationPattern.state());
        System.out.println("Nas JSON:\n");
        JSONObject obj = new JSONObject(activationPattern.state());
        JSONArray arr =  obj.getJSONArray("activations");
        StringBuilder s = new StringBuilder();
        for(int i=0; i<arr.length(); i++) {
            JSONObject pair = arr.getJSONObject(i);
            s.append(String.format("(%s,%s)", pair.getInt("x"), pair.getInt("y")));
        }

        assertEquals(expectedList, s.toString());
        assertEquals(expectedActivated, obj.getBoolean("selected"));
    }

    @Test
    public void testDataForwarding() {
        checkStateString("(0,0)(0,0)(-1,1)", false);
        activationPattern.select();
        checkStateString("(0,0)(0,0)(-1,1)", true);
        assertEquals(3, grid.activations.size());
        assertEquals(0, grid.activations.get(0).getX());
        assertEquals(0, grid.activations.get(0).getY());
        assertEquals(0, grid.activations.get(1).getX());
        assertEquals(0, grid.activations.get(1).getY());
        assertEquals(-1, grid.activations.get(2).getX());
        assertEquals(1, grid.activations.get(2).getY());
    }


    @Test
    public void testPatternCannotBeActivatedTwice() {
        activationPattern.select();
        //assertThrows(activationPattern.select());
    }
}
