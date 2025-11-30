package sk.uniba.fmph.dcs.terra_futura;

import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

/**
 * Main game logic class that manages game state and player turns.
 * Implements state transitions according to game rules.
 */
public class Game {
    private static final int MAX_TURNS = 9;

    private GameState state;
    private final int[] players;
    private int onTurn;
    private final int startingPlayer;
    private int turnNumber;

    private final Map<Integer, Grid> playerGrids;
    private final Map<Deck, Pile> piles;
    private final SelectReward selectReward;
    private final MoveCard moveCard;
    private final ProcessAction processAction;
    private final ProcessActionAssistance processActionAssistance;

    /**
     * Constructor initializes the game.
     *
     * @param players Array of player IDs
     * @param startingPlayer ID of the starting player
     * @param playerGrids Map of player IDs to their grids
     * @param piles Map of decks to their piles
     */
    public Game(int[] players, int startingPlayer,
                Map<Integer, Grid> playerGrids, Map<Deck, Pile> piles) {
        if (players.length < 2 || players.length > 4) {
            throw new IllegalArgumentException("Game requires 2-4 players");
        }

        this.players = Arrays.copyOf(players, players.length);
        this.startingPlayer = startingPlayer;
        this.onTurn = startingPlayer;
        this.turnNumber = 1;
        this.state = GameState.TakeCardNoCardDiscarded;

        this.playerGrids = new HashMap<>(playerGrids);
        this.piles = new HashMap<>(piles);
        this.selectReward = new SelectReward();
        this.moveCard = new MoveCard();
        this.processAction = new ProcessAction();
        this.processActionAssistance = new ProcessActionAssistance();
    }

    /**
     * Handle takeCard action.
     */
    public boolean takeCard(int playerId, CardSource source, GridPosition destination) {
        if (!isPlayerOnTurn(playerId)) {
            return false;
        }

        if (state != GameState.TakeCardNoCardDiscarded &&
                state != GameState.TakeCardCardDiscarded) {
            return false;
        }

        Pile pile = piles.get(source.deck());
        Grid grid = playerGrids.get(playerId);

        if (pile == null || grid == null) {
            return false;
        }

        boolean success = moveCard.moveCard(pile, destination, grid);
        if (success) {
            transitionAfterTakeCard();
        }

        return success;
    }

    /**
     * Handle discardLastCardFromDeck action.
     */
    public boolean discardLastCardFromDeck(int playerId, Deck deck) {
        if (!isPlayerOnTurn(playerId)) {
            return false;
        }

        if (state != GameState.TakeCardNoCardDiscarded) {
            return false;
        }

        Pile pile = piles.get(deck);
        if (pile == null) {
            return false;
        }

        pile.removeLastCard();
        state = GameState.TakeCardCardDiscarded;
        return true;
    }

    /**
     * Handle activateCard action.
     */
    public boolean activateCard(int playerId, GridPosition card,
                                List<Pair<Resource, GridPosition>> inputs,
                                List<Pair<Resource, GridPosition>> outputs,
                                List<GridPosition> pollution,
                                Optional<Integer> otherPlayerId,
                                Optional<GridPosition> otherCard) {
        if (!isPlayerOnTurn(playerId)) {
            return false;
        }

        if (state != GameState.ActivateCard) {
            return false;
        }

        Grid grid = playerGrids.get(playerId);
        if (grid == null) {
            return false;
        }

        Optional<Card> cardToActivate = grid.getCard(card);
        if (cardToActivate.isEmpty()) {
            return false;
        }

        boolean success;

        if (otherPlayerId.isPresent() && otherCard.isPresent()) {
            // Assistance activation
            Grid otherGrid = playerGrids.get(otherPlayerId.get());
            if (otherGrid == null) {
                return false;
            }

            Optional<Card> assistingCard = otherGrid.getCard(otherCard.get());
            if (assistingCard.isEmpty()) {
                return false;
            }

            success = processActionAssistance.activateCard(
                    cardToActivate.get(), grid,
                    otherPlayerId.get(), assistingCard.get(),
                    inputs, outputs, pollution
            );

            if (success) {
                transitionAfterAssistanceActivation(otherPlayerId.get(), assistingCard.get());
            }
        } else {
            // Normal activation
            success = processAction.activateCard(
                    cardToActivate.get(), grid, inputs, outputs, pollution
            );

            if (success && turnNumber >= 1 && turnNumber <= MAX_TURNS) {
                // Stay in ActivateCard state - player can activate more cards
                // or they can call turnFinished
            }
        }

        return success;
    }

    /**
     * Handle selectReward action.
     */
    public boolean selectReward(int playerId, Resource resource) {
        if (state != GameState.SelectReward) {
            return false;
        }

        if (!selectReward.isPlayerTurn(playerId)) {
            return false;
        }

        boolean success = selectReward.selectReward(resource);

        if (success && !selectReward.hasRemainingRewards()) {
            state = GameState.ActivateCard;
        }

        return success;
    }

    /**
     * Handle turnFinished action.
     */
    public boolean turnFinished(int playerId) {
        if (!isPlayerOnTurn(playerId)) {
            return false;
        }

        if (state != GameState.ActivateCard) {
            return false;
        }

        Grid grid = playerGrids.get(playerId);
        if (grid != null) {
            grid.endTurn();
        }

        if (turnNumber < MAX_TURNS) {
            // Next turn
            advanceTurn();
            state = GameState.TakeCardNoCardDiscarded;
        } else if (turnNumber == MAX_TURNS) {
            // End of regular turns, move to pattern selection
            state = GameState.SelectActivationPattern;
        } else {
            // After all patterns selected, move to scoring
            state = GameState.SelectScoringMethod;
        }

        return true;
    }

    /**
     * Handle selectActivationPattern action.
     */
    public boolean selectActivationPattern(int playerId, int patternIndex) {
        if (state != GameState.SelectActivationPattern) {
            return false;
        }

        // Implementation depends on ActivationPattern class
        // For now, simplified logic
        advanceTurn();

        if (onTurn == startingPlayer) {
            // All players selected, move to activation
            state = GameState.ActivateCard;
            turnNumber = MAX_TURNS + 1; // Mark as post-game
        }

        return true;
    }

    /**
     * Handle selectScoring action.
     */
    public boolean selectScoring(int playerId, int scoringIndex) {
        if (state != GameState.SelectScoringMethod) {
            return false;
        }

        // Implementation depends on ScoringMethod class
        advanceTurn();

        if (onTurn == startingPlayer) {
            // All players selected scoring
            state = GameState.Finish;
        }

        return true;
    }

    /**
     * Get current game state.
     */
    public GameState getState() {
        return state;
    }

    /**
     * Get player currently on turn.
     */
    public int getOnTurn() {
        return onTurn;
    }

    /**
     * Get current turn number.
     */
    public int getTurnNumber() {
        return turnNumber;
    }

    /**
     * Get game state as string for observers.
     */
    public Map<Integer, String> getStateForPlayers() {
        Map<Integer, String> states = new HashMap<>();

        for (int playerId : players) {
            StringBuilder sb = new StringBuilder();
            sb.append("State: ").append(state);
            sb.append(", Turn: ").append(turnNumber);
            sb.append(", OnTurn: ").append(onTurn);
            sb.append(", YourGrid: ");

            Grid grid = playerGrids.get(playerId);
            if (grid != null) {
                sb.append(grid.state());
            }

            states.put(playerId, sb.toString());
        }

        return states;
    }

    /**
     * Check if specified player is on turn.
     */
    private boolean isPlayerOnTurn(int playerId) {
        return playerId == onTurn;
    }

    /**
     * Advance to next player's turn.
     */
    private void advanceTurn() {
        int currentIndex = -1;
        for (int i = 0; i < players.length; i++) {
            if (players[i] == onTurn) {
                currentIndex = i;
                break;
            }
        }

        if (currentIndex == -1) {
            throw new IllegalStateException("Current player not found in players array");
        }

        int nextIndex = (currentIndex + 1) % players.length;
        onTurn = players[nextIndex];

        if (onTurn == startingPlayer) {
            turnNumber++;
        }
    }

    /**
     * Transition after taking a card.
     */
    private void transitionAfterTakeCard() {
        state = GameState.ActivateCard;
    }

    /**
     * Transition after assistance activation.
     */
    private void transitionAfterAssistanceActivation(int assistingPlayer, Card assistingCard) {
        // Set up reward selection for assisting player
        List<Resource> rewards = new ArrayList<>();
        // Rewards logic would be determined by the card
        // For now, simplified
        if (assistingCard.hasAssistance()) {
            rewards.add(Resource.Green);
            rewards.add(Resource.Red);
        }

        if (!rewards.isEmpty()) {
            selectReward.setReward(assistingPlayer, assistingCard, rewards);
            state = GameState.SelectReward;
        }
    }
}