package dev.heimz.heimdall.model.entity;

import java.util.List;
import org.immutables.value.Value.Immutable;

@Immutable
public interface Group extends Subject {

  GroupID id();

  List<? extends Subject> subjects();
}
