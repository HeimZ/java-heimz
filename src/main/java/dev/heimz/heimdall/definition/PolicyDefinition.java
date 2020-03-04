package dev.heimz.heimdall.definition;

import org.immutables.value.Value.Default;

interface PolicyDefinition extends RoleDefinition, SubjectDefinition, RuleDefinition {

  @Default
  default boolean object() {
    return false;
  }

  @Default
  default boolean action() {
    return false;
  }

  @Default
  default boolean priority() {
    return false;
  }
}
