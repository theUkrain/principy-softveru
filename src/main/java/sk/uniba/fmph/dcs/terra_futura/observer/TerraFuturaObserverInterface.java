package sk.uniba.fmph.dcs.terra_futura.observer;

/**
 * Interface for observers that receive game state notifications.
 */
public interface TerraFuturaObserverInterface {

    void notify(String gameState);
}