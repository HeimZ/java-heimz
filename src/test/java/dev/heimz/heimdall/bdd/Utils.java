package dev.heimz.heimdall.bdd;

import dev.heimz.heimdall.policy.Rule;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public interface Utils {

  static Rule[] getRulesFromString(final String input) {
    if (StringUtils.isBlank(input)) {
      return new Rule[] {};
    }
    return Arrays.stream(input.split("\\s*,\\s*"))
        .map(String::toUpperCase)
        .map(Rule::valueOf)
        .toArray(Rule[]::new);
  }
}
