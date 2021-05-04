package com.bham.bc.components.triggers;

public enum TriggerType {
    HEALTH_GIVER(TriggerGroup.POWERUP),
    IMMUNE(TriggerGroup.POWERUP),
    TRIPLE_BULLETS(TriggerGroup.POWERUP),
    FREEZE(TriggerGroup.TRAP),
    INVERSE_TRAP(TriggerGroup.TRAP);

    /** Represents 3 possible rigger groups: {@code POWERUP}, {@code TRAP} and {@code EFFECT} */
    public enum TriggerGroup { POWERUP, TRAP, EFFECT }

    /** Trigger group this trigger type belongs to */
    public final TriggerGroup GROUP;

    /**
     * Constructs a trigger type enum
     * @param triggerGroup {@code POWERUP}, {@code TRAP} or {@code EFFECT} group this trigger type should belong to
     */
    TriggerType(TriggerGroup triggerGroup) {
        GROUP = triggerGroup;
    }
}
