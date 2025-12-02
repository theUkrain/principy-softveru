package sk.uniba.fmph.dcs.terra_futura.tiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Pile implements PileInterface {
    private static final int MAX_VISIBLE_CARDS = 4;
    private static final int MIN_INDEX = 0;

    private List<Card> input;
    private List<Card> discardPile;

    public Pile(List<Card> input) {
        this.input = input;
        this.discardPile = new ArrayList<>();
        Collections.shuffle(this.input);
    }

    private void reshuffleDiscardPile() {
        if (discardPile.isEmpty()) {
            throw new IllegalArgumentException("Pile is empty");
        }

        Collections.shuffle(discardPile);
        input = new ArrayList<>(discardPile);
        discardPile.clear();
    }

    public Optional<Card> getCard(int index) {
        int adjustedIndex = normalizeIndex(index);

        Card card = input.get(adjustedIndex);
        input.remove(adjustedIndex);

        return Optional.ofNullable(card);
    }

    private int normalizeIndex(int index) {
        if (index < MIN_INDEX) {
            return MIN_INDEX;
        }

        if (index >= input.size()) {
            if (input.isEmpty()) {
                reshuffleDiscardPile();
            }
            index = input.size() - 1;
        }

        if (index > MAX_VISIBLE_CARDS - 1) {
            return MAX_VISIBLE_CARDS - 1;
        }

        return index;
    }

    @Override
    public void discardCard() {
        if (input.isEmpty()) {
            reshuffleDiscardPile();
        }

        discardPile.add(input.getFirst());
        input.removeFirst();
    }

    public String state() {
        ArrayList<Card> current = new ArrayList<>();

        for (int i = 0; i < input.size() && i < MAX_VISIBLE_CARDS; ++i) {
            current.add(input.get(i));
        }

        return current.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int visibleCardCount = Math.min(input.size(), MAX_VISIBLE_CARDS);

        for (int i = 0; i < visibleCardCount; ++i) {
            sb.append("Card (")
                    .append(i)
                    .append("): ")
                    .append(input.get(i).toString())
                    .append("\n");
        }

        return sb.toString();
    }
}