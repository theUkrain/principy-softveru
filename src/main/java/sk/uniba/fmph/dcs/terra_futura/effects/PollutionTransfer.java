package sk.uniba.fmph.dcs.terra_futura.effects;

import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;

import java.util.List;

public class PollutionTransfer implements Effect{
    private int currentPolutionQuantity;

    @Override
    public boolean check(List<Resource> input, List<Resource> output, int pollution) {
        return currentPolutionQuantity<4;
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
