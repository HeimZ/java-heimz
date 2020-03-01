package dev.heimz.heimdall.definition;

import dev.heimz.heimdall.bdd.Utils;
import io.cucumber.java8.DataTableEntryDefinitionBody;
import java.util.Map;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;

interface ModelDefinitionBodies {

  DataTableEntryDefinitionBody<ModelDefinition> MODEL_DEFINITION_DATA_TABLE_ENTRY =
      (Map<String, String> row) ->
          ImmutableModelDefinition.builder()
              .role(BooleanUtils.toBoolean(row.get("role")))
              .roleHierarchy(BooleanUtils.toBoolean(row.get("roleHierarchy")))
              .maxRoleHierarchy(NumberUtils.toInt(row.get("maxRoleHierarchy")))
              .application(BooleanUtils.toBoolean(row.get("application")))
              .user(BooleanUtils.toBoolean(row.get("user")))
              .organization(BooleanUtils.toBoolean(row.get("organization")))
              .group(BooleanUtils.toBoolean(row.get("group")))
              .rules(Utils.getRulesFromString(row.get("rules")))
              .object(BooleanUtils.toBoolean(row.get("object")))
              .action(BooleanUtils.toBoolean(row.get("action")))
              .priority(BooleanUtils.toBoolean(row.get("priority")))
              .build();
}
