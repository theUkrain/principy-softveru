package sk.uniba.fmph.dcs.terra_futura;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

import org.apache.commons.lang3.tuple.Pair;

import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;
import sk.uniba.fmph.dcs.terra_futura.effects.*;
import sk.uniba.fmph.dcs.terra_futura.tiles.Card;
import sk.uniba.fmph.dcs.terra_futura.tiles.Grid;
import sk.uniba.fmph.dcs.terra_futura.tiles.GridPosition;

public class Game {//implements TerraFuturaInterface {
    private Scanner sc;
    private InputStream in;
    private OutputStream out;

    private List<Grid> players;
    private int ind;
    private Grid curPlayer;

    public Game(InputStream in, OutputStream out){
        this.in = in;
        this.out = out;

        this.sc = new Scanner(in);

        players = new ArrayList<>();
        int ind=0;

        curPlayer = players.get(ind);
    }

    public void process(Effect effect) {
        effect.apply(this);
    }

    public void process(RawMaterialProducer effect){
        effect.execute();
    }

    public void process(TransformationFixed effect){
        // Map<Resource, List<Pair<Card, Integer>>> cards
        Map<Resource, List<Pair<Card, Integer>>> taken = new HashMap<>();
        Map<Resource, Integer> requieredRes = effect.getRequiredInputs();

        for(int dx = -2; dx<=2; ++dx){
            for(int dy = -2; dy<=2; ++dy){
                GridPosition pos = new GridPosition(dx, dy);
                Optional<Card> optional = curPlayer.getCard(pos);

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
        effect.execute(taken);
    }

    public void process(Exchange effect){

    }

    public void process(EffectOr effect){

    }

    public void process(AssistanceEffect effect){

    }

    public void process(PollutionTransfer effect){

    }
}
