package sk.uniba.fmph.dcs.terra_futura;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Manages the grid of cards for a player.
 * Grid is 5x5 with coordinates from -2 to +2, center (0,0) is the starting card.
 */
public class Grid {
    private static final int GRID_SIZE = 5;
    private static final int OFFSET = 2; // To convert from -2..2 to 0..4

    private final Map<GridPosition, Card> cards;
    private final Map<GridPosition, Boolean> activatedThisTurn;
    private List<GridPosition> activationPattern;

    /**
     * Constructor initializes empty grid with starting card at (0,0).
     *
     * @param startingCard The card placed at position (0,0)
     */
    public Grid(Card startingCard) {
        this.cards = new HashMap<>();
        this.activatedThisTurn = new HashMap<>();
        this.activationPattern = new ArrayList<>();

        GridPosition center = new GridPosition(0, 0);
        cards.put(center, startingCard);
        activatedThisTurn.put(center, false);
    }

    /**
     * Get card at specified grid coordinate.
     *
     * @param coordinate Grid position
     * @return Card at position or empty if no card there
     */
    public Optional<Card> getCard(GridPosition coordinate) {
        if (!isValidPosition(coordinate)) {
            return Optional.empty();
        }
        return Optional.ofNullable(cards.get(coordinate));
    }

    /**
     * Check if a card can be placed at specified position.
     * Card can be placed if position is empty and adjacent to existing card.
     *
     * @param coordinate Grid position
     * @return true if card can be placed, false otherwise
     */
    public boolean canPutCard(GridPosition coordinate) {
        if (!isValidPosition(coordinate)) {
            return false;
        }

        if (cards.containsKey(coordinate)) {
            return false;
        }

        return hasAdjacentCard(coordinate);
    }

    /**
     * Place a card at specified position.
     *
     * @param coordinate Grid position
     * @param card Card to place
     * @throws IllegalStateException if card cannot be placed at position
     */
    public void putCard(GridPosition coordinate, Card card) {
        if (!canPutCard(coordinate)) {
            throw new IllegalStateException("Cannot place card at position " + coordinate);
        }

        cards.put(coordinate, card);
        activatedThisTurn.put(coordinate, false);
    }

    /**
     * Check if card at position can be activated.
     * Card can be activated if it exists and hasn't been activated this turn.
     *
     * @param coordinate Grid position
     * @return true if card can be activated, false otherwise
     */
    public boolean canBeActivated(GridPosition coordinate) {
        if (!cards.containsKey(coordinate)) {
            return false;
        }

        Boolean activated = activatedThisTurn.get(coordinate);
        return activated != null && !activated;
    }

    /**
     * Mark card at position as activated.
     *
     * @param coordinate Grid position
     * @throws IllegalStateException if card cannot be activated
     */
    public void setActivated(GridPosition coordinate) {
        if (!canBeActivated(coordinate)) {
            throw new IllegalStateException("Card at position " + coordinate + " cannot be activated");
        }

        activatedThisTurn.put(coordinate, true);
    }

    /**
     * Set the activation pattern for end-game scoring.
     *
     * @param pattern List of grid positions forming the pattern
     */
    public void setActivationPattern(List<GridPosition> pattern) {
        this.activationPattern = new ArrayList<>(pattern);
    }

    /**
     * Reset activation status for all cards (called at end of turn).
     */
    public void endTurn() {
        for (GridPosition pos : activatedThisTurn.keySet()) {
            activatedThisTurn.put(pos, false);
        }
    }

    /**
     * Get current state of the grid as string.
     *
     * @return String representation of grid state
     */
    public String state() {
        StringBuilder sb = new StringBuilder();
        sb.append("Grid[cards: {");

        boolean first = true;
        for (Map.Entry<GridPosition, Card> entry : cards.entrySet()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(entry.getKey()).append(": ").append(entry.getValue().state());
            first = false;
        }

        sb.append("}, activated: {");
        first = true;
        for (Map.Entry<GridPosition, Boolean> entry : activatedThisTurn.entrySet()) {
            if (entry.getValue()) {
                if (!first) {
                    sb.append(", ");
                }
                sb.append(entry.getKey());
                first = false;
            }
        }

        sb.append("}]");
        return sb.toString();
    }

    /**
     * Check if position is within valid grid bounds (-2 to 2 for both x and y).
     *
     * @param coordinate Grid position
     * @return true if position is valid, false otherwise
     */
    private boolean isValidPosition(GridPosition coordinate) {
        return coordinate.x() >= -2 && coordinate.x() <= 2
                && coordinate.y() >= -2 && coordinate.y() <= 2;
    }

    /**
     * Check if position has at least one adjacent card.
     * Adjacent means directly left, right, up, or down (not diagonal).
     *
     * @param coordinate Grid position
     * @return true if has adjacent card, false otherwise
     */
    private boolean hasAdjacentCard(GridPosition coordinate) {
        GridPosition[] adjacentPositions = {
                new GridPosition(coordinate.x() - 1, coordinate.y()),
                new GridPosition(coordinate.x() + 1, coordinate.y()),
                new GridPosition(coordinate.x(), coordinate.y() - 1),
                new GridPosition(coordinate.x(), coordinate.y() + 1)
        };

        for (GridPosition pos : adjacentPositions) {
            if (cards.containsKey(pos)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get all cards currently on the grid.
     *
     * @return Map of positions to cards
     */
    public Map<GridPosition, Card> getAllCards() {
        return new HashMap<>(cards);
    }

    /**
     * Get the activation pattern.
     *
     * @return List of positions in the activation pattern
     */
    public List<GridPosition> getActivationPattern() {
        return new ArrayList<>(activationPattern);
    }
}