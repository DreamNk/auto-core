/*
 * Copyright (c) 2013 Global Healthcare Exchange, LLC. All rights reserved.
 */

package com.ghx.auto.core.ui.page;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import com.ghx.auto.core.ui.support.EnvConfig;

public abstract class AbstractPage<T extends AbstractPage<T>> {

    /* [Lingo]
     * is/check - Y/N
     * verify - fail if N
     * handle
     * enter
     * click
     * submit
     * doAction? e.g. doLogin
     */

    /* [HTML components]
     * link - lnk
     * button - btn
     * text box - txt, tbx
     * check box - chk, cbx
     * radio box - rdo, rbx
     * table header - thr
     * table row - trw
     * table column -
     */

    private WebDriver driver;

    private EnvConfig envConfig;

    private static final int DEFAULT_TIMEOUT = 100;
    

    /**
     * @return Page name.
     */
    public String getName() {
        return me().getClass().getSimpleName();
    }

    public String getApplicationId() {
        return "";
    }
    
    public ResourceBundle getPageTitles() {
    	return null;
    }

    public T load(String configUrl) {
    	String baseUrl = getConfigParamValue(configUrl);
        String pageUrl = getUrl();

        Assert.assertNotNull(StringUtils.defaultIfBlank(baseUrl+pageUrl, null),
                "No url provided on " + getName()
                        + ", Cannot perform load(),");

        this.driver.get(baseUrl + pageUrl);
        return me();
    }
    
    public T refresh_until(final By locator) {

    	verify(new ExpectedCondition<Boolean>() {
        	int loop = 5;
			
        	@Override
			public Boolean apply(WebDriver driver) {
				while (loop-- > 0) {
					if (driver.findElements(locator).size() > 0) {
						return true;
					}
					// else, we need to refresh the page and check again.
					driver.navigate().refresh();
				}
				// exhausted number of max attempts
				return false;
			}
        });
    	
        return me();
    }
    
	public T wait_until(final ExpectedCondition<Boolean> condition,
			final Callable action, long timeOutInSeconds, long sleepInMillis) {

		verify(new ExpectedCondition<Boolean>() {

			@Override
			public Boolean apply(WebDriver driver) {

				try {
					verify(condition, 0, 0);
					return Boolean.TRUE;
				} catch (TimeoutException te) {
					try {
						action.call();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return Boolean.FALSE;
			}
		}, timeOutInSeconds, sleepInMillis);

		return me();
	}
    
  	/**
     * Wait until the passed condition is true, timeout is defined in the configuration.
     *
     * @return current page.
     */
  	protected T wait_until(ExpectedCondition<WebElement> condition) {
    	verify(condition);
        return me();
    } 
  	
    public T handle_browser_exceptions() {
        if (this.driver instanceof InternetExplorerDriver 
        		|| this.driver instanceof RemoteWebDriver) {
            while (this.driver.getTitle().contains("Certificate Error")) {
                this.driver.get("javascript:document.getElementById('overridelink').click()");
            }
        }
        return me();
    }
  	
    public T verify_page_title() throws Error {
        verify(ExpectedConditions.titleIs(getTitle()));
        return me();
    }

    public boolean verify_pageIsLoaded() {
    	//verify title for now, may include elements check later
    	return this.driver.getTitle().equals(getTitle());
    }

    public T enter(By locator, String text) {
    	WebElement textField = findElement(locator); 
        textField.clear();
        try {
            textField.sendKeys(text);
        }
        catch (StaleElementReferenceException ex) {
            textField = findElement(locator);
            textField.sendKeys(text);
        }
        return me();
    }

    public T scan(By locator, String barcode) {
        WebElement body = findElement(locator);
        body.sendKeys("~" + barcode + Keys.RETURN);
        return me();
    }
    
    public T click_button(By locator) {
    	click(locator);
    	return me();
    }

    public T click_link(By locator) {
    	click(locator);
    	return me();
    }
    
    public T click_tab(By locator) {
    	click(locator);
        return me();
    }
    
    public T click_popup(By locator) {
    	click(locator);
        return me();
    }
         
    public T submit(By locator) {
        WebElement submittable = findElement(locator);
        submittable.submit();
        return me();
    }

    public T select_by_value(By locator, String value) {
    	Select selectable = findSelect(locator);
        selectable.selectByValue(value);
        return me();
    }
    
    public T deselect_all(By locator) {
    	Select selectable = findSelect(locator);
        selectable.deselectAll();
        return me();
    }
    
    protected T alert_comments(String text) {
    	this.driver.switchTo().alert().sendKeys(text);
    	return me();
    }

    protected T alert_accept() {
    	this.driver.switchTo().alert().accept();
    	return me();
    }
    
	public static String format_current_date() {
  		DateFormat dateFormat = new SimpleDateFormat("MMM d yyyy");
  		Calendar cal = Calendar.getInstance();
  		String dateString = dateFormat.format(cal.getTime()).toString();
  		return dateString;
  	}
  	
  	public static String patient_identifier() {
  		DateFormat dateFormat = new SimpleDateFormat("MM-dd-yy HH:mm");
  		Date date = new Date();
		return "Automation " + dateFormat.format(date);
  	}
  	
  	public String find_element_value(By locator) {
  		return findElement(locator).getAttribute("value");
  	}
  	
  	public String find_element_text(By locator) {
  		return findElement(locator).getText();
  	}
  	
  	public String find_current_url() {
  		return this.driver.getCurrentUrl();
  	}
  	
 	public T verify_element_selected(By locator) {
 	  	  verify(ExpectedConditions.elementToBeSelected(locator));
 	  	  return me();
 	}
  	
    public T verify_element_by_text (By locator, String text) {
        verify(ExpectedConditions.textToBePresentInElementLocated(locator, text));
        return me();
    }
    
    public void refresh_page() {
    	driver.navigate().refresh();
    }

  	 @SuppressWarnings("unchecked")
     protected T me() {
         return (T) this;
     }
  	 
  	protected T click(By locator) {
        WebElement clickable = findElement(ExpectedConditions.elementToBeClickable(locator));
        clickable.click();
        return me();
    }
  	 
    protected String getUrl() {
        return "";
    }
    
    /**
     * Generic API to retrieve a specific parameter value from application section in configuration.
     *
     * @param param - Parameter to retrieve under a section
     * @return parameter value
     */
    protected String getConfigParamValue(String param) {
    	String paramValue =  this.envConfig.get(getApplicationId().toUpperCase()).get(param);
    	Assert.assertNotNull(StringUtils.defaultIfBlank(paramValue, null),
                "No configuration provided for the param: " + param
                        + ",under section: " + getApplicationId().toUpperCase());
    	return paramValue;
    }

    /**
     * Returns localized page title from resource bundle, if available.
     *
     * @return Localized page title.
     */
    protected String getTitle() {
        // check localized title.
        try {
            return getPageTitles().getString(getName());
        }
        catch (MissingResourceException ex) {
            // no localized title provided
            Assert.fail("No title found for " + getName() + " in " + getApplicationId() +
                    " PageTitles file for " + getPageTitles().getLocale().getDisplayName() + " " +
                    '[' + getPageTitles().getLocale() + ']', ex);
        }
        // should never happen.
        return "Title not found";
    }

    /**
     * Check the Select element is clickable and wait until loaded (i.e. size is > 1).
     *
     * @param locator (web element locator)
     * @return Select.
     */
    private Select findSelect(By locator) {
    	final Select selectable = new Select(findElement(ExpectedConditions.elementToBeClickable(locator)));
        
    	verify(new ExpectedCondition<Boolean>() {
        	
			@Override
			public Boolean apply(WebDriver driver) {
			    return selectable.getOptions().size() > 1;
			}
        });
        
        return selectable;

    }
    
	public T wait_until_SelectOption(By locator, final String option) {
		final Select selectable = new Select(
				findElement(ExpectedConditions.elementToBeClickable(locator)));

		verify(new ExpectedCondition<Boolean>() {

			@Override
			public Boolean apply(WebDriver driver) {
				return selectable.getFirstSelectedOption().getText()
						.equals(option);
			}
		});

		return me();
	}
  
    private WebElement findElement(By locator) {
        return verify(ExpectedConditions.presenceOfElementLocated(locator));
    }

    private WebElement findElement(ExpectedCondition<WebElement> condition) {
        return verify(condition);
    }
    
    /**
     * Verify passed condition, wait for {@link EnvConfig#getTimeout()} before throwing
     * {@link TimeoutException}
     * @param condition {@link ExpectedCondition} to verify.
     * @throws TimeoutException
     * @see WebDriverWait
     */
    @SuppressWarnings("hiding")
    protected <T> T verify(ExpectedCondition<T> condition) {
        return verify(condition, this.envConfig.getTimeout(), DEFAULT_TIMEOUT);
    }

    @SuppressWarnings("hiding")
    protected <T> T verify(ExpectedCondition<T> condition, long timeout, long sleep) {
        return new WebDriverWait(this.driver, timeout, sleep).until(condition);
    }
    
}
