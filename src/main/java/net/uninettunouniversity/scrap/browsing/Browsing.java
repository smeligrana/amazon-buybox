package net.uninettunouniversity.scrap.browsing;

import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class Browsing {
	
	private WebDriver driver;
	
	
	public void espandiPagina(String url) {
		
		inizializzaDriver();
		
		// apre il browser
		driver.get(url);

		// chiude finestra cookie
		try {
			driver.findElement(By.xpath("//*[@id=\"sp-cc-accept\"]")).click();
		} catch (org.openqa.selenium.NoSuchElementException e) {

		}
		// apre finestra altri venditori
		driver.findElement(By.xpath("//*[@id=\"olpLinkWidget_feature_div\"]/div[2]/span/a/div/div")).click();

		// apre opzione "mostra altro"
		// try {
		// driver.findElement(By.xpath("//*[@id=\"aod-pinned-offer-show-more-link\"]")).click();
		// } catch (NoSuchElementException e) {

		// }

		// contatore venditori
		String cont = driver.findElement(By.xpath("//*[@id=\"aod-filter-offer-count-string\"]")).getText();
		StringTokenizer st = new StringTokenizer(cont, " ");
		int numVend = Integer.parseInt(st.nextToken());

		// facciamo lo scroll
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		for (int i = 0; i <= numVend; i++) {
			WebElement e;
			try {
				e = driver.findElement(By.xpath("//*[@id=\"aod-price-" + i + "\"]/span/span[2]"));
				jse.executeScript("arguments[0].scrollIntoView(true)", e);
			} catch (org.openqa.selenium.NoSuchElementException e1) {
				try {
					driver.findElement(By.xpath("//*[@id=\"aod-show-more-offers\"]")).click();
					e = driver.findElement(By.xpath("//*[@id=\"aod-price-" + i + "\"]/span/span[2]"));
					jse.executeScript("arguments[0].scrollIntoView(true)", e);
				} catch (Exception exx) {

				}
			}

		}
		// opzione altro
		try {
			List<WebElement> ee = driver.findElements(By.id("aod-delivery-more-action"));
			for (WebElement we : ee) {
				jse.executeScript("arguments[0].scrollIntoView(true)", we);
				jse.executeScript("arguments[0].click()", we);
			}

		} catch (org.openqa.selenium.NoSuchElementException e1) {

		}
		
	}
	
	private void inizializzaDriver() {
		System.setProperty("webdriver.chrome.driver", "./Driver/chromedriver");
		ChromeOptions options = new ChromeOptions();
//		options.addArguments("start-maximized"); // open Browser in maximized mode
//		options.addArguments("disable-infobars"); // disabling infobars
//		options.addArguments("--disable-extensions"); // disabling extensions
//		options.addArguments("--disable-gpu"); // applicable to windows os only
		options.addArguments("--disable-dev-shm-usage"); // overcome limited resource problems
		options.addArguments("--no-sandbox"); // Bypass OS security model
		options.addArguments("--headless");
		driver = new ChromeDriver(options);
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.manage().window().maximize();
	}
	
	public String getHtml() {
		return driver.getPageSource();
	}

	public WebDriver getDriver() {
		return this.driver;
	}
	
	public static void main(String...args) {
		Browsing browsing = new Browsing();
		browsing.espandiPagina("https://www.google.com");
	}
}
