package sk.uniba.fmph.dcs.terra_futura;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Manages a pile of cards with visible and hidden cards.
 * Players can take visible cards or discard the oldest one.
 */
public class Pile {
    private static final int VISIBLE_CARDS_COUNT = 4;

    private final List<Card> visibleCards;
    private final List<Card> hiddenCards;
    private final Random random;

    /**
     * Constructor for production use with default random generator.
     *
     * @param allCards All cards to be distributed between visible and hidden
     */
    public Pile(List<Card> allCards) {
        this(allCards, new Random());
    }

    /**
     * Constructor for testing with controllable random generator.
     *
     * @param allCards All cards to be distributed between visible and hidden
     * @param random Random generator for testing purposes
     */
    public Pile(List<Card> allCards, Random random) {
        if (allCards.size() < VISIBLE_CARDS_COUNT) {
            throw new IllegalArgumentException("Not enough cards for pile initialization");
        }

        this.random = random;
        this.visibleCards = new ArrayList<>();
        this.hiddenCards = new ArrayList<>(allCards);

        // Initialize visible cards with nulls
        for (int i = 0; i < VISIBLE_CARDS_COUNT; i++) {
            this.visibleCards.add(null);
        }

        // Fill visible cards from hidden
        for (int i = 0; i < VISIBLE_CARDS_COUNT; i++) {
            refillVisibleCard();
        }
    }

    /**
     * Get a card at specified index from visible cards.
     * Index 0 is the newest card, index 3 is the oldest.
     *
     * @param index Index of the card (0-3)
     * @return Card at the index, or empty if index is invalid or no card available
     */
    public Optional<Card> getCard(int index) {
        if (index < 0 || index >= visibleCards.size()) {
            return Optional.empty();
        }
        return Optional.ofNullable(visibleCards.get(index));
    }

    /**
     * Take a card from visible cards at specified index.
     * Removes the card and refills from hidden cards.
     *
     * @param index Index of the card to take (0-3)
     * @return true if card was successfully taken, false otherwise
     */
    public boolean takeCard(int index) {
        if (index < 0 || index >= visibleCards.size()) {
            return false;
        }

        if (visibleCards.get(index) == null) {
            return false;
        }

        visibleCards.set(index, null);
        refillVisibleCard();
        return true;
    }

    /**
     * Remove the last (oldest) card from visible cards.
     * The oldest card is at index 3.
     */
    public void removeLastCard() {
        int lastIndex = visibleCards.size() - 1;
        if (lastIndex >= 0 && visibleCards.get(lastIndex) != null) {
            visibleCards.set(lastIndex, null);
            refillVisibleCard();
        }
    }

    /**
     * Get current state of the pile as string.
     * Format: "Pile[visible: [card1, card2, card3, card4], hidden: X]"
     *
     * @return String representation of pile state
     */
    public String state() {
        StringBuilder sb = new StringBuilder();
        sb.append("Pile[visible: [");

        for (int i = 0; i < visibleCards.size(); i++) {
            Card card = visibleCards.get(i);
            if (card != null) {
                sb.append(card.state());
            } else {
                sb.append("null");
            }
            if (i < visibleCards.size() - 1) {
                sb.append(", ");
            }
        }

        sb.append("], hidden: ").append(hiddenCards.size()).append("]");
        return sb.toString();
    }

    /**
     * Refill one visible card from hidden cards.
     * Randomly selects a card from hidden cards if available.
     */
    private void refillVisibleCard() {
        if (hiddenCards.isEmpty()) {
            return;
        }

        // Find first null position in visible cards
        for (int i = 0; i < visibleCards.size(); i++) {
            if (visibleCards.get(i) == null) {
                int randomIndex = random.nextInt(hiddenCards.size());
                Card card = hiddenCards.remove(randomIndex);
                visibleCards.set(i, card);
                return;
            }
        }
    }

    /**
     * Check if pile has any cards left (visible or hidden).
     *
     * @return true if pile has cards, false if empty
     */
    public boolean hasCards() {
        for (Card card : visibleCards) {
            if (card != null) {
                return true;
            }
        }
        return !hiddenCards.isEmpty();
    }

    /**
     * Get count of hidden cards remaining.
     *
     * @return Number of hidden cards
     */
    public int getHiddenCardsCount() {
        return hiddenCards.size();
    }
}