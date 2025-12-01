package sk.uniba.fmph.dcs.terra_futura.effects;

import sk.uniba.fmph.dcs.terra_futura.tiles.Card;

public abstract class SetCardToEffect {
    protected Card card;
    protected boolean set = false;

    public void setCard(Card card) {
        if (set) {
            return;
        }

        this.card = card;
        set = true;
    }
}
