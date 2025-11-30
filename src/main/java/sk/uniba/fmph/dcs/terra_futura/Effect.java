package sk.uniba.fmph.dcs.terra_futura;

import java.util.List;

/**
 * Interface for card effects.
 * Uses composite pattern for complex effect combinations.
 */
public interface Effect {

    /**
     * Check if transformation is valid for this effect.
     *
     * @param input List of input resources
     * @param output List of output resources
     * @param pollution Amount of pollution produced
     * @return true if transformation is valid, false otherwise
     */
    boolean check(List<Resource> input, List<Resource> output, int pollution);

    /**
     * Check if this effect has assistance capability.
     *
     * @return true if has assistance, false otherwise
     */
    boolean hasAssistance();

    /**
     * Get state of the effect as string.
     *
     * @return String representation of effect state
     */
    String state();
}