package sk.uniba.fmph.dcs.terra_futura;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Deck;
import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;
import sk.uniba.fmph.dcs.terra_futura.tiles.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScoringMethodTest {
    Grid grid;
    List<Pair<Resource, Integer>> combination;
    int pointsPerCombination;
    ScoringMethod sm;

    @BeforeEach
    public void setup() {
        CardFactory.reset();
        this.grid = new Grid();
        for (int i = -2; i < 1; i++) {
            for (int j = -2; j < 1; j++) {
                Card card = CardFactory.card(1, null, null, new CardSource(1, Deck.I));
                card.putResources(Map.ofEntries(Map.entry(Resource.GREEN, 1)
                        , Map.entry(Resource.RED, 1)
                        , Map.entry(Resource.YELLOW, 1)
                        , Map.entry(Resource.MONEY, 1)
                        , Map.entry(Resource.BULB, 1)
                        , Map.entry(Resource.CAR, 1)
                        , Map.entry(Resource.GEAR, 2)));
                if (i == 0 && j == 0) continue;
                grid.putCard(new GridPosition(i, j), card);
            }
        }
        combination = new ArrayList<>(List.of(Pair.of(Resource.GEAR, 2), Pair.of(Resource.CAR, 1)));
        pointsPerCombination = 5;
        sm = new ScoringMethod(grid, combination, pointsPerCombination);
    }

    @AfterEach
    public void reset(){
        CardFactory.reset();
    }

    @Test
    public void testBasicCalculation() {
        Assertions.assertEquals(232, sm.selectThisMethodAndCalculate());
    }

    @Test
    public void testSubstractionByPollution() {
        try {
            grid.getCard(new GridPosition(-1, -1)).get().putPollution(1);
            sm = new ScoringMethod(grid, combination, pointsPerCombination);
            Assertions.assertEquals(195, sm.selectThisMethodAndCalculate());

            grid.getCard(new GridPosition(0, -1)).get().putPollution(1);
            sm = new ScoringMethod(grid, combination, pointsPerCombination);
            Assertions.assertEquals(171, sm.selectThisMethodAndCalculate());
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testEmptyGrid() {
        try {
            grid = new Grid();
            for (int i = -2; i < 1; i++) {
                for (int j = -2; j < 1; j++) {
                    Card card = CardFactory.card(1, null, null, new CardSource(1, Deck.I));
                    grid.putCard(new GridPosition(i, j), card);
                }
            }
            sm = new ScoringMethod(grid, combination, pointsPerCombination);
            Assertions.assertEquals(0, sm.selectThisMethodAndCalculate());
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testAllLockedCards() {
        for (int i = -2; i < 1; i++) {
            for (int j = -2; j < 1; j++) {
                grid.getCard(new GridPosition(i, j)).get().putPollution(1);
            }
        }
        try {
            sm = new ScoringMethod(grid, combination, pointsPerCombination);
            Assertions.assertEquals(0, sm.selectThisMethodAndCalculate());
        }
        catch(IllegalArgumentException e){}
    }

    @Test
    public void unEvenDistroOfBonus(){
        grid.getCard(new GridPosition(-1,-2)).get().putResources(Map.of(Resource.CAR,2));
        try {
            sm = new ScoringMethod(grid, combination, pointsPerCombination);
            Assertions.assertEquals(244, sm.selectThisMethodAndCalculate());
        }
        catch(IllegalArgumentException e){}
    }
}