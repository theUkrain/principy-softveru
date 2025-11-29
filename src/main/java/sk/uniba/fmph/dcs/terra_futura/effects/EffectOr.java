package sk.uniba.fmph.dcs.terra_futura.effects;

import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;

import java.util.List;

public class EffectOr implements Effect {
    private List<Effect> effects;

    public EffectOr(Effect e1, Effect e2){
        effects.add(e1);
        effects.add(e2);
    }

    @Override
    public boolean check(List<Resource> input, List<Resource> output, int pollution) {
        for (Effect e : effects) {
            if (e.check(input, output, pollution)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasAssistance() {
        for (Effect e : effects) {
            if (e.hasAssistance()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String state() {
        StringBuilder out = new StringBuilder();
        out.append("[");
        for (Effect e : effects) {
            out.append(e.state());
            out.append(",");
        }
        out.append("]");
        return out.toString();
    }
}
