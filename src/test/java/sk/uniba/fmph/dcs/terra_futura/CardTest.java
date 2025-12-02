package sk.uniba.fmph.dcs.terra_futura;


import org.junit.Test;
import org.junit.Before;
import org.junit.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Deck;
import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;
import sk.uniba.fmph.dcs.terra_futura.tiles.Card;
import sk.uniba.fmph.dcs.terra_futura.tiles.CardFactory;
import sk.uniba.fmph.dcs.terra_futura.tiles.CardSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CardTest {

    private List<Card> cards;
    private Map<Resource, Integer> onePollution = Map.of(Resource.POLLUTION, 1);


    @Before
    public void setup() {
        cards = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            Card card = CardFactory.card(i, null, null, new CardSource(i, Deck.I));
            cards.add(card);
        }
    }

    @After
    public void reset(){
        CardFactory.reset();
    }

    @Test
    public void pollutionLimitTest() {
        Card c = cards.get(3);

        Assert.assertFalse(c.canPutPollution(4));

        c.putPollution(1);
        c.putPollution(1);
        c.putPollution(1);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            c.putPollution(1);
        });

        Assertions.assertFalse(c.canGetPollution(100));
        Assertions.assertThrows(IllegalArgumentException.class, () -> {c.getPollution(100);});

    }

    @Test
    public void inactiveIfOverpollutedTest() {

        Card c = cards.get(1);

        c.putPollution(1);

        Assertions.assertTrue(c.isOverPolluted());
        Assertions.assertFalse(c.canPutResources(Map.of()));
        Assertions.assertFalse(c.canPutResources(Map.of()));
        Assertions.assertFalse(c.canGetResources(Map.of()));
    }

    @Test
    public void getResourcesTest() {

        Card c = cards.get(9);

        c.putResources(Map.ofEntries( Map.entry(Resource.GREEN, 10), Map.entry(Resource.RED, 5)));
        Assertions.assertFalse(c.canGetResources(Map.ofEntries(Map.entry(Resource.GREEN, 11), Map.entry(Resource.RED, 5))));

        c.getResources(Map.ofEntries(Map.entry(Resource.GREEN, 5)));

        Assertions.assertFalse(c.canGetResources(Map.ofEntries(Map.entry (Resource.GREEN, 6), Map.entry(Resource.RED, 5))));
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            c.getResources(Map.ofEntries( Map.entry(Resource.GREEN, 6), Map.entry(Resource.RED, 5)));
        });
    }
}