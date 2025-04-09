package pt.ua.tqs.moliceiro.meals.selenium;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import pt.ua.tqs.moliceiro.meals.repository.MealRepository;
import pt.ua.tqs.moliceiro.meals.repository.ReservationRepository;
import pt.ua.tqs.moliceiro.meals.repository.RestaurantRepository;
import pt.ua.tqs.moliceiro.meals.repository.WeatherForecastRepository;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Testcontainers
class TestFullMoliceiroMealsTest {
    private WebDriver driver;
    private Map<String, Object> vars;
    private JavascriptExecutor js;
    private WebDriverWait wait;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private WeatherForecastRepository weatherForecastRepository;

    @BeforeEach
    void setUp() {
        // Clean up the database
        reservationRepository.deleteAll();
        mealRepository.deleteAll();
        restaurantRepository.deleteAll();
        weatherForecastRepository.deleteAll();

        // Setup Selenium
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        driver = new ChromeDriver(options);
        js = (JavascriptExecutor) driver;
        vars = new HashMap<>();
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.manage().window().setSize(new org.openqa.selenium.Dimension(1850, 1053));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void testFullMoliceiroMeals() {
        // Navigate to home page
        driver.get("http://localhost:8080/");
        
        // Navigate to Restaurants page
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Restaurants"))).click();
        
        // Add new restaurant
        wait.until(ExpectedConditions.elementToBeClickable(By.id("addRestaurantBtn"))).click();
        
        // Fill restaurant details
        driver.findElement(By.id("name")).sendKeys("Burguer King");
        driver.findElement(By.id("location")).sendKeys("Espinho");
        driver.findElement(By.id("capacity")).sendKeys("100");
        driver.findElement(By.id("operatingHours")).sendKeys("9:00-19:00");
        driver.findElement(By.cssSelector(".btn:nth-child(5)")).click();
        
        // Wait for the restaurant to be saved
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Navigate to Meals page
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Meals"))).click();
        
        // Add new meal
        wait.until(ExpectedConditions.elementToBeClickable(By.id("addMealBtn"))).click();
        
        // Select restaurant from dropdown using Select class
        WebElement restaurantDropdown = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("restaurant")));
        org.openqa.selenium.support.ui.Select restaurantSelect = new org.openqa.selenium.support.ui.Select(restaurantDropdown);
        
        // Wait for the dropdown to be populated
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Try to select by visible text
        try {
            restaurantSelect.selectByVisibleText("Burguer King");
        } catch (Exception e) {
            // If that fails, try to select by index
            java.util.List<WebElement> options = restaurantSelect.getOptions();
            for (int i = 0; i < options.size(); i++) {
                if (options.get(i).getText().contains("Burguer King")) {
                    restaurantSelect.selectByIndex(i);
                    break;
                }
            }
        }
        
        // Fill meal details
        driver.findElement(By.id("name")).sendKeys("Cheeseburger");
        driver.findElement(By.id("description")).sendKeys("Yummy!");
        driver.findElement(By.id("price")).sendKeys("10");
        
        // Set date using JavaScript to ensure correct format
        WebElement dateInput = driver.findElement(By.id("date"));
        js.executeScript("arguments[0].value = '2025-04-10';", dateInput);
        
        // Select meal type using Select class
        WebElement mealTypeDropdown = driver.findElement(By.id("mealType"));
        org.openqa.selenium.support.ui.Select mealTypeSelect = new org.openqa.selenium.support.ui.Select(mealTypeDropdown);
        mealTypeSelect.selectByVisibleText("Lunch");
        
        // Submit the form
        driver.findElement(By.cssSelector(".btn:nth-child(7)")).click();
        
        // Wait for the meal to be saved
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Navigate to Reservations page
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Reservations"))).click();
        
        // Add new reservation
        wait.until(ExpectedConditions.elementToBeClickable(By.id("addReservationBtn"))).click();
        
        // Select meal from dropdown using Select class
        WebElement mealSelect = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("mealSelect")));
        org.openqa.selenium.support.ui.Select mealSelectDropdown = new org.openqa.selenium.support.ui.Select(mealSelect);
        
        // Wait for the dropdown to be populated
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Try to select by visible text
        try {
            mealSelectDropdown.selectByVisibleText("Cheeseburger - Burguer King (2025-04-10)");
        } catch (Exception e) {
            // If that fails, try to select by index
            java.util.List<WebElement> options = mealSelectDropdown.getOptions();
            for (int i = 0; i < options.size(); i++) {
                if (options.get(i).getText().contains("Cheeseburger") && options.get(i).getText().contains("Burguer King")) {
                    mealSelectDropdown.selectByIndex(i);
                    break;
                }
            }
        }
        
        // Fill reservation details
        driver.findElement(By.id("customerName")).sendKeys("Hugo Castro");
        driver.findElement(By.id("customerEmail")).sendKeys("hugocastro@ua.pt");
        driver.findElement(By.id("numberOfPeople")).sendKeys("1");
        driver.findElement(By.cssSelector(".form-actions > .btn-primary")).click();
        
        // Wait for the reservation to be saved
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Verify restaurant details
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Restaurants"))).click();
        WebElement restaurantCard = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//div[contains(@class, 'restaurant-card') and contains(., 'Burguer King')]")));
        assertThat(restaurantCard).isNotNull();
        
        // Verify reservation and check-in
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Reservations"))).click();
        
        // Find the reservation by its content
        WebElement reservationCard = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//div[contains(@class, 'reservation-card') and contains(., 'Hugo Castro') and contains(., 'Cheeseburger')]")));
        
        // Find and click the check-in button within this reservation card
        WebElement checkInButton = reservationCard.findElement(By.cssSelector(".btn-primary"));
        wait.until(ExpectedConditions.elementToBeClickable(checkInButton)).click();
        
        // Handle alert
        String alertText = wait.until(ExpectedConditions.alertIsPresent()).getText();
        assertThat(alertText).isEqualTo("Are you sure you want to check in this reservation?");
        driver.switchTo().alert().accept();
        
        // Wait for the check-in to be processed
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Final verification
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Restaurants"))).click();
        WebElement finalRestaurantCard = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//div[contains(@class, 'restaurant-card') and contains(., 'Burguer King')]")));
        assertThat(finalRestaurantCard).isNotNull();
    }
}