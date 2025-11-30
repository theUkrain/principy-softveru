package sk.uniba.fmph.dcs.terra_futura.tiles;

import java.util.*;

public class Pile implements PileInterface {
    private List<Card> active;
    private List<Card> discard;

    public Pile(List<Card> input){
        this.active = new ArrayList<>(input);
        Collections.shuffle(active);
        discard = new ArrayList<>();
    }

    @Override
    public Optional<Card> getCard(int index) {
        if(active.isEmpty()){
            Collections.shuffle(discard);
            active = new ArrayList<>(discard);
        }
        if(index >= 4){
            if(active.size() <= 4){
                index = active.size()-1;
            }else index = 4;
        }
        Card element = active.get(index);
        active.remove(index);
        return Optional.of(element);
    }

    @Override
    public void discardCard() {
        discard.add(active.getFirst());
        active.removeFirst();
        if(active.isEmpty()){
            Collections.shuffle(discard);
            active = new ArrayList<>(discard);
        }
    }

    @Override
    public String toString(){
        String s = "Active:\n";
        for(Card c: active){
            s += " " + c + "\n";
        }

        s += "Discard:\n";
        for(Card c: active){
            s += " " + c + "\n";
        }
        return s;
    }

}
