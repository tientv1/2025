package com.assignment.selenium;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.time.Duration;

@SuppressWarnings("unused")
public class OpenCartTest {
    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;
    private static final String BASE_URL = "https://demo.opencart.com/";
    private static final String TEST_EMAIL = "test" + System.currentTimeMillis() + "@example.com";
    private static final String TEST_PASSWORD = "Test123456!";

    @BeforeClass
    public void setUp() {
        initializeDriver();
    }

    private void initializeDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        options.addArguments("--start-maximized");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-blink-features=AutomationControlled");
        
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        js = (JavascriptExecutor) driver;
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30));
        
        // Set window size explicitly
        driver.manage().window().setSize(new Dimension(1920, 1080));
    }

    private void waitForPageLoad() {
        try {
            // Wait for document to be ready
            wait.until(webDriver -> ((JavascriptExecutor) webDriver)
                .executeScript("return document.readyState").equals("complete"));
            
            // Wait for jQuery to be loaded and active (if present)
            Boolean jQueryLoaded = (Boolean) js.executeScript(
                "return typeof jQuery != 'undefined' && jQuery.active == 0"
            );
            if (jQueryLoaded) {
                wait.until(webDriver -> (Boolean) ((JavascriptExecutor) webDriver)
                    .executeScript("return jQuery.active == 0"));
            }
            
            // Additional wait for dynamic content
            Thread.sleep(2000);
        } catch (Exception e) {
            // Ignore exceptions from jQuery check
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void restartBrowser() {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                // Ignore any exceptions during quit
            }
        }
        initializeDriver();
    }

    private void ensureValidSession() {
        try {
            // Try to execute a simple command to check session
            driver.getCurrentUrl();
        } catch (WebDriverException e) {
            restartBrowser();
            // Reload the current page
            driver.get(BASE_URL);
            wait.until(webDriver -> ((JavascriptExecutor) webDriver)
                .executeScript("return document.readyState").equals("complete"));
        }
    }

    private void scrollToElement(WebElement element) {
        try {
            ensureValidSession();
            js.executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'center'});", element);
            Thread.sleep(1000);
            // Try to scroll a bit up to avoid header overlapping
            js.executeScript("window.scrollBy(0, -100);");
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void clickElement(By locator) {
        int attempts = 0;
        while (attempts < 3) {
            try {
                ensureValidSession();
                waitForPageLoad();
                WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
                scrollToElement(element);
                
                // Wait for any animations to complete
                Thread.sleep(500);
                
                try {
                    element.click();
                    return;
                } catch (ElementClickInterceptedException e) {
                    js.executeScript("arguments[0].click();", element);
                    return;
                }
            } catch (TimeoutException | StaleElementReferenceException e) {
                attempts++;
                if (attempts == 3) {
                    throw e;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            } catch (WebDriverException e) {
                restartBrowser();
                attempts++;
                if (attempts == 3) {
                    throw e;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void clickMyAccount() {
        waitForPageLoad();
        
        // First try to find the dropdown container
        try {
            // Wait for the navbar to be present
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("nav.navbar")));
            
            // Try to find and click the My Account dropdown
            By[] dropdownSelectors = {
                By.cssSelector("#top > div.container > div > div.nav.float-end > ul > li:nth-child(2) > div > a"),
                By.cssSelector("div.nav.float-end .dropdown a"),
                By.cssSelector("div.nav .dropdown-toggle"),
                By.cssSelector("div.list-inline-item .dropdown-toggle"),
                By.cssSelector("[data-bs-toggle='dropdown']"),
                By.cssSelector("a.dropdown-toggle")
            };

            boolean clicked = false;
            for (By selector : dropdownSelectors) {
                try {
                    WebElement dropdown = wait.until(ExpectedConditions.presenceOfElementLocated(selector));
                    if (dropdown.isDisplayed()) {
                        // Try regular click first
                        try {
                            scrollToElement(dropdown);
                            Thread.sleep(500);
                            dropdown.click();
                            clicked = true;
                            break;
                        } catch (ElementClickInterceptedException e) {
                            // If regular click fails, try JavaScript click
                            js.executeScript("arguments[0].click();", dropdown);
                            clicked = true;
                            break;
                        }
                    }
                } catch (Exception e) {
                    continue;
                }
            }

            if (!clicked) {
                // If dropdown click failed, try direct My Account link
                By[] directSelectors = {
                    By.xpath("//a[normalize-space()='My Account']"),
                    By.xpath("//span[normalize-space()='My Account']"),
                    By.cssSelector("a[title='My Account']"),
                    By.linkText("My Account")
                };

                for (By selector : directSelectors) {
                    try {
                        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(selector));
                        if (element.isDisplayed()) {
                            scrollToElement(element);
                            Thread.sleep(500);
                            try {
                                element.click();
                                clicked = true;
                                break;
                            } catch (ElementClickInterceptedException e) {
                                js.executeScript("arguments[0].click();", element);
                                clicked = true;
                                break;
                            }
                        }
                    } catch (Exception e) {
                        continue;
                    }
                }
            }

            if (!clicked) {
                // Last resort: try to find any element containing "My Account" text
                try {
                    WebElement element = driver.findElement(
                        By.xpath("//*[contains(translate(text(), 'MYACCOUNT', 'myaccount'), 'my account')]")
                    );
                    scrollToElement(element);
                    Thread.sleep(500);
                    js.executeScript("arguments[0].click();", element);
                } catch (Exception e) {
                    throw new TimeoutException("Could not find or click My Account with any known method");
                }
            }

        } catch (Exception e) {
            throw new TimeoutException("Failed to interact with My Account: " + e.getMessage());
        }
    }

    @Test(retryAnalyzer = TestRetry.class)
    public void testRegistration() {
        try {
            ensureValidSession();
            driver.get(BASE_URL);
            wait.until(webDriver -> ((JavascriptExecutor) webDriver)
                .executeScript("return document.readyState").equals("complete"));
            clickMyAccount();
            clickElement(By.linkText("Register"));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("input-firstname"))).sendKeys("Test");
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("input-lastname"))).sendKeys("User");
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("input-email"))).sendKeys(TEST_EMAIL);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("input-password"))).sendKeys(TEST_PASSWORD);
            clickElement(By.name("agree"));
            clickElement(By.cssSelector("button[type='submit']"));
            wait.until(ExpectedConditions.urlContains("account/success"));
            Assert.assertTrue(driver.getCurrentUrl().contains("success"));
        } catch (WebDriverException e) {
            restartBrowser();
            throw e;
        }
    }

    @Test(retryAnalyzer = TestRetry.class)
    public void testLogin() {
        try {
            ensureValidSession();
            driver.get(BASE_URL);
            wait.until(webDriver -> ((JavascriptExecutor) webDriver)
                .executeScript("return document.readyState").equals("complete"));
            clickMyAccount();
            clickElement(By.linkText("Login"));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("input-email"))).sendKeys(TEST_EMAIL);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("input-password"))).sendKeys(TEST_PASSWORD);
            clickElement(By.cssSelector("button[type='submit']"));
            wait.until(ExpectedConditions.urlContains("account"));
            Assert.assertTrue(driver.getCurrentUrl().contains("account"));
        } catch (WebDriverException e) {
            restartBrowser();
            throw e;
        }
    }

    @Test(retryAnalyzer = TestRetry.class)
    public void testAddToWishlist() {
        try {
            ensureValidSession();
            driver.get(BASE_URL);
            
            // Wait for products to be visible and click the first one
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".product-thumb")));
            clickElement(By.cssSelector(".product-thumb h4 a"));
            
            // Wait for product page to load and add to wishlist
            // Try multiple possible selectors for the wishlist button
            try {
                clickElement(By.cssSelector("button[data-bs-original-title='Add to Wish List']"));
            } catch (TimeoutException e) {
                try {
                    clickElement(By.cssSelector("button[title='Add to Wish List']"));
                } catch (TimeoutException e2) {
                    // If both selectors fail, try finding by icon
                    clickElement(By.cssSelector("button i.fa-heart"));
                }
            }
            
            // Verify success message
            try {
                wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".alert-success")));
            } catch (TimeoutException e) {
                // Some versions might show different success indicators
                wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".alert-success")),
                    ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#alert"))
                ));
            }
        } catch (WebDriverException e) {
            restartBrowser();
            throw e;
        }
    }

    @Test(retryAnalyzer = TestRetry.class)
    public void testRemoveFromWishlist() {
        try {
            ensureValidSession();
            // Go to wishlist
            clickElement(By.linkText("My Account"));
            clickElement(By.linkText("Wish List"));
            
            // Wait for wishlist page to load
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".table-responsive")));
            
            // Try multiple possible selectors for the remove button
            try {
                clickElement(By.cssSelector("a[data-bs-original-title='Remove']"));
            } catch (TimeoutException e) {
                try {
                    clickElement(By.cssSelector("a[title='Remove']"));
                } catch (TimeoutException e2) {
                    // If both fail, try finding by icon
                    clickElement(By.cssSelector("a i.fa-times"));
                }
            }
            
            // Verify success message with multiple possible selectors
            try {
                wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".alert-success")));
            } catch (TimeoutException e) {
                wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".alert-success")),
                    ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#alert"))
                ));
            }
        } catch (WebDriverException e) {
            restartBrowser();
            throw e;
        }
    }

    @Test(retryAnalyzer = TestRetry.class)
    public void testUpdateAccount() {
        try {
            ensureValidSession();
            // Go to edit account
            clickElement(By.linkText("My Account"));
            clickElement(By.linkText("Edit Account"));
            
            // Update first name
            WebElement firstNameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("input-firstname")));
            firstNameInput.clear();
            firstNameInput.sendKeys("Updated Test");
            
            // Submit changes
            clickElement(By.cssSelector("button[type='submit']"));
            
            // Verify success message
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".alert-success")));
        } catch (WebDriverException e) {
            restartBrowser();
            throw e;
        }
    }
    
    @AfterMethod
    public void checkSessionAfterMethod() {
        try {
            ensureValidSession();
        } catch (Exception e) {
            restartBrowser();
        }
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                // Ignore exceptions during quit
            }
        }
    }
} 
// driver.get()
// driver.findElement(By.id()).senkey
// .click
// Assert asserTrue(driver.getCurrentUrl().contain(""))
//Assert.assertEqual(,"")