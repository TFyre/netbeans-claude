package com.tfyre.netbeans.claude.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

/**
 *
 * @author Francois Steyn - (fsteyn@tfyre.co.za)
 */
@Value.Immutable
@JsonSerialize(as = ImmutableContent.class)
@JsonDeserialize(as = ImmutableContent.class)
public interface Content {

    String type();

    String text();
}
