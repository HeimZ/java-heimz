package dev.heimz.heimdall.model.entity;

import org.immutables.value.Value.Immutable;

@Immutable
public interface User extends Subject {

  UserID id();
}
