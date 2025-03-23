import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

public class SauceDemoTest {
    WebDriver driver;
    WebDriverWait wait;
    String url = "https://www.saucedemo.com/";
    String username = "standard_user";
    String password = "secret_sauce";

    @BeforeMethod
    public void setUp() {
        // Set up ChromeDriver using WebDriverManager
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();

        // Initialize WebDriverWait with a timeout of 10 seconds
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Test
    public void testHighestPriceItem() throws InterruptedException {
        // Step 1: Navigate to the URL
        driver.get(url);
        Thread.sleep(2000); // Delay of 5 seconds

        // Step 2: Login
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user-name"))).sendKeys(username);
        Thread.sleep(2000); // Delay of 5 seconds
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password"))).sendKeys(password);
        Thread.sleep(2000); // Delay of 5 seconds
        wait.until(ExpectedConditions.elementToBeClickable(By.id("login-button"))).click();
        Thread.sleep(2000); // Delay of 5 seconds

        // Step 3: Select the highest price item
        List<WebElement> items = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("inventory_item")));
        WebElement highestPriceItem = null;
        double highestPrice = 0;

        for (WebElement item : items) {
            String priceText = item.findElement(By.className("inventory_item_price")).getText();
            double price = Double.parseDouble(priceText.replace("$", ""));
            if (price > highestPrice) {
                highestPrice = price;
                highestPriceItem = item;
            }
        }

        // Highlight the highest price item (optional)
        if (highestPriceItem != null) {
            // Use JavaScript to highlight the element
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].style.border='3px solid red'", highestPriceItem);
            Thread.sleep(2000); // Delay of 5 seconds
        }

        // Step 4: Add the highest price item to the cart
        assert highestPriceItem != null;
        highestPriceItem.findElement(By.className("btn_inventory")).click();
        Thread.sleep(2000); // Delay of 5 seconds

        // Verify the item is added to the cart
        String cartCount = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("shopping_cart_badge"))).getText();
        Assert.assertEquals(cartCount, "1", "Item was not added to the cart");
    }

    @AfterMethod
    public void tearDown() {
        // Close the browser
        driver.quit();
    }
}