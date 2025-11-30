package sk.uniba.fmph.dcs.terra_futura.tiles;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class Grid {
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
        topLeft = new GridPosition(2, 2);
        bottomRight = new GridPosition(2, 2);
    }

    public Optional<Card> getCard(GridPosition coordinate){
        return Optional.ofNullable(field[2 + coordinate.getY()][2 +coordinate.getX()]);
    }

    public boolean canPutCard(GridPosition coordinate){
        if(field[2 + coordinate.getY()][2 + coordinate.getX()] != null &&
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
            return false;
        }
        return true;
    }

    public Set<Card> putCard(GridPosition coordinate, Card card){
        if(!canPutCard(coordinate)) throw new IllegalStateException("Cannot put card");
        field[coordinate.getY()][coordinate.getX()] = card;
        Set<Card> act = new HashSet<>();
        for(int i = 0; i<field.length; ++i){
            for(int j = 0; j<field[i].length; ++j){
                if(field[i][j] != null){
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
        for(GridPosition pos: pattern){
            Optional<Card> c = getCard(pos);
            if(c.isPresent()){
                ans.add(c.get());
            }
        }
        return ans;
    }

    public void setActivationPattern(List<GridPosition> pattern){
        this.pattern = pattern;
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

}
