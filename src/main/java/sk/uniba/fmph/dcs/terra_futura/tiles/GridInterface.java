package sk.uniba.fmph.dcs.terra_futura.tiles;

import java.util.Optional;
import java.util.Set;

public interface GridInterface {
    Optional<Card> getCard(GridPosition coordinate);
    boolean canPutCard(GridPosition coordinate);
    Set<Card> putCard(GridPosition coordinate, Card card);
    Set<Card> getActivatedCards();
}
