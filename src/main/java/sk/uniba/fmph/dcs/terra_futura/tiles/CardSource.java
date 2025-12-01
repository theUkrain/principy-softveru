package sk.uniba.fmph.dcs.terra_futura.tiles;
import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Deck;

public  class CardSource {
    private final byte index;
    private final Deck source;

    public CardSource(int index, Deck source) {
        if (index < 0 || index > 3) {
            throw new IllegalArgumentException("Index must be between 0 and 3");
        }
        this.index = (byte)index;
        this.source = source;
    }

    public int getIndex() {
        return (int)index;
    }

    public Deck getSourceDeck() {
        return source;
    }

}
