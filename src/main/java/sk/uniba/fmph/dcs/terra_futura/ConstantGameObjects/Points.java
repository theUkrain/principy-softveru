package sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects;

import java.util.Map;

import static java.util.Map.entry;

public class Points {
    Map<Resource, Integer> basicScoring = Map.ofEntries(
            entry(Resource.RED, 1),
            entry(Resource.YELLOW, 1),
            entry(Resource.GREEN, 1),
            entry(Resource.BULB, 5),
            entry(Resource.GEAR, 5),
            entry(Resource.CAR, 6),
            entry(Resource.MONEY,0),
            entry(Resource.POLLUTION, -1));

    public int getPoints(Resource resource) {
        return basicScoring.get(resource);
    }

}
