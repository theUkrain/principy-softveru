package sk.uniba.fmph.dcs.terra_futura;

import org.apache.commons.lang3.tuple.Pair;
import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;
import sk.uniba.fmph.dcs.terra_futura.effects.*;
import sk.uniba.fmph.dcs.terra_futura.process.*;
import sk.uniba.fmph.dcs.terra_futura.tiles.Card;
import sk.uniba.fmph.dcs.terra_futura.tiles.Grid;
import sk.uniba.fmph.dcs.terra_futura.tiles.GridInterface;
import sk.uniba.fmph.dcs.terra_futura.tiles.GridPosition;

import java.util.*;

public class ProcessActionDeliver {

    private final Game this.game = game ;
    private Game game;
    private Scanner sc;
    private Grid grid;

    public ProcessActionDeliver(Game game) {
         this.game = game ;
         this.sc = new Scanner(System.in);
    }

    private Map<Resource, List<Pair<Card, Integer>>> requestResourceMap(Map<Resource, Integer> requiredRes, Grid grid) {
        Map<Resource, List<Pair<Card, Integer>>> taken = new HashMap<>();

        for (int dx = -2; dx <= 2; ++dx) {
            for (int dy = -2; dy <= 2; ++dy) {
                GridPosition pos = new GridPosition(dx, dy);
                Optional<Card> optional = grid.getCard(pos);

                if (optional.isEmpty()) continue;

                Card card = optional.get();
                Map<Resource, Integer> available = card.takeResources();

                List<Resource> matching = available.keySet().stream().filter(requiredRes::containsKey).toList();

                if (matching.isEmpty()) continue;

                System.out.println("Card: " + card);
                System.out.println("Use this cards resources? (y/n)");

                if (!sc.next().equalsIgnoreCase("y")) continue;

                for (Resource res : matching) {
                    int maxAvailable = available.get(res);
                    int maxRequired = requiredRes.get(res);

                    System.out.println("Resource: " + res + " | Available: " + maxAvailable + " | Required: " + maxRequired);
                    System.out.println("How many to take? ");

                    int amount = sc.nextInt();
                    amount = Math.max(0, Math.min(amount, maxAvailable));

                    if (amount > 0) {
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
        this.grid = null;
    }

    public void process(RawMaterialProducer effect) {
        ProcessActionRawMaterialProducer processAction = new ProcessActionRawMaterialProducer(effect);
        processAction.activateCard();
    }

    public void process(TransformationFixed effect) {
        ProcessActionTransformationFixed processAction = new ProcessActionTransformationFixed(effect, requestResourceMap(effect.getRequiredInputs(), grid));
        int generatedPollution = processAction.activateCard();

        askPlacePollution(generatedPollution, grid);
    }

    public void process(Exchange effect) {

        System.out.println("Enter one of available inputs and it's distribution among your cards:\n");

        int i = 0;

        for (Set<Pair<Resource, Integer>> input : effect.getInputs()) {
            System.out.println("Input " + ++i + ":");
            System.out.println(input);
        }

        Map<Resource, List<Pair<Integer, Card>>> input = new HashMap<>();

        for (int dx = -2; dx <= 2; ++dx) {
            for (int dy = -2; dy <= 2; ++dy) {
                GridPosition pos = new GridPosition(dx, dy);
                Optional<Card> optional = grid.getCard(pos);

                if (optional.isEmpty()) continue;

                Card card = optional.get();

                System.out.println("Card: " + card);
                System.out.println("Use this cards resources? (y/n)");

                if (!sc.next().equalsIgnoreCase("y")) continue;

                Map<Resource, Integer> cardRes = card.getCurResources();

                for (Resource r : cardRes.keySet()) {

                    System.out.println("How much of " + r + "take?\n");

                    int amount = sc.nextInt();

                    if (!input.keySet().contains(r)) input.put(r, List.of(Pair.of(amount, card)));
                    else input.get(r).add(Pair.of(amount, card));
                }
            }
        }

        Set<Pair<Resource, Integer>> res = new HashSet<>(); //OUTPUT!!!!

        System.out.println("Enter one of available outputs:\n");

        i = 0;

        for (Set<Pair<Resource, Integer>> output : effect.getOutputs()) {
            System.out.println("Output " + ++i + ":\n");
            System.out.println(output);
        }

        for (Resource r : Resource.values()) {
            System.out.println("How much " + r + "to put in output? \n");
            res.add(Pair.of(r, sc.nextInt()));
        }

        ProcessActionExchange processAction = new ProcessActionExchange(effect, input, res);
        int generatedPollution = processAction.activateCard();
        askPlacePollution(generatedPollution, grid);

    }

    public void process(EffectOr effect) {
        System.out.println("What effect would you like to use? (0-" + (effect.getEffectList().size() - 1) + ") ");
        int index = sc.nextInt();

        ProcessActionEffectOr processAction = new ProcessActionEffectOr(effect, index, this, grid);
        int generatedPollution = processAction.activateCard();

        askPlacePollution(generatedPollution, grid);

    }

    public void process(AssistanceEffect effect) {
        
    }

    public void process(PollutionTransfer effect) {
        List<Pair<Card, Integer>> params = new ArrayList<>();

        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                GridPosition pos = new GridPosition(dx, dy);
                Optional<Card> optional = grid.getCard(pos);

                if (optional.isEmpty()) continue;

                System.out.println("Card: " + optional.get());
                System.out.println("Transfer pollution from this card? (y/n)");

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

    private void askPlacePollution(int generatedPollution, Grid grid) {
        if (generatedPollution <= 0) return;

        System.out.println("Generated pollution: " + generatedPollution);
        System.out.println("You have to put your pollution");

        List<Pair<Card, Integer>> params = new ArrayList<>();

        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                GridPosition pos = new GridPosition(dx, dy);
                Optional<Card> optional = grid.getCard(pos);

                if (optional.isEmpty()) continue;

                System.out.println("Card: " + optional.get());
                System.out.println("Transfer pollution to this card? (y/n)");

                if (!sc.next().equalsIgnoreCase("y")) continue;

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
        for (Pair<Card, Integer> info : placements) {
            Card card = info.getLeft();

            if (!card.canPutPollution(info.getRight())) {
                throw new IllegalArgumentException("Card " + card + " doesnt have place to put pollution");
            }

            card.putPollution(info.getRight());
        }
    }
}
