package dev.heimz.heimdall.definition;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import io.cucumber.java8.En;
import java.io.InputStream;
import java.util.Map;
import org.apache.commons.io.IOUtils;

public class ModelDefinitionSteps implements En {

  private InputStream modelInputStream;

  private Map<String, ModelDefinition> actualModelDefinitionMap;

  private ModelDefinition expectedModelDefinition;

  private Throwable throwable;

  public ModelDefinitionSteps() {
    DataTableType(ModelDefinitionBodies.MODEL_DEFINITION_DATA_TABLE);

    Given(
        "^The Heimdall Model is defined as below$",
        (String model) -> {
          modelInputStream = IOUtils.toInputStream(model);
        });

    When(
        "^The ModelDefinitionLoader loads the given Heimdall Model$",
        () -> {
          try {
            actualModelDefinitionMap = new ModelDefinitionLoader(modelInputStream).load();
          } catch (Throwable throwable) {
            this.throwable = throwable;
          }
        });

    Then(
        "^The ModelDefinitionException is thrown with following message$",
        (String message) -> {
          assertThat(throwable, notNullValue());
          assertThat(throwable, instanceOf(ModelDefinitionException.class));
          assertThat(throwable.getMessage(), equalTo(message));
        });

    Then(
        "^A ModelDefinition is loaded into a map with key \"([^\"]*)\" and following values$",
        (String key, ModelDefinition expectedModelDefinition) -> {
          assertThat(actualModelDefinitionMap.containsKey(key), equalTo(true));
          assertThat(actualModelDefinitionMap.get(key), equalTo(expectedModelDefinition));
        });
  }
}
