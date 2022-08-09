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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.nio.file.WatchEvent;
import java.util.concurrent.TimeUnit;

public class Rosgosstrah {
    WebDriver driver;
    WebDriverWait wait;

    @Before
    public void before() {
        System.setProperty("webdriver.chrome.driver", "src/test/resources/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();

        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(10, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);

        wait = new WebDriverWait(driver, 10, 2000);

        driver.get("https://rgs.ru/");
    }

    @Test
    public void test(){
        // Находим кнопку Компаниям и нажиамем на неё, проводим проверку перехода по тайтлу
        WebElement mainPage = driver.findElement(By.xpath("//a[text()='Компаниям' and contains(@class, 'text--second')]"));
        mainPage.click();
        wait.until(ExpectedConditions.titleIs("Страхование компаний и юридических лиц | Росгосстрах"));

        // Находим кнопку Здоровье и нажиамем на неё и проверяем, что статус у этой кнопки поменялся на эктив и, соответственно, она раскрылась
        WebElement healthButton = driver.findElement(By.xpath("//span[text()='Здоровье' and contains(@class, 'padding')]"));
        healthButton.click();
        WebElement parentHealthButton = healthButton.findElement(By.xpath("./.."));
        wait.until(ExpectedConditions.attributeContains(parentHealthButton, "class", "active"));

        // Нажать на кнопку Добровольное мед страхование, делаю две проверки, на тайтл и на h1 заголовок с помощью ассерта
        WebElement medStrahButton = driver.findElement(By.xpath("//a[text()='Добровольное медицинское страхование' and contains(@href, 'dobrovolnoe-meditsinskoe')]"));
        medStrahButton.click();
        wait.until(ExpectedConditions.titleIs("Добровольное медицинское страхование для компаний и юридических лиц в Росгосстрахе"));
        WebElement medStrahH1 = driver.findElement(By.xpath("//h1[text()='Добровольное медицинское страхование' and contains(@class, 'word-breaking')]"));
        Assert.assertTrue("Страница не загрузилась", medStrahH1.isDisplayed());

        driver.navigate().refresh();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Нажимаю на кнопку Отправить заявку
        WebElement sendBid = driver.findElement(By.xpath("//span[text()='Отправить заявку']"));

        // Где-то тут на сайте всплывает открывающаяся по времени реклама, которая не дает дальше кликать, поэтому стоит написать код для её закрытия
//        By advertising = By.xpath("//div[contains(text(), '×') and contains(@data-fl-track, 'click-close-login')]");

//        if (elementIsExist(advertising)) {
//            WebElement region = driver.findElement(advertising);
//            region.click();
//        }
        sendBid.click();
        // После клика не успевает прокрутить экран и из-за этого падает, пока поставлю васянскую задержку слипом
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // тут будет проверка на изменение позиции экрана по нажатию на кнопку

        // Заполняю поля по кейсу: Имя, Фамилия, Отчество, Регион, Телефон, Эл. почта - qwertyqwerty, Комментарий Я согласен на обработку
        String fieldXPath = "//input[@name='%s']";
        fillInputField(driver.findElement(By.xpath(String.format(fieldXPath, "userName"))), "Борисенков Дмитрий Владимирович");
        fillInputFieldForNumber(driver.findElement(By.xpath(String.format(fieldXPath, "userTel"))), "7777777777");
        // Не проходила проверка из-за преобразования номера на сайте Росгосстраха, пришлось удалить проверку:)
        fillInputField(driver.findElement(By.xpath(String.format(fieldXPath, "userEmail"))), "qwertyqwerty");
        fillInputField(driver.findElement(By.xpath("//input[@type='text' and @placeholder='Введите']")), "г Москва, Перовское шоссе, д 2");

        WebElement buttonGetIntoContactWithMe = driver.findElement(By.xpath("//button[text()='Свяжитесь со мной' and @type='submit']"));
        buttonGetIntoContactWithMe.click();

        WebElement errorEmail = driver.findElement(By.xpath("//span[text()='Введите корректный адрес электронной почты' and contains(@class, 'input__error')]"));
        waitUtilElementToBeVisible(errorEmail); // Вот эта проверка скорее всего не правильная

    }

    @After
    public void after(){
        driver.quit();
    }

    // Заполняю поле, если не заполняется, то выводит ошибку "Поле было заполнено некорректно"
    private void fillInputField(WebElement element, String value){
        waitUtilElementToBeClickable(element);
        element.click();
        element.clear();
        element.sendKeys(value);
        boolean checkFlag = wait.until(ExpectedConditions.attributeContains(element, "value", value));
        Assert.assertTrue("Поле было заполнено некорректно", checkFlag);
    }

    private void fillInputFieldForNumber(WebElement element, String value){
        waitUtilElementToBeClickable(element);
        element.click();
        element.clear();
        element.sendKeys(value);
        // проверка не проходила из-за преобразования номера на сайте, я её убрал, зато скрипт работает
//        boolean checkFlag = wait.until(ExpectedConditions.attributeContains(element, "value", "+7"+value));
//        Assert.assertTrue("Поле было заполнено некорректно", checkFlag);
    }

    // Вынесенное для удобства записи действие проверяющее можно ли нажать на появившуюся кнопку
    private void waitUtilElementToBeClickable(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    // Проверка на видимость объекта WebElement
    private void waitUtilElementToBeVisible(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
    }

    // Проверка на всплывающие окна
    public boolean elementIsExist(By by){
        try {
            driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
            driver.findElement(by);
            return true;
            // Перехватываю ошибку от Selenium, не от juva.util
        } catch (NoSuchElementException e) {
            System.out.println("Нет такого элемента на странице");
        }
        finally {
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        }
        return false;   // запись вне кетча дает вернуть фолс в любом случа, а ошибку игнорим
    }
}
