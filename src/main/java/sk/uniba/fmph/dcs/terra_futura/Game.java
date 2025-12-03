package sk.uniba.fmph.dcs.terra_futura;

import java.io.InputStream;
import java.util.*;

import org.apache.commons.lang3.tuple.Pair;

import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.GameState;
import sk.uniba.fmph.dcs.terra_futura.effects.*;
import sk.uniba.fmph.dcs.terra_futura.tiles.Card;
import sk.uniba.fmph.dcs.terra_futura.tiles.Grid;
import sk.uniba.fmph.dcs.terra_futura.tiles.GridPosition;
import sk.uniba.fmph.dcs.terra_futura.tiles.Pile;

public class Game implements TerraFuturaInterface {
    private Set<Pair<Player, Integer>> scoreTable;
    private Scanner sc;
    private InputStream in;
    private GameState state;

    private List<Player> players;
    private int index;

    private Pile tier1;
    private Pile tier2;

    private ProcessActionDeliver actionDeliver;

    public Game(InputStream in) {
        this.in = in;
        this.sc = new Scanner(in);
        index = 0;

        gameInit();
    }

    public int playerCount() {
        return players.size();
    }

    public Player getPlayer(int ind) {
        return players.get(ind);
    }

    @Override
    public void gameInit() {
        int n = sc.nextInt();
        if (n < 2 || n > 4) throw new IllegalArgumentException("Unable to create a game for " + n + " players");

        gameInitPile();

        actionDeliver = new ProcessActionDeliver(in);

        scoreTable = new TreeSet<>(new Comparator<Pair<Player, Integer>>() {
            @Override
            public int compare(Pair<Player, Integer> obj1, Pair<Player, Integer> obj2) {
                return obj1.getRight().compareTo(obj2.getRight());
            }
        });

        List<ActivationPattern> patterns = gameInitActivationPatterns();
        Collections.shuffle(patterns);

        List<ScoringMethod> methods = gameInitScoringMethods();
        Collections.shuffle(methods);

        for (int i = 0; i < n; ++i) {
            Grid g = new Grid();
            ActivationPattern pattern1 = patterns.get(i * 2);
            ActivationPattern pattern2 = patterns.get(i * 2 + 1);

            ScoringMethod scoringMethod1 = methods.get(i * 2);
            ScoringMethod scoringMethod2 = methods.get(i * 2 + 1);

            Player player = new Player(requestName(), g, pattern1, pattern2, scoringMethod1, scoringMethod2);
            players.add(player);
        }
    }

    private String requestName() {
        return sc.nextLine();
    }

    private void gameInitPile() {
        ArrayList<Card> inputTier1 = new ArrayList<>();
        //TODO fill cards for tier 1

        ArrayList<Card> inputTier2 = new ArrayList<>();
        //TODO fill cards for tier 2

        tier1 = new Pile(inputTier1);
        tier2 = new Pile(inputTier2);
    }

    private List<ActivationPattern> gameInitActivationPatterns() {
        ArrayList<ActivationPattern> patterns = new ArrayList<>();
        //TODO fill activation patterns

        return patterns;
    }

    private List<ScoringMethod> gameInitScoringMethods() {
        ArrayList<ScoringMethod> methods = new ArrayList<>();
        //TODO fill scoring methods

        return methods;
    }

    @Override
    public void gameStart() {
        System.out.println("----------------< Terra Futura Game >----------------");
        gameMethod();
    }

    private void gameMethod() {
        boolean firstRun = true;
        while (true) {
            Player curPlayer = players.get(index);
            System.out.println("--< Player`s " + (index + 1) + " turn >--");

            // Card discard
            System.out.println("-- Discard card");
            System.out.println(tier1.state());
            System.out.println(tier2.state());
            System.out.println("Would you like to discard any card? (y/n)");
            if (sc.next().equalsIgnoreCase("y")) discardCard();

            // Selecting card
            System.out.println("-- Select card");
            if (firstRun) {
                System.out.println("------------------------ Info ------------------------\n" +
                        "Indexes 1 to 4 correspond to cards you see\n" +
                        "Choosing index 0 you will select the next card in pile\n" +
                        "------------------------------------------------------");
            }
            Card card = selectCard();
            System.out.println("Chosen card:\n" + card + "\n");

            // Puting card
            System.out.println("-- Putting card");
            if (firstRun) {
                System.out.println("------------------------ Info ------------------------\n" +
                        "Coordinates in game are given relative to centre\n" +
                        "Thus coordinates (-2, -2) and (2, 2) reference to\n" +
                        "Choosing index 0 you\n" +
                        "------------------------------------------------------");
            }
            System.out.println(curPlayer.getGrid());
            Set<Card> activatedCards = putCard(curPlayer.getGrid(), card);

            // Activating cards
            System.out.println("-- Activating card");
            if (firstRun) {
                System.out.println("------------------------- Info -------------------------\n" +
                        "You will need to determine order of execution by\n" +
                        "inputing idexes one by one (starting from 1). You don`t\n" +
                        "need to activate every card. To stop inputing type -1\n" +
                        "--------------------------------------------------------");
            }

            executeActivatedCards(activatedCards, curPlayer.getGrid());

            index = (index + 1) % players.size();
            firstRun = false;
            if (finishConditionCheck()) break;
        }

        gameFinish();
    }

    private void gameFinish(){
        System.out.println("-------- Final activation --------");

        for (int i = 0; i < players.size(); ++i) {
            System.out.println("--< Player`s " + (i + 1) + " turn >--");

            // Select activation pattern
            System.out.println("-- Select pattern");
            selectActivationPattern(players.get(i));
            Set<Card> activatedCards = players.get(i).getGrid().getActivatedCards();
            executeActivatedCards(activatedCards, players.get(i).getGrid());

            // Select Scoring Method
            System.out.println("-- Select scoring pattern");
            int points = selectScoringMethod(players.get(i));
            scoreTable.add(Pair.of(players.get(i), points));
        }
    }

    private void selectActivationPattern(Player player) {
        System.out.println("Your activation patterns:");
        System.out.println(player.getFirstActivationPattern());
        System.out.println(player.getSecondActivationPattern());

        System.out.println("Select pattern to use: (1 or 2)");
        int choice = sc.nextInt();
        if (choice != 1 && choice != 2) {
            System.out.println("Invalid pattern choice");
            selectActivationPattern(player);
            return;
        }
        switch (choice) {
            case 1:
                player.selectFirstActivationPattern();
                break;

            case 2:
                player.selectSecondActivationPattern();
                break;
        }
    }

    private int selectScoringMethod(Player player) {
        System.out.println("Your activation patterns:");
        System.out.println(player.getFirstScoringMethod());
        System.out.println(player.getSecondScoringMethod());

        System.out.println("Select pattern to use: (1 or 2)");
        int choice = sc.nextInt();
        if (choice != 1 && choice != 2) {
            System.out.println("Invalid method choice");
            return selectScoringMethod(player);
        }
        int points = 0;
        switch (choice) {
            case 1:
                points = player.selectFirstScoringMethod();
                break;

            case 2:
                points = player.selectSecondScoringMethod();
                break;
        }
        return points;
    }

    private void discardCard() {
        System.out.println("From which pile? (1 or 2)");
        int pile = sc.nextInt();

        if (pile < 1 && pile > 2) {
            System.out.println("Insufficient pile number");
            System.out.println("Would you like to abbort? (y/n)");
            if (sc.next().equalsIgnoreCase("y")) {
                return;
            }
            discardCard();
            return;
        }

        switch (pile) {
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

    private Card selectCard() {
        System.out.println("From which pile? (1 or 2)");
        int pile = sc.nextInt();

        if (pile < 1 && pile > 2) {
            System.out.println("Insufficient pile number");
            return selectCard();
        }

        System.out.println("Which card? (0 to 4)");
        int ind = sc.nextInt();

        if (ind < 0 || ind > 4) {
            System.out.println("Isufficient card index");
            return selectCard();
        }

        if (ind == 0) {
            ind = 5;
        } else --ind;

        Card card = null;

        switch (pile) {
            case 1:
                card = (tier1.getCard(ind)).get();
                break;

            case 2:
                card = (tier2.getCard(ind)).get();
                break;
        }

        return card;
    }

    private Set<Card> putCard(Grid grid, Card card) {
        System.out.println(grid);
        System.out.print("Select coordinate X: (-2 to 2)");
        int x = sc.nextInt();
        if (x < -2 || x > 2) {
            System.out.println("Insufficient X coordinate");
            return putCard(grid, card);
        }

        System.out.print("Select coordinate Y: (-2 to 2)");
        int y = sc.nextInt();
        if (y < -2 || y > 2) {
            System.out.println("Insufficient Y coordinate");
            return putCard(grid, card);
        }

        GridPosition coordinate = new GridPosition(x, y);

        if (!grid.canPutCard(coordinate)) {
            System.out.println("Unable to put card there");
            return putCard(grid, card);
        }

        Set<Card> activatedCards = grid.putCard(coordinate, card);
        return activatedCards;
    }

    private void executeActivatedCards(Set<Card> ac, Grid g) {
        List<Card> activatedCards = new ArrayList<>(ac);
        int i = 0;
        for (Card c : activatedCards) {
            System.out.println(++i + ":\n" + c);
        }
        System.out.println("Select order of execution");
        List<Card> pattern = new ArrayList<>();
        while (true) {
            int ind = sc.nextInt();
            if (ind == -1) break;
            if (ind <= 0 || ind > activatedCards.size()) continue;

            if (pattern.contains(activatedCards.get(ind))) {
                System.out.println("Already in execution order");
                continue;
            }

            pattern.add(activatedCards.get(ind));
        }

        System.out.println("Starting execution");

        for (Card c : pattern) {
            executeCard(c, g);
        }
    }

    private void executeCard(Card card, Grid g) {
        System.out.println(card);
        Effect effect = card.getUpper();
        if (card.getLower() != null) {
            System.out.println("Which effect to execute? (1 or 2)");
            int choice = sc.nextInt();
            if (choice != 1 && choice != 2) {
                System.out.println("Invalid effect choice");
                executeCard(card, g);
                return;
            }
            switch (choice) {
                case 1:
                    effect = card.getUpper();
                    break;

                case 2:
                    effect = card.getLower();
                    break;
            }
        }

        try {
            actionDeliver.process(effect, g);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage() + "\nTry again");
            executeCard(card, g);
            return;
        }
    }

    private boolean finishConditionCheck() {
        for (Player player : players) {
            if (!player.getGrid().isFull()) {
                return false;
            }
        }
        return true;
    }
}
