/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pm_ca3_william_moey;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 *
 * @author williamhadnett
 */
public class User
{

    //***********************
    // STATIC FIELDS
    //***********************
    /**
     * The password pattern below was sourced from
     * https://stackoverflow.com/questions/19605150/regex-for-password-must-contain-at-least-eight-characters-at-least-one-number-a
     */
    public static final String PASSWORD_PATTERN = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$";
    public static final String ERROR_NO_WEBSITE = "\n[No Website Found] This website does not exist please try another id!\n";
    public static final String ERROR_FILE_NOT_FOUND = "[File Not Found] Sorry your file could not be found!";
    public static final String ERROR_NO_USER = "[No Such User] Sorry the user does not exist!";
    public static final String ERROR_PASSWORD_WARNING = "\n[Password Warning] We have set your password, but it could be improved!";

    //******************
    //FIELDS
    //******************
    private String passwordSalt;
    private String masterPassword;
    private String keySalt;
    private String fortKey;
    private int attempts;
    private long lastAttemptTime;
    private ArrayList<Website> websites;

    //*****************
    //CONSTRUCTORS
    //*****************
    /**
     * Creates a new user object given the master password.
     *
     * @param masterPassword
     */
    public User(String masterPassword)
    {

        setPasswordSalt();
        setMasterPassword(masterPassword);
        setKeySalt();
        this.websites = new ArrayList<>();
    }

    /**
     * Create a no argument User object.
     */
    public User()
    {

        this.passwordSalt = "";
        this.masterPassword = "";
        this.keySalt = "";
        this.websites = new ArrayList<>();
    }

    //******************
    //GETTERS & SETTERS
    //******************
    /**
     * Gets the password Salt.
     *
     * @return String The Password Salt.
     */
    public String getPasswordSalt()
    {
        return passwordSalt;
    }

    /**
     * Sets the password salt.
     */
    public void setPasswordSalt()
    {
        this.passwordSalt = Password.generateRandomSalt();
    }

    /**
     * Gets the master password (Hash).
     *
     * @return
     */
    public String getMasterPassword()
    {
        return masterPassword;
    }

    /**
     * Sets the master password (Hash)
     *
     * @param masterPassword The users plaintext Password.
     */
    public void setMasterPassword(String masterPassword)
    {

        Password newPassword = new Password(masterPassword, this.getPasswordSalt());
        this.masterPassword = newPassword.generateHash();
    }

    /**
     * Gets the key salt.
     *
     * @return
     */
    public String getKeySalt()
    {
        return keySalt;
    }

    /**
     * Sets the key salt.
     */
    public void setKeySalt()
    {
        this.keySalt = Password.generateRandomSalt();
    }

    /**
     * Gets the Fort Key (Symmetric Encryption and Decryption Key).
     *
     * @return
     */
    public String getFortKey()
    {
        return fortKey;
    }

    /**
     * Sets the Fort Key (Symmetric Encryption and Decryption Key).
     *
     * @param fortKey
     */
    public void setFortKey(String fortKey)
    {
        this.fortKey = fortKey;
    }

    /**
     * Gets the ArrayList of Websites.
     *
     * @return ArrayList of Websites.
     */
    public ArrayList<Website> getWebsites()
    {
        return websites;
    }

    /**
     * Sets the ArrayList of Websites.
     *
     * @param websites The ArrayList of websites to be set.
     */
    public void setWebsites(ArrayList<Website> websites)
    {
        this.websites = websites;
    }

    /**
     * Gets the user Attempts to login.
     *
     * @return int The number of Attempts.
     */
    public int getAttempts()
    {
        return attempts;
    }

    /**
     * Sets the number of Attempts.
     *
     * @param attempts The number of attempts made.
     */
    public void setAttempts(int attempts)
    {
        this.attempts = attempts;
    }

    /**
     * Gets the last Attempt Time.
     *
     * @return Long The last Attempt Time.
     */
    public long getLastAttemptTime()
    {
        return lastAttemptTime;
    }

    /**
     * Sets the Last Attempt Time.
     *
     * @param lastAttemptTime The time the user last made an Attempt.
     */
    public void setLastAttemptTime(long lastAttemptTime)
    {
        this.lastAttemptTime = lastAttemptTime;
    }

    //*****************
    //PRIVATE FIELDS
    //*****************
    private int findWebsite(int id)
    {

        int i = 0;
        int index = -1;
        boolean isFound = false;
        while (!isFound && i < this.getWebsites().size())
        {
            if (this.websites.get(i).getId() == id)
            {
                index = i;
                isFound = true;
            }
            i++;
        }
        return index;
    }

    //*****************
    //PUBLIC FIELDS
    //*****************
    /**
     * Reads in an existing users information so that it can be used to identify
     * said user.
     *
     * @param filePath The file path needed to find the user file.
     */
    public void setExistingUser(String filePath)
    {

        try (Scanner sc = new Scanner(new File(filePath)))
        {
            sc.useDelimiter("[,\r\n]+");
            while (sc.hasNext())
            {
                this.passwordSalt = sc.next();
                this.masterPassword = sc.next();
                this.keySalt = sc.next();
            }

        }
        catch (FileNotFoundException e)
        {
            System.out.println(ERROR_FILE_NOT_FOUND);
        }
        catch (NoSuchElementException e)
        {
            System.out.println(ERROR_NO_USER);
        }
    }

    /**
     * Outputs the user in a normalised format so that the information can be
     * read back in again.
     *
     * @return String The users information.
     */
    public String toOutputFormat()
    {
        return this.getPasswordSalt() + "," + this.getMasterPassword() + "," + this.getKeySalt();
    }

    /**
     * Checks the users log in information and allows them to log in.
     *
     * @param password The users password.
     * @return boolean Whether or not the information given is correct.
     */
    public boolean logUserIn(String password)
    {
        try
        {
            Password usersPassword = new Password(password, this.getPasswordSalt());
            return usersPassword.matchesHash(this.getMasterPassword());

        }
        catch (IllegalArgumentException e)
        {
            return false;
        }
    }

    /**
     * Adds a website to the system.
     *
     * @param title The websites title.
     * @param websiteURL The websites URL.
     * @param password The websites password.
     * @return boolean Whether or not the website was added.
     */
    public boolean addWebsite(String title, String websiteURL, String password)
    {
        boolean isAdded = false;

        Website usersWebsite = null;

        try
        {
            usersWebsite = new Website(title, websiteURL, password);
            checkWebsitePassword(password);

        }
        catch (IllegalArgumentException e)
        {
            System.out.println(e.getMessage());
            Utilities.awaitForEnter();
        }

        if (!this.websites.contains(usersWebsite) && usersWebsite != null)
        {
            this.websites.add(usersWebsite);
            isAdded = true;
        }
        return isAdded;
    }

    /**
     * Checks to see if a password has been reused.
     *
     * @param password The websites password.
     */
    public void checkWebsitePassword(String password)
    {
        if (!password.matches(PASSWORD_PATTERN))
        {
            throw new IllegalArgumentException(ERROR_PASSWORD_WARNING);
        }
    }

    /**
     * Displays all the user in alphabetical order.
     */
    public void displayAllWebsites()
    {
        WebsiteNameComparator compareByName = new WebsiteNameComparator();
        Collections.sort(this.getWebsites(), compareByName);

        System.out.printf("\n%-15s%-15s%-15s\n", "ID", "Title", "URL");
        Utilities.printLine("-", 55);
        for (Website website : this.websites)
        {
            System.out.printf("%-15s%-15s%-15s\n", website.getId(), website.getTitle(), website.getURL());
        }
    }

    /**
     * Displays the websites password.
     *
     * @param selection The website the user wants to view the password for.
     */
    public void displayWebsitePassword(int selection)
    {
        int websitePosition = findWebsite(selection);
        if (websitePosition > -1)
        {
            Website website = websites.get(websitePosition);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyy");
            String lastUpdated = formatter.format(website.getLastUpdated());
            System.out.printf("\n%-15s%-35s%-20s%-15s\n", "Title", "URL", "Password", "Last Updated");
            Utilities.printLine("-", 85);
            System.out.printf("%-15s%-35s%-20s%-15s\n\n", website.getTitle(), website.getURL(), website.getPassword(), lastUpdated);
        }
        else
        {
            System.out.println(ERROR_NO_WEBSITE);
        }
    }

    /**
     * Generates the Fort Key so that the user can encrypt and decrypt their
     * website information.
     *
     * @param password The users password.
     */
    public void generateFortKey(String password)
    {
        setFortKey(new Password(password, this.getKeySalt()).generateHash());
    }

    /**
     * Deletes a website from the system.
     *
     * @param id The websites id.
     * @return boolean Whether or not the website has been deleted.
     */
    public boolean deleteWebsite(int id)
    {

        int websitePosition = findWebsite(id);

        boolean isDeleted = false;
        if (websitePosition > -1)
        {
            this.websites.remove(websitePosition);
            isDeleted = true;
        }
        else
        {
            System.out.println(ERROR_NO_WEBSITE);
        }
        return isDeleted;
    }

    /**
     * Checks if a website exists in the system.
     *
     * @param id The website id.
     * @return int > -1 if Exists == -1 if not.
     */
    public int checkId(int id)
    {
        return findWebsite(id);
    }

    /**
     * Edits a website details.
     *
     * @param id The websites id.
     * @param title The websites title.
     * @param URL The websites URL.
     * @param password The websites password.
     * @return boolean Whether or not the website has been edited.
     */
    public boolean editWebsite(int id, String title, String URL, String password)
    {

        int websitePosition = findWebsite(id);
        boolean isEdited = false;
        if (websitePosition > -1)
        {
            Website website = this.websites.get(websitePosition);
            website.setTitle(title);
            website.setURL(URL);
            website.setPassword(password);
            website.setLastUpdated();
            isEdited = true;
        }
        return isEdited;
    }

    /**
     * Resets the users master password.
     *
     * @param oldPassword The users old password.
     * @param newPassword The users new password.
     * @param confirmPassword Confirm the new password.
     * @return boolean Whether or not the password has been reset.
     */
    public boolean resetMasterPassword(String oldPassword, String newPassword, String confirmPassword)
    {

        if (new Password(oldPassword, this.passwordSalt).matchesHash(this.masterPassword)
                && newPassword.equals(confirmPassword))
        {
            this.setMasterPassword(newPassword);
            this.generateFortKey(newPassword);
            return true;
        }
        else
        {
            return false;
        }

    }

    /**
     * Searches the websites by Title.
     *
     * @param input The title of the website.
     * @return ArrayList of websites that match search criteria.
     */
    public ArrayList<Website> searchByName(String input)
    {
        ArrayList<Website> results = new ArrayList<>();

        for (int i = 0; i < this.getWebsites().size(); i++)
        {
            if (this.getWebsites().get(i).getTitle().contains(input))
            {
                results.add(this.getWebsites().get(i));
            }
        }
        return results;
    }

    /**
     * Searches the wesbites by URL.
     *
     * @param input The URL of the website.
     * @return ArrayList of websites that match the search criteria.
     */
    public ArrayList<Website> searchByURL(String input)
    {
        ArrayList<Website> results = new ArrayList<>();

        for (int i = 0; i < this.getWebsites().size(); i++)
        {
            if (this.getWebsites().get(i).getURL().contains(input))
            {
                results.add(this.getWebsites().get(i));
            }
        }
        return results;
    }

    /**
     * Check if the password is repeated else where in the system.
     *
     * @param password The password to be checked.
     * @return boolean Whether or not the password is repeated.
     */
    public boolean checkRepeatedPasswords(String password)
    {
        int i = 0;
        boolean isRepeated = false;
        while (!isRepeated && i < this.getWebsites().size())
        {
            if (this.getWebsites().get(i).cheakRepeatPassword(password))
            {
                isRepeated = true;
            }
            i++;
        }
        return isRepeated;
    }

}
