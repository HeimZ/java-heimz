package dev.heimz.heimdall.model.entity;

import java.util.List;
import org.immutables.value.Value.Immutable;

@Immutable
public interface Role {

  String name();

  Application application();

  List<? extends Subject> subjects();

  List<Policy> policies();
}
