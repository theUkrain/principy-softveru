package sk.uniba.fmph.dcs.terra_futura;

import sk.uniba.fmph.dcs.terra_futura.tiles.Grid;

public class Player {
    Grid grid;
    ActivationPattern activationPattern1;
    ActivationPattern activationPattern2;
    ScoringMethod scoringMethod1;
    ScoringMethod scoringMethod2;



    public Player(Grid grid, ActivationPattern activationPattern1, ActivationPattern activationPattern2,
                  ScoringMethod scoringMethod1, ScoringMethod scoringMethod2) {
        
        this.grid = grid;
        this.activationPattern1 = activationPattern1;
        this.activationPattern2 = activationPattern2;
        this.scoringMethod1 = scoringMethod1;
        this.scoringMethod2 = scoringMethod2;
    }

    public int selectFirstScoringMethod(){
        return scoringMethod1.selectThisMethodAndCalculate();
    }

    public int selectSecondScoringMethod(){
        return scoringMethod2.selectThisMethodAndCalculate();
    }

    public void selectFirstActivationPattern(){
        activationPattern1.select();
    }

    public void selectSecondActivationPattern(){
        activationPattern2.select();
    }
}
