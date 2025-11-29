package sk.uniba.fmph.dcs.terra_futura.effects;

import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;

import java.util.List;

public class AssistanceEffect implements Effect{

    @Override
    public boolean check(List<Resource> input, List<Resource> output, int pollution) {
        return true;
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
