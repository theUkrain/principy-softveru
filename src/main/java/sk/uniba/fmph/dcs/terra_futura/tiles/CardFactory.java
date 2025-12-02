package sk.uniba.fmph.dcs.terra_futura.tiles;

import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Deck;
import sk.uniba.fmph.dcs.terra_futura.effects.*;
import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CardFactory {

    private CardFactory() {};

    private static int maxI;
    private static int maxII;

    private  static int counterI;
    private static int counterII;

    static {
        counterI = 0;
        counterII = 0;

        maxI = 23;
        maxII = 24;
    }

    public static Card card(int pollutionSpaces, SetCardToEffect upper, SetCardToEffect lower, CardSource cardSource) {

        if( cardSource.getSourceDeck() == Deck.I &&  maxI == counterI) throw new ArrayIndexOutOfBoundsException("With " + counterI
                + " cards of I level produced, you cannot produce more cards");
        if( cardSource.getSourceDeck() == Deck.II &&  maxII == counterII) throw new ArrayIndexOutOfBoundsException("With " + counterII
                + " cards of II level produced, you cannot produce more cards");

        if(cardSource.getSourceDeck() == Deck.I) counterI++;
        if(cardSource.getSourceDeck() == Deck.II) counterII++;


        ConcreteCard card  = new ConcreteCard(pollutionSpaces,  upper, lower, cardSource);
        return card;
    }

    public static Card pollutionTransferCard(CardSource cardSource) {
        Card card = card(4, new PollutionTransfer(), null, cardSource);
        return card;
    }

    public static Card startCard() {
        ConcreteCard card = new ConcreteCard(1, new EffectOr(new RawMaterialProducer(Resource.UNIVERSAL), new RawMaterialProducer(Resource.MONEY)), new AssistanceEffect(), null);
        return card;
    }

    public static void reset() {
        counterI = 0;
        counterII = 0;
    }

    private static class ConcreteCard implements Card {

        private Map<Resource, Integer> resources;

        private final int pollutionSpaces;
        private int curPollution;
        private final CardSource cardSource;

        private final SetCardToEffect upper;
        private final SetCardToEffect lower;

        private final boolean hasAssistance;

        public ConcreteCard(int pollutionSpaces, SetCardToEffect upper, SetCardToEffect lower, CardSource cardSource) {

            resources = new HashMap<>();

            this.upper = upper;
            this.lower = lower;

            if (upper != null) {
                this.upper.setCard(this);
            }

            if (lower != null) {
                this.lower.setCard(this);
            }

            this.pollutionSpaces = pollutionSpaces;

            this.hasAssistance = (upper != null &&  upper.canProvideAssistance()) || (lower != null &&  lower.canProvideAssistance());
            this.cardSource = cardSource;

        }

        public Map<Resource, Integer> getCurResources() {
            System.out.println(curPollution);
            return Collections.unmodifiableMap(this.resources);
        }


        public boolean isOverPolluted() {
            return curPollution >= pollutionSpaces;
        }

        /**
         * @param resources, whose quantities are expected to be validated by this method.
         * @return true, if card is not overpolluted and have all resources listed in parameter resources available on this card.
         */
        @Override
        public boolean canGetResources(Map<Resource, Integer> resources)  {

            if(isOverPolluted()) return false;

            for(Resource resource : resources.keySet()) {
                if(this.resources.getOrDefault(resource, 0) < resources.get(resource)) return false;
            }

            return true;

        }

        /**
         * Removes listed resources from this card.
         * @param resources to be removed.
         * @throws IllegalArgumentException, if method canGetResources returns false with param resources.
         */
        @Override
        public void getResources(Map<Resource, Integer> resources) {

            if(!canGetResources(resources)) throw new IllegalArgumentException("Resources: " + "\n" + resources.toString() +
                    "\n" +  "can't be get from card already filled with :" + "\n"  + this.resources + "\n");

            for(Resource resource : resources.keySet()) {
                this.resources.put(resource, this.resources.getOrDefault(resource, 0) - resources.get(resource));
            }

            this.resources.entrySet().removeIf(e -> e.getValue() == 0);

        }

        /**
         *
         * @return true, if isn't overpolluted.
         */
        @Override
        public boolean canPutResources(Map<Resource, Integer> resources) {


            if(isOverPolluted()) return false;

            if(this.resources.keySet().contains(Resource.POLLUTION)) return false;

            return true;

        }

        /**
         * puts every resource listed in resources  on this card.
         * @param resources to be putted on this card.
         *  @throws IllegalArgumentException, if method canPutResources returns false with param resources.
         */
        @Override
        public void putResources(Map<Resource, Integer> resources) throws IllegalArgumentException {

            if(!canPutResources(resources)) throw new IllegalArgumentException("Resources: " + "\n" + resources.toString() +
                    "\n" +  "can't be put on card already filled with :" + "\n"  + this.resources.toString() + "\n" +
                    "and  pollution  spaces in quantity of:" + pollutionSpaces + '\n');

            for(Resource resource : resources.keySet()) {

                if(!this.resources.keySet().contains(resource)) this.resources.put(resource, resources.get(resource));
                else this.resources.put(resource, this.resources.get(resource)+resources.get(resource));
            }

        }

        @Override
        public Effect getUpper() {
            return upper;
        }

        @Override
        public Effect getLower() {
            return lower;
        }

        @Override
        public boolean canGetPollution(int amount) {
            return curPollution >= amount;
        }

        @Override
        public void getPollution(int amount) {
            if (!canGetPollution(amount)) throw new IllegalArgumentException("Card only has " + curPollution
                    + " pollution, you are trying to take " + amount
                    + " pollution" + '\n');
            curPollution -= amount;
        }

        @Override
        public boolean canPutPollution(int amount) {
            return curPollution + amount <= pollutionSpaces;
        }

        @Override
        public void putPollution(int amount) {
            if(!canPutPollution(amount)) throw new IllegalArgumentException("You can't put " + amount +
                    " pollution on card with " + (pollutionSpaces - curPollution)  + " free pollution spaces");
            curPollution += amount;
        }

        @Override
        public boolean hasAssistance() {
            return hasAssistance;
        }

        @Override
        public CardSource getCardSource() {
            return cardSource;
        }

        @Override
        public String toString() {
            return "resources: " + this.resources.toString() + '\n' +
                    "pollution spaces: " + pollutionSpaces + '\n' +
                    "upper effect: " + ( upper != null ? upper.toString() : "none") + '\n' +
                    "lower effect: " + ( lower != null ? lower.toString() : "none") + '\n' +
                    "given index : " + cardSource.getIndex() + '\n' +
                    "source deck: " + cardSource.getSourceDeck() + '\n';
        }

        @Override
        public Map<Resource, Integer> takeResources() {
            return resources;
        }
    }
}
