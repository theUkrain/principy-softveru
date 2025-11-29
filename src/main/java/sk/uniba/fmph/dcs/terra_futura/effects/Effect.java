package sk.uniba.fmph.dcs.terra_futura.effects;
import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;

import java.util.List;

public interface Effect {
    boolean check(List<Resource> input, List<Resource> output, int pollution);
    boolean hasAssistance();
    String state();
}
