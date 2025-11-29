package sk.uniba.fmph.dcs.terra_futura.effects;

import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;
import sk.uniba.fmph.dcs.terra_futura.tiles.Card;

import java.util.List;

public class AssistanceEffect implements Effect {

    @Override
    public boolean activate(Card card) {
        return false;
    }

    @Override
    public boolean hasAssistance() {
        return true;
    }

    @Override
    public String state() {
        return "Assistance effect";
    }
}
