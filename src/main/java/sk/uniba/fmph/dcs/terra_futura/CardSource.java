package sk.uniba.fmph.dcs.terra_futura;

public record CardSource(Deck deck, int index) {
    public CardSource {
        if (index < 0 || index > 3) {
            throw new IllegalArgumentException("Index must be between 0 and 3");
        }
    }
}