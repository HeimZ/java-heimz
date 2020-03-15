package dev.heimz.heimdall.model.entity;

import java.util.List;
import org.immutables.value.Value.Immutable;

@Immutable
public interface Application {

  String name();

  List<Role> roles();
}