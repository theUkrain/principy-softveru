package sk.uniba.fmph.dcs.terra_futura.effects;

import sk.uniba.fmph.dcs.terra_futura.enums.Resource;

import java.util.List;

public class TransformationFixed implements Effect{

    private List<Resource> from;
    private List<Resource> to;

    @Override
    public boolean check(List<Resource> input, List<Resource> output, int pollution) {
        return false;
    }

    @Override
    public boolean hasAssistance() {
        return false;
    }

    @Override
    public String state() {
        return "";
    }
}
