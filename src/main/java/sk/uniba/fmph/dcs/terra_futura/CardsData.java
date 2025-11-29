package sk.uniba.fmph.dcs.terra_futura;

import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Deck;

public class CardsData {
    private CardsData() {
    }

    public static class GridPosition {
        private byte x;
        private byte y;

        public GridPosition(int x, int y) throws IllegalArgumentException {
            if (x < -2 || x > 2 || y < -2 || y > 2)
                throw new IllegalArgumentException("Position (" + x + "," + y + ") is out of bounds of grid.");
            this.x = (byte) x;
            this.y = (byte) y;
        }

        public int getX() {
            return (int) x;
        }

        public int getY() {
            return (int) y;
        }
    }

    public static class CardSource {
        private final byte index;
        private final Deck source;

        public CardSource(int index, Deck source) {
            this.index = (byte) index;
            this.source = source;
        }

        public int getIndex() {
            return (int) index;
        }

        public Deck getSourceDeck() {
            return source;
        }

    }
}
