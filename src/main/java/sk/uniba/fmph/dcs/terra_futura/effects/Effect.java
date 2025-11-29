package sk.uniba.fmph.dcs.terra_futura.effects;
import sk.uniba.fmph.dcs.terra_futura.enums.Resource;
import sk.uniba.fmph.dcs.terra_futura.tiles.Card;

import java.util.List;

public interface Effect {
    boolean activate(Card card);
    boolean hasAssistance();
    String state();
}
