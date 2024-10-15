package com.tfyre.netbeans.claude.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import java.util.Optional;
import org.immutables.value.Value;

/**
 *
 * @author Francois Steyn - (fsteyn@tfyre.co.za)
 */
@Value.Immutable
@JsonSerialize(as = ImmutableClaudeResponse.class)
@JsonDeserialize(as = ImmutableClaudeResponse.class)
public interface ClaudeResponse {

    String id();

    String type();

    String role();

    List<Content> content();

    String model();

    @JsonProperty("stop_reason")
    StopReason stopReason();

    @JsonProperty("stop_sequence")
    Optional<String> stopSequence();

    Usage usage();
}
