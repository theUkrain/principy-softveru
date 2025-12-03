package sk.uniba.fmph.dcs.terra_futura;

import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Deck;
import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;
import sk.uniba.fmph.dcs.terra_futura.tiles.CardSource;
import sk.uniba.fmph.dcs.terra_futura.tiles.GridPosition;

public interface TerraFuturaInterface {
    void takeCard(int playerId, CardSource source, GridPosition destination);

    boolean discardLastCardFromDeck(int playerId, Deck deck);

    void selectReward(int playerId, Resource resource);

    boolean turnFinished(int playerId);

    boolean selectActivationPattern(int playerId, int card);

    boolean selectScoring(int playerId, int card);
}
