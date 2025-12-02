package sk.uniba.fmph.dcs.terra_futura.effects;

import sk.uniba.fmph.dcs.terra_futura.tiles.Card;

public abstract class SetCardToEffect implements Effect {
    protected Card card;
    protected boolean set = false;

    /**
     * bounds specific effect to card
     * @param card
     */
    public void setCard(Card card) {
        if (card == null) {
            return;
        }

        if (set) {
            throw new IllegalStateException("setCard() can be called only once");
        }

        this.card = card;
        set = true;
    }

    /**
     *
     * @return instance of card that bounds
     */
    public Card getCard() {
        return card;
    }
}
