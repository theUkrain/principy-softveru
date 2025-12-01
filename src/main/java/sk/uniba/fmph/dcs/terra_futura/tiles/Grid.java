package sk.uniba.fmph.dcs.terra_futura.tiles;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import sk.uniba.fmph.dcs.terra_futura.InterfaceActivateGrid;

public class Grid implements GridInterface, InterfaceActivateGrid{
    private Card[][] field;

    private GridPosition topLeft, bottomRight;

    private List<GridPosition> pattern;

    public Grid(){
        field = new Card[5][5];
        for(int i = 0; i<field.length; ++i){
            for(int j = 0; j<field[i].length; ++j){
                field[i][j] = null;
            }
        }
        field[2][2] = CardFactory.startCard();
        topLeft = new GridPosition(0, 0);
        bottomRight = new GridPosition(0, 0);
    }

    public Optional<Card> getCard(GridPosition coordinate){
        return Optional.ofNullable(field[2 + coordinate.getY()][2 +coordinate.getX()]);
    }

    public boolean canPutCard(GridPosition coordinate){
        if(field[2 + coordinate.getY()][2 + coordinate.getX()] == null &&
            (
                Math.abs(topLeft.getX() - bottomRight.getX()) + 1 < 3 ||
                (
                    coordinate.getX() <= bottomRight.getX() &&
                    coordinate.getX() >= topLeft.getX()
                )
            ) &&
            (
                Math.abs(topLeft.getY() - bottomRight.getY()) + 1 < 3 ||
                (
                    coordinate.getY() <= bottomRight.getY() &&
                    coordinate.getY() >= topLeft.getY()
                )
            )
        ){
            return true;
        }
        return false;
    }

    public Set<Card> putCard(GridPosition coordinate, Card card){
        if(!canPutCard(coordinate)) throw new IllegalArgumentException("Cannot put card");
        field[2 + coordinate.getY()][2 + coordinate.getX()] = card;
        if(coordinate.getX() < topLeft.getX()){
            topLeft = new GridPosition(coordinate.getX(), topLeft.getY());
        }
        if(coordinate.getX() > bottomRight.getX()){
            bottomRight = new GridPosition(coordinate.getX(), bottomRight.getY());
        }
        if(coordinate.getY() < topLeft.getY()){
            topLeft = new GridPosition(topLeft.getX(), coordinate.getY());
        }
        if(coordinate.getY() > bottomRight.getY()){
            bottomRight = new GridPosition(bottomRight.getX(), coordinate.getY());
        }

        Set<Card> act = new HashSet<>();
        for(int i = 0; i<field.length; ++i){
            for(int j = 0; j<field[i].length; ++j){
                if(field[i][j] != null && (i == coordinate.getY() + 2 || j == coordinate.getX() + 2)){
                    act.add(field[i][j]);
                }
            }
        }
        return act;
    }

    public boolean canBeActivated(GridPosition coordinate){
        return !field[2 + coordinate.getY()][2 + coordinate.getX()].isOverPolluted();
    }

    public Set<Card> getActivatedCards(){
        Set<Card> ans = new HashSet<>();
        for(GridPosition tpos: pattern){
            GridPosition pos = new GridPosition(tpos.getX() + topLeft.getX() + 1, tpos.getY() + topLeft.getY() + 1);
            Optional<Card> c = getCard(pos);
            if(c.isPresent()){
                ans.add(c.get());
            }
        }
        return ans;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int j = topLeft.getY(); j < bottomRight.getY(); j++) {
            for (int i = topLeft.getX(); i < bottomRight.getX(); i++) {
                Optional<Card> card = getCard(new GridPosition(j, i));

                if (card.isPresent()) {
                    sb.append("X: " + i + ", Y:" + j + ": " + card.get().toString());
                }
            }
        }

        return sb.toString();
    }

    @Override
    public void setActivationPattern(Collection<GridPosition> pattern) {
        this.pattern = new ArrayList<>(pattern);
    }

}