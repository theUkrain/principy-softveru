package sk.uniba.fmph.dcs.terra_futura.effects;

import sk.uniba.fmph.dcs.terra_futura.enums.Resource;
import sk.uniba.fmph.dcs.terra_futura.tiles.Card;

import java.util.List;

public class TransformationFixed implements Effect{

    private List<Resource> from;
    private List<Resource> to;

    public TransformationFixed(List<Resource> from, List<Resource> to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean activate(Card card) {
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
