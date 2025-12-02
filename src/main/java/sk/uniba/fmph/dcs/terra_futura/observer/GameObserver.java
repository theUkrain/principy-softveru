package sk.uniba.fmph.dcs.terra_futura.observer;

import sk.uniba.fmph.dcs.terra_futura.Player;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class GameObserver implements TerraFuturaObserverInterface {
    private final InputStream input;
    private final OutputStream output;
    private final BufferedReader reader;
    private final PrintWriter writer;
    private final Player player;

    public GameObserver(InputStream input, OutputStream output, Player player) {
        this.input = input;
        this.output = output;
        this.player = player;
        this.reader = new BufferedReader(new InputStreamReader(input));
        this.writer = new PrintWriter(output, true);
    }

    @Override
    public void notify(String gameState) {
        write(gameState);
    }

    @Override
    public String read() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            System.err.println("Error reading from player " + player.getName() + ": " + e.getMessage());
            return null;
        }
    }

    @Override
    public void write(String message) {
        writer.println(message);
        writer.flush();
    }

    public Player getPlayer() {
        return player;
    }

    public void close() {
        try {
            reader.close();
            writer.close();
            input.close();
            output.close();
        } catch (IOException e) {
            System.err.println("Error closing streams for player " + player.getName());
        }
    }
}