package sk.uniba.fmph.dcs.terra_futura.observer;

// input output to connect + method from input take string + method output string
/**
 * Interface for observers that receive game state notifications.
 */
public interface TerraFuturaObserverInterface {

    void notify(String gameState);

    String read();

    void write(String message);

    void close();
}
