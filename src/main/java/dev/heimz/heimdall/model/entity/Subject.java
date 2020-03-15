package dev.heimz.heimdall.model.entity;

import org.immutables.value.Value.Immutable;

import java.util.List;

@Immutable
public interface Subject {

    Organization organization();

    List<Role> roles();
}
