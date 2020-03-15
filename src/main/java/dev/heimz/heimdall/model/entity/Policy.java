package dev.heimz.heimdall.model.entity;

import org.immutables.value.Value.Immutable;

@Immutable
public interface Policy {

  Role role();

  String object();

  String action();

  Rule rule();

  Priority priority();
}
