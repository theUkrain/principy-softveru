package sk.uniba.fmph.dcs.terra_futura.effects;

import sk.uniba.fmph.dcs.terra_futura.tiles.Card;

public abstract class SetCardToEffect implements Effect {
    protected Card card;
    protected boolean set = false;

    public void setCard(Card card) {
        if (set) {
            throw new IllegalStateException("setCard() can be called only once");
        }

        this.card = card;
        set = true;
    }

    public Card getCard() {
        return card;
    }
}
