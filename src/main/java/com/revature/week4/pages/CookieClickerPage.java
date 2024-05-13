package com.revature.week4.pages;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class CookieClickerPage {
    private WebDriver driver;

    @FindBy(css = "a[href = '#null']")
    private WebElement acceptCookiesButton;

    @FindBy(id = "langSelect-EN")
    private WebElement selectLanguageButton;

    @FindBy(css = "*[onclick='Game.prefs.showBackupWarning=0;Game.CloseNote(1);']")
    private WebElement backupNote;

    @FindBy(id = "bigCookie")
    private WebElement bigCookie;

    @FindBy(id = "cookies")
    private WebElement cookieCount;

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
        } catch (Exception e) {
            System.out.println("Accept Cookies button not found.");
            e.printStackTrace();
        }
    }

    private void setLanguage() {
        try {
            selectLanguageButton.click();
            System.out.println("Set EN Language!");
        } catch (Exception e) {
            System.out.println("Set EN Language button not found.");
            e.printStackTrace();
        }
    }

    private void ignoreBackupNote() {
        try {
            backupNote.click();
            System.out.println("Ignored Backup Note!");
        } catch (Exception e) {
            System.out.println("Backup Note not found.");
            e.printStackTrace();
        }
    }

    public void gameplayLoop() {
        clickCookieMultiple(15);
        buyUpgrades();
        System.out.println(getCookieCount());
    }

    private void buyUpgrades() {
        List<WebElement> availableUpgrades = driver.findElements(By.cssSelector("div[class='product unlocked enabled']"));
    }

    public boolean clickCookie() {
        try {
            new WebDriverWait(driver, Duration.ofMillis(100))
                    .until(ExpectedConditions.elementToBeClickable(bigCookie)).click();
            return true;
        } catch (Exception e) {
            System.out.println("Failed to click cookie.");
        }
        return false;
    }

    public void clickCookieMultiple(int i) {
        int clickCount = 0;
        while (clickCount < i) {
            boolean successfulClick = clickCookie();
            if (successfulClick) clickCount++;
        }
    }

    public int getCookieCount() {
        String cookieText = "";
        try {
            cookieText = new WebDriverWait(driver, Duration.ofSeconds(3))
                    .until(ExpectedConditions.visibilityOf(cookieCount)).getText();
        }
        catch (Exception e) {
            System.out.println("Could not get Cookie Count web element.");
            return -1;
        }
        int cookieCount = -1;
        try{
            String[] split = cookieText.split(" ");
            if (split.length > 0) {
                cookieText = split[0];
                cookieCount = Integer.parseInt(cookieText);
            }
            else
            {
                System.out.println("Could not get number from Cookie Count text.");
            }
        }
        catch (NumberFormatException e) {
            System.out.println("NumberFormatException while parsing Cookie Count.");
            e.printStackTrace();
        }
        return cookieCount;
    }
}
