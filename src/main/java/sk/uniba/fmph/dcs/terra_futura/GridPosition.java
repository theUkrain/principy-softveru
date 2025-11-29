package sk.uniba.fmph.dcs.terra_futura;

public record GridPosition(int x, int y) {
    public GridPosition {
        if (x < -2 || x > 2 || y < -2 || y > 2) {
            throw new IllegalArgumentException("Coordinates must be between -2 and 2");
        }
    }
}
