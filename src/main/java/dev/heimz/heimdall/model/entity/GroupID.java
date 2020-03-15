package dev.heimz.heimdall.model.entity;

import org.immutables.value.Value.Immutable;

@Immutable
public interface GroupID {

  String name();

  OrganizationID organizationID();
}
