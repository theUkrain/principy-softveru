package sk.uniba.fmph.dcs.terra_futura;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Deck;
import sk.uniba.fmph.dcs.terra_futura.tiles.Card;
import sk.uniba.fmph.dcs.terra_futura.tiles.CardFactory;
import sk.uniba.fmph.dcs.terra_futura.tiles.CardSource;

import java.util.ArrayList;
import java.util.List;

public class CardTest {

    private List<Card> cards;

    @BeforeEach
    void setup() {
        cards = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
                    Card card = CardFactory.card(i, null, null, new CardSource(i, Deck.I));
                    cards.add(card);
        }
    }

    @Test
    public void test1() {
        cards.get(3);

    }

}
