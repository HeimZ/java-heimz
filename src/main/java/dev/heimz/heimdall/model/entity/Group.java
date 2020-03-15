package dev.heimz.heimdall.model.entity;

import org.immutables.value.Value.Immutable;

import java.util.List;

@Immutable
public interface Group extends Subject {

  GroupID id();

  List<? extends Subject> subjects();
}
