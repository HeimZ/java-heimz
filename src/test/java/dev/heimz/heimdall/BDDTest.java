package dev.heimz.heimdall;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    strict = true,
    features = "src/test/resources",
    glue = {"dev.heimz.heimdall"})
public class BDDTest {}
