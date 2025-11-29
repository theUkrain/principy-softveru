package sk.uniba.fmph.dcs.terra_futura;

import java.util.Optional;

public class MoveCard {

    public boolean moveCard(Pile pile, GridPosition gridCoordinate, Grid grid) {

        int cardIndex = -1;
        for (int i = 0; i < 4; i++) {
            Optional<Card> card = pile.getCard(i);
            if (card.isPresent()) {
                cardIndex = i;
                break;
            }
        }

        if (cardIndex == -1) {
            return false; // No cards in pile
        }

        // Check if grid position is valid
        if (!grid.canPutCard(gridCoordinate)) {
            return false;
        }

        // Get the card (this doesn't remove it yet)
        Optional<Card> cardOpt = pile.getCard(cardIndex);
        if (cardOpt.isEmpty()) {
            return false;
        }

        Card card = cardOpt.get();

        // Now perform the actual operations
        // Take card from pile
        if (!pile.takeCard(cardIndex)) {
            return false;
        }

        // Put card on grid
        try {
            grid.putCard(gridCoordinate, card);
            return true;
        } catch (IllegalStateException e) {
            // If !grid.putCard - problem, card is already taken from pile
            return false;
        }
    }
}