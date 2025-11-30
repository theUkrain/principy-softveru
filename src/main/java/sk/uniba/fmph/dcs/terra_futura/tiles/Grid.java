package sk.uniba.fmph.dcs.terra_futura.tiles;

import java.util.List;
import java.util.Optional;

public class Grid {
    private Card[][] field = new Card[4][4];

    public Grid(){}

    public Optional<Card> getCard(GridPosition coordinate){
        return null;
    }

    public boolean canPutCard(GridPosition coordinate){
        return true;
    }

    public void putCard(GridPosition coordinate){}

    public boolean canBeActivated(GridPosition coordinate){
        return true;
    }

    public void setActivated(GridPosition coordinate){}

    public void setActivationPattern(List<GridPosition> pattern){}

    public void endTurn(){}

    public String state(){
        return "";
    }
}
