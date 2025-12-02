package sk.uniba.fmph.dcs.terra_futura;

import org.apache.commons.lang3.tuple.Pair;
import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Deck;
import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;
import sk.uniba.fmph.dcs.terra_futura.tiles.Card;
import sk.uniba.fmph.dcs.terra_futura.tiles.CardSource;
import sk.uniba.fmph.dcs.terra_futura.tiles.GridPosition;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface TerraFuturaInterface {
    void takeCard(int playerId, CardSource source, GridPosition destination);
    boolean discardLastCardFromDeck(int playerId, Deck deck);
    void selectReward(int playerId, Resource resource);
    boolean turnFinished(int playerId);
    boolean selectActivationPattern(int playerId, int card);
    boolean selectScoring(int playerId, int card);
}
