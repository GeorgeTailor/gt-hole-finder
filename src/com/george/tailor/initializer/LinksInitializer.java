package com.george.tailor.initializer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.george.tailor.breaker.ContentBreaker;
import com.george.tailor.breaker.LoginBreaker;
import com.george.tailor.breaker.UploadBreaker;

public class LinksInitializer {
	
	public String domainName = "";
	
	Map<String, Boolean> globalMap = new LinkedHashMap<String, Boolean>();
	
	WebDriver driver = new FirefoxDriver();

	public void init (String url, int intention){

		driver = new FirefoxDriver();

		Map<String, Boolean> links = initializeLinkSearch(url);
		switch(intention){
			case 0:
				for (Map.Entry<String, Boolean> entry : links.entrySet()) {
					if(entry.getValue() && LoginBreaker.checkEligibility(entry.getKey(), driver)){
						LoginBreaker loginBreaker = new LoginBreaker();
						loginBreaker.loginBreakTry(entry.getKey(), driver, 0);
					}						
				}				
				break;
			case 1:
				for (Map.Entry<String, Boolean> entry : links.entrySet()) {
					if(entry.getValue() && UploadBreaker.checkEligibility(entry.getKey(), driver)){
						UploadBreaker uploadBreaker = new UploadBreaker();
						uploadBreaker.uploadBreakTry(entry.getKey(), driver);
					}						
				}
				break;
			case 2:
				for (Map.Entry<String, Boolean> entry : links.entrySet()) {
					if(entry.getValue() && ContentBreaker.checkEligibility(entry.getKey(), driver)){
						ContentBreaker contentBreaker = new ContentBreaker();
						contentBreaker.contentBreakTry(entry.getKey(), driver);
					}						
				}
				break;
			default:
				break;
		}
		/*for (Map.Entry<String, Boolean> entry : links.entrySet()) {
			System.out.println(entry.getKey() + " " + entry.getValue());
		}*/
	}
	
	public Map<String, Boolean> initializeLinkSearch(String currentPage) {
		domainName = currentPage;
		
		List<String> list = getHrefsList(driver, currentPage);
		
		recursiveLinksSearch(list, driver);

		return globalMap;
	}
	
	public void recursiveLinksSearch(List<String> list, WebDriver driver){
		for(String link : list){
			List<String> newList = getHrefsList(driver, link);
			recursiveLinksSearch(newList, driver);
		}
	}
	
	public List<String> getHrefsList(WebDriver driver, String currentPage){
		List<String> list = new ArrayList<String>();
		driver.get(currentPage);
		List<WebElement> webElements= driver.findElements(By.tagName("a"));
		
		for (WebElement webElement : webElements) {
			String currentUrl = webElement.getAttribute("href");
			if(currentUrl == null)
				continue;
			if (!checkExistance(globalMap, currentUrl)){
				if(currentUrl.contains(domainName)){
					list.add(currentUrl);
					globalMap.put(currentUrl, true);
				}else{
					globalMap.put(currentUrl, false);
				}											
			}
		}
		return list;
	}

	public Boolean checkExistance(Map<String, Boolean> links, String currentUrl) {
		for (Map.Entry<String, Boolean> entry : links.entrySet()) {
			if (entry.getKey().equals(currentUrl)){
				return true;				
			}
		}
		return false;
	}
}
