package dev.heimz.heimdall.model.entity;

import org.immutables.value.Value.Immutable;

import java.util.List;

@Immutable
public interface Application {

    String name();

    List<Role> roles();
}
