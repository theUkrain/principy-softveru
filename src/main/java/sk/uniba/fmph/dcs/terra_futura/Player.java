package sk.uniba.fmph.dcs.terra_futura;

import sk.uniba.fmph.dcs.terra_futura.tiles.Grid;

public class Player {
    private String name;
    private Grid grid;
    private ActivationPattern activationPattern1;
    private ActivationPattern activationPattern2;
    private ScoringMethod scoringMethod1;
    private ScoringMethod scoringMethod2;

    public Player(String name, Grid grid, ActivationPattern activationPattern1, ActivationPattern activationPattern2,
                  ScoringMethod scoringMethod1, ScoringMethod scoringMethod2) {
        this.name = name;
        this.grid = grid;
        this.activationPattern1 = activationPattern1;
        this.activationPattern2 = activationPattern2;
        this.scoringMethod1 = scoringMethod1;
        this.scoringMethod2 = scoringMethod2;
    }

    public String getName() {
        return name;
    }

    public int selectFirstScoringMethod() {
        return scoringMethod1.selectThisMethodAndCalculate();
    }

    public ScoringMethod getFirstScoringMethod() {
        return scoringMethod1;
    }

    public int selectSecondScoringMethod() {
        return scoringMethod2.selectThisMethodAndCalculate();
    }

    public ScoringMethod getSecondScoringMethod() {
        return scoringMethod2;
    }

    public void selectFirstActivationPattern() {
        activationPattern1.select();
    }

    public ActivationPattern getFirstActivationPattern() {
        return activationPattern1;
    }

    public void selectSecondActivationPattern() {
        activationPattern2.select();
    }

    public ActivationPattern getSecondActivationPattern() {
        return activationPattern2;
    }

    public Grid getGrid() {
        return grid;
    }
}
