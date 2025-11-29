package sk.uniba.fmph.dcs.terra_futura;

import java.util.HashMap;
import java.util.Map;

public class GameObserver {
    private final Map<Integer, TerraFuturaObserverInterface> observers;

    public GameObserver() {
        this.observers = new HashMap<>();
    }

    public void registerObserver(int playerId, TerraFuturaObserverInterface observer) {
        observers.put(playerId, observer);
    }

    public void unregisterObserver(int playerId) {
        observers.remove(playerId);
    }

    public void notifyAll(Map<Integer, String> newState) {
        for (Map.Entry<Integer, String> entry : newState.entrySet()) {
            int playerId = entry.getKey();
            String state = entry.getValue();

            TerraFuturaObserverInterface observer = observers.get(playerId);
            if (observer != null) {
                observer.notify(state);
            }
        }
    }

    public void notifyPlayer(int playerId, String state) {
        TerraFuturaObserverInterface observer = observers.get(playerId);
        if (observer != null) {
            observer.notify(state);
        }
    }

    public boolean hasObserver(int playerId) {
        return observers.containsKey(playerId);
    }

    public int getObserverCount() {
        return observers.size();
    }
}