package uk.co.hughpowell.payments;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/uk/co/hughpowell/features")
public class CucumberTest { }