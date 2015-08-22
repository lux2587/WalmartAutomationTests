# UI Automation Tests for Walmart's e-commerce website

### Installation guide:
Before running the code you need to set up the following tools and frameworks on your machine

- Java 8 :- 
    You can install and check for system requirements for Java 8 here                       https://www.java.com/en/download/help/index_installing.xml

- Eclipse -: Latest Version preferred. You can install eclipse from here https://eclipse.org/downloads/

- Selenium: (http://www.seleniumhq.org/download/ ) . All the Selenium jar files have been included in the project under the “jars” folder(no need to download again). They are included as part of the project classpath.

- TestNG :- Install the TestNG eclipse plugin from http://testng.org/doc/download.html  

- Drivers:- You need to install different Drivers for different web browsers. For this project you need the Chrome driver to be installed, which can be downloaded from 
https://sites.google.com/a/chromium.org/chromedriver/downloads .
Make sure to add the correct location of the Chrome driver in the code which is explained in one of the next sections.

### Problem Statement/Requirement: 
The requirement for this project was to automate an end-to-end scenario for UI testing the e-commerce website mobile.walmart.com<http://mobile.walmart.com>. 

Scenarios to be tested:
    
    1. Perform a search on home page from a pool of key words given below
    2. Identify an item from the result set that you can add to cart
    3. Add the item to cart and then login using existing account, which is set up with at least one shipping address
    4. Validate that item added is present in the cart and is the only item in the cart

Test Data:

    Account details: lux2587@rediffmail.com / testpassword
    Search terms: tv, socks, dvd, toys, iPhone
    Shipping Address: 850 Cherry Ave, San Bruno, CA 94066

Assumptions:
    
    1. Even if the same item has more than 1 quantity in the cart, it is treated as 1 item. 
    2. According to the problem statement, shipping address should already be present in the account 
       so I did not add a test for validating or adding the address. 
    3. The item we add will have at least 1 item to buy online that the user can add to the  cart
    4. The user does not have any items in the cart.
    5. The project has been tested only on Windows Chrome and Mac Chrome.


### How to Import and setup the project in eclipse?
- Open Eclipse -> Import -> Existing Projects Into Workspace
-	Select Root Directory and then Navigate to the unzipped project folder (which contains the .project file) and make sure that the project shows up in the Projects section
-	Click Finish
-	You should see the project in your workspace now. 
-	Change the chrome driver path in both _**MobileEmulatorTests.java**_ and _**WebPageTests.java**_. Make sure to update the WebDriver statement with the correct Chrome executable path. The “path/to/driver” is where the file “chromedriver.exe” or "chromedriver" has been downloaded. 
  	-  Open both _**MobileEmulatorTests.java**_ and _**WebPageTests.java**_  and modify the following statement
		- Windows :-   _**System.setProperty("webdriver.chrome.driver","/path/to/driver/chromedriver.exe");**_
		- Mac/Linux :- _**System.setProperty("webdriver.chrome.driver", "/path/to/driver/chromedriver");**_


### Run the project:-
    
    1.	Click on Run in the Tool Bar -> Run Configurations -> Right Click on TestNG on the left bar -> click new
        a. Select the imported project WalmartAutomationTests
        b. Select a class for this configuration. There are 2 classes avaiable
            i. WebPageTests 	
            ii. MobileEmulatorTests
        c. Create a configurations for each class by clicking Apply and giving it an
           appropriate name
    2.	Click close.
    3.	Now you can run these configurations by going to Run -> Run configurations -> Select your config and run
    4.	You will see the results in the eclipse console. 

### Possible Bugs found by the automation tests :-
- **Desktop web page** : - Clicking on my account after adding item to cart behaves differently on different OS. Using chrome on a mac, clicking on my account takes you to the login page . But while using chrome on windows , clicking on my account opens two options and then you need to click on sign in to reach the login page.
- **Mobile logout** :- Doing a logout from the mobile emulator takes us to a desktop web page . Because of this, I am using a new browser window for each test data. 
- **Web logout** :- Sometimes doing a logout on the desktop website does not actually log out the user. My test case first caught this bug around 9 pm on August 21, 2015. It is still happening at 3:40 AM PST(August 22, 2015) and hence the test cases are failing. Once this bug goes away , the web tests should pass successfully.
See snapshot
![alt tag](http://i.imgur.com/Vo9IO2b.png)
 
 

### Reasoning behind the technical choices
I have had hands-on experience with Selenium Webdriver TestNG during my internship and it was easier to set-up for the project requirements and get the basic test cases up and running. Also Selenium WebDriver has been around for a while now and has been used widely for UI testing and is tried and tested.

### Solution:
I have automated two scenarios to test the mobile.walmart.com<http://mobile.walmart.com> website on Chrome Web Browser and Mobile simulator(Google Nexus 5) on Chrome browser.

**Scenario 1** : Positive test case

    1. Open Web Browser
    2. Open the link mobile.walmart.com
    3. Perform a search on home page from a pool of key words
    4: Identify an item from the result set that can be added to cart
    5: Add the item selected in Step 4 to Cart
    6. Login using existing account,
    7. Validate that item added is present in the cart by checking against the Product-id 
       and also that there is only one item in the cart

**Scenario 2** : Negative Test Case
    
    1. Open Web Browser
    2. Open the link mobile.walmart.com
    3. Perform a search on home page from a pool of key words
    4. Identify an item from the result set that can be added to cart
    5. Add the item selected in Step 4 to Cart
    6. Login using invalid account
    7. validate if the user isn’t allowed to login/access the account and gets an error message.


### Other tests that can be automated for the problem scenario:
    1. Access the website with no internet connection
    2. Search for an item which doesn’t exist.
    3. Test if the Add cart button isn’t visible.
    4. Test if item quantity is added more than available items (if it is possible on the UI)
    5. If there is any upper limit on the amount to be in the cart, test for amounts 
       almost equal to that higher limit and amount over limit (for example the upper limit to buy online is $50,000 test for 49,999 and 50,000+)
    6. Test login by different paths e.g. by clicking on “Checkout” or by creating “New Account” etc.
    7. Test on various browsers
    8. Test on different emulator devices
    9. Test on actual device
    10. Run the test in parallel on different browser, emulators.
    11.	Use virtual machine to run the tests e.g using sauslabs

### Trade-offs
    1.	To make sure all the test cases run, I have added waitForLoad to wait for the 
    	webpage to completely load and then perform further actions on the WebElements. This maybe optimized.
    2.	Because of the mobile logout bug, I had to invoke a new browser window for each test data call. 
	
### Things that would have been added if more time spent/Things might do differently if spent additional time on project / Missing parts in the code:

- Test on all the OS i.e Windows,MAC,Linux etc.
- Test on all the available and different versions of all the browsers IE, Chrome, FireFox, Safari etc. 
- Mobile Devices: Test on most widely used Devices also with the different versions of OS 
  (Practically it is difficult to test on all devices, shapes and sizes, different OS version, it is best to pick the most widely used devices and OS versions)
- Mobile simulators should also be used to expedite the testing process.
- Reuse more code between web and mobile test classes
- Have a CleanFile() method to reset the account to an ideal state (e.g with no items in cart) before starting the test case.
- Additional test cases mentioned above can be automated
- Take snapshots of the screen during the test cycle
- Highlight the item which is being clicked, or edited etc.
- Try to improve the testing time of the test cases by investigating replacement of wait for web page to load.
- Investigate and work to find a more optimal solution to take the data from one single file instead of “data Provider”
- Make tests more generic, like if the data changes we have to make sure all the special cases are handled, example “Toys or See price details in cart” cases are handled in current tests
- Create separate classes for storing error messages and XPaths in one place.
- Add documentation to all methods with examples and usages.
- Modularize the classes further so that each sub scenario can be tested independently(if  possible)
- Make a few things configurable by storing/retreiving values in an xml.json file. For example, wait timeout, test data. Also handle password securely. 
- Modify and optimize the XPaths so that we don't have to change a lot of code if the web pages change.

### Why is it missing
    Time constraint 

