package sk.uniba.fmph.dcs.terra_futura.tiles;

import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;

import java.util.Map;

public interface Card {
    
    boolean isOverPolluted();
    
    boolean canGetResources(Map<Resource, Integer> resources);
    void  getResources(Map<Resource, Integer> resources);

    boolean canPutResources(Map<Resource, Integer> resources);
    void putResources(Map<Resource, Integer> resources);

    CardSource getCardSource();

    boolean hasAssistance();
}
