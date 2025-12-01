package sk.uniba.fmph.dcs.terra_futura.Samostatne;

import sk.uniba.fmph.dcs.terra_futura.InterfaceActivateGrid;
import sk.uniba.fmph.dcs.terra_futura.tiles.Card;
import sk.uniba.fmph.dcs.terra_futura.tiles.CardFactory;
import sk.uniba.fmph.dcs.terra_futura.tiles.GridInterface;
import sk.uniba.fmph.dcs.terra_futura.tiles.GridPosition;

import java.util.*;

public class Grid implements InterfaceActivateGrid, GridInterface {
    private final int OFFSET = 2;

    private int leftTopX;
    private int leftTopY;

    private int rightBottomX;
    private int rightBottomY;

    private List<List<Card>> grid;

    private Collection<GridPosition> pattern;

    public Grid() {
        grid = new ArrayList<>(5);

        for (int i = 0; i < 5; i++) {
            grid.add(new ArrayList<>());

            for (int j = 0; j < 5; j++) {
                grid.get(i).add(null);
            }
        }

        Card card = CardFactory.startCard();
        grid.get(OFFSET).set(OFFSET, card);

        leftTopX = OFFSET;
        leftTopY = OFFSET;
        rightBottomX = OFFSET;
        rightBottomY = OFFSET;
    }

    public Optional<Card> getCard(GridPosition coordinate) {
        return Optional.ofNullable(grid.get(coordinate.getY() + OFFSET).get(coordinate.getX() + OFFSET));
    }

    public boolean canPutCard(GridPosition coordinate) {
        return getCard(coordinate).isEmpty() && (
                (rightBottomX - leftTopX + 1 < 3  && rightBottomY - leftTopY + 1 < 3) || // case when out of boundaries
                        (coordinate.getX() + OFFSET >= leftTopX && coordinate.getX() + OFFSET <= rightBottomX && // case when in boundaries
                                coordinate.getY() + OFFSET >= leftTopY && coordinate.getY() + OFFSET <= rightBottomY));
    }

    private Set<Card> proccessActivation(int x, int y) {
        Set<Card> cards = new HashSet<>();

        for (int i = -OFFSET; i <= OFFSET; i++) {
            Optional<Card> activeCardX = getCard(new GridPosition(i, y));
            Optional<Card> activeCardY = getCard(new GridPosition(x, i));

            if (activeCardX.isPresent()) {
                cards.add(activeCardX.get());
            }

            if (activeCardY.isPresent()) {
                cards.add(activeCardY.get());
            }
        }

        return cards;
    }

    public Set<Card> putCard(GridPosition coordinate, Card card) {
        if (!canPutCard(coordinate)) {
            throw new IllegalArgumentException("Can't put card");
        }

        grid.get(coordinate.getY() + OFFSET).set(coordinate.getX() + OFFSET, card);

        if (coordinate.getX() + OFFSET > rightBottomX) {
            rightBottomX = coordinate.getX() + OFFSET;
        }

        else if (coordinate.getX() + OFFSET < leftTopX) {
            leftTopX = coordinate.getX() + OFFSET;
        }

        if (coordinate.getY() + OFFSET > rightBottomY) {
            rightBottomY = coordinate.getY() + OFFSET;
        }

        else if (coordinate.getY() + OFFSET < leftTopY) {
            leftTopY = coordinate.getY() + OFFSET;
        }

        return proccessActivation(coordinate.getX(), coordinate.getY());
    }

    public Set<Card> getActivatedCards() {
        Set<Card> cards = new HashSet<>();

        for (GridPosition pos : pattern) {
            Card card = grid.get(pos.getY() + leftTopY + 1).get(pos.getX() + leftTopX + 1);

            if (card != null) {
                cards.add(card);
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

        for (int j = -OFFSET; j < OFFSET; j++) {
            for (int i = -OFFSET; i < OFFSET; i++) {
                Optional<Card> card = getCard(new GridPosition(j, i));

                if (card.isPresent()) {
                    sb.append("X: ").append(i).append(", Y:").append(j).append(": \n").append(card.get());
                }
            }
        }

        return sb.toString();
    }
}
