package sk.uniba.fmph.dcs.terra_futura.tiles;

import java.util.Optional;

public interface PileInterface {

    Optional<Card> getCard(int index);

    void discardCard();

}
