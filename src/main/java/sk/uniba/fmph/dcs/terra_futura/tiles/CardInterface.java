package sk.uniba.fmph.dcs.terra_futura.tiles;

import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;

import java.util.Collection;

public interface CardInterface {
    boolean canGetResources(Collection<Resource> resources);
    void  getResources(Collection<Resource> resources);
    boolean canPutResources();
    void putResources(Collection<Resource> resources);
    boolean hasAssistance();
}
