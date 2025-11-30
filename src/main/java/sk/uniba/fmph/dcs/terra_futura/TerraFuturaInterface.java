package sk.uniba.fmph.dcs.terra_futura;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Optional;

public interface TerraFuturaInterface {

    /**
     * Player takes a card from specified source and places it on the grid.
     *
     * @param playerId ID of the player performing the action
     * @param source Source of the card (deck and index)
     * @param destination Position on the grid where card will be placed
     * @return true if action was successful, false otherwise
     */
    boolean takeCard(int playerId, CardSource source, GridPosition destination);

    /**
     * Discard the last (oldest) card from the specified deck.
     *
     * @param playerId ID of the player performing the action
     * @param deck Which deck to discard from (I or II)
     * @return true if action was successful, false otherwise
     */
    boolean discardLastCardFromDeck(int playerId, Deck deck);

    /**
     * Activate a card on the grid with specified resource flows.
     *
     * @param playerId ID of the player performing the action
     * @param card Grid coordinate of the card to activate
     * @param inputs List of input resources and their source positions
     * @param outputs List of output resources and their destination positions
     * @param pollution List of positions where pollution will be placed
     * @param otherPlayerId Optional ID of assisting player
     * @param otherCard Optional position of assisting card
     * @return true if action was successful, false otherwise
     */
    boolean activateCard(
            int playerId,
            GridPosition card,
            List<Pair<Resource, GridPosition>> inputs,
            List<Pair<Resource, GridPosition>> outputs,
            List<GridPosition> pollution,
            Optional<Integer> otherPlayerId,
            Optional<GridPosition> otherCard
    );

    /**
     * Select a reward resource after assistance action.
     *
     * @param playerId ID of the player selecting the reward
     * @param resource Resource type to select as reward
     * @return true if action was successful, false otherwise
     */
    boolean selectReward(int playerId, Resource resource);

    /**
     * Mark player's turn as finished.
     *
     * @param playerId ID of the player finishing their turn
     * @return true if action was successful, false otherwise
     */
    boolean turnFinished(int playerId);

    /**
     * Select activation pattern for end-game scoring.
     *
     * @param playerId ID of the player selecting pattern
     * @param patternIndex Index of the activation pattern (0 or 1)
     * @return true if action was successful, false otherwise
     */
    boolean selectActivationPattern(int playerId, int patternIndex);

    /**
     * Select scoring method for end-game points calculation.
     *
     * @param playerId ID of the player selecting scoring method
     * @param scoringIndex Index of the scoring method
     * @return true if action was successful, false otherwise
     */
    boolean selectScoring(int playerId, int scoringIndex);
}