package sk.uniba.fmph.dcs.terra_futura;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Main component implementation of Terra Futura game.
 * Coordinates Game, GameObserver and all game logic.
 */
public class TerraFuturaComponent implements TerraFuturaInterface {

    private final Game game;
    private final GameObserver gameObserver;

    /**
     * Constructor initializes the game component.
     *
     * @param players Array of player IDs
     * @param startingPlayer ID of starting player
     * @param playerGrids Map of player IDs to their grids
     * @param piles Map of decks to their piles
     */
    public TerraFuturaComponent(int[] players, int startingPlayer,
                                Map<Integer, Grid> playerGrids,
                                Map<Deck, Pile> piles) {
        this.game = new Game(players, startingPlayer, playerGrids, piles);
        this.gameObserver = new GameObserver();
    }

    /**
     * Register an observer for a player.
     *
     * @param playerId ID of the player
     * @param observer Observer to receive notifications
     */
    public void registerObserver(int playerId, TerraFuturaObserverInterface observer) {
        gameObserver.registerObserver(playerId, observer);
    }

    @Override
    public boolean takeCard(int playerId, CardSource source, GridPosition destination) {
        boolean success = game.takeCard(playerId, source, destination);
        if (success) {
            notifyObservers();
        }
        return success;
    }

    @Override
    public boolean discardLastCardFromDeck(int playerId, Deck deck) {
        boolean success = game.discardLastCardFromDeck(playerId, deck);
        if (success) {
            notifyObservers();
        }
        return success;
    }

    @Override
    public boolean activateCard(int playerId, GridPosition card,
                                List<Pair<Resource, GridPosition>> inputs,
                                List<Pair<Resource, GridPosition>> outputs,
                                List<GridPosition> pollution,
                                Optional<Integer> otherPlayerId,
                                Optional<GridPosition> otherCard) {
        // Delegate to game logic
        boolean success = game.activateCard(playerId, card, inputs, outputs,
                pollution, otherPlayerId, otherCard);

        // Notify all observers if action was successful
        if (success) {
            notifyObservers();
        }

        return success;
    }

    @Override
    public boolean selectReward(int playerId, Resource resource) {
        boolean success = game.selectReward(playerId, resource);
        if (success) {
            notifyObservers();
        }
        return success;
    }

    @Override
    public boolean turnFinished(int playerId) {
        boolean success = game.turnFinished(playerId);
        if (success) {
            notifyObservers();
        }
        return success;
    }

    @Override
    public boolean selectActivationPattern(int playerId, int patternIndex) {
        boolean success = game.selectActivationPattern(playerId, patternIndex);
        if (success) {
            notifyObservers();
        }
        return success;
    }

    @Override
    public boolean selectScoring(int playerId, int scoringIndex) {
        boolean success = game.selectScoring(playerId, scoringIndex);
        if (success) {
            notifyObservers();
        }
        return success;
    }

    /**
     * Get current game state.
     */
    public GameState getGameState() {
        return game.getState();
    }

    /**
     * Get player currently on turn.
     */
    public int getPlayerOnTurn() {
        return game.getOnTurn();
    }

    /**
     * Get current turn number.
     */
    public int getTurnNumber() {
        return game.getTurnNumber();
    }

    /**
     * Notify all observers of current game state.
     */
    private void notifyObservers() {
        Map<Integer, String> states = game.getStateForPlayers();
        gameObserver.notifyAll(states);
    }
}