package sk.uniba.fmph.dcs.terra_futura;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Deck;
import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;
import sk.uniba.fmph.dcs.terra_futura.effects.*;
import sk.uniba.fmph.dcs.terra_futura.tiles.Card;
import sk.uniba.fmph.dcs.terra_futura.tiles.CardFactory;
import sk.uniba.fmph.dcs.terra_futura.tiles.CardSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EffectsTest {
    @Test
    @DisplayName("StartingCardEffect test")
    public void testStartingCardEffectTrigger() {
        StartingCardEffect effect = new StartingCardEffect();

        Effect trigger1 = effect.execute(0);

        Assertions.assertInstanceOf(EffectOr.class, trigger1,
                "execute(0) must return EffectOr");

        Effect trigger2 = effect.execute(1);

        Assertions.assertInstanceOf(AssistanceEffect.class, trigger2,
                "execute(1) must return AssistanceEffect, cuz the second effect in EffectOr");

        EffectOr expectedEffect1 = new EffectOr(
                new RawMaterialProducer(Resource.UNIVERSAL),
                new RawMaterialProducer(Resource.MONEY));

        Assertions.assertTrue(trigger1.equals(expectedEffect1));
    }

}
