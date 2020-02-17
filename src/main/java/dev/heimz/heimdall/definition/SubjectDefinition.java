package dev.heimz.heimdall.definition;

import org.immutables.value.Value.Default;

interface SubjectDefinition {

  @Default
  default boolean user() {
    return true;
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
