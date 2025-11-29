package sk.uniba.fmph.dcs.terra_futura.effects;

import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;

import java.util.List;

public class StartingCardEffect implements Effect {
    private List<Effect> effects;

    public StartingCardEffect() {
        effects.add(new EffectOr(
                new EffectOr(
                        new ArbitraryBasic(0, List.of(Resource.GREEN, Resource.RED, Resource.YELLOW), 0),
                        new ArbitraryBasic(0, List.of(Resource.MONEY), 0)),
                new AssistanceEffect()));
    }

    @Override
    public boolean check(final List<Resource> input,final List<Resource> output,final int pollution) {
        for (Effect e : effects) {
            if (e.check(input, output, pollution)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasAssistance() {
        return true;
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
