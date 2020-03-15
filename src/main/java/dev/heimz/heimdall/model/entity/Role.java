package dev.heimz.heimdall.model.entity;

import org.immutables.value.Value.Immutable;

import java.util.List;

@Immutable
public interface Role {

    String name();

    Application application();

    List<? extends Subject> subjects();

    List<Policy> policies();
}
