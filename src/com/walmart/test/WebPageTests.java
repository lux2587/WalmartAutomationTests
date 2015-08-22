package com.walmart.test;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.*;
//import org.openqa.selenium.safari.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.AfterTest;
//import org.testng.annotations.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class WebPageTests {

	public WebDriver _driver;
	public String _baseURL = "http://mobile.walmart.com";

	// Timer to wait on elements to appear to be visible
	WebDriverWait _wait;
	WebElementHelper _elemHelper;

	/**
	 * Set up configurations to be done before the test
	 * 
	 * @throws Exception
	 */
	@BeforeTest
	public void setup() throws Exception {
		// Set up the webdriver for chrome
		System.setProperty("webdriver.chrome.driver",
				"/path/to/driver/chromedriver");
		_driver = new ChromeDriver();
		_wait = new WebDriverWait(_driver, 20);
		_elemHelper = new WebElementHelper(_driver, null);
		// Goto to mobile.walmart.com
		_driver.get(_baseURL);

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
			throws InterruptedException {
		try {
			// Search item, select, add to cart and save the product ID of the
			// item
			String itemIDAddedToCart = handleUntilLoginAndGetItemId(searchText,
					accountId, "testpassword");

			// Goto cart
			clickOnCart();

			// Get the Product ID of the item in the cart and assert against the
			// itemIDAddedToCart
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
			// Handle all cases till login
			handleUntilLoginAndGetItemId(searchText, accountId, "testpassword");

			// Make sure that there was an error
			String loginErrorMessageXpath = "//div[1]/section/section[4]/div/div/div/div/div/div/form/div/span";
			WebElement loginError = _elemHelper
					.waitAndgetWebElement(loginErrorMessageXpath);

			Assert.assertTrue(loginError != null,
					"Authentication succeded with invalid credentials");

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
	 * Input the search text
	 * 
	 * @param searchText
	 */
	public void searchText(String searchText) {
		// SearchText
		WebElement searchTextBar = _elemHelper
				.waitAndgetWebElement("id('top')/div[3]/div/div/div/div/div[3]/form/div/div[2]/span/input");
		if (searchTextBar != null) {
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
				.getWebElementFromDriverAndClick("id('top')/div[3]/div/div/div/div/div[3]/form/div/div[3]/button");
	}

	/**
	 * Click on 1 item from the results
	 */
	public void clickOnOneItemFromTheSearchList() {
		_elemHelper.waitForLoad();
		_wait.until(ExpectedConditions.visibilityOfElementLocated(By
				.id("tile-container")));

		// check if the item can be added to cart by checking if the
		// product price is visible
		String productPriceDetail = null;
		_elemHelper.waitForLoad();
		String noPriceXpath = "id('tile-container')/div[1]/div/div/div[1]/div[1]/div";
		WebElement productPriceElement = _elemHelper
				.getWebElementFromDriver(noPriceXpath);
		if (productPriceElement != null) {
			String seeDeatilsinCartXpath = "id('tile-container')/div[1]/div/div/div[1]/div[1]/div";
			WebElement seeDetailsinCart = _elemHelper
					.getWebElementFromDriver(seeDeatilsinCartXpath);
			if (seeDetailsinCart != null)
				productPriceDetail = seeDetailsinCart.getText();
		}

		if (!productPriceDetail.contains("See details in cart")) {
			_elemHelper
					.getWebElementFromDriverAndClick("id('tile-container')/div[1]/div/div/h4/a");
		} else {
			_elemHelper
					.getWebElementFromDriverAndClick("id('tile-container')/div[2]/div/div/h4/a");
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
				"Product Id not found, so can't compare");
		String productIDAddedToCart = webElement
				.getAttribute("data-product-id");
		return productIDAddedToCart;
	}

	/**
	 * Click on add to cart button
	 */
	public void clickAddToCart() {
		// Wait for "Add to Cart" button to be visible
		_elemHelper.waitForLoad();
		_wait.until(ExpectedConditions.elementToBeClickable(By
				.id("WMItemAddToCartBtn")));

		// Handle expanded add cart for iPhone, pop-up to enter zip code.
		WebElement addCart = _elemHelper
				.getWebElementFromDriverById("WMItemAddToCartBtn");
		boolean isAttributePresent = _elemHelper.isAttributePresent(addCart,
				"aria-expanded");
		if (isAttributePresent) {
			WebElement elem = _elemHelper
					.getWebElementFromDriverById("WMItemAddToCartBtn");
			boolean checkAddToCartExpanded = false;
			if (elem != null) {
				String aria = elem.getAttribute("aria-expanded");
				checkAddToCartExpanded = aria != null
						&& aria.equalsIgnoreCase("true");
			}

			if (checkAddToCartExpanded) {
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
		clickAddToCart();

		// Handle Pop-up message after Add to cart clicked
		handlePopUp();

		// Login through My Account
		clickMyAccount();
		signIntoAccount(accountId, password);

		// Product ID of the item added to cart
		return itemIDAddedToCart;

	}

	/**
	 * 
	 */
	public void handleSpecialCaseWhenSeacrhDoesnotShowItemList() {
		try {
			By specialCase = By
					.xpath("//div[1]/section/section[4]/div/div/div[3]/div[1]/div[1]");
			WebElement clickSeeItems = _driver.findElement(specialCase);
			if (clickSeeItems != null) {

				WebElement urlToSeeItemsContent = _driver
						.findElement(By
								.xpath("//div[1]/section/section[4]/div/div/div[3]/div[1]/div[1]/a"));
				if (urlToSeeItemsContent != null) {
					urlToSeeItemsContent.click();
				}
			}
		} catch (Exception ex) {
			// ignore
		}

	}

	/**
	 * Handle case when a pop up appears after clicking on any item
	 */
	public void handlePopUp() {
		_wait.until(ExpectedConditions.visibilityOfElementLocated(By
				.xpath("id('spa-layout')/div/div/div/button")));
		_driver.findElement(By.xpath("id('spa-layout')/div/div/div/button"))
				.click();
	}

	/**
	 * Click on My account
	 */
	public void clickMyAccount() {
		_elemHelper.waitForLoad();
		String signInPath = "//*[@id='top']/div[3]/div/div/div/div/div[4]/div/div[1]/div[1]/p/span[2]/a";
		_wait.until(ExpectedConditions.elementToBeClickable(By
				.xpath(signInPath)));
		_elemHelper.getWebElementFromDriverAndClick(signInPath);
	}

	/**
	 * Click on the signin left navigation
	 */
	public void clickSignInFromLeftNavBar() {
		_elemHelper.waitForLoad();
		_wait.until(ExpectedConditions.elementToBeClickable(By
				.xpath("//*[@id='top']/div[2]/div/nav/div/div[1]/div[1]/a")));
		_elemHelper
				.getWebElementFromDriverAndClick("//*[@id='top']/div[2]/div/nav/div/div[1]/div[1]/a");
	}

	/**
	 * Sign in the user
	 * 
	 * @param accountId
	 * @param password
	 */
	public void signIntoAccount(String accountId, String password) {
		WebElement accountIdTextField = _elemHelper
				.waitAndgetWebElementById("login-username");
		if (accountIdTextField != null) {
			accountIdTextField.clear();
			accountIdTextField.sendKeys(accountId);
		}
		WebElement passwordTextField = _driver.findElement(By
				.id("login-password"));
		if (passwordTextField != null)
			passwordTextField.sendKeys(password);

		String signInXpath = "//form[@class='js-signin-form signin-form']//div/button";
		_elemHelper.getWebElementFromDriverAndClick(signInXpath);
	}

	/**
	 * Click on cart
	 */
	public void clickOnCart() {
		_elemHelper.waitForLoad();
		_driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		_wait.until(ExpectedConditions.elementToBeClickable(_driver.findElement(By
				.xpath("/html/body/div[1]/section/section[4]/div/div/div/div/div/div[2]/div[5]/a"))));
		String cartNumberOfItems = "id('top')/div[3]/div/div/div/div/div[4]/div/div[2]/div/a/b";
		_wait.until(ExpectedConditions.elementToBeClickable(By
				.xpath(cartNumberOfItems)));
		String clickOnCartXpath = "id('top')/div[3]/div/div/div/div/div[4]/div/div[2]/div/a/i";
		WebElement clickOnCart = _driver
				.findElement(By.xpath(clickOnCartXpath));
		_driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		if (clickOnCart != null)
			clickOnCart.click();
	}

	/**
	 * Remove item from cart
	 */
	public void removeItemFromCart() {
		// remove items from cart for clean up
		_elemHelper.waitForLoad();
		By removeButtonXpath = By.id("CartRemItemBtn");
		_wait.until(ExpectedConditions.elementToBeClickable(_driver
				.findElement(removeButtonXpath)));
		WebElement removeButton = _driver.findElement(removeButtonXpath);
		if (removeButton != null)
			removeButton.click();

	}

	/**
	 * Sign out the user
	 */
	public void signOut() {
		// Signout at end of every test

		_driver.get("https://www.walmart.com/account/logout");

		/*
		 * Below steps can be used to hover over "My Account" and the invisible
		 * element "Sign Out". Below steps aren't tried and tested need some
		 * more work to be done. But this is another way to handle Sign Out.
		 * 
		 * // WebElement myAccountHoverOver = // driver.findElement(By.xpath(
		 * "id('top')/div[3]/div/div/div/div/div[4]/div/div[1]/div[1]/div/a"));
		 * // Actions builder = new Actions(driver); //
		 * builder.moveToElement(myAccountHoverOver).build().perform(); // //
		 * WebElement signOutHoverOver = //
		 * driver.findElement(By.xpath("id('flyout17')/ul/span[1]/li[6]/a")); //
		 * builder.moveToElement(signOutHoverOver); // builder.click(); //
		 * builder.perform(); // signOutHoverOver.click();
		 */

	}

	/**
	 * Quit the driver after each method
	 */
	@AfterTest
	public void tearDown() throws Exception {
		_driver.quit();
	}

}
