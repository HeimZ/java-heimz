package dev.heimz.heimdall.definition;

import dev.heimz.heimdall.policy.Rule;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.parser.ParserException;
import org.yaml.snakeyaml.scanner.ScannerException;

import java.io.InputStream;
import java.util.*;

public class ModelDefinitionLoader {

  private final InputStream configInputStream;

  public ModelDefinitionLoader() {
    this(loadFromResources());
  }

  ModelDefinitionLoader(InputStream configInputStream) {
    this.configInputStream = configInputStream;
  }

  private static InputStream loadFromResources() {
    return ModelDefinitionLoader.class.getClassLoader().getResourceAsStream("heimdall.yml");
  }

  private static String asString(String name, Object object) {
    if (object instanceof String) {
      return ((String) object).toLowerCase();
    }
    return asKeyCaseInsensitiveSingleKeyMap(name, object).keySet().iterator().next();
  }

  private static List<Object> asSequence(String name, Object object) {
    if (!(object instanceof List) || ((List<Object>) object).isEmpty()) {
      throwInvalidObjectException(name, "a sequence of objects with at least one object", object);
    }
    return (List<Object>) object;
  }

  private static Map<String, Object> asKeyCaseInsensitiveSingleKeyMap(String name, Object object) {
    final Map<String, Object> map = asKeyCaseInsensitiveMap(name, object);
    if (map.size() != 1) {
      throwInvalidObjectException(
          name, "a map with only one key", object, " with " + map.size() + " keys");
    }
    return map;
  }

  private static Map<String, Object> asKeyCaseInsensitiveMap(String name, Object object) {
    if (!(object instanceof Map) || ((Map<String, Object>) object).isEmpty()) {
      throwInvalidObjectException(name, "a map with at least one key", object);
    }
    final Map<String, Object> map = new HashMap<>();
    for (Map.Entry<String, Object> entry : ((Map<String, Object>) object).entrySet()) {
      map.put(entry.getKey() == null ? null : entry.getKey().toLowerCase(), entry.getValue());
    }
    return Collections.unmodifiableMap(map);
  }

  private static void throwBlankOrEmptyException() {
    throw new ModelDefinitionException("Provided Heimdall model is either blank or empty!");
  }

  private static void throwInvalidObjectException(String name, String expected, String actual) {
    throw new ModelDefinitionException(
        String.format("The %s object must be %s, but was: %s", name, expected, actual));
  }

  private static void throwInvalidObjectException(String name, String expected, Object object) {
    throwInvalidObjectException(name, expected, object, "");
  }

  private static void throwInvalidObjectException(
      String name, String expected, Object object, String actual) {
    throw new ModelDefinitionException(
        String.format(
            "The %s object must be %s, but was: %s%s",
            name, expected, object.getClass().getSimpleName(), actual));
  }

  public Map<String, ModelDefinition> load() {
    final Map<String, ModelDefinition> modelDefinitionMap = new HashMap<>();

    if (configInputStream == null) {
      // TODO: 29/02/20 Replace with RBAC standard
      modelDefinitionMap.put("default", ImmutableModelDefinition.builder().build());
    }

    final Yaml yaml = new Yaml();

    try {
      final Iterable<Object> modelDocuments = yaml.loadAll(configInputStream);
      final Iterator<Object> modelDocumentsIterator = modelDocuments.iterator();

      if (modelDocumentsIterator.hasNext()) {
        while (modelDocumentsIterator.hasNext()) {
          loadModelDefinition(modelDefinitionMap, modelDocumentsIterator.next());
        }
      } else {
        throwBlankOrEmptyException();
      }
    } catch (ParserException | ScannerException ex) {
      throw new ModelDefinitionException(
          String.format(
              "Provided Heimdall model is a malformed YAML document!%n%s", ex.getMessage()));
    }

    return modelDefinitionMap;
  }

  private void loadModelDefinition(
      Map<String, ModelDefinition> modelDefinitionMap, Object modelDocumentObject) {
    if (modelDocumentObject == null) {
      throwBlankOrEmptyException();
    }

    // 'model'
    final Map<String, Object> rootObjectMap =
        asKeyCaseInsensitiveSingleKeyMap("root", modelDocumentObject);
    final Map.Entry<String, Object> rootObject = rootObjectMap.entrySet().iterator().next();
    final String modelIdentifier = rootObject.getKey();

    final ImmutableModelDefinition.Builder builder = ImmutableModelDefinition.builder();

    // 'use' or 'policy'
    final Map<String, Object> modelMap = asKeyCaseInsensitiveMap("model", rootObject.getValue());
    boolean eitherUseOrPolicyDefined = false;
    if (modelMap.containsKey("use")) {
      eitherUseOrPolicyDefined = true;
      throw new ModelDefinitionException("Using standard models is not supported yet!");
    }
    if (modelMap.containsKey("policy")) {
      eitherUseOrPolicyDefined = true;
      loadPolicyDefinition(builder, modelMap.get("policy"));
    }
    if (!eitherUseOrPolicyDefined) {
      throw new ModelDefinitionException("Either 'use' or 'policy' object must be defined!");
    }

    modelDefinitionMap.put(modelIdentifier, builder.build());
  }

  private void loadPolicyDefinition(ImmutableModelDefinition.Builder builder, Object object) {
    final List<Object> policyItems = asSequence("policy", object);

    for (int i = 1; i <= policyItems.size(); i++) {
      final Object policyItem = policyItems.get(i - 1);
      final String itemName = asString("policy-item-" + i, policyItem);
      switch (itemName) {
        case "object":
          builder.object(true);
          break;

        case "action":
          builder.action(true);
          break;

        case "priority":
          builder.priority(true);
          break;

        case "role":
          loadRoleDefinition(builder, policyItem);
          break;

        case "subject":
          loadSubjectDefinition(builder, policyItem);
          break;

        case "rule":
          loadRuleDefinition(builder, policyItem);
          break;
      }
    }
  }

  private void loadRoleDefinition(ImmutableModelDefinition.Builder builder, Object object) {
    builder.role(true);
    if (!(object instanceof String)) {
      final List<Object> roleItems =
          asSequence("role", asKeyCaseInsensitiveSingleKeyMap("role", object).get("role"));
      for (int i = 1; i <= roleItems.size(); i++) {
        final Object roleItem = roleItems.get(i - 1);
        final String itemName = asString("role-item-" + i, roleItem);
        switch (itemName) {
          case "hierarchy":
            builder.roleHierarchy(true);
            if (!(roleItem instanceof String)) {
              final Object maxRoleHierarchyValueObject =
                  asKeyCaseInsensitiveSingleKeyMap("hierarchy", roleItem).get("hierarchy");
              if (maxRoleHierarchyValueObject instanceof Integer) {
                builder.maxRoleHierarchy(Math.abs((Integer) maxRoleHierarchyValueObject));
              }
            }
            break;

          case "application":
            builder.application(true);
            break;
        }
      }
    }
  }

  private void loadSubjectDefinition(ImmutableModelDefinition.Builder builder, Object object) {
    final List<Object> subjectItems =
        asSequence("subject", asKeyCaseInsensitiveSingleKeyMap("subject", object).get("subject"));
    for (int i = 1; i <= subjectItems.size(); i++) {
      final Object subjectItem = subjectItems.get(i - 1);
      final String itemName = asString("subject-item-" + i, subjectItem);
      switch (itemName) {
        case "user":
          loadUserDefinition(builder, subjectItem);
          break;

        case "group":
          builder.group(true);
          break;
      }
    }
  }

  private void loadUserDefinition(ImmutableModelDefinition.Builder builder, Object object) {
    builder.user(true);
    if (!(object instanceof String)) {
      final List<Object> userItems =
          asSequence("user", asKeyCaseInsensitiveSingleKeyMap("user", object).get("user"));
      for (int i = 1; i <= userItems.size(); i++) {
        final Object userItem = userItems.get(i - 1);
        final String itemName = asString("user-item-" + i, userItem);
        if ("organization".equals(itemName)) {
          builder.organization(true);
        }
      }
    }
  }

  private void loadRuleDefinition(ImmutableModelDefinition.Builder builder, Object object) {
    final List<Object> ruleItems =
        asSequence("rule", asKeyCaseInsensitiveSingleKeyMap("rule", object).get("rule"));
    final Rule[] rules = new Rule[ruleItems.size()];
    for (int i = 1; i <= ruleItems.size(); i++) {
      final Object ruleItem = ruleItems.get(i - 1);
      if (!(ruleItem instanceof String)) {
        throwInvalidObjectException("rule-item-" + i, "string", ruleItem);
      }
      try {
        rules[i - 1] = Rule.valueOf(((String) ruleItem).toUpperCase());
      } catch (IllegalArgumentException ex) {
        throwInvalidObjectException(
            "rule-item-" + i, "one of [permit, recommend, oblige, prohibit]", (String) ruleItem);
      }
    }
    builder.rules(rules);
  }
}
