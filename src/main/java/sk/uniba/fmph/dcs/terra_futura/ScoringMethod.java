package sk.uniba.fmph.dcs.terra_futura;

import org.apache.commons.lang3.tuple.Pair;
import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Points;
import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;
import sk.uniba.fmph.dcs.terra_futura.tiles.Card;
import sk.uniba.fmph.dcs.terra_futura.tiles.Grid;
import sk.uniba.fmph.dcs.terra_futura.tiles.GridPosition;

import java.util.*;

public class ScoringMethod {
    private Grid grid;
    public Points pointsPerResource = new Points();
    private List<Pair<Resource, Integer>> combination;
    private int pointsPerCombination;

    public ScoringMethod(Grid grid, List<Pair<Resource, Integer>> combination, int pointsPerCombination) {
        this.grid = grid;
        this.combination = combination;
        this.pointsPerCombination = pointsPerCombination;
    }

    public int selectThisMethodAndCalculate() {
        GridPosition position;
        Optional<Card> card;
        Map<Resource, Integer> allResources = new HashMap<>();
        for (int i = -2; i < 3; i++) {
            for (int j = -2; j < 3; j++) {
                position = new GridPosition(i, j);
                card = grid.getCard(position);
                if (card.isPresent()) {
                    if(card.get().canGetResources(Map.of())){
                        Map<Resource, Integer> resources = card.get().getCurResources();
                        for (Resource r : resources.keySet()) {
                            if (!allResources.containsKey(r)) {
                                allResources.put(r, resources.get(r));
                            } else {
                                allResources.put(r, allResources.get(r) + resources.get(r));
                            }
                        }
                    }
                }
            }
        }
        int score = 0;

        for (Resource r : allResources.keySet()) {
            score += allResources.get(r) * pointsPerResource.getPoints(r);
        }

        int minMultiplicationOfCombination = 0;
        boolean isFirst = true;
        for (Pair<Resource, Integer> p : combination) {
            if (isFirst && allResources.containsKey(p.getLeft())) {
                minMultiplicationOfCombination = allResources.get(p.getLeft()) / p.getRight();
                isFirst = false;
            }
            if (allResources.containsKey(p.getLeft())) {
                minMultiplicationOfCombination = Math.min(minMultiplicationOfCombination, allResources.get(p.getLeft()) / p.getRight());
            }
        }
        return score + (minMultiplicationOfCombination * pointsPerCombination);
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append("This scoring method for ");
        boolean isFirst = true;
        for (Pair<Resource, Integer> p : combination) {
            if (isFirst) {
                res.append(p.getLeft());
                res.append(" in quantity ");
                res.append(p.getRight());
                isFirst = false;
            }
            res.append(", ");
            res.append(p.getLeft());
            res.append(" in quantity ");
            res.append(p.getRight());
            res.append(" ");
        }
        res.append("will give you");
        res.append(pointsPerCombination);
        return res.toString();
    }
}
