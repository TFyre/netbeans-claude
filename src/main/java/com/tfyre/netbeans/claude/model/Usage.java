package com.tfyre.netbeans.claude.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

/**
 *
 * @author Francois Steyn - (fsteyn@tfyre.co.za)
 */
@Value.Immutable
@JsonSerialize(as = ImmutableUsage.class)
@JsonDeserialize(as = ImmutableUsage.class)
public interface Usage {

    @JsonProperty("input_tokens")
    int inputTokens();

    @JsonProperty("output_tokens")
    int outputTokens();
}
