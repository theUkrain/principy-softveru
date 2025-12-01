package sk.uniba.fmph.dcs.terra_futura.Samostatne;

import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;
import sk.uniba.fmph.dcs.terra_futura.tiles.Card;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Manages reward selection after assistance action
 * Tracks which player can select rewards and what resources are available
 */
public class SelectReward {
    private Optional<Integer> player;
    private final List<Resource> availableRewards;
    private final Map<Resource, Integer> selectedRewards;
    private Card targetCard;

    /**
     * Constructor initializes empty reward selection
     */
    public SelectReward() {
        this.player = Optional.empty();
        this.availableRewards = new ArrayList<>();
        this.selectedRewards = new HashMap<>();
        this.targetCard = null;
    }

    /**
     * Set up reward selection for a player
     * Called after successful assistance action
     *
     * @param playerId ID of player who can select rewards
     * @param card Card where rewards will be placed
     * @param rewards Available resources to choose from (with quantities)
     */
    public void setReward(int playerId, Card card, Map<Resource, Integer> rewards) {
        if (rewards == null || rewards.isEmpty()) {
            throw new IllegalArgumentException("Rewards map cannot be null or empty");
        }
        if (card == null) {
            throw new IllegalArgumentException("Card cannot be null");
        }

        this.player = Optional.of(playerId);
        this.targetCard = card;
        this.availableRewards.clear();
        this.selectedRewards.clear();

        // Convert map to list of resources (with repetitions for quantities)
        for (Map.Entry<Resource, Integer> entry : rewards.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                this.availableRewards.add(entry.getKey());
            }
        }
    }

    /**
     * Check if specified resource can be selected as reward
     * Resource must be in available rewards and not yet fully selected.
     *
     * @param resource Resource to check
     * @return true if resource can be selected, false otherwise
     */
    public boolean canSelectReward(Resource resource) {
        if (player.isEmpty()) {
            return false;
        }

        // Count how many of this resource are available
        long availableCount = availableRewards.stream()
                .filter(r -> r == resource)
                .count();

        // Count how many already selected
        int selectedCount = selectedRewards.getOrDefault(resource, 0);

        return selectedCount < availableCount;
    }

    /**
     * Select a resource as reward
     * Adds resource to target card and marks as selected
     *
     * @param resource Resource to select
     * @return true if selection successful, false otherwise
     */
    public boolean selectReward(Resource resource) {
        if (!canSelectReward(resource)) {
            return false;
        }

        if (targetCard == null) {
            return false;
        }

        // Create map with single resource
        Map<Resource, Integer> resourceToAdd = new HashMap<>();
        resourceToAdd.put(resource, 1);

        if (!targetCard.canPutResources(resourceToAdd)) {
            return false;
        }

        targetCard.putResources(resourceToAdd);
        selectedRewards.put(resource, selectedRewards.getOrDefault(resource, 0) + 1);

        // If all rewards selected, clear the reward selection
        int totalSelected = selectedRewards.values().stream()
                .mapToInt(Integer::intValue)
                .sum();

        if (totalSelected >= availableRewards.size()) {
            clear();
        }

        return true;
    }

    /**
     * Get current state of reward selection
     *
     * @return String representation of state
     */
    public String state() {
        StringBuilder sb = new StringBuilder();
        sb.append("SelectReward[");

        if (player.isPresent()) {
            sb.append("player: ").append(player.get());
            sb.append(", available: [");

            // Group available by resource type
            Map<Resource, Long> availableCounts = new HashMap<>();
            for (Resource r : availableRewards) {
                availableCounts.put(r, availableCounts.getOrDefault(r, 0L) + 1);
            }

            boolean first = true;
            for (Map.Entry<Resource, Long> entry : availableCounts.entrySet()) {
                if (!first) sb.append(", ");
                sb.append(entry.getKey()).append(":").append(entry.getValue());
                first = false;
            }

            sb.append("], selected: [");
            first = true;
            for (Map.Entry<Resource, Integer> entry : selectedRewards.entrySet()) {
                if (!first) sb.append(", ");
                sb.append(entry.getKey()).append(":").append(entry.getValue());
                first = false;
            }
            sb.append("]");
        } else {
            sb.append("no active selection");
        }

        sb.append("]");
        return sb.toString();
    }

    /**
     * Check if specified player is the one who can select rewards
     *
     * @param playerId Player ID to check
     * @return true if this player can select, false otherwise
     */
    public boolean isPlayerTurn(int playerId) {
        return player.isPresent() && player.get() == playerId;
    }

    /**
     * Check if there are still rewards to select
     *
     * @return true if rewards remain, false otherwise
     */
    public boolean hasRemainingRewards() {
        if (player.isEmpty()) {
            return false;
        }

        int totalSelected = selectedRewards.values().stream()
                .mapToInt(Integer::intValue)
                .sum();

        return totalSelected < availableRewards.size();
    }

    /**
     * Clear reward selection state
     */
    private void clear() {
        this.player = Optional.empty();
        this.availableRewards.clear();
        this.selectedRewards.clear();
        this.targetCard = null;
    }

    /**
     * Get the player who can select rewards
     *
     * @return Optional player ID
     */
    public Optional<Integer> getPlayer() {
        return player;
    }

    /**
     * Get map of available rewards with quantities
     *
     * @return Map of resources to quantities
     */
    public Map<Resource, Integer> getAvailableRewards() {
        Map<Resource, Integer> result = new HashMap<>();
        for (Resource r : availableRewards) {
            result.put(r, result.getOrDefault(r, 0) + 1);
        }
        return result;
    }

    /**
     * Get map of already selected rewards with quantities
     *
     * @return Map of resources to quantities
     */
    public Map<Resource, Integer> getSelectedRewards() {
        return new HashMap<>(selectedRewards);
    }
}