package sk.uniba.fmph.dcs.terra_futura.tiles;

import sk.uniba.fmph.dcs.terra_futura.InterfaceActivateGrid;

import java.util.*;

import sk.uniba.fmph.dcs.terra_futura.InterfaceActivateGrid;

public class Grid implements GridInterface, InterfaceActivateGrid{
    private Card[][] field;

    private GridPosition topLeft, bottomRight;

    private List<List<Card>> field;

    private Collection<GridPosition> pattern;

    public Grid() {
        field = new ArrayList<>(5);

        for (int i = 0; i < 5; i++) {
            field.add(new ArrayList<>());

            for (int j = 0; j < 5; j++) {
                field.get(i).add(null);
            }
        }

        Card card = CardFactory.startCard();
        field.get(2).set(2, card);

        ltX = 2;
        ltY = 2;
        rbX = 2;
        rbY = 2;
    }

    public Optional<Card> getCard(GridPosition coordinate) {
        return Optional.ofNullable(field.get(coordinate.getY() + 2).get(coordinate.getX() + 2));
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

        else if (coordinate.getX() + 2 < ltX) {
            ltX = coordinate.getX() + 2;
        }

        if (coordinate.getY() + 2 > rbY) {
            rbY = coordinate.getY() + 2;
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

        return cards;
    }

    @Override
    public void setActivationPattern(Collection<GridPosition> pattern) {
        this.pattern = pattern;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int j = -2; j < 2; j++) {
            for (int i = -2; i < 2; i++) {
                Optional<Card> card = getCard(new GridPosition(j, i));


                if (card.isPresent()) {
                    sb.append("X: ").append(i).append(", Y:").append(j).append(": \n").append(card.get());
                }
            }
        }


        return sb.toString();
    }
}
