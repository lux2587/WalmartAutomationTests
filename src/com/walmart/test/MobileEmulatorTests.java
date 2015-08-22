package com.walmart.test;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.*;
//import org.openqa.selenium.safari.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
//import org.testng.annotations.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class MobileEmulatorTests {

	public WebDriver driver;
	public String baseURL = "http://mobile.walmart.com";

	// Timer to wait on elements to appear to be visible
	WebDriverWait wait;
	WebElementHelper _elemHelper;

	/**
	 * Set up configurations to be done before the test
	 * 
	 * @throws Exception
	 */
	@BeforeMethod
	public void setup() throws Exception {
		Map<String, String> mobileEmulationDevice = new HashMap<String, String>();
		mobileEmulationDevice.put("deviceName", "Google Nexus 5");

		Map<String, Object> chromeOptions = new HashMap<String, Object>();
		chromeOptions.put("mobileEmulation", mobileEmulationDevice);
		DesiredCapabilities capabilities = DesiredCapabilities.chrome();
		capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
		System.setProperty("webdriver.chrome.driver",
				"/path/to/driver/chromedriver");
		driver = new ChromeDriver(capabilities);
		wait = new WebDriverWait(driver, 20);//
		_elemHelper = new WebElementHelper(driver, null);

		// Goto to mobile.walmart.com
		driver.get(baseURL);
	}

	@DataProvider(name = "SearchTextAndCredentials")
	public Object[][] getTestData(Method M) {
		if (M.getName().equalsIgnoreCase("addItemToCart")) {
			return new Object[][] { { "tv", "lux2587@rediffmail.com" },
					{ "socks", "lux2587@rediffmail.com" },
					{ "dvd", "lux2587@rediffmail.com" },
					{ "toys", "lux2587@rediffmail.com" },
					{ "iPhone", "lux2587@rediffmail.com" } };
		} else if (M.getName().equalsIgnoreCase("invalidAccountDetails")) {
			return new Object[][] { { "tv", "invalidId@rediffmail.com" } };
		}
		return null;
	}

	/**
	 * The test method which is the starting point for the positive test cases.
	 * 
	 * @param searchText
	 * @param accountId
	 * @throws Exception
	 */
	@Test(description = "Test to Add item to cart, login and assert the added item", dataProvider = "SearchTextAndCredentials")
	public void addItemToCart(String searchText, String accountId)
			throws Exception {
		try {
			// Search item, select, add to cart and save the product ID of the
			// item
			String itemIDAddedToCart = handleUntilLoginAndGetItemId(searchText,
					accountId, "testpassword");

			// Check the item in the cart
			String cartItemInfoXpath = "//*[@id='spa-layout']/div/div/div[1]/div/div[4]/div[2]/div/div[@class='cart-item-row']";
			_elemHelper.handleAfterSigningIn(itemIDAddedToCart,
					cartItemInfoXpath);

		} catch (Exception ex) {
			throw ex;
		} finally {
			// Clean up : remove item from cart and sign out
			try {
				removeItemFromCart();
				signOut();
			} catch (Exception ex) {
				// ignore
			}

		}

	}

	/**
	 * The test method which is the starting point for the negative test cases.
	 * 
	 * @param searchText
	 * @param accountId
	 * @throws Exception
	 */
	@Test(description = "Negative test: Add item to cart and provide invalid credentials and assert on login", dataProvider = "SearchTextAndCredentials")
	public void invalidAccountDetails(String searchText, String accountId) {
		try {
			// Add item to cart and pass invalid credentials
			handleUntilLoginAndGetItemId(searchText, accountId,
					"invalidpassword");

			// Handle error
			String loginErrorMessageXpath = "//div[@class='main-content']/div[2]/div[@class='signin card']/div[@class='info-header field-error']";
			WebElement loginError = _elemHelper
					.waitAndgetWebElement(loginErrorMessageXpath);

			Assert.assertTrue(loginError != null,
					"Authentication succeded with invalid credentials");

		} catch (Exception ex) {
			throw ex;
		} finally {
			// Clean up : remove item from cart and sign out and ignore any
			// exceptions
			try {
				removeItemFromCart();
				signOut();
			} catch (Exception ex) {
				// ignore
			}

		}

	}

	/**
	 * Input the search text
	 * 
	 * @param searchText
	 */
	public void searchText(String searchText) {
		// SearchText
		WebElement searchTextBar = _elemHelper
				.waitAndgetWebElement("//*[@id='top']/div[2]/div/div/div/div/div[3]/form/div/div[1]/span/input");
		// if (searchTextBar != null)
		{
			searchTextBar.clear();
			searchTextBar.sendKeys(searchText);
		}
	}

	/**
	 * Click on the search button
	 */
	public void clickSearchButton() {
		// Click on search button
		_elemHelper
				.getWebElementFromDriverAndClick("//*[@id='top']/div[2]/div/div/div/div/div[3]/form/div/div[2]/button");
	}

	/**
	 * Click on 1 item from the results
	 */
	public void clickOnOneItemFromTheSearchList() {
		wait.until(ExpectedConditions.visibilityOfElementLocated(By
				.xpath("//*[@id='searchContent']/div/div[4]/div[2]")));

		// check if the item can be added to cart by checking if the
		// product price is visible
		WebElement productElement = _elemHelper
				.getWebElementFromDriver("//*[@id='searchContent']/div/div[4]/div[2]/div[1]/div[2]/a/div/div[1]");

		String productPriceDetail = "";
		if (productElement != null)
			productPriceDetail = productElement.getText();

		if (!productPriceDetail.equalsIgnoreCase("See details in cart")) {
			_elemHelper
					.getWebElementFromDriverAndClick("//*[@id='searchContent']/div/div[4]/div[2]/div[1]/div[2]/a/div/div[1]/span/span[1]");
		} else {
			_elemHelper
					.getWebElementFromDriverAndClick("//*[@id='searchContent']/div/div[4]/div[2]/div[2]/div[2]/a/div/div[1]/span/span");
		}
	}

	/**
	 * 
	 * @return
	 */
	public String saveItemProductDetailsBeforeAddCart() {
		// Store the name of the item that we will add to cart in the
		// variable "itemAddedToCart"
		_elemHelper.waitForLoad();
		String itemInfoBeforeAddingToCart = "//div[@class='js-reviews see-all-reviews']";
		WebElement webElement = _elemHelper
				.waitAndgetWebElement(itemInfoBeforeAddingToCart);
		Assert.assertTrue(webElement != null);

		boolean isProductIdPresent = _elemHelper.isAttributePresent(webElement,
				"data-product-id");
		Assert.assertTrue(isProductIdPresent,
				"product Id not found, so can't comapre");
		String productIDAddedToCart = webElement
				.getAttribute("data-product-id");
		return productIDAddedToCart;

	}

	/**
	 * Click on the cart button
	 */
	public void clickOnCart() {
		// Wait for "Add to cart" button to be visible
		_elemHelper.waitForLoad();
		wait.until(ExpectedConditions.elementToBeClickable(By
				.id("WMItemAddToCartBtn")));

		// Handle expanded add cart for iPhone, pop-up to enter zip code.
		WebElement addCart = driver.findElement(By.id("WMItemAddToCartBtn"));
		boolean isAttributePresent = _elemHelper.isAttributePresent(addCart,
				"aria-expanded");
		if (isAttributePresent) {
			WebElement elem = _elemHelper
					.getWebElementFromDriverById("WMItemAddToCartBtn");
			boolean checkAddToChatExpanded = false;
			if (elem != null) {
				String aria = elem.getAttribute("aria-expanded");
				checkAddToChatExpanded = aria != null
						&& aria.equalsIgnoreCase("true");
			}
			if (checkAddToChatExpanded) {
				_elemHelper
						.getWebElementFromDriverAndClick("//div[1]/section/section[4]/div/div[2]/div[1]/div[4]/div[2]/div/div[2]/div/div[2]/div/div[3]/div/div[2]/div/div/div/button/i");
			}
		}

		// Add item to cart
		_elemHelper.getWebElementFromDriverByIdAndClick("WMItemAddToCartBtn");
	}

	/**
	 * Handle all cases until signing into the account
	 * 
	 * @param searchText
	 * @param accountId
	 * @param password
	 * @return
	 */
	public String handleUntilLoginAndGetItemId(String searchText,
			String accountId, String password) {

		// Search Text and Click on Search button
		searchText(searchText);
		clickSearchButton();

		// Handle special case for search text like Toy where the item list
		// isn't visible on the first page
		if (searchText.equalsIgnoreCase("toys")) {
			handleSpecialCaseWhenSeacrhDoesnotShowItemList();
		}

		// Select appropriate item form list and click on the item
		clickOnOneItemFromTheSearchList();

		// Store the product ID of the item added to the cart for assertion
		String itemIDAddedToCart = saveItemProductDetailsBeforeAddCart();

		// Add to cart
		clickOnCart();

		_elemHelper.waitForLoad();
		clickBackButton();

		// Login through Left nav Bar
		clickHomeLeftNavBar();
		clickSignInFromLeftNavBar();
		signIntoAccount(accountId, "testpassword");

		return itemIDAddedToCart;
	}

	/**
	 * Handle special cases for items like toys where the results do not show up
	 * after the first search click
	 */
	public void handleSpecialCaseWhenSeacrhDoesnotShowItemList() {
		try {
			By specialCase = By.id("categoryContent");
			driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			WebElement clickSeeItems = driver.findElement(specialCase);
			if (clickSeeItems != null) {
				WebElement urlToSeeItemsContent = driver
						.findElement(By
								.xpath("//*[@id='categoryContent']/div/div/div[2]/div/a"));
				if (urlToSeeItemsContent != null) {
					urlToSeeItemsContent.click();
				}
			}
		} catch (Exception ex) {
			// ignore
		}
		_elemHelper.waitForLoad();
	}

	/*
	 * Click on the back button
	 */
	public void clickBackButton() {

		WebElement backButton = _elemHelper
				.waitAndgetWebElement("//*[@id='spa-layout']/div/div/div/div/div[1]/div/div[1]/button/span");
		if (backButton != null)
			backButton.click();
	}

	/**
	 * Click on the home left navigation
	 */
	public void clickHomeLeftNavBar() {
		_elemHelper.waitForLoad();
		wait.until(ExpectedConditions.elementToBeClickable(By
				.xpath("//*[@id='top']/div[2]/div/div/div/div/div[1]/div/div[1]/button/i")));

		_elemHelper
				.getWebElementFromDriverAndClick("//*[@id='top']/div[2]/div/div/div/div/div[1]/div/div[1]/button/i");
	}

	/**
	 * Click on the signin left navigation
	 */
	public void clickSignInFromLeftNavBar() {
		_elemHelper.waitForLoad();
		wait.until(ExpectedConditions.elementToBeClickable(By
				.xpath("//*[@id='top']/div[2]/div/nav/div/div[1]/div[1]/a")));

		_elemHelper
				.getWebElementFromDriverAndClick("//*[@id='top']/div[2]/div/nav/div/div[1]/div[1]/a");
	}

	/**
	 * Sign into the user account
	 * 
	 * @param accountId
	 * @param password
	 */
	public void signIntoAccount(String accountId, String password) {
		_elemHelper.waitForLoad();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By
				.xpath("/html/body/div/div[2]/div[2]/div/form/div[1]")));
		WebElement accountIdTextField = _elemHelper
				.waitAndgetWebElement("/html/body/div/div[2]/div[2]/div/form/div[1]/input");
		if (accountIdTextField != null) {
			accountIdTextField.clear();
			accountIdTextField.sendKeys(accountId);
		}

		WebElement passwordTextField = driver.findElement(By
				.xpath("/html/body/div/div[2]/div[2]/div/form/div[2]/input"));
		passwordTextField.sendKeys(password);

		_elemHelper
				.getWebElementFromDriverAndClick("/html/body/div/div[2]/div[2]/div/form/button");
	}

	/**
	 * Remove 1 item from the account
	 */
	public void removeItemFromCart() {
		// remove items from cart for clean up
		_elemHelper.waitForLoad();
		By removeButtonXpath = By.xpath("//*[@id='CartRemItemBtn']/i");
		wait.until(ExpectedConditions.elementToBeClickable(driver
				.findElement(removeButtonXpath)));
		WebElement removeButton = driver.findElement(removeButtonXpath);
		if (removeButton != null)
			removeButton.click();

	}

	/**
	 * Sign out the user
	 */
	public void signOut() {

		_elemHelper
				.getWebElementFromDriverAndClick("//*[@id='top']/div[2]/div/div/div/div/div[1]/div/div[1]/button/i");

		_elemHelper.waitForLoad();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By
				.xpath("//*[@id='top']/div[2]/div/nav/div/div[4]/a")));
		WebElement signOutButton = _elemHelper
				.waitAndgetWebElement("//*[@id='top']/div[2]/div/nav/div/div[4]/a");
		if (signOutButton != null)
			signOutButton.click();
	}

	/**
	 * Quit the driver after each method
	 */
	@AfterMethod
	public void teardown() {
		driver.quit();
	}

}
