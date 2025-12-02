package sk.uniba.fmph.dcs.terra_futura.tiles;

import sk.uniba.fmph.dcs.terra_futura.InterfaceActivateGrid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class Grid implements InterfaceActivateGrid, GridInterface {
    private Card[][] grid;

    private GridPosition topRight, bottomLeft;

    private List<GridPosition> pattern;

    public Grid() {
        grid = new Card[5][5];

        grid[2][2] = CardFactory.startCard();
        topRight = new GridPosition(0, 0);
        bottomLeft = new GridPosition(0, 0);
    }

    public Optional<Card> getCard(GridPosition coordinate) {
        return Optional.ofNullable(grid[2 + coordinate.getY()][2 + coordinate.getX()]);
    }

    public boolean canPutCard(GridPosition coordinate) {

        if (grid[2 + coordinate.getY()][2 + coordinate.getX()] != null) return false;

        if (!(Math.abs(topRight.getX() - bottomLeft.getX()) + 1 < 3) && !(coordinate.getX() >= bottomLeft.getX() && coordinate.getX() <= topRight.getX()))
            return false;

        if (!(Math.abs(topRight.getY() - bottomLeft.getY()) + 1 < 3) && !(coordinate.getY() <= bottomLeft.getY() && coordinate.getY() >= topRight.getY()))
            return false;

        return true;

    }

    public Set<Card> putCard(GridPosition coordinate, Card card) {

        if (!canPutCard(coordinate)) throw new IllegalArgumentException("Cannot put card");

        grid[2 + coordinate.getY()][2 + coordinate.getX()] = card;

        if (coordinate.getX() < bottomLeft.getX()) {
            bottomLeft = new GridPosition(coordinate.getX(), bottomLeft.getY());
        }
        if (coordinate.getX() > topRight.getX()) {
            topRight = new GridPosition(coordinate.getX(), topRight.getY());
        }
        if (coordinate.getY() < topRight.getY()) {
            topRight = new GridPosition(topRight.getX(), coordinate.getY());
        }
        if (coordinate.getY() > bottomLeft.getY()) {
            bottomLeft = new GridPosition(bottomLeft.getX(), coordinate.getY());
        }

        Set<Card> act = new HashSet<>();

        for (int i = 0; i < grid.length; ++i) {
            for (int j = 0; j < grid[i].length; ++j) {

                if (j != coordinate.getX() + 2 && i != coordinate.getY() + 2) continue;

                if (grid[i][j] != null) {
                    act.add(grid[i][j]);
                }

            }
        }

        return act;
    }

    public boolean canBeActivated(GridPosition coordinate) {
        return !grid[2 + coordinate.getY()][2 + coordinate.getX()].isOverPolluted();
    }

    public Set<Card> getActivatedCards() {
        Set<Card> ans = new HashSet<>();

        for (GridPosition position : pattern) {

            Optional<Card> c = getCard(new GridPosition(position.getX() + bottomLeft.getX() + 1, position.getY() + bottomLeft.getY() - 1));
            if (c.isPresent()) ans.add(c.get());
        }

        return ans;
    }

    @Override
    public boolean isFull() {

        int occupied = 0;

        for(int i = 0; i < 5; i++) {
            for(int j = 0; j < 5; j++) {
                if(grid[i][j] != null) occupied++;
            }
        }

        return occupied == 9;

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int j = bottomLeft.getY(); j < topRight.getY(); j++) {
            for (int i = bottomLeft.getX(); i < topRight.getX(); i++) {
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