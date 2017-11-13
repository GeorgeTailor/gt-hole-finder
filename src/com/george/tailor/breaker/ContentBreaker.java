package com.george.tailor.breaker;

import org.openqa.selenium.WebDriver;

public class ContentBreaker {
	
	public static Boolean checkEligibility(String url, WebDriver driver){
		driver.get(url);
		if(driver.getCurrentUrl().contains("?")){
			return true;
		}
		return false;
	}
	
	public void contentBreakTry(String url, WebDriver driver){
		driver.get(url);
		
		//tryUpload(fileWebElement, submitWebElement, driver);
	}
}
