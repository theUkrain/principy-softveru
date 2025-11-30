package sk.uniba.fmph.dcs.terra_futura.effects;

import java.util.HashMap;
import java.util.Map;

public  class GameEffects {

    private static final Map<Integer, Effect> effects;

    static { effects = new HashMap<>(); }

    public static Effect get(Effect effect) {
        effects.putIfAbsent(effect.hashcode(), effect);
        return effects.get(effect.hashcode());
    }

    public static void  remove(Effect effect) {
        effects.remove(effect);
    }
    
}
