package sk.uniba.fmph.dcs.terra_futura;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Deck;
import sk.uniba.fmph.dcs.terra_futura.tiles.Card;
import sk.uniba.fmph.dcs.terra_futura.tiles.CardFactory;
import sk.uniba.fmph.dcs.terra_futura.tiles.CardSource;
import sk.uniba.fmph.dcs.terra_futura.tiles.Pile;

import java.util.HashSet;
import java.util.Set;

public class PileTest {

    @DisplayName("Test 1")
    @Test
    public void test() {

        Set<Card> cardSet = new HashSet<>();

        cardSet.add(CardFactory.card(2, null, null, new CardSource(1, Deck.I)));
        cardSet.add(CardFactory.card(1, null, null, new CardSource(2, Deck.II)));
        cardSet.add(CardFactory.card(3, null, null, new CardSource(3, Deck.I)));

        Pile pile = new Pile(cardSet);

        pile.discardCard();
        pile.discardCard();
        pile.discardCard();

        assert(cardSet.contains(pile.getCard(0).get()));
    }



}

