package com.revature.week4;

import com.revature.week4.pages.CookieClickerPage;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.PageFactory;

public class Main {
    private static WebDriver driver;
    public static void main(String[] args) {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();;

        CookieClickerPage page = PageFactory.initElements(driver, CookieClickerPage.class);
        page.getPage();
        page.setup();
        page.gameplayLoop();
    }
}
