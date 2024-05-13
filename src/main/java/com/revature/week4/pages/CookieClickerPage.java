package com.revature.week4.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;

public class CookieClickerPage {
    private WebDriver driver;
    private int buyIncrement = 1;

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
    public void closePopups() {
        //Set a long implicit wait because this website can take a while to load.
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(180));
        acceptCookies();
        setLanguage();
        ignoreBackupNote();

        //Once everything is loaded, the implicit wait can be lowered.
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    private void acceptCookies() {
        try {
            acceptCookiesButton.click();
            System.out.println("Accepted Cookies!");
        } catch (Exception e) {
            System.out.println("ERROR: Accept Cookies button not found.");
            e.printStackTrace();
        }
    }

    private void setLanguage() {
        try {
            selectLanguageButton.click();
            System.out.println("Set EN Language!");
        } catch (Exception e) {
            System.out.println("ERROR: Set EN Language button not found.");
            e.printStackTrace();
        }
    }

    private void ignoreBackupNote() {
        try {
            backupNote.click();
            System.out.println("Ignored Backup Note!");
        } catch (Exception e) {
            System.out.println("ERROR: Backup Note not found.");
            e.printStackTrace();
        }
    }

    public void gameplayLoop() {
        //Prioritize upgrading the autoclick first.
        //Not sure if it's possible for clicks to get lost,
        //Which is why I'm using clickCookieUntilCount instead of clickMultiple
        clickCookieUntilCount(15);
        buyProduct("Cursor");

        buyProductUntilCount("Cursor", 1);

        clickCookieUntilCount(100);
        buyUpgrade("Reinforced index finger", 10);

        clickCookieUntilCount(500);
        buyUpgrade("Carpal tunnel prevention cream", 10);

        buyProductUntilCount("Cursor", 5);
        buyProductUntilCount("Grandma", 1);
        buyProductUntilCount("Cursor", 10);
        buyProductUntilCount("Grandma", 5);
        buyProductUntilCount("Farm", 1);
    }

    public void buyProductUntilCount(String name, int targetCount) {
        int productCount = 0;

        HashMap<String, ShopProduct> catalog = getCompleteProductCatalog();
        ShopProduct product = catalog.get(name);
        if (product == null) {
            System.out.println("ERROR: buyProductUntilCount : Could not find product, aborting.");
            return;
        }

        productCount = product.getCount();

        while (productCount < targetCount) {
            int cost = product.getCost();
            if (cost < getCookieCount()) {

                try{
                    product.buy();
                    productCount += buyIncrement;
                }
                catch(Exception e) {
                    System.out.println("ERROR: buyProductUntilCount : Exception while buying item.");
                }

                //Reset stuff after buying to prevent stale element
                catalog = getCompleteProductCatalog();
                product = catalog.get(name);
            }
            else
            {
                clickCookieUntilCount(cost);
            }
        }
    }

    private boolean buyProduct(String name) {
        return buyProduct(name, getAvailableProductCatalog());
    }

    private boolean buyProduct(String name, HashMap<String, ShopProduct> catalog) {
        boolean bought = false;
        ShopProduct product = catalog.get(name);
        if (product != null) {
            bought = product.buy();
            if (bought) {
                System.out.println("buyProduct : Bought " + name);
            }
        }
        return bought;
    }

    private HashMap<String, ShopProduct> getAvailableProductCatalog() {
        HashMap<String, ShopProduct> productCatalog = new HashMap<String, ShopProduct>();
        List<WebElement> availableProducts = driver.findElements(By.cssSelector("div[class='product unlocked enabled']"));
        for (WebElement element : availableProducts) {
            ShopProduct product = createShopProduct(element);
            //System.out.println(product.toString() + "\n");
            if (product != null) {
                productCatalog.put(product.getName(), product);
            }
        }
        return productCatalog;
    }

    private HashMap<String, ShopProduct> getCompleteProductCatalog() {
        HashMap<String, ShopProduct> productCatalog = new HashMap<String, ShopProduct>();
        List<WebElement> allProducts = driver.findElements(By.className("product"));
        for (WebElement element : allProducts) {
            ShopProduct product = createShopProduct(element);
            if (product != null) {
                System.out.println(product.toString() + "\n");
                productCatalog.put(product.getName(), product);
            }
        }
        return productCatalog;
    }

    private ShopProduct createShopProduct(WebElement element) {
        ShopProduct product = null;

        String name = "";
        int cost = -1, count = -1;

        WebElement nameElement = element.findElement(By.className("productName"));
        if (nameElement != null) {
            name = nameElement.getText();
        }
        else {
            System.out.println("ERROR: createShopProduct: Could not find product name.");
        }

        //Only create product listing if name is valid.
        if (!name.isEmpty()) {
            WebElement priceElement = element.findElement(By.className("price"));
            if (priceElement != null) {
                try {
                    cost = Integer.parseInt(priceElement.getText().replace(",", "").trim());
                }
                catch(NumberFormatException e) {
                    System.out.println("ERROR: createShopProduct: Could not parse price.");
                    e.printStackTrace();
                }
            }
            else {
                System.out.println("ERROR: createShopProduct: Could not find price.");
            }

            WebElement countElement = element.findElement(By.className("owned"));
            if (countElement != null) {
                String countText = countElement.getText();
                if (!countText.isEmpty()) {
                    try {
                        count = Integer.parseInt(countText.replace(",", "").trim());
                    }
                    catch(NumberFormatException e) {
                        System.out.println("ERROR: createShopProduct: Could not parse count.");
                        e.printStackTrace();
                    }
                }
                else {
                    count = 0;
                }
            }
            else {
                System.out.println("ERROR: printProductInfo: Could not find count.");
            }

            product = new ShopProduct(element, name, cost, count);
        }

        return product;
    }

    private boolean buyUpgrade(String name, int retryCount) {
        boolean bought = false;
        int attempts = 0;
        while (!bought && attempts < retryCount) {
            int currentCookies = getCookieCount();
            HashMap<String, ShopUpgrade> catalog = getUpgradeCatalog();
            ShopUpgrade upgrade = catalog.get(name);
            if (upgrade != null) {
                //Redundant check to account for delays in the number updating.
                if (upgrade.cost > getCookieCount()) {
                    System.out.println("buyUpgrade : Not enough money for " + name);
                    return false;
                }
                bought = upgrade.buy();
                if (bought) {
                    System.out.println("buyUpgrade : Bought " + name);
                }
            }
            attempts++;

            if (!bought && attempts >= retryCount) {
                System.out.println("buyUpgrade : Could not buy upgrade after max retries.");
            }
        }
        return bought;
    }

    private HashMap<String, ShopUpgrade> getUpgradeCatalog() {
        HashMap<String, ShopUpgrade> upgradeCatalog = new HashMap<String, ShopUpgrade>();
        List<WebElement> availableUpgrades = driver.findElements(By.cssSelector(".crate.upgrade"));
        for (WebElement element : availableUpgrades) {
            ShopUpgrade upgrade = null;
            try {
                new WebDriverWait(driver, Duration.ofMillis(500))
                        .until(ExpectedConditions.elementToBeClickable(element));

                upgrade = createShopUpgrade(element);
            }
            catch (Exception e) {
                upgrade = null;
            }

            if (upgrade != null) {
                //System.out.println(upgrade.toString());
                upgradeCatalog.put(upgrade.getName(), upgrade);
            }
        }

        return upgradeCatalog;
    }

    private ShopUpgrade createShopUpgrade(WebElement element) {
        ShopUpgrade upgrade = null;

        //Info only shows on mouseover
        new Actions(driver).moveToElement(element).perform();
        WebElement tooltipCrate = driver.findElement(By.id("tooltipCrate"));
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(500));
            wait.until(ExpectedConditions.visibilityOf(tooltipCrate));
            String name = "";
            int cost = -1;
            String costString = "";

            //TODO: REFACTOR
            //I don't like that this is being used to check cost.
            //Disabled elements have a .price.disabled class
            int myCookieCount = getCookieCount();

            WebElement costElement = tooltipCrate.findElement(By.className("price"));
            wait.until(ExpectedConditions.visibilityOf(costElement));
            if (costElement != null) costString = costElement.getText();
            try {
                cost = Integer.parseInt(costString.replace(",", "").trim());
            }
            catch (NumberFormatException e) {
                System.out.println("ERROR: createShopUpgrade : Failed to parse cost.");
                e.printStackTrace();
            }

            WebElement nameElement = tooltipCrate.findElement(By.className("name"));
            wait.until(ExpectedConditions.visibilityOf(nameElement));
            if (nameElement != null) name = nameElement.getText();

            if (name.isEmpty() || cost < 0) {
                upgrade = null;
                System.out.println("ERROR: createShopUpgrade : Failed to parse ShopUpgrade.");
            }
            /*else if (cost > myCookieCount) {
                System.out.println("createShopUpgrade : Cannot afford upgrade, skipping.");
            }*/
            else {
                upgrade = new ShopUpgrade(element, name, cost);
            }
        }
        catch(Exception e) {
            upgrade = null;
            System.out.println("ERROR: createShopUpgrade : Exception when creating ShopUpgrade entry.");
        }
        return upgrade;
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

    public void clickCookieUntilCount(int i) {
        int currentCount = getCookieCount();
        while (currentCount < i) {
            clickCookie();
            currentCount = getCookieCount();
        }
    }

    //This might lag behind the actual cookie count a bit
    public int getCookieCount() {
        String cookieText = "";
        try {
            cookieText = new WebDriverWait(driver, Duration.ofMillis(500))
                    .until(ExpectedConditions.visibilityOf(cookieCount)).getText();
        }
        catch (Exception e) {
            System.out.println("ERROR: getCookieCount: Could not get Cookie Count web element.");
            return -1;
        }
        int cookieCount = -1;
        if (!cookieText.isEmpty()) {
            try {
                String[] split = cookieText.split(" ");
                if (split.length > 0) {
                    cookieText = split[0];
                    cookieCount = Integer.parseInt(cookieText.replace(",", "").trim());
                }
                else
                {
                    System.out.println("ERROR: getCookieCount: Could not split Cookie Count text.");
                }
            }
            catch (NumberFormatException e) {
                System.out.println("ERROR: getCookieCount: NumberFormatException while parsing Cookie Count.");
                e.printStackTrace();
            }
        }
        else {
            System.out.println("ERROR: getCookieCount: Cookie Count text was empty.");
        }

        return cookieCount;
    }

    private class ShopProduct {
        private WebElement element;
        private String name;
        private int cost, count;

        public ShopProduct(WebElement element, String name, int cost, int count) {
            this.element = element;
            this.name = name;
            this.cost = cost;
            this.count = count;
        }

        public String getName() { return name; }
        public int getCost() { return cost; }
        public int getCount() { return count; }

        public String toString() {
            return name + "\nCost: " + cost + "\nCount: " + count;
        }

        public boolean buy() {
            boolean bought = false;
            try {
                new WebDriverWait(driver, Duration.ofMillis(500))
                        .until(ExpectedConditions.elementToBeClickable(element)).click();
                return true;
            }
            catch (Exception e) {
                System.out.println("ERROR: ShopProduct::buy : Could not buy item.");
                e.printStackTrace();
            }
            return bought;
        }
    }

    private class ShopUpgrade {
        private WebElement element;
        private int cost;
        private String name;

        public ShopUpgrade(WebElement element, String name, int cost) {
            this.element = element;
            this.name = name;
            this.cost = cost;
        }

        public String getName() { return name; }
        public int getCost() { return cost; }

        public String toString() {
            return name + "\nCost: " + cost;
        }

        public boolean buy() {
            boolean bought = false;
            try {
                new WebDriverWait(driver, Duration.ofMillis(500))
                        .until(ExpectedConditions.elementToBeClickable(element)).click();
                return true;
            }
            catch (Exception e) {
                System.out.println("ERROR: ShopUpgrade::buy : Could not buy item due to an exception.");
            }
            return bought;
        }
    }
}
