package sk.uniba.fmph.dcs.terra_futura;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Manages reward selection after assistance action.
 * Tracks which player can select rewards and what resources are available.
 */
public class SelectReward {
    private Optional<Integer> player;
    private final List<Resource> availableRewards;
    private final List<Resource> selectedRewards;
    private Card targetCard;

    /**
     * Constructor initializes empty reward selection.
     */
    public SelectReward() {
        this.player = Optional.empty();
        this.availableRewards = new ArrayList<>();
        this.selectedRewards = new ArrayList<>();
        this.targetCard = null;
    }

    /**
     * Set up reward selection for a player.
     * Called after successful assistance action.
     *
     * @param playerId ID of player who can select rewards
     * @param card Card where rewards will be placed
     * @param rewards Available resources to choose from
     */
    public void setReward(int playerId, Card card, List<Resource> rewards) {
        if (rewards == null || rewards.isEmpty()) {
            throw new IllegalArgumentException("Rewards list cannot be null or empty");
        }
        if (card == null) {
            throw new IllegalArgumentException("Card cannot be null");
        }

        this.player = Optional.of(playerId);
        this.targetCard = card;
        this.availableRewards.clear();
        this.availableRewards.addAll(rewards);
        this.selectedRewards.clear();
    }

    /**
     * Check if specified resource can be selected as reward.
     * Resource must be in available rewards and not yet selected.
     *
     * @param resource Resource to check
     * @return true if resource can be selected, false otherwise
     */
    public boolean canSelectReward(Resource resource) {
        if (player.isEmpty()) {
            return false;
        }

        if (selectedRewards.contains(resource)) {
            return false;
        }

        return availableRewards.contains(resource);
    }

    /**
     * Select a resource as reward.
     * Adds resource to target card and marks as selected.
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

        List<Resource> resourceToAdd = List.of(resource);
        if (!targetCard.canPutResources(resourceToAdd)) {
            return false;
        }

        targetCard.putResources(resourceToAdd);
        selectedRewards.add(resource);

        // If all rewards selected, clear the reward selection
        if (selectedRewards.size() == availableRewards.size()) {
            clear();
        }

        return true;
    }

    /**
     * Get current state of reward selection.
     *
     * @return String representation of state
     */
    public String state() {
        StringBuilder sb = new StringBuilder();
        sb.append("SelectReward[");

        if (player.isPresent()) {
            sb.append("player: ").append(player.get());
            sb.append(", available: [");
            for (int i = 0; i < availableRewards.size(); i++) {
                sb.append(availableRewards.get(i));
                if (i < availableRewards.size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append("], selected: [");
            for (int i = 0; i < selectedRewards.size(); i++) {
                sb.append(selectedRewards.get(i));
                if (i < selectedRewards.size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append("]");
        } else {
            sb.append("no active selection");
        }

        sb.append("]");
        return sb.toString();
    }

    /**
     * Check if specified player is the one who can select rewards.
     *
     * @param playerId Player ID to check
     * @return true if this player can select, false otherwise
     */
    public boolean isPlayerTurn(int playerId) {
        return player.isPresent() && player.get() == playerId;
    }

    /**
     * Check if there are still rewards to select.
     *
     * @return true if rewards remain, false otherwise
     */
    public boolean hasRemainingRewards() {
        return player.isPresent() && selectedRewards.size() < availableRewards.size();
    }

    /**
     * Clear reward selection state.
     */
    private void clear() {
        this.player = Optional.empty();
        this.availableRewards.clear();
        this.selectedRewards.clear();
        this.targetCard = null;
    }

    /**
     * Get the player who can select rewards.
     *
     * @return Optional player ID
     */
    public Optional<Integer> getPlayer() {
        return player;
    }

    /**
     * Get list of available rewards.
     *
     * @return List of available resources
     */
    public List<Resource> getAvailableRewards() {
        return new ArrayList<>(availableRewards);
    }

    /**
     * Get list of already selected rewards.
     *
     * @return List of selected resources
     */
    public List<Resource> getSelectedRewards() {
        return new ArrayList<>(selectedRewards);
    }
}