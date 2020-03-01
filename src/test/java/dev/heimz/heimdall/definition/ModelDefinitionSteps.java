package dev.heimz.heimdall.definition;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import io.cucumber.java8.En;
import java.io.InputStream;
import java.util.Map;
import org.apache.commons.io.IOUtils;

public class ModelDefinitionSteps implements En {

  private InputStream modelInputStream;

  private Map<String, ModelDefinition> actualModelDefinitions;

  private ModelDefinition expectedModelDefinition;

  private Throwable throwable;

  public ModelDefinitionSteps() {
    DataTableType(ModelDefinitionBodies.MODEL_DEFINITION_DATA_TABLE_ENTRY);

    Given(
        "^The Heimdall Model is defined as below$",
        (String model) -> {
          modelInputStream = IOUtils.toInputStream(model);
        });

    When(
        "^The ModelDefinitionLoader loads the given Heimdall Model$",
        () -> {
          try {
            actualModelDefinitions = new ModelDefinitionLoader(modelInputStream).load();
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
  }
}
