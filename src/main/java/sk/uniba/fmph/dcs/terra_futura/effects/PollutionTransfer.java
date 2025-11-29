package sk.uniba.fmph.dcs.terra_futura.effects;

import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;
import sk.uniba.fmph.dcs.terra_futura.tiles.Card;

import java.util.List;

public class PollutionTransfer implements Effect{
    private int currentPolutionQuantity;
    private static final int MAX_POLUTION = 4;

    @Override
    public boolean activate(Card card) {

    }

    @Override
    public boolean hasAssistance() {
        return false;
    }

    @Override
    public String state() {
        return "Current pollution quantity " + currentPolutionQuantity;
    }
}
