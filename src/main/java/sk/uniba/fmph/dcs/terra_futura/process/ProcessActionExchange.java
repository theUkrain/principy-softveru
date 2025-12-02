package sk.uniba.fmph.dcs.terra_futura.process;

import org.apache.commons.lang3.tuple.Pair;
import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;
import sk.uniba.fmph.dcs.terra_futura.effects.Effect;
import sk.uniba.fmph.dcs.terra_futura.effects.Exchange;
import sk.uniba.fmph.dcs.terra_futura.tiles.Card;
import sk.uniba.fmph.dcs.terra_futura.tiles.Grid;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProcessActionExchange extends ProcessAction{
    private Grid grid;
    private final Map<Resource, List<Pair<Integer, Card>>> input;
    private final Set<Pair<Resource, Integer>> output;

    public ProcessActionExchange(Effect effect, Grid grid, Map<Resource, List<Pair<Integer, Card>>> input, Set<Pair<Resource, Integer>> output) {
        super(effect);
        this.grid = grid;
        this.input = input;
        this.output = output;
    }

    @Override
    public int activateCard() {
        Exchange effectCasted = (Exchange) effect;
        return effectCasted.execute(input, output);
    }
}