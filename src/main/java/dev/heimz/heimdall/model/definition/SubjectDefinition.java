package dev.heimz.heimdall.model.definition;

import org.immutables.value.Value.Default;

interface SubjectDefinition {

  @Default
  default boolean user() {
    return false;
  }

  @Default
  default boolean organization() {
    return false;
  }

  @Default
  default boolean group() {
    return false;
  }
}
