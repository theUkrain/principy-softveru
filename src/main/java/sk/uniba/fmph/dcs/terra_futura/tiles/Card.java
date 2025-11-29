package sk.uniba.fmph.dcs.terra_futura.tiles;

import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;

import java.util.*;

public class Card implements  CardInterface {

    private  Map<Resource, Integer> resources;
    private final int pollutionSpaces;
    private int curPollution;
    private final boolean hasAssistance;

    public Card(int pollutionSpaces, boolean hasAssistance) {
        resources = new HashMap();
        this.pollutionSpaces = pollutionSpaces;
        this.hasAssistance = hasAssistance;
    }

    private boolean isOverPolluted() {
        return curPollution > pollutionSpaces;
    }

    /**
     * @param resources, whose quantities are expected to be validated by this method.
     * @return true, if card is not overpolluted and have all resources listed in parameter resources available on this card.
     */
    @Override
    public boolean canGetResources(Collection<Resource> resources)  {

        if(isOverPolluted()) return false;

        Map<Resource, Integer> temp = new HashMap(this.resources);

        for(Resource resource : resources) {
            if(this.resources.getOrDefault(resource, 0) > 0) return false;
            temp.put(resource, temp.get(resource) - 1);
        }

        return true;

    }

    /**
     * If resources are available in required quantities ob this card, removes listed resources from this card.
     * @param resources to be removed.
     */
    @Override
    public void getResources(Collection<Resource> resources) {

        if(!canGetResources(resources)) return;

        for(Resource resource : resources) {
            this.resources.put(resource, this.resources.get(resource) - 1);
        }

        for(Resource resource : this.resources.keySet()) {
            this.resources.compute(resource, (key, number) -> {if(number == 0) return null; return number;});
        }

        curPollution = this.resources.getOrDefault(Resource.POLLUTION, 0);

    }


    /**
     *
     * @return true, if isn't overpolluted.
     */
    @Override
    public boolean canPutResources(List<Resource> resources) {
        return !isOverPolluted();
    }


    /**
     * puts every resource listed in resources  on this card.
     * @param resources to be putted on this card.
     */
    @Override
    public void putResources(Collection<Resource> resources) {
        for(Resource resource : resources) {
            this.resources.putIfAbsent(resource, 1);
            this.resources.put(resource, this.resources.get(resource)+1);
        }
    }

    @Override
    public boolean hasAssistance() {
        return hasAssistance;
    }
}
