import junit.framework.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.DecimalFormat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class SeleniumTest extends TestCase{
	
	private double convert(double Farenheit){
		return 100.0*(Farenheit - 32.0)/180.0;
	}
	
	public void testLogin() {
		System.out.println("Testing login...");
		System.out.println("Waiting for 10 seconds...");
		try{
			Thread.sleep(10000);
		}catch(InterruptedException e){}
		WebDriver driver = new HtmlUnitDriver();
        driver.get("http://apt-public.appspot.com/testing-lab-login.html");
		WebElement name = driver.findElement(By.name("userId"));
		name.clear();
		name.sendKeys("andy");
		WebElement password = driver.findElement(By.name("userPassword"));
		password.clear();
		password.sendKeys("apple");
		password.submit();
		assertTrue(driver.findElement(By.tagName("body")).getText().contains("Convert"));
		driver.get("http://apt-public.appspot.com/testing-lab-login.html");
		name = driver.findElement(By.name("userId"));
		name.clear();
		name.sendKeys("bob");
		password = driver.findElement(By.name("userPassword"));
		password.clear();
		password.sendKeys("bathtub");
		password.submit();
		assertTrue(driver.findElement(By.tagName("body")).getText().contains("Convert"));
		driver.get("http://apt-public.appspot.com/testing-lab-login.html");
		name = driver.findElement(By.name("userId"));
		name.clear();
		name.sendKeys("charley");
		password = driver.findElement(By.name("userPassword"));
		password.clear();
		password.sendKeys("china");
		password.submit();
		assertTrue(driver.findElement(By.tagName("body")).getText().contains("Convert"));
		System.out.println("Pass!");
	}
	
	public void testUserNameCaseInsensitive(){
		System.out.println("Testing user name case insensitive...");
		System.out.println("Waiting for 10 seconds...");
		try{
			Thread.sleep(10000);
		}catch(InterruptedException e){}
		WebDriver driver = new HtmlUnitDriver();
        driver.get("http://apt-public.appspot.com/testing-lab-login.html");
		WebElement name = driver.findElement(By.name("userId"));
		name.clear();
		name.sendKeys("BOB");
		WebElement password = driver.findElement(By.name("userPassword"));
		password.clear();
		password.sendKeys("bathtub");
		password.submit();
		assertTrue(driver.findElement(By.tagName("body")).getText().contains("Convert"));
		driver.get("http://apt-public.appspot.com/testing-lab-login.html");
		name = driver.findElement(By.name("userId"));
		name.clear();
		name.sendKeys("Andy");
		password = driver.findElement(By.name("userPassword"));
		password.clear();
		password.sendKeys("apple");
		password.submit();
		assertTrue(driver.findElement(By.tagName("body")).getText().contains("Convert"));
		driver.get("http://apt-public.appspot.com/testing-lab-login.html");
		name = driver.findElement(By.name("userId"));
		name.clear();
		name.sendKeys("CHARLEY");
		password = driver.findElement(By.name("userPassword"));
		password.clear();
		password.sendKeys("china");
		password.submit();
		assertTrue(driver.findElement(By.tagName("body")).getText().contains("Convert"));
		System.out.println("Pass!");
	}
	
	public void testPasswordCaseSensitive(){
		System.out.println("Testing password case sensitive...");
		System.out.println("Waiting for 10 seconds...");
		try{
			Thread.sleep(10000);
		}catch(InterruptedException e){}
		WebDriver driver = new HtmlUnitDriver();
        driver.get("http://apt-public.appspot.com/testing-lab-login.html");
		WebElement name = driver.findElement(By.name("userId"));
		name.clear();
		name.sendKeys("charley");
		WebElement password = driver.findElement(By.name("userPassword"));
		password.clear();
		password.sendKeys("China");
		password.submit();
		assertTrue(driver.findElement(By.tagName("body")).getText().contains("incorrect"));
		driver.get("http://apt-public.appspot.com/testing-lab-login.html");
		name = driver.findElement(By.name("userId"));
		name.clear();
		name.sendKeys("andy");
		password = driver.findElement(By.name("userPassword"));
		password.clear();
		password.sendKeys("APPLE");
		password.submit();
		assertTrue(driver.findElement(By.tagName("body")).getText().contains("incorrect"));
		driver.get("http://apt-public.appspot.com/testing-lab-login.html");
		name = driver.findElement(By.name("userId"));
		name.clear();
		name.sendKeys("bob");
		password = driver.findElement(By.name("userPassword"));
		password.clear();
		password.sendKeys("BATHTUB");
		password.submit();
		assertTrue(driver.findElement(By.tagName("body")).getText().contains("incorrect"));
		System.out.println("Pass!");
	}
	
	public void testLoginFail(){
		System.out.println("Testing log in fails...");
		System.out.println("Waiting for 10 seconds...");
		try{
			Thread.sleep(10000);
		}catch(InterruptedException e){}
		WebDriver driver = new HtmlUnitDriver();
        driver.get("http://apt-public.appspot.com/testing-lab-login.html");
		WebElement name = driver.findElement(By.name("userId"));
		name.clear();
		name.sendKeys("andy");
		WebElement password = driver.findElement(By.name("userPassword"));
		password.clear();
		password.sendKeys("sss");
		password.submit();
		assertTrue(driver.findElement(By.tagName("body")).getText().contains("incorrect"));
		driver.get("http://apt-public.appspot.com/testing-lab-login.html");
		name = driver.findElement(By.name("userId"));
		name.clear();
		name.sendKeys("andy");
		password = driver.findElement(By.name("userPassword"));
		password.clear();
		password.sendKeys("sss");
		password.submit();
		assertTrue(driver.findElement(By.tagName("body")).getText().contains("incorrect"));
		driver.get("http://apt-public.appspot.com/testing-lab-login.html");
		name = driver.findElement(By.name("userId"));
		name.clear();
		name.sendKeys("andy");
		password = driver.findElement(By.name("userPassword"));
		password.clear();
		password.sendKeys("sss");
		password.submit();
		assertTrue(driver.findElement(By.tagName("body")).getText().contains("incorrect"));
		driver.get("http://apt-public.appspot.com/testing-lab-login.html");
		name = driver.findElement(By.name("userId"));
		name.clear();
		name.sendKeys("andy");
		password = driver.findElement(By.name("userPassword"));
		password.clear();
		password.sendKeys("apple");
		password.submit();
		assertTrue(driver.findElement(By.tagName("body")).getText().contains("Wait"));
		System.out.println("Andy login fails for 3 times, waiting 60 seconds...");
		try{
			Thread.sleep(60000);
		}catch(InterruptedException e){}
		System.out.println("Try to login again...");
		driver.get("http://apt-public.appspot.com/testing-lab-login.html");
		name = driver.findElement(By.name("userId"));
		name.clear();
		name.sendKeys("andy");
		password = driver.findElement(By.name("userPassword"));
		password.clear();
		password.sendKeys("apple");
		password.submit();
		assertTrue(driver.findElement(By.tagName("body")).getText().contains("Convert"));
		System.out.println("Pass!");
	}
	
	public void testConvertResult(){
		System.out.println("Testing conversion result...");
		WebDriver driver = new HtmlUnitDriver();
        driver.get("http://apt-public.appspot.com/testing-lab-calculator.html");
		WebElement farenheit = driver.findElement(By.name("farenheitTemperature"));
		farenheit.clear();
		farenheit.sendKeys("0.0");
		farenheit.submit();
		String result = driver.findElement(By.tagName("body")).getText();
		assertTrue(result.contains("Celsius"));
		Pattern pattern = Pattern.compile("= (.+) Celsius");
		Matcher matcher = pattern.matcher(result);
		matcher.find();
		assertEquals(Double.parseDouble(matcher.group(1)), convert(0.0), 0.1);
		driver.get("http://apt-public.appspot.com/testing-lab-calculator.html");
		farenheit = driver.findElement(By.name("farenheitTemperature"));
		farenheit.clear();
		farenheit.sendKeys("10000.0");
		farenheit.submit();
		result = driver.findElement(By.tagName("body")).getText();
		assertTrue(result.contains("Celsius"));
		pattern = Pattern.compile("= (.+) Celsius");
		matcher = pattern.matcher(result);
		matcher.find();
		assertEquals(Double.parseDouble(matcher.group(1)), convert(10000.0), 0.1);
		driver.get("http://apt-public.appspot.com/testing-lab-calculator.html");
		farenheit = driver.findElement(By.name("farenheitTemperature"));
		farenheit.clear();
		farenheit.sendKeys("-30.1");
		farenheit.submit();
		result = driver.findElement(By.tagName("body")).getText();
		assertTrue(result.contains("Celsius"));
		pattern = Pattern.compile("= (.+) Celsius");
		matcher = pattern.matcher(result);
		matcher.find();
		assertEquals(Double.parseDouble(matcher.group(1)), convert(-30.1), 0.1);
		driver.get("http://apt-public.appspot.com/testing-lab-calculator.html");
		farenheit = driver.findElement(By.name("farenheitTemperature"));
		farenheit.clear();
		farenheit.sendKeys("65.8");
		farenheit.submit();
		result = driver.findElement(By.tagName("body")).getText();
		assertTrue(result.contains("Celsius"));
		pattern = Pattern.compile("= (.+) Celsius");
		matcher = pattern.matcher(result);
		matcher.find();
		assertEquals(Double.parseDouble(matcher.group(1)), convert(65.8), 0.1);
		System.out.println("Pass!");
	}
	
	public void testPrecision(){
		System.out.println("Testing conversion precision...");
		WebDriver driver = new HtmlUnitDriver();
        driver.get("http://apt-public.appspot.com/testing-lab-calculator.html");
		WebElement farenheit = driver.findElement(By.name("farenheitTemperature"));
		farenheit.clear();
		farenheit.sendKeys("0.0");
		farenheit.submit();
		String result = driver.findElement(By.tagName("body")).getText();
		assertTrue(result.contains("Celsius"));
		Pattern pattern = Pattern.compile("= (.+) Celsius");
		Matcher matcher = pattern.matcher(result);
		matcher.find();
		assertEquals(matcher.group(1), new DecimalFormat("0.00").format(convert(0.0)));
		driver.get("http://apt-public.appspot.com/testing-lab-calculator.html");
		farenheit = driver.findElement(By.name("farenheitTemperature"));
		farenheit.clear();
		farenheit.sendKeys("212.0");
		farenheit.submit();
		result = driver.findElement(By.tagName("body")).getText();
		assertTrue(result.contains("Celsius"));
		pattern = Pattern.compile("= (.+) Celsius");
		matcher = pattern.matcher(result);
		matcher.find();
		assertEquals(matcher.group(1), new DecimalFormat("0.00").format(convert(212.0)));
		driver.get("http://apt-public.appspot.com/testing-lab-calculator.html");
		farenheit = driver.findElement(By.name("farenheitTemperature"));
		farenheit.clear();
		farenheit.sendKeys("-34.0");
		farenheit.submit();
		result = driver.findElement(By.tagName("body")).getText();
		assertTrue(result.contains("Celsius"));
		pattern = Pattern.compile("= (.+) Celsius");
		matcher = pattern.matcher(result);
		matcher.find();
		assertEquals(matcher.group(1), new DecimalFormat("0.0").format(convert(-34.0)));
		driver.get("http://apt-public.appspot.com/testing-lab-calculator.html");
		farenheit = driver.findElement(By.name("farenheitTemperature"));
		farenheit.clear();
		farenheit.sendKeys("1000.0");
		farenheit.submit();
		result = driver.findElement(By.tagName("body")).getText();
		assertTrue(result.contains("Celsius"));
		pattern = Pattern.compile("= (.+) Celsius");
		matcher = pattern.matcher(result);
		matcher.find();
		assertEquals(matcher.group(1), new DecimalFormat("0.0").format(convert(1000.0)));
		System.out.println("Pass!");
	}
	
	public void testInputFormatError(){
		System.out.println("Testing invalid input number format...");
		WebDriver driver = new HtmlUnitDriver();
        driver.get("http://apt-public.appspot.com/testing-lab-calculator.html");
		WebElement farenheit = driver.findElement(By.name("farenheitTemperature"));
		farenheit.clear();
		farenheit.sendKeys("asfd");
		farenheit.submit();
		String result = driver.findElement(By.tagName("body")).getText();
		assertTrue(result.contains("NumberFormatException"));
		driver.get("http://apt-public.appspot.com/testing-lab-calculator.html");
		farenheit = driver.findElement(By.name("farenheitTemperature"));
		farenheit.clear();
		farenheit.sendKeys("23.0.0");
		farenheit.submit();
		result = driver.findElement(By.tagName("body")).getText();
		assertTrue(result.contains("NumberFormatException"));
		driver.get("http://apt-public.appspot.com/testing-lab-calculator.html");
		farenheit = driver.findElement(By.name("farenheitTemperature"));
		farenheit.clear();
		farenheit.sendKeys("+-324");
		farenheit.submit();
		result = driver.findElement(By.tagName("body")).getText();
		assertTrue(result.contains("NumberFormatException"));
		driver.get("http://apt-public.appspot.com/testing-lab-calculator.html");
		farenheit = driver.findElement(By.name("farenheitTemperature"));
		farenheit.clear();
		farenheit.sendKeys("1e3");
		farenheit.submit();
		result = driver.findElement(By.tagName("body")).getText();
		assertTrue(result.contains("NumberFormatException"));
		System.out.println("Pass!");
	}
	
	public void testQueryFromURL(){
		System.out.println("Testing query from url...");
		WebDriver driver = new HtmlUnitDriver();
        driver.get("http://apt-public.appspot.com/testing-lab-conversion?farenheitTemperature=-43");
        String result = driver.findElement(By.tagName("body")).getText();
        assertTrue(result.contains("Celsius"));
        driver.get("http://apt-public.appspot.com/testing-lab-conversion?farenheitTemperature=345.0");
        result = driver.findElement(By.tagName("body")).getText();
        assertTrue(result.contains("Celsius"));
        driver.get("http://apt-public.appspot.com/testing-lab-conversion?farenheitTemperature=76.0");
        result = driver.findElement(By.tagName("body")).getText();
        assertTrue(result.contains("Celsius"));
        driver.get("http://apt-public.appspot.com/testing-lab-conversion?FarenheitTemperature=-0.0");
        result = driver.findElement(By.tagName("body")).getText();
        assertFalse(result.contains("Celsius"));
        driver.get("http://apt-public.appspot.com/testing-lab-conversion?fareNheitTemperature=-43");
        result = driver.findElement(By.tagName("body")).getText();
        assertFalse(result.contains("Celsius"));
		System.out.println("Pass!");
	}
}