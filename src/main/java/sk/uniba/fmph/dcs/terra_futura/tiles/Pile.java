package sk.uniba.fmph.dcs.terra_futura.tiles;

import java.util.*;

public class Pile implements PileInterface{
    private List<Card> currentPile;
    private List<Card> discardPile;

    public Pile(List<Card> currentPile){
        this.currentPile = currentPile;
        this.discardPile = new ArrayList<>();
        Collections.shuffle(currentPile);
    }

    private void putDiscardPileToCurrent(){
        Collections.shuffle(discardPile);
        currentPile = new ArrayList<>(discardPile);
        discardPile = new ArrayList<>();
    }

    @Override
    public Optional<Card> getCard(int index){
        if(index < 0){
            index = 0;
        }
        if(index >= currentPile.size()){
            if(currentPile.isEmpty()){
                putDiscardPileToCurrent();
                if(currentPile.isEmpty()){
                    throw new IllegalArgumentException("Pile is gone");
                }
            }
        }
        index = (currentPile.size() - 1) % 5;
        Card card = currentPile.get(index);
        currentPile.remove(index);
        return Optional.ofNullable(card);
    }

    @Override
    public void discardCard(){
        discardPile.add(currentPile.getLast());
        currentPile.removeLast();

        if(currentPile.isEmpty()){
            putDiscardPileToCurrent();
        }
    }

    @Override
    public String toString(){
        StringBuilder out = new StringBuilder();
        int boundary = Math.min(currentPile.size(), 4);
        for(int i = 0; i< boundary; i++) {
            out.append("Card on place");
            out.append(i);
            out.append(" is ");
            out.append(currentPile.get(i).toString());
            out.append("\n");
        }
        return out.toString();
    }
}
