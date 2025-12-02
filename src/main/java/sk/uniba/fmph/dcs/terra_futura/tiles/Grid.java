package sk.uniba.fmph.dcs.terra_futura.tiles;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import sk.uniba.fmph.dcs.terra_futura.InterfaceActivateGrid;

public class Grid implements GridInterface, InterfaceActivateGrid {
    private static final int GRID_SIZE = 5;
    private static final int CENTER_OFFSET = 2;
    private static final int MAX_GRID_DIMENSION = 3;

    private Card[][] field;
    private int cardQuantity = 0;
    private GridPosition topLeft, bottomRight;
    private List<GridPosition> pattern;

    public Grid() {
        field = new Card[GRID_SIZE][GRID_SIZE];

        for (int i = 0; i < field.length; ++i) {
            for (int j = 0; j < field[i].length; ++j) {
                field[i][j] = null;
            }
        }

        field[CENTER_OFFSET][CENTER_OFFSET] = CardFactory.startCard();
        cardQuantity++;
        topLeft = new GridPosition(0, 0);
        bottomRight = new GridPosition(0, 0);
    }

    public Optional<Card> getCard(GridPosition coordinate) {
        int row = CENTER_OFFSET + coordinate.getY();
        int col = CENTER_OFFSET + coordinate.getX();
        return Optional.ofNullable(field[row][col]);
    }

    public boolean canPutCard(GridPosition coordinate) {
        int row = CENTER_OFFSET + coordinate.getY();
        int col = CENTER_OFFSET + coordinate.getX();

        if (field[row][col] != null) {
            return false;
        }

        boolean horizontalValid = isCoordinateWithinBounds(
                coordinate.getX(),
                topLeft.getX(),
                bottomRight.getX()
        );

        boolean verticalValid = isCoordinateWithinBounds(
                coordinate.getY(),
                topLeft.getY(),
                bottomRight.getY()
        );

        return horizontalValid && verticalValid;
    }

    private boolean isCoordinateWithinBounds(int coordinate, int minBound, int maxBound) {
        int currentSpan = Math.abs(minBound - maxBound) + 1;

        if (currentSpan < MAX_GRID_DIMENSION) {
            return true;
        }

        return coordinate >= minBound && coordinate <= maxBound;
    }

    public Set<Card> putCard(GridPosition coordinate, Card card) {
        if (!canPutCard(coordinate)) {
            throw new IllegalArgumentException("Cannot put card");
        }

        cardQuantity++;
        int row = CENTER_OFFSET + coordinate.getY();
        int col = CENTER_OFFSET + coordinate.getX();
        field[row][col] = card;

        updateBoundaries(coordinate);

        return collectActivatedCards(coordinate);
    }

    private void updateBoundaries(GridPosition coordinate) {
        if (coordinate.getX() < topLeft.getX()) {
            topLeft = new GridPosition(coordinate.getX(), topLeft.getY());
        }
        if (coordinate.getX() > bottomRight.getX()) {
            bottomRight = new GridPosition(coordinate.getX(), bottomRight.getY());
        }
        if (coordinate.getY() < topLeft.getY()) {
            topLeft = new GridPosition(topLeft.getX(), coordinate.getY());
        }
        if (coordinate.getY() > bottomRight.getY()) {
            bottomRight = new GridPosition(bottomRight.getX(), coordinate.getY());
        }
    }

    private Set<Card> collectActivatedCards(GridPosition coordinate) {
        Set<Card> act = new HashSet<>();
        int targetRow = coordinate.getY() + CENTER_OFFSET;
        int targetCol = coordinate.getX() + CENTER_OFFSET;

        for (int i = 0; i < field.length; ++i) {
            for (int j = 0; j < field[i].length; ++j) {
                if (field[i][j] != null && (i == targetRow || j == targetCol)) {
                    act.add(field[i][j]);
                }
            }
        }

        return act;
    }

    public boolean canBeActivated(GridPosition coordinate) {
        int row = CENTER_OFFSET + coordinate.getY();
        int col = CENTER_OFFSET + coordinate.getX();
        return !field[row][col].isOverPolluted();
    }

    public Set<Card> getActivatedCards() {
        Set<Card> ans = new HashSet<>();

        for (GridPosition tpos : pattern) {
            GridPosition pos = new GridPosition(
                    tpos.getX() + topLeft.getX() + 1,
                    tpos.getY() + topLeft.getY() + 1
            );

            Optional<Card> c = getCard(pos);
            if (c.isPresent()) {
                ans.add(c.get());
            }
        }

        return ans;
    }

    /**
     * @return whether grid contiains 9 cards or not
     */
    @Override
    public boolean isFull() {
        return cardQuantity == 9;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int j = topLeft.getY(); j < bottomRight.getY(); ++j) {
            for (int i = topLeft.getX(); i < bottomRight.getX(); ++i) {
                Optional<Card> card = getCard(new GridPosition(i, j));

                if (card.isPresent()) {
                    sb.append("X: ")
                            .append(i)
                            .append(", Y: ")
                            .append(j)
                            .append(": ")
                            .append(card.get().toString());
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