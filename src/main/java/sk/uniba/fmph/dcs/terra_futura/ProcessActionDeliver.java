package sk.uniba.fmph.dcs.terra_futura;

import org.apache.commons.lang3.tuple.Pair;
import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;
import sk.uniba.fmph.dcs.terra_futura.effects.*;
import sk.uniba.fmph.dcs.terra_futura.process.*;
import sk.uniba.fmph.dcs.terra_futura.tiles.Card;
import sk.uniba.fmph.dcs.terra_futura.tiles.Grid;
import sk.uniba.fmph.dcs.terra_futura.tiles.GridPosition;

import java.io.InputStream;
import java.util.*;

public class ProcessActionDeliver {
    private Scanner sc;
    private Grid grid;

    public ProcessActionDeliver(InputStream in) {
        this.sc = new Scanner(in);
    }

    private Map<Resource, List<Pair<Card, Integer>>> requestResourceMap(Map<Resource, Integer> requieredRes, Grid grid){
        Map<Resource, List<Pair<Card, Integer>>> taken = new HashMap<>();

        for(int dx = -2; dx<=2; ++dx){
            for(int dy = -2; dy<=2; ++dy){
                GridPosition pos = new GridPosition(dx, dy);
                Optional<Card> optional = grid.getCard(pos);

                if(optional.isEmpty()) continue;

                Card card = optional.get();
                Map<Resource, Integer> available = card.takeResources();

                List<Resource> matching = available.keySet().stream().filter(requieredRes::containsKey).toList();

                if(matching.isEmpty()) continue;

                System.out.println("Card: " + card);
                System.out.println("Use this cards resources? (y/n)");

                if(!sc.next().equalsIgnoreCase("y")) continue;

                for(Resource res: matching){
                    int maxAvailable = available.get(res);
                    int maxRequired = requieredRes.get(res);

                    System.out.println("Resource: " + res + " | Available: " + maxAvailable + " | Required: " + maxRequired);
                    System.out.println("How many to take? ");

                    int amount = sc.nextInt();
                    amount = Math.max(0, Math.min(amount, maxAvailable));

                    if(amount > 0){
                        taken.computeIfAbsent(res, k -> new ArrayList<>()).add(Pair.of(card, amount));
                    }
                }
            }
        }

        return taken;
    }

    public void process(Effect effect, Grid grid) {
        this.grid = grid;
        effect.apply(this);
    }

    public void process(RawMaterialProducer effect){
        ProcessActionRawMaterialProducer processAction = new ProcessActionRawMaterialProducer(effect);
        processAction.activateCard();
    }

    public void process(TransformationFixed effect){
        ProcessActionTransformationFixed processAction = new ProcessActionTransformationFixed(effect, requestResourceMap(effect.getRequiredInputs(), grid));
        int generatedPollution = processAction.activateCard();

        askPlacePollution(generatedPollution, processAction, grid);
    }

    public void process(Exchange effect){

    }

    public void process(EffectOr effect){
        System.out.println("What effect would you like to use? (0-" + (effect.getEffectList().size() - 1) + ") ");
        int index = sc.nextInt();

        ProcessActionEffectOr processAction = new ProcessActionEffectOr(effect, index, this, grid);
        int generatedPollution = processAction.activateCard();

        askPlacePollution(generatedPollution, processAction, grid);
    }

    public void process(AssistanceEffect effect){
        // TODO pollution transfer effect fix
    }

    public void process(PollutionTransfer effect) {
        List<Pair<Card, Integer>> params = new ArrayList<>();

        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                GridPosition pos = new GridPosition(dx, dy);
                Optional<Card> optional = grid.getCard(pos);

                if (optional.isEmpty()) continue;

                System.out.println("Card: " + optional.get());
                System.out.println("Transfer polution from this card? (y/n)");

                if (!sc.next().equalsIgnoreCase("y")) continue;

                System.out.println("Enter count of pollution to move: ");

                int count = sc.nextInt();
                Card card = optional.get();

                params.add(Pair.of(card, count));
            }
        }

        ProcessActionPollutionTransfer processAction = new ProcessActionPollutionTransfer(effect, params);
        processAction.activateCard();
    }

    private void askPlacePollution(int generatedPollution, ProcessAction processAction, Grid grid) {
        if (generatedPollution <= 0) return;

        System.out.println("Generated pollution: " + generatedPollution);
        System.out.println("You have to put your pollution");

        List<Pair<Card, Integer>> params = new ArrayList<>();

        for (int dx = -2; dx <= 2; dx++){
            for (int dy = -2; dy <= 2; dy++){
                GridPosition pos = new GridPosition(dx, dy);
                Optional<Card> optional = grid.getCard(pos);

                if(optional.isEmpty()) continue;

                System.out.println("Card: " + optional.get());
                System.out.println("Transfer polution to this card? (y/n)");

                if(!sc.next().equalsIgnoreCase("y")) continue;

                System.out.println("Enter count of pollution to move: ");

                int count = sc.nextInt();
                Card card = optional.get();

                generatedPollution -= count;

                if (generatedPollution < 0) {
                    throw new IllegalArgumentException("You already put full pollution");
                }

                params.add(Pair.of(card, count));
            }
        }

        placePollution(params);
    }

    public void placePollution(List<Pair<Card, Integer>> placements) {
        for (Pair<Card, Integer> info: placements) {
            Card card = info.getLeft();

            if (!card.canPutPollution(info.getRight())) {
                throw new IllegalArgumentException("Card " + card + " doesnt have place to put pollution");
            }

            card.putPollution(info.getRight());
        }
    }
}
