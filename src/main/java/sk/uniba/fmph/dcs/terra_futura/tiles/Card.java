package sk.uniba.fmph.dcs.terra_futura.tiles;

import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;
import sk.uniba.fmph.dcs.terra_futura.effects.Effect;

import java.util.Map;

public interface Card {

    boolean isOverPolluted();

    boolean canGetResources(Map<Resource, Integer> resources);

    void getResources(Map<Resource, Integer> resources);

    boolean canPutResources(Map<Resource, Integer> resources);

    void putResources(Map<Resource, Integer> resources);

    Effect getUpper();

    Effect getLower();

    CardSource getCardSource();

    boolean canGetPollution(int amount);

    void getPollution(int amount);

    boolean canPutPollution(int amount);

    void putPollution(int amount);

    Map<Resource, Integer> takeResources();

    //TODO

    /**
     * method, created ONLY for debug. Returns immutable copy of resources map.
     */
    public Map<Resource, Integer> getCurResources();

    boolean hasAssistance();
}
