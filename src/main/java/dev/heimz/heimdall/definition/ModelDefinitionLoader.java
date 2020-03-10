package dev.heimz.heimdall.definition;

import dev.heimz.heimdall.policy.Rule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.parser.ParserException;
import org.yaml.snakeyaml.scanner.ScannerException;

import java.io.InputStream;
import java.util.*;

public class ModelDefinitionLoader {

  private static final Logger logger = LogManager.getLogger(ModelDefinitionLoader.class);

  private final String resourceName;

  private final InputStream configInputStream;

  public ModelDefinitionLoader() {
    this("heimdall.yml");
  }

  private ModelDefinitionLoader(String resourceName) {
    this(resourceName, loadFromResource(resourceName));
  }

  ModelDefinitionLoader(String resourceName, InputStream configInputStream) {
    this.resourceName = resourceName;
    this.configInputStream = configInputStream;
  }

  private static InputStream loadFromResource(String resourceName) {
    return ModelDefinitionLoader.class.getClassLoader().getResourceAsStream(resourceName);
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

  private static void throwModelDefinitionException(String message) {
    logger.error(message);
    throw new ModelDefinitionException(message);
  }

  private static void throwModelNotFoundException(String resourceName) {
    throwModelDefinitionException(
        String.format(
            "Heimdall model '%s' not found in classpath!", resourceName));
  }

  private static void throwBlankOrEmptyException() {
    throwModelDefinitionException("Provided Heimdall model is either blank or empty!");
  }

  private static void throwInvalidObjectException(String name, String expected, String actual) {
    throwModelDefinitionException(
        String.format("The %s object must be %s, but was: %s", name, expected, actual));
  }

  private static void throwInvalidObjectException(String name, String expected, Object object) {
    throwInvalidObjectException(name, expected, object, "");
  }

  private static void throwInvalidObjectException(
      String name, String expected, Object object, String actual) {
    throwModelDefinitionException(
        String.format(
            "The %s object must be %s, but was: %s%s",
            name, expected, object.getClass().getSimpleName(), actual));
  }

  public Map<String, ModelDefinition> load() {
    logger.info("Starting to load the 'heimdall.yml' model from classpath!");
    return load(true);
  }

  private Map<String, ModelDefinition> load(boolean fallback) {
    final Map<String, ModelDefinition> modelDefinitionMap = new HashMap<>();

    if (configInputStream == null) {
      if (fallback) {
        logger.debug(
            "Heimdall model '{}' not found in classpath. Falling back to standard model 'rbac'.",
            resourceName);

        final Map<String, ModelDefinition> rbacModelDefinition =
            new ModelDefinitionLoader("rbac.yml").load(false);
        modelDefinitionMap.put(
            "default",
            ImmutableModelDefinition.builder().from(rbacModelDefinition.get("rbac")).build());
      } else {
        throwModelNotFoundException(resourceName);
      }
    } else {
      loadModelDocument(modelDefinitionMap);
    }

    return modelDefinitionMap;
  }

  private void loadModelDocument(Map<String, ModelDefinition> modelDefinitionMap) {
    logger.debug("Loading model document '{}'", resourceName);

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
      throwModelDefinitionException(
          String.format(
              "Provided Heimdall model is a malformed YAML document!%n%s", ex.getMessage()));
    }
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

    logger.info("Loading model definition with identifier '{}'", modelIdentifier);

    final ImmutableModelDefinition.Builder builder = ImmutableModelDefinition.builder();

    // 'use' or 'policy'
    final Map<String, Object> modelMap = asKeyCaseInsensitiveMap("model", rootObject.getValue());
    boolean eitherUseOrPolicyDefined = false;
    if (modelMap.containsKey("use")) {
      eitherUseOrPolicyDefined = true;
      final String standardModelName = asString("use", modelMap.get("use"));
      logger.debug("Using standard model '{}'", standardModelName);
      final Map<String, ModelDefinition> standardModelDefinition =
          new ModelDefinitionLoader(standardModelName + ".yml").load(false);
      builder.from(standardModelDefinition.get(standardModelName));
    }
    if (modelMap.containsKey("policy")) {
      eitherUseOrPolicyDefined = true;
      loadPolicyDefinition(builder, modelMap.get("policy"));
    }
    if (!eitherUseOrPolicyDefined) {
      throwModelDefinitionException("Either 'use' or 'policy' object must be defined!");
    }

    modelDefinitionMap.put(modelIdentifier, builder.build());
  }

  private void loadPolicyDefinition(ImmutableModelDefinition.Builder builder, Object object) {
    logger.debug("Loading policy definition");

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
    logger.debug("Loading role definition");

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
    logger.debug("Loading subject definition");

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
    logger.debug("Loading user definition");

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
    logger.debug("Loading rule definition");

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
