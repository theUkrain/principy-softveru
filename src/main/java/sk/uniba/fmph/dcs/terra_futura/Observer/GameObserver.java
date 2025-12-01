package sk.uniba.fmph.dcs.terra_futura.Observer;

import java.util.HashMap;
import java.util.Map;

/**
 * GameObserver manages notification distribution to registered observers
 * Implements Observer Pattern for game state updates
 * Each player can have their own observer that receives personalized
 * game state updates when the game state changes
 */
public class GameObserver {
    private final Map<Integer, TerraFuturaObserverInterface> observers;

    /**
     * Constructor initializes empty observers map
     */
    public GameObserver() {
        this.observers = new HashMap<>();
    }

    /**
     * Register an observer for a specific player
     * If player already has an observer, it will be replaced
     *
     * @param playerId ID of the player
     * @param observer Observer interface to receive notifications
     * @throws IllegalArgumentException if observer is null
     */
    public void registerObserver(int playerId, TerraFuturaObserverInterface observer) {
        if (observer == null) {
            throw new IllegalArgumentException("Observer cannot be null");
        }
        observers.put(playerId, observer);
    }

    /**
     * Unregister an observer for a specific player
     * Does nothing if player has no registered observer
     *
     * @param playerId ID of the player
     */
    public void unregisterObserver(int playerId) {
        observers.remove(playerId);
    }

    /**
     * Notify all registered observers with their respective game states
     * Each observer receives only the state relevant to their player
     * If an observer throws an exception, it won't affect other notifications.
     *
     * @param newState Map of player IDs to their game state strings
     * @throws IllegalArgumentException if newState is null
     */
    public void notifyAll(Map<Integer, String> newState) {
        if (newState == null) {
            throw new IllegalArgumentException("State map cannot be null");
        }

        for (Map.Entry<Integer, String> entry : newState.entrySet()) {
            int playerId = entry.getKey();
            String state = entry.getValue();

            TerraFuturaObserverInterface observer = observers.get(playerId);
            if (observer != null) {
                try {
                    observer.notify(state);
                } catch (Exception e) {
                    // Log error but continue notifying other observers
                    System.err.println("Error notifying player " + playerId + ": " + e.getMessage());
                }
            }
        }
    }

    /**
     * Notify a specific observer
     * Does nothing if player has no registered observer
     *
     * @param playerId ID of the player to notify
     * @param state Game state string
     * @throws IllegalArgumentException if state is null
     */
    public void notifyPlayer(int playerId, String state) {
        if (state == null) {
            throw new IllegalArgumentException("State cannot be null");
        }

        TerraFuturaObserverInterface observer = observers.get(playerId);
        if (observer != null) {
            try {
                observer.notify(state);
            } catch (Exception e) {
                System.err.println("Error notifying player " + playerId + ": " + e.getMessage());
            }
        }
    }

    /**
     * Check if observer is registered for a player
     *
     * @param playerId ID of the player
     * @return true if observer is registered, false otherwise
     */
    public boolean hasObserver(int playerId) {
        return observers.containsKey(playerId);
    }

    /**
     * Get number of registered observers
     *
     * @return Number of observers
     */
    public int getObserverCount() {
        return observers.size();
    }

    /**
     * Clear all registered observers
     * Useful for cleanup or resetting the game
     */
    public void clearAllObservers() {
        observers.clear();
    }
}