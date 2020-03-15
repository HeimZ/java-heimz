package dev.heimz.heimdall.model.entity;

import java.util.List;
import org.immutables.value.Value.Immutable;

@Immutable
public interface Organization {

  OrganizationID id();

  List<User> users();

  List<Group> groups();
}
