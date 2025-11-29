package sk.uniba.fmph.dcs.terra_futura.effects;

import sk.uniba.fmph.dcs.terra_futura.enums.Resource;

import java.util.List;

public class StartingCard implements Effect{
    List<Effect> effects;
    @Override
    public boolean check(List<Resource> input, List<Resource> output, int pollution) {
        /*write if one of effects can perform with wanted settings. Composite*/
        return false;
    }

    @Override
    public boolean hasAssistance() {
        return true;
    }

    @Override
    public String state() {
        return "";
    }
}
