package sk.uniba.fmph.dcs.terra_futura;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

import org.apache.commons.lang3.tuple.Pair;

import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Deck;
import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.GameState;
import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;
import sk.uniba.fmph.dcs.terra_futura.effects.*;
import sk.uniba.fmph.dcs.terra_futura.process.*;
import sk.uniba.fmph.dcs.terra_futura.tiles.Card;
import sk.uniba.fmph.dcs.terra_futura.tiles.CardSource;
import sk.uniba.fmph.dcs.terra_futura.tiles.Grid;
import sk.uniba.fmph.dcs.terra_futura.tiles.GridPosition;
import sk.uniba.fmph.dcs.terra_futura.tiles.Pile;

public class Game implements TerraFuturaInterface {
    private Scanner sc;
    private InputStream in;
    private OutputStream out;
    private GameState state;

    private List<Player> players;
    private int index;

    private Pile tier1;
    private Pile tier2;

    public Game(InputStream in, OutputStream out){
        this.in = in;
        this.out = out;
        this.sc = new Scanner(in);
        index = 0;

        gameInit();
    }

    public void gameInit(){
        int n = sc.nextInt();
        if(n < 2 || n > 4) throw new IllegalArgumentException("Unable to create a game for " + n + " players");

        gameInitPile();

        List<ActivationPattern> patterns = gameInitActivationPatterns();
        Collections.shuffle(patterns);

        List<ScoringMethod> methods = gameInitScoringMethods();
        Collections.shuffle(methods);

        for(int i=0; i<n; ++i){
            Grid g = new Grid();
            ActivationPattern pattern1 = patterns.get(i * 2);
            ActivationPattern pattern2 = patterns.get(i * 2 + 1);

            ScoringMethod scorringMethod1 = methods.get(i * 2);
            ScoringMethod scorringMethod2 = methods.get(i * 2 + 1);

            Player player = new Player(g, pattern1, pattern2, scorringMethod1, scorringMethod2);
            players.add(player);
        }
    }

    private void gameInitPile(){
        ArrayList<Card> inputTier1 = new ArrayList<>();
        //TODO fill cards for tier 1

        ArrayList<Card> inputTier2 = new ArrayList<>();
        //TODO fill cards for tier 2

        tier1 = new Pile(inputTier1);
        tier2 = new Pile(inputTier2);
    }

    private List<ActivationPattern> gameInitActivationPatterns(){
        ArrayList<ActivationPattern> patterns = new ArrayList<>();
        //TODO fill activation patterns

        return patterns;
    }

    private List<ScoringMethod> gameInitScoringMethods(){
        ArrayList<ScoringMethod> methods = new ArrayList<>();
        //TODO fill scoring methods

        return methods;
    }

    public void gameStart(){
        gameMethod();
    }

    private void gameMethod(){
        boolean firstRun = true;
        while(true){

            // Card discard
            System.out.println("-- Discard card");
            System.out.println(tier1.state());
            System.out.println(tier2.state());
            System.out.println("Would you like to discard any card? (y/n)");
            if(sc.next().equalsIgnoreCase("y")) discardCard();

            // Selecting card
            System.out.println("-- Select card");
            if(firstRun){
                System.out.println("------------------------ Info ------------------------\nIndexes 1 to 4 correspond to cards you see\nChoosing index 0 you will select the next card in pile\n------------------------------------------------------");
            }
            Card card = selectCard();
            System.out.println("Chosen card:\n" + card + "\n");

            //  Puting card
            System.out.println("-- Putting card");
            

            index = (index + 1) % players.size();
            firstRun = false;
        }
    }

    private void discardCard(){
        System.out.println("From which pile? (1 or 2)");
        int pile = sc.nextInt();

        if(pile < 1 && pile > 2){
            System.out.println("Isufficient pile number");
            System.out.println("Would you like to abbort? (y/n)");
            if(sc.next().equalsIgnoreCase("y")){
                return;
            }
            discardCard();
            return;
        }

        switch(pile){
            case 1:
                tier1.discardCard();
                break;

            case 2:
                tier2.discardCard();
                break;
        }

        System.out.println("Card was discarded\n");
        System.out.println(tier1.state());
        System.out.println(tier2.state());
    }

    private Card selectCard(){
        System.out.println("From which pile? (1 or 2)");
        int pile = sc.nextInt();

        if(pile < 1 && pile > 2){
            System.out.println("Isufficient pile number");
            return selectCard();
        }

        System.out.println("Which card? (0 to 4)");
        int ind = sc.nextInt();

        if(ind < 0 || ind > 4){
            System.out.println("Isufficient card index");
            return selectCard();
        }

        if(ind == 0){
            ind = 5;
        }

        Card card = null;

        switch(pile){
            case 1:
                card = (tier1.getCard(ind)).get();
                break;

            case 2:
                card = (tier2.getCard(ind)).get();
                break;
        }

        return card;
    }

    public void process(Effect effect) {
        effect.apply(this);
    }

    public void process(RawMaterialProducer effect){
        ProcessActionRawMaterialProducer processAction = new ProcessActionRawMaterialProducer(effect);
        int generatedPollution = processAction.activateCard();

        placePollution(generatedPollution, processAction);
    }

    private Map<Resource, List<Pair<Card, Integer>>> requestResourceMap(Map<Resource, Integer> requieredRes){
        Map<Resource, List<Pair<Card, Integer>>> taken = new HashMap<>();

        Player player = players.get(index);

        for(int dx = -2; dx<=2; ++dx){
            for(int dy = -2; dy<=2; ++dy){
                GridPosition pos = new GridPosition(dx, dy);
                Optional<Card> optional = player.getGrid().getCard(pos);

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

    public void process(TransformationFixed effect){
        ProcessActionTransformationFixed processAction = new ProcessActionTransformationFixed(effect, requestResourceMap(effect.getRequiredInputs()));
        int generatedPollution = processAction.activateCard();

        placePollution(generatedPollution, processAction);
    }

    public void process(Exchange effect){

    }

    public void process(EffectOr effect){
        System.out.println("What effect would you like to use? (0-" + (effect.getEffectList().size() - 1) + ") ");
        int index = sc.nextInt();

        ProcessActionEffectOr processAction = new ProcessActionEffectOr(effect, index, this);
        int generatedPollution = processAction.activateCard();

        placePollution(generatedPollution, processAction);
    }

    public void process(AssistanceEffect effect){

    }

    public void process(PollutionTransfer effect) {
        List<Pair<Card, Integer>> params = new ArrayList<>();

        Player player = players.get(index);

        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                GridPosition pos = new GridPosition(dx, dy);
                Optional<Card> optional = player.getGrid().getCard(pos);

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
    }

    @Override
    public void takeCard(int playerId, CardSource source, GridPosition destination) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'takeCard'");
    }

    @Override
    public boolean discardLastCardFromDeck(int playerId, Deck deck) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'discardLastCardFromDeck'");
    }

    @Override
    public void selectReward(int playerId, Resource resource) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'selectReward'");
    }

    @Override
    public boolean turnFinished(int playerId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'turnFinished'");
    }

    @Override
    public boolean selectActivationPattern(int playerId, int card) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'selectActivationPattern'");
    }

    @Override
    public boolean selectScoring(int playerId, int card) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'selectScoring'");
    }

    private void placePollution(int generatedPollution, ProcessAction processAction) {
        if (generatedPollution <= 0) return;

        System.out.println("Generated pollution: " + generatedPollution);
        System.out.println("You have to put your pollution");

        List<Pair<Card, Integer>> params = new ArrayList<>();

        Player player = players.get(index);

        for (int dx = -2; dx <= 2; dx++){
            for (int dy = -2; dy <= 2; dy++){
                GridPosition pos = new GridPosition(dx, dy);
                Optional<Card> optional = player.getGrid().getCard(pos);

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

        processAction.placePollution(params);
    }
}
