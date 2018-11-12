package scrapingforlife;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import scrapingforlife.configuration.DriverFactory;

public class TestScrap implements Runnable {

	private SearchModel searchModel;
	private Path peakListPath;
	private DriverFactory driverFactory;
	private String[][] peptideCombinations = {
			{"Da", "1.2"},
			{"Da", "1.0"},
			{"Da", "0.8"},
			{"Da", "0.6"},
			{"Da", "0.4"},
			{"Da", "0.2"},
			{"ppm", "100"},
			{"ppm", "200"}
	};
                      
	public int SignificScore;

	public TestScrap(SearchModel searchModel, Path peakListPath) {
		this.driverFactory =  new DriverFactory();
		this.searchModel = searchModel;
		this.peakListPath = peakListPath;
	}

	@Override
	public void run() {
        
        final RemoteWebDriver webDriver = driverFactory.getDriver();
    	
		for(int i = 0; i < this.peptideCombinations.length; i++) {
			
			webDriver.get("http://www.matrixscience.com/cgi/search_form.pl?FORMVER=2&SEARCH=PMF");
			completeQueryParameters(webDriver, i);
			
			// Waiting 10 seconds to get result page correctly instead processing page
                        try{
			WebDriverWait wait = new WebDriverWait(webDriver, 
                                                               Integer.parseInt(TestThread.getProp().getProperty("loading.wait.time")));
			wait.until(ExpectedConditions.urlContains("http://www.matrixscience.com/cgi/master_results.pl"));
                        } catch(IOException e){}
			
			// Getting fields to be save	    
			writeResults(webDriver, i);
			webDriver.manage().deleteAllCookies();
		}
		
		webDriver.quit();
	}

	private void writeResults(final RemoteWebDriver driver, final Integer i) {
		final List<WebElement> resultHeader = driver.findElementsByXPath("/html/body/font[1]/pre//b");
                
		final Object SignificScoreDetail = driver.executeScript("return document.querySelector(\"body > br:nth-child(4)\", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).nextSibling.nodeValue");
                final int SignificScore = Integer.parseInt(SignificScoreDetail.toString().substring(0,30).replaceAll("[^0-9]", ""));
                
                final WebElement Score = driver.findElementByXPath("/html/body/font[1]/pre/b[8]");
		final int TopScore = Integer.parseInt(Score.getText().substring(0, 21).replaceAll("[^0-9]", ""));
                
                final String Significance;
                
                    if(TopScore >= SignificScore){
                        Significance = "!SIGNIFICANT!";
                    }else{
                        Significance = "Insignificant";
                    }
                   
                
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.searchModel.getPeakListResultPath() + //file title
				this.peakListPath.getFileName().toString() + 
				"_" + this.peptideCombinations[i][1] +
				"_" + this.peptideCombinations[i][0] +
				"_" + "Score" + "_" + Significance + ".txt"))){
			for(final WebElement header : resultHeader) {
				writer.write(header.getText());
				writer.newLine();
			}
                        writer.write((String) "Siginificance minimum Score: " + String.valueOf(SignificScore));
                        writer.newLine();
                        
			writer.newLine();
			buildProteinDetails(driver, writer);                   
                        
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
        //Web Testing Controls
	private void completeQueryParameters(final RemoteWebDriver driver, int i) {
		final Select dataBaseSelect = new Select(driver.findElement(By.name("DB")));
		dataBaseSelect.selectByValue(searchModel.getDatabase());
		dataBaseSelect.deselectByValue("contaminants");
		
		
		if(driver.findElement(By.id("USERNAME")).getText().isEmpty()) {
			driver.findElement(By.id("USERNAME")).sendKeys(searchModel.getName());
		}
		
		if(driver.findElement(By.id("USEREMAIL")).getText().isEmpty()) {
			driver.findElement(By.id("USEREMAIL")).sendKeys(searchModel.getEmail());
		}
                
                final Select taxonomySelect = new Select(driver.findElement(By.name("TAXONOMY")));
                        taxonomySelect.selectByValue(searchModel.getTaxonomy());
                
                final Select ModsSelect = new Select(driver.findElementByName("MASTER_MODS"));
                        
                        ModsSelect.selectByValue("Carbamidomethyl (C)");
                        driver.findElement(By.name("add_MODS")).click();                        
                   
                        ModsSelect.selectByValue("Oxidation (M)");
                        driver.findElement(By.name("add_IT_MODS")).click();
                
                
              
		
		final Select unitySelect = new Select(driver.findElement(By.name("TOLU")));
		unitySelect.selectByValue(this.peptideCombinations[i][0]);
		
		driver.findElement(By.name("TOL")).sendKeys(Keys.CONTROL + "a");
		driver.findElement(By.name("TOL")).sendKeys(Keys.DELETE);
		driver.findElement(By.name("TOL")).sendKeys(this.peptideCombinations[i][1]);
		
		driver.findElement(By.id("InputRadio-DATAFILE")).click();
		driver.findElement(By.id("InputSource-DATAFILE")).sendKeys(peakListPath.toAbsolutePath().toString());
		
		driver.findElement(By.id("Start_Search_Button")).submit();
	}

	private void buildProteinDetails(final RemoteWebDriver driver, final BufferedWriter writer) {
		final List<WebElement> resultProteinIds = driver.findElementsByXPath("/html/body/form[2]/table/tbody/tr[1]/td[2]/tt/a");
		final List<String> proteinDetailsUrls = resultProteinIds.stream()
				.map(element -> {return element.getAttribute("href");}).collect(Collectors.toList());
		for(final String proteinDetailUrl : proteinDetailsUrls) {
			driver.navigate().to(proteinDetailUrl);
			final List<WebElement> proteinDetails = driver.findElementsByXPath("/html/body/form/table[1]/tbody/tr");
			try {
				for(WebElement detail : proteinDetails) {
					final String detailTitle = detail.findElement(By.tagName("th")).getText();
					final String detailValue = detail.findElement(By.tagName("td")).getText();
					writer.write(detailTitle + detailValue);
					writer.newLine();
				}
				writer.newLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
