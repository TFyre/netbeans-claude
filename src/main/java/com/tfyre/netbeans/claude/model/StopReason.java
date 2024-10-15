package com.tfyre.netbeans.claude.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 *
 * @author Francois Steyn - (fsteyn@tfyre.co.za)
 */
public enum StopReason {
    END_TURN("end_turn"),
    MAX_TOKENS("max_tokens"),
    STOP_SEQUENCE("stop_sequence"),
    TOOL_USE("tool_use");

    private final String value;

    StopReason(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static StopReason fromValue(String value) {
        for (StopReason reason : values()) {
            if (reason.value.equals(value)) {
                return reason;
            }
        }
        throw new IllegalArgumentException("Unknown StopReason: " + value);
    }
}
