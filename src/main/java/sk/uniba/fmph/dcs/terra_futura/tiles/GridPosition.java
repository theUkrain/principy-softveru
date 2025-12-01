package sk.uniba.fmph.dcs.terra_futura.tiles;

public class GridPosition {
        private byte x ;
        private byte y;
        public GridPosition(int x, int y) throws IllegalArgumentException {
            if(x < -2 || x > 2 || y < -2 || y > 2) throw new IllegalArgumentException("Position (" + x + "," + y + ") is out of bounds of grid.");
            this.x = (byte)x;
            this.y = (byte)y;
        }

        public int getX() {
            return (int)x;
        }

        public int getY() {
            return (int)y;
        }

        @Override
    public boolean equals(Object o) {
            if(!(o instanceof GridPosition)) return false;
            return ((GridPosition) o).getX() == getX() && getY() == ((GridPosition) o).getY();
        }
}
