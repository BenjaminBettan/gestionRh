package com.bbe.theatre;

import org.junit.Assert;

import cucumber.api.java.en.*;

public class StepDefinitions {
	
String s="";

@Given("^je charge \"(.*?)\"$")
public void je_charge(String arg1) throws Throwable {
    s+=arg1;
}

@When("^je me repose (\\d+) secondes$")
public void je_me_repose_secondes(int arg1) throws Throwable {
    Thread.sleep(arg1*1000);
}

@Then("^je dois trouver \"(.*?)\"$")
public void je_dois_trouver(String arg1) throws Throwable {
    Assert.assertTrue(s.equals(arg1));
}

}
