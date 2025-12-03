package sk.uniba.fmph.dcs.terra_futura.tiles;

import java.util.Optional;

public class MoveCard {

    /**
     * Move card from pile to grid
     * @param pile Source pile
     * @param index Index of card to take (0-3 for visible cards)
     * @param gridCoordinate Destination position on grid
     * @param grid Target grid
     * @return true if successful, false otherwise
     */
    public boolean moveCard(Pile pile, int index, GridPosition gridCoordinate, Grid grid) {
        if (!grid.canPutCard(gridCoordinate)) {
            return false;
        }

        // If this fails, card is already gone from pile
        Optional<Card> cardOpt;
        try {
            cardOpt = pile.getCard(index);
        } catch (ArrayIndexOutOfBoundsException e) {
            // Pile is completely empty (both allCards and discardPile)
            return false;
        }

        if (cardOpt.isEmpty()) {
            return false;
        }

        Card card = cardOpt.get();

        // Put card on grid
        try {
            grid.putCard(gridCoordinate, card);
            return true;
        } catch (IllegalStateException e) {
            System.err.println("ERROR: Card removed from pile but cannot be placed on grid: "
                    + e.getMessage());

            return false;
        }
    }

    /**
     * Alternative method with better atomicity guarantee
     * Checks grid validity more thoroughly before taking card
     *
     * @param pile Source pile
     * @param index Index of card to take
     * @param gridCoordinate Destination position on grid
     * @param grid Target grid
     * @return true if successful, false otherwise
     */
    public boolean moveCardSafe(Pile pile, int index, GridPosition gridCoordinate, Grid grid) {
        // Double-check grid can accept card
        if (!grid.canPutCard(gridCoordinate)) {
            return false;
        }

        // Validate grid coordinate is within bounds
        if (gridCoordinate.getX() < -2 || gridCoordinate.getX() > 2 ||
                gridCoordinate.getY() < -2 || gridCoordinate.getY() > 2) {
            return false;
        }

        // Now safe to take card
        return moveCard(pile, index, gridCoordinate, grid);
    }
}