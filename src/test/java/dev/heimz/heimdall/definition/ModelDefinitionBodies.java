package dev.heimz.heimdall.definition;

import dev.heimz.heimdall.bdd.Utils;
import io.cucumber.datatable.DataTable;
import io.cucumber.java8.DataTableDefinitionBody;
import io.cucumber.java8.DataTableEntryDefinitionBody;
import java.util.List;
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

  DataTableDefinitionBody<ModelDefinition> MODEL_DEFINITION_DATA_TABLE =
      (DataTable dataTable) -> {
        final List<List<String>> data = dataTable.asLists();
        final ImmutableModelDefinition.Builder builder = ImmutableModelDefinition.builder();
        for (List<String> row : data) {
          final String fieldName = row.get(0);
          final String fieldValue = row.get(1);
          switch (fieldName) {
            case "role":
              builder.role(BooleanUtils.toBoolean(fieldValue));
              break;

            case "roleHierarchy":
              builder.roleHierarchy(BooleanUtils.toBoolean(fieldValue));
              break;

            case "maxRoleHierarchy":
              builder.maxRoleHierarchy(NumberUtils.toInt(fieldValue));
              break;

            case "application":
              builder.application(BooleanUtils.toBoolean(fieldValue));
              break;

            case "user":
              builder.user(BooleanUtils.toBoolean(fieldValue));
              break;

            case "organization":
              builder.organization(BooleanUtils.toBoolean(fieldValue));
              break;

            case "group":
              builder.group(BooleanUtils.toBoolean(fieldValue));
              break;

            case "rules":
              builder.rules(Utils.getRulesFromString(fieldValue));
              break;

            case "object":
              builder.object(BooleanUtils.toBoolean(fieldValue));
              break;

            case "action":
              builder.action(BooleanUtils.toBoolean(fieldValue));
              break;

            case "priority":
              builder.priority(BooleanUtils.toBoolean(fieldValue));
              break;
          }
        }
        return builder.build();
      };
}
