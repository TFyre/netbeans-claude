package com.tfyre.netbeans.claude;

/**
 *
 * @author Francois Steyn - (fsteyn@tfyre.co.za)
 */
public enum ClaudeModel {
    CLAUDE_3_OPUS_20240229("claude-3-opus-20240229"),
    CLAUDE_3_SONNET_20240229("claude-3-sonnet-20240229"),
    CLAUDE_3_HAIKU_20240307("claude-3-haiku-20240307");

    private final String apiValue;

    ClaudeModel(String apiValue) {
        this.apiValue = apiValue;
    }

    public String getApiValue() {
        return apiValue;
    }

    public static ClaudeModel fromApiValue(String apiValue) {
        for (ClaudeModel model : values()) {
            if (model.getApiValue().equals(apiValue)) {
                return model;
            }
        }
        throw new IllegalArgumentException("Unknown API value: " + apiValue);
    }
}
