package sk.uniba.fmph.dcs.terra_futura.tiles;

import java.util.*;
import java.util.*;

public class Pile implements PileInterface {
    private List<Card> active;
    private List<Card> discard;

    public Pile(Collection<Card> input){
        active = new ArrayList<>(input);
        discard = new ArrayList<>();
        Collections.shuffle(active);
    }

    private void refill() {
        List<Card> temp = active;
        Collections.shuffle(discard);
        active = discard;
        discard = temp;
    }

    @Override
    public Optional<Card> getCard(int index) {

        if(active.isEmpty()) refill();

        if(index > 4) index = 4;

        if(index >= active.size()) index = active.size()-1;

        return Optional.ofNullable(active.remove(index));

    }

    @Override
    public void discardCard() {

        if(active.isEmpty()) return;

        discard.add(active.removeFirst());

    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Active:\n");
        for (Card c : active) sb.append(" ").append(c).append("\n");
        sb.append("Discard:\n");
        for (Card c : discard) sb.append(" ").append(c).append("\n");
        return sb.toString();
    }

}