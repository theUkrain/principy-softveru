package sk.uniba.fmph.dcs.terra_futura.tiles;

import sk.uniba.fmph.dcs.terra_futura.ActivationPattern;
import sk.uniba.fmph.dcs.terra_futura.InterfaceActivateGrid;

import java.util.*;

public class Grid implements InterfaceActivateGrid, GridInterface {
    private int lbX;
    private int lbY;

    private int rtX;
    private int rtY;

    private List<List<Card>> field;

    private Collection<GridPosition> pattern;

    public Grid() {
        field = new ArrayList<>(5);

        for (int i = 0; i < 5; i++) {
            field.set(i, new ArrayList<>(5));
        }

        // start Card
    }

    public Optional<Card> getCard(GridPosition coordinate) {
        return Optional.ofNullable(field.get(coordinate.getY()).get(coordinate.getX()));
    }

    public boolean canPutCard(GridPosition coordinate) {
        return getCard(coordinate).isEmpty() && (
                        (rtX - lbX + 1 < 3  && rtY - lbY + 1 < 3) || // case when out of boundaries
                        (coordinate.getX() > lbX && coordinate.getX() < rtX && // case when in boundaries
                         coordinate.getY() > lbY && coordinate.getY() < rtY));
    }

    private Set<Card> proccessActivation(GridPosition coordinate) {
        if (!canPutCard(coordinate)) {
            throw new IllegalStateException("Can't put card");
        }

        Set<Card> cards = new HashSet<>();

        for (int i = 0; i < 5; i++) {
            Optional<Card> activeCardX = getCard(new GridPosition(i, coordinate.getY()));
            Optional<Card> activeCardY = getCard(new GridPosition(coordinate.getX(), i));

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
            throw new IllegalStateException("Can't put card");
        }

        field.get(coordinate.getY()).set(coordinate.getX(), card);

        if (coordinate.getX() > rtX) {
            rtX = coordinate.getX();
        }

        else if (coordinate.getX() < lbX) {
            lbX = coordinate.getX();
        }

        if (coordinate.getY() > rtY) {
            rtY = coordinate.getY();
        }

        else if (coordinate.getY() < lbY) {
            lbY = coordinate.getY();
        }

        return proccessActivation(coordinate);
    }

    public Set<Card> getActivatedCards() {
        Set<Card> cards = new HashSet<>();

        for (GridPosition pos : pattern) {
            Set<Card> currentActivation = proccessActivation(pos);
            cards.addAll(currentActivation);
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

        for (int j = lbY; j < rtY; j++) {
            for (int i = lbX; i < rtX; i++) {
                Optional<Card> card = getCard(new GridPosition(j, i));

                if (card.isPresent()) {
                    sb.append("X: " + i + ", Y:" + j + ": \n" + card.get().toString());
                }
            }
        }

        return sb.toString();
    }
}
