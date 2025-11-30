package sk.uniba.fmph.dcs.terra_futura;

import java.util.ArrayList;
import java.util.List;

/**
 * Base implementation of Card class.
 * Manages resources on cards and pollution.
 */
public class Card {
    private final List<Resource> resources;
    private final int pollutionSpaces;
    private Effect upperEffect;
    private Effect lowerEffect;

    /**
     * Constructor.
     *
     * @param resources Initial resources on the card
     * @param pollutionSpaces Number of pollution spaces on card
     */
    public Card(List<Resource> resources, int pollutionSpaces) {
        this.resources = new ArrayList<>(resources);
        this.pollutionSpaces = pollutionSpaces;
        this.upperEffect = null;
        this.lowerEffect = null;
    }

    /**
     * Set upper effect.
     */
    public void setUpperEffect(Effect effect) {
        this.upperEffect = effect;
    }

    /**
     * Set lower effect.
     */
    public void setLowerEffect(Effect effect) {
        this.lowerEffect = effect;
    }

    /**
     * Check if resources can be taken from card.
     */
    public boolean canGetResources(List<Resource> resourcesToGet) {
        List<Resource> tempResources = new ArrayList<>(resources);

        for (Resource resource : resourcesToGet) {
            if (!tempResources.remove(resource)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Get resources from card (remove them).
     */
    public void getResources(List<Resource> resourcesToGet) {
        if (!canGetResources(resourcesToGet)) {
            throw new IllegalStateException("Cannot get these resources from card");
        }

        for (Resource resource : resourcesToGet) {
            resources.remove(resource);
        }
    }

    /**
     * Check if resources can be put on card.
     */
    public boolean canPutResources(List<Resource> resourcesToPut) {
        // Count pollution in resources to put
        long pollutionCount = resourcesToPut.stream()
                .filter(r -> r == Resource.Polution)
                .count();

        // Count existing pollution
        long existingPolution = resources.stream()
                .filter(r -> r == Resource.Polution)
                .count();

        // Check if pollution limit is exceeded
        if (existingPolution + pollutionCount > pollutionSpaces) {
            return false;
        }

        return true;
    }

    /**
     * Put resources on card (add them).
     */
    public void putResources(List<Resource> resourcesToPut) {
        if (!canPutResources(resourcesToPut)) {
            throw new IllegalStateException("Cannot put these resources on card");
        }

        resources.addAll(resourcesToPut);
    }

    /**
     * Check if transformation is valid using upper effect.
     */
    public boolean check(List<Resource> input, List<Resource> output, int pollution) {
        if (upperEffect == null) {
            return false;
        }
        return upperEffect.check(input, output, pollution);
    }

    /**
     * Check if transformation is valid using lower effect.
     */
    public boolean checkLower(List<Resource> input, List<Resource> output, int pollution) {
        if (lowerEffect == null) {
            return false;
        }
        return lowerEffect.check(input, output, pollution);
    }

    /**
     * Check if card has assistance capability.
     */
    public boolean hasAssistance() {
        if (upperEffect != null && upperEffect.hasAssistance()) {
            return true;
        }
        if (lowerEffect != null && lowerEffect.hasAssistance()) {
            return true;
        }
        return false;
    }

    /**
     * Get state of the card as string.
     */
    public String state() {
        StringBuilder sb = new StringBuilder();
        sb.append("Card[resources: [");

        for (int i = 0; i < resources.size(); i++) {
            sb.append(resources.get(i));
            if (i < resources.size() - 1) {
                sb.append(", ");
            }
        }

        sb.append("], pollution: ");
        long pollutionCount = resources.stream()
                .filter(r -> r == Resource.Polution)
                .count();
        sb.append(pollutionCount).append("/").append(pollutionSpaces);
        sb.append("]");

        return sb.toString();
    }

    /**
     * Get current resources on card.
     */
    public List<Resource> getResources() {
        return new ArrayList<>(resources);
    }

    /**
     * Get pollution spaces.
     */
    public int getPolutionSpaces() {
        return pollutionSpaces;
    }
}