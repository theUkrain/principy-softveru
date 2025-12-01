package sk.uniba.fmph.dcs.terra_futura.tiles;

import sk.uniba.fmph.dcs.terra_futura.effects.*;
import sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects.Resource;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CardFactory {

    private CardFactory() {
    }

    ;

    public static Card card(int pollutionSpaces, Effect upper, Effect lower, CardSource cardSource) {
        ConcreteCard card = new ConcreteCard(pollutionSpaces, upper, lower, cardSource);
        return card;
    }

    public static Card startCard() {
        ConcreteCard card = new ConcreteCard(1, new StartingCardEffect(), null, null);
        return card;
    }


    private static class ConcreteCard implements Card {

        private Map<Resource, Integer> resources;

        private final int pollutionSpaces;
        private int curPollution;
        private final CardSource cardSource;

        private final Effect upper;
        private final Effect lower;

        private final boolean hasAssistance;

        public ConcreteCard(int pollutionSpaces, Effect upper, Effect lower, CardSource cardSource) {

            resources = new HashMap<>();

            this.upper = upper;
            this.lower = lower;

            this.pollutionSpaces = pollutionSpaces;
            this.hasAssistance = upper.canProvideAssistance() || lower.canProvideAssistance();
            this.cardSource = cardSource;

        }


        @Override
        public boolean isOverPolluted() {
            return curPollution > pollutionSpaces;
        }

        /**
         * @param resources, whose quantities are expected to be validated by this method.
         * @return true, if card is not overpolluted and have all resources listed in parameter resources available on this card.
         */
        @Override
        public boolean canGetResources(Map<Resource, Integer> resources) {

            if (isOverPolluted()) return false;

            for (Resource resource : resources.keySet()) {
                if (this.resources.getOrDefault(resource, 0) < resources.get(resource)) return false;
            }

            return true;

        }

        /**
         * Removes listed resources from this card.
         *
         * @param resources to be removed.
         * @throws IllegalArgumentException, if method canGetResources returns false with param resources.
         */
        @Override
        public void getResources(Map<Resource, Integer> resources) {

            if (!canPutResources(resources)) throw new IllegalArgumentException("Resources: " + "\n" + resources +
                    "\n" + "can't be get from card already filled with :" + "\n" + this.resources + "\n");

            for (Resource resource : resources.keySet()) {
                this.resources.put(resource, this.resources.get(resource) - resources.get(resource));
            }

            for (Resource resource : this.resources.keySet()) {
                this.resources.compute(resource, (key, number) -> {
                    if (number == 0) return null;
                    return number;
                });
            }

            curPollution = this.resources.getOrDefault(Resource.POLLUTION, 0);

        }

        /**
         * @return true, if isn't overpolluted.
         */
        @Override
        public boolean canPutResources(Map<Resource, Integer> resources) {

            if (isOverPolluted()) return false;

            if (curPollution + resources.getOrDefault(Resource.POLLUTION, 0) > pollutionSpaces) return false;

            return true;

        }

        /**
         * puts every resource listed in resources  on this card.
         *
         * @param resources to be putted on this card.
         * @throws IllegalArgumentException, if method canPutResources returns false with param resources.
         */
        @Override
        public void putResources(Map<Resource, Integer> resources) throws IllegalArgumentException {

            if (!canPutResources(resources)) throw new IllegalArgumentException("Resources: " + "\n" + resources +
                    "\n" + "can't be put on card already filled with :" + "\n" + this.resources + "\n" + "and  pollution  spaces in quantity of:"
                    + pollutionSpaces + '\n');


            for (Resource resource : resources.keySet()) {
                this.resources.putIfAbsent(resource, resources.get(resource));
                this.resources.put(resource, this.resources.get(resource) + resources.get(resource));
            }
            curPollution = this.resources.get(Resource.POLLUTION);

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
        public Effect getUpper() {
            return upper;
        }

        @Override
        public Effect getLower() {
            return lower;
        }

    }

}
