package sk.uniba.fmph.dcs.terra_futura;

import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;
import sk.uniba.fmph.dcs.terra_futura.effects.Effect;
import sk.uniba.fmph.dcs.terra_futura.tiles.Card;
import sk.uniba.fmph.dcs.terra_futura.tiles.CardSource;

import java.util.Map;

public class TestingCard implements Card {

    private String id;

    public TestingCard(String id) {
        this.id = id;
    }

    @Override
    public boolean isOverPolluted() {
        return false;
    }

    @Override
    public boolean canGetResources(Map<Resource, Integer> resources) {
        return false;
    }

    @Override
    public void getResources(Map<Resource, Integer> resources) {

    }

    @Override
    public boolean canPutResources(Map<Resource, Integer> resources) {
        return false;
    }

    @Override
    public void putResources(Map<Resource, Integer> resources) {

    }

    @Override
    public Effect getUpper() {
        return null;
    }

    @Override
    public Effect getLower() {
        return null;
    }

    @Override
    public CardSource getCardSource() {
        return null;
    }

    @Override
    public boolean hasAssistance() {
        return false;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof TestingCard)) return false;
        return ((TestingCard) o).getId().equals(getId());
    }
}
