package ru.rgs.tests;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.swing.*;
import java.nio.file.WatchEvent;
import java.util.concurrent.TimeUnit;

public class Weather {
    private WebDriver driver;
    private WebDriverWait wait;
    private Actions actions;

    @Before
    public void before() {
        System.setProperty("webdriver.chrome.driver", "src/test/resources/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();

        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(10, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);

        wait = new WebDriverWait(driver, 10, 2000);

        actions = new Actions(driver);

        driver.get("https://0.0.0.0:8000/");
    }

    @Test
    public void test(){

        WebElement fullName = driver.findElement(By.xpath("//input[@name='fullname']"));
        fullName.sendKeys("Иван Иванович Иванов");

        String email = "test@gamil.com";
        WebElement address = driver.findElement(By.xpath("//input[@name='email']"));
        address.sendKeys(email);

        String password = "1234";
        WebElement pass = driver.findElement(By.xpath("//input[@name='password1']"));
        pass.sendKeys(password);

        WebElement secondPass = driver.findElement(By.xpath("//input[@name='password2']"));
        secondPass.sendKeys(password);

        WebElement submitButton = driver.findElement(By.xpath("//button[@type='submit']"));
        submitButton.click();

        WebElement afterSubmit = driver.findElement(By.xpath("//p[contains(text(), 'Hello')]"));
        Assert.assertEquals(afterSubmit.getText(), "Hello You Logged in as " + email);

    }

    @After
    public void after(){
        driver.quit();
    }
}
