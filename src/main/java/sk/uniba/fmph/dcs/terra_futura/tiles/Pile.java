package sk.uniba.fmph.dcs.terra_futura.tiles;

import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Deck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Pile implements PileInterface {
    private List<Card> input;
    private List<Card> discardPile;

    public Pile(List<Card> input) {
        this.input = input;
        this.discardPile = new ArrayList<>();
        Collections.shuffle(input);
    }

    private void newPile() {
        Collections.shuffle(discardPile);
        input = new ArrayList<>(discardPile);
        discardPile.clear();

        if (input.isEmpty()) {
            throw new ArrayIndexOutOfBoundsException("Pile is empty");
        }
    }

    public Optional<Card> getCard(int index) {
        if (index < 0) {
            index = 0;
        }

        if (index >= input.size()) {
            if (input.isEmpty()) {
                newPile();
            }

            index = input.size() - 1;
        }

        if (index > 3) {
            index = 4;
        }

        Card card = input.get(index);
        input.remove(index);
        return Optional.ofNullable(card);
    }

    @Override
    public void discardCard() {
        if (input.isEmpty()) {
            newPile();
        }

        discardPile.add(input.getFirst());
        input.removeFirst();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < (Math.min(input.size(), 4)); i++) {
            sb.append("Card (" + i + "): " + input.get(i).toString() + "\n");
        }

        return sb.toString();
    }
}