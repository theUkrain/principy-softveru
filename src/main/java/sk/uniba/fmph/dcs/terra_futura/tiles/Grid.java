
package sk.uniba.fmph.dcs.terra_futura.tiles;

import sk.uniba.fmph.dcs.terra_futura.InterfaceActivateGrid;

import java.util.*;

public class Grid implements InterfaceActivateGrid, GridInterface {
    private int ltX;
    private int ltY;

    private int rbX;
    private int rbY;

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

    public boolean canPutCard(GridPosition coordinate) {
        return getCard(coordinate).isEmpty() && (
                (rbX - ltX + 1 < 3  && rbY - ltY + 1 < 3) || // case when out of boundaries
                        (coordinate.getX() + 2 >= ltX && coordinate.getX() + 2 <= rbX && // case when in boundaries
                                coordinate.getY() + 2 >= ltY && coordinate.getY() + 2 <= rbY));
    }

    private Set<Card> proccessActivation(int x, int y) {
        Set<Card> cards = new HashSet<>();

        for (int i = -2; i <= 2; i++) {
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

        field.get(coordinate.getY() + 2).set(coordinate.getX() + 2, card);

        if (coordinate.getX() + 2 > rbX) {
            rbX = coordinate.getX() + 2;
        }

        else if (coordinate.getX() + 2 < ltX) {
            ltX = coordinate.getX() + 2;
        }

        if (coordinate.getY() + 2 > rbY) {
            rbY = coordinate.getY() + 2;
        }

        else if (coordinate.getY() + 2 < ltY) {
            ltY = coordinate.getY() + 2;
        }

        return proccessActivation(coordinate.getX(), coordinate.getY());
    }

    public Set<Card> getActivatedCards() {
        Set<Card> cards = new HashSet<>();

        for (GridPosition pos : pattern) {
            Card card = field.get(pos.getY() + ltY + 1).get(pos.getX() + ltX + 1);

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
