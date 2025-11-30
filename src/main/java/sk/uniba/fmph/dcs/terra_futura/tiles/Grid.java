package sk.uniba.fmph.dcs.terra_futura.tiles;

import sk.uniba.fmph.dcs.terra_futura.ActivationPattern;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class Grid {
    private int lbX;
    private int lbY;

    private int rtX;
    private int rtY;

    private List<List<Card>> field;

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

    public Set<GridPosition> putCard(GridPosition coordinate, Card card) {
        return null;
    }

    public Set<GridPosition> getActivatedCards(ActivationPattern pattern) {
        return null;
    }
}
