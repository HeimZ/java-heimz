package dev.heimz.heimdall.definition;

import dev.heimz.heimdall.policy.Rule;
import org.immutables.value.Value.Default;

interface RuleDefinition {

  @Default
  default Rule[] rules() {
    return new Rule[] {Rule.PERMIT};
  }
}
