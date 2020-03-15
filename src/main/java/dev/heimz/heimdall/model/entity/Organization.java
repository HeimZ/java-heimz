package dev.heimz.heimdall.model.entity;

import org.immutables.value.Value.Immutable;

import java.util.List;

@Immutable
public interface Organization {

    OrganizationID id();

    List<User> users();

    List<Group> groups();
}
