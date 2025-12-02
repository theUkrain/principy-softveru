package sk.uniba.fmph.dcs.terra_futura;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Deck;
import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;
import sk.uniba.fmph.dcs.terra_futura.effects.*;
import sk.uniba.fmph.dcs.terra_futura.process.*;
import sk.uniba.fmph.dcs.terra_futura.tiles.Card;
import sk.uniba.fmph.dcs.terra_futura.tiles.CardSource;
import sk.uniba.fmph.dcs.terra_futura.tiles.Grid;
import sk.uniba.fmph.dcs.terra_futura.tiles.GridPosition;

public class Game implements TerraFuturaInterface {
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
}
