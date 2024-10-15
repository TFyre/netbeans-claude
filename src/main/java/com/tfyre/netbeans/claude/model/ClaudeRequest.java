package com.tfyre.netbeans.claude.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import org.immutables.value.Value;

/**
 *
 * @author Francois Steyn - (fsteyn@tfyre.co.za)
 */
@Value.Immutable
@JsonSerialize(as = ImmutableClaudeRequest.class)
@JsonDeserialize(as = ImmutableClaudeRequest.class)
public interface ClaudeRequest {

    String model();

    @JsonProperty("max_tokens")
    @Value.Default
    default int maxTokens() {
        return 1000;
    }

    List<Message> messages();
}
