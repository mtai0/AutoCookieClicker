package com.revature.week4.pages;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class CookieClickerPage {
    private WebDriver driver;

    @FindBy(css = "a[href = '#null']")
    private WebElement acceptCookiesButton;

    @FindBy(id = "langSelect-EN")
    private WebElement selectLanguageButton;

    @FindBy(css = "*[onclick='Game.prefs.showBackupWarning=0;Game.CloseNote(1);']")
    private WebElement backupNote;

    public CookieClickerPage(WebDriver driver) {
        this.driver = driver;
    }

    public void getPage() {
        driver.get("https://orteil.dashnet.org/cookieclicker/");
    }

    //First-time setup before getting to the meat of the program
    public void setup() {
        //Set a long implicit wait because this website can take a while to load
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(180));
        acceptCookies();
        setLanguage();
        ignoreBackupNote();
    }

    private void acceptCookies() {
        try {
            acceptCookiesButton.click();
            System.out.println("Accepted Cookies!");
        }
        catch (Exception e) {
            System.out.println("Accept Cookies button not found.");
            e.printStackTrace();
        }
    }

    private void setLanguage() {
        try {
            selectLanguageButton.click();
            System.out.println("Set EN Language!");
        }
        catch (Exception e) {
            System.out.println("Set EN Language button not found.");
            e.printStackTrace();
        }
    }

    private void ignoreBackupNote() {
        try {
            backupNote.click();
            System.out.println("Ignored Backup Note!");
        }
        catch (Exception e) {
            System.out.println("Backup Note not found.");
            e.printStackTrace();
        }
    }
}
