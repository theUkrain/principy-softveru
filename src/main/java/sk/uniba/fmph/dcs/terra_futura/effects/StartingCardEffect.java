package sk.uniba.fmph.dcs.terra_futura.effects;

import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;

import java.util.List;

public class StartingCardEffect implements Effect {
    List<Effect> effects;

    public StartingCardEffect() {
        effects.add(new EffectOr(
                new EffectOr(
                        new ArbitraryBasic(0, List.of(Resource.GREEN, Resource.RED, Resource.YELLOW), 0),
                        new ArbitraryBasic(0, List.of(Resource.MONEY), 0)),
                new AssistanceEffect()));
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
        return true;
    }

    @Override
    public String state() {
        return "";
    }
}
