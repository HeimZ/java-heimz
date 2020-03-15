package dev.heimz.heimdall.model.definition;

import dev.heimz.heimdall.model.entity.Rule;
import org.immutables.value.Value.Default;

interface RuleDefinition {

  @Default
  default Rule[] rules() {
    return new Rule[] {Rule.PERMIT};
  }
}
