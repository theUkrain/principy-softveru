package sk.uniba.fmph.dcs.terra_futura.Samostatne;

import sk.uniba.fmph.dcs.terra_futura.tiles.Card;
import sk.uniba.fmph.dcs.terra_futura.tiles.PileInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class Pile implements PileInterface {
    private List<Card> allCards;
    private List<Card> discardPile;
    private final Random random;

    /**
     * Constructor for production use with default random generator.
     */
    public Pile(List<Card> allCards) {
        this(allCards, new Random());
    }

    /**
     * Constructor for testing with controllable random generator.
     * Keeps the logic of shuffling the allCards immediately.
     */
    public Pile(List<Card> allCards, Random random) {
        this.random = random;
        this.allCards = new ArrayList<>(allCards);
        this.discardPile = new ArrayList<>();
        // Shuffle using the provided random to maintain testability
        Collections.shuffle(this.allCards, this.random);
    }

    /**
     * Refills the allCards pile from the discard pile.
     */
    private void newPile() {
        if (discardPile.isEmpty()) {
            throw new ArrayIndexOutOfBoundsException("Pile is empty");
        }

        // Shuffle discard pile before moving to allCards
        Collections.shuffle(discardPile, this.random);
        allCards = new ArrayList<>(discardPile);
        discardPile.clear();
    }

    @Override
    public Optional<Card> getCard(int index) {
        if (index < 0) {
            index = 0;
        }

        // If index is out of bounds, try to refill or cap the index
        if (index >= allCards.size()) {
            if (allCards.isEmpty()) {
                newPile();
            }
            // Cap index at the last available card
            index = allCards.size() - 1;
        }

        // Specific logic from the requirement: cap index at 4 if it exceeds 3
        if (index > 3) {
            index = 4;
        }

        // Get and REMOVE the card (simulating taking the card)
        Card card = allCards.get(index);
        allCards.remove(index);
        return Optional.ofNullable(card);
    }

    @Override
    public void discardCard() {
        if (allCards.isEmpty()) {
            newPile();
        }

        // Move the top card to the discard pile
        discardPile.add(allCards.get(0));
        allCards.remove(0);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // Display up to 4 cards to match the requested logic
        for (int i = 0; i < (Math.min(allCards.size(), 4)); i++) {
            sb.append("Card (").append(i).append("): ").append(allCards.get(i).toString()).append("\n");
        }

        return sb.toString();
    }

    // Note: The methods takeCard, hasCards, getHiddenCardsCount were removed
    // because the logic you provided in the second snippet manages everything
    // via getCard (which removes the item) and internal state management.
}