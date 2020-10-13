package pm_ca3_william_moey;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 *
 * @author willMoeyNotFoundException
 */
public class PasswordManager
{

    //***********************
    // STATIC FIELDS
    //***********************
    public static final String MESSAGE_WELCOME = "Welcome to Password Fort\n";
    public static final String MESSAGE_PASSWORD = "Please enter your password > ";
    public static final String MESSAGE_PROMPT_OPTION = "Please select an option > ";
    public static final String MESSAGE_LOGIN = "\nPlease now login\n";
    public static final String MESSAGE_NEW_USER = "\nIt's your first time lets get you Registered!";
    public static final String MESSAGE_NO_FILE = "Sorry File Not Found!";
    public static final String MESSAGE_CONFIRM_PASSWORD = "\nPlease confirm your password below.";
    public static final String MESSAGE_CONFIRM_FAILED = "\nPasswords did not match please try again > ";
    public static final String MESSAGE_CONFIRM_DELETE = "Are you sure you want to delete this website, all data will be lost! [Yes/No] > ";
    public static final String MESSAGE_GOODBYE = "\nGoodbye...";
    public static final String MESSAGE_WEBSITE_ID = "\nPlease enter the websites id > ";
    public static final String MESSAGE_PROMPT_TITLE = "Please enter the websites title [Google] > ";
    public static final String MESSAGE_PROMPT_URL = "Please enter the websites URL [https://www.example.com] > ";
    public static final String MESSAGE_WEBSITE_ADDED = "\nWebsite Added";
    public static final String MESSAGE_WEBSITE_FAILED = "\nFailed to add website please try again!";
    public static final String MESSAGE_WEBSITE_CANCELLED = "\nCancelled...";
    public static final String MESSAGE_GET_PASSWORD = "\nEnter the wesbite's ID to get your password or enter Q to Quit > ";
    public static final String MESSAGE_DELETE_WEBSITE = "\nPlease enter the Id of the website you would like to delete > ";
    public static final String MESSAGE_WEBSITE_DELETED = "\nWebsite Deleted!";
    public static final String MESSAGE_DELETE_FAILED = "\nFailed to delete website try again!";
    public static final String MESSAGE_WEBSITE_EDITED = "\nWebsite Edited";
    public static final String MESSAGE_FAILED_EDIT = "\nFailed to edit website!";
    public static final String MESSAGE_RESET_MASTER = "Are you sure you want to reset your master password [Yes/No] > ";
    public static final String MESSAGE_PASSWORD_RESET = "\nPassword Changed!";
    public static final String MESSAGE_GET_TITLE = "\nPlease enter the websites title > ";
    public static final String MESSAGE_GET_URL = "\nPlease enter the website URL > ";
    public static final String MESSAGE_CANT_FIND = "\nSorry we couldn't find the website you are after!";
    public static final String MESSAGE_PASSWORD_LENGTH = "Please enter password length [length >= 8] or enter Q to quit > ";
    public static final String MESSAGE_COPY_PASSWORD = "\nYou can now copy this password and use it where you like!";
    public static final String MESSAGE_LOADING = "\nloading...";

    public static final String ERROR_NOT_OPTION = "\n[Not An Option] Not an option try again!";
    public static final String ERROR_STRING_ENTERED = "\n[Input Mismatch]You tried to enter a string try again!";
    public static final String ERROR_CREDENTIALS_INCORRECT = "\n[Incorrect Credentails]\nSorry Incorrect Credentails\n";
    public static final String ERROR_PASSWORD_REUSE = "\n[Password Reuse] You are reusing a password are you okay with this [Yes/No]> ";
    public static final String ERROR_USER_OUTPUT_FAIL = "\n[No User]Sorry user could not be output!";
    public static final String ERROR_FILE_NOT_FOUND = "\n[File Not Found]Sorry your file could not be found!";
    public static final String ERROR_NO_USER = "\n[No Such User]Sorry the user does not exist!";
    public static final String ERROR_NO_WEBSITE = "\n[No Website] This website does not exist please try again > ";
    public static final String ERROR_BAD_ID = "\n[Id Invalid] Id must be a number";
    public static final String ERROR_RESET_FAILED = "\n[Details Don't Match]The details entered do not match!";

    /**
     * The number of attempts the user is allowed at entering the incorrect
     * password.
     */
    public static final int QUIT_LOGIN = 10;

    //***********************
    //FIELDS
    //***********************
    private Scanner keyboard;
    private boolean running;
    private User user;

    //**********************
    //CONSTRUCTORS
    //**********************
    /**
     * Constructs a new PasswordManager object.
     */
    public PasswordManager()
    {

        this.keyboard = new Scanner(System.in);
        this.running = false;
        this.user = new User();

    }

    //*********************
    //PRIVATE FIELDS
    //*********************
    private void checkUserExists(String fileName)
    {

        File userFile = new File(fileName);
        if (userFile.exists())
        {
            logUserIn();
        }
        else
        {
            registerNewUser();
        }
    }

    private String confirmPassword(String password)
    {

        boolean isMatch = false;
        String confirmPassword = "";
        while (!isMatch)
        {
            System.out.println(MESSAGE_CONFIRM_PASSWORD);
            confirmPassword = getPassword();

            if (confirmPassword.equals(password))
            {
                isMatch = true;
            }
            else
            {
                System.out.println(MESSAGE_CONFIRM_FAILED);
                confirmPassword = getPassword();
            }
        }

        return confirmPassword;
    }

    private String getPassword()
    {
        System.out.print(MESSAGE_PASSWORD);
        return keyboard.nextLine();
    }

    private int getId()
    {

        System.out.println(MESSAGE_WEBSITE_ID);
        boolean isValidId = false;
        int websitePosition = -1;
        while (!isValidId)
        {
            String input = this.keyboard.nextLine();
            if (Utilities.isNumeric(input))
            {
                int id = this.user.checkId(Integer.parseInt(input));
                if (id > -1)
                {
                    websitePosition = id;
                    isValidId = true;
                }
                else
                {
                    System.out.print(ERROR_NO_WEBSITE);
                }
            }
            else
            {
                System.out.print(ERROR_BAD_ID);
            }
        }
        return websitePosition;
    }

    private boolean findMatchingPassword(String password)
    {
        boolean isAcknowledged = false;
        if (this.user.checkRepeatedPasswords(password))
        {
            if (!Utilities.checkYesOrNo(ERROR_PASSWORD_REUSE))
            {
                isAcknowledged = true;
            }
            else
            {
                isAcknowledged = false;
            }
        }
        return isAcknowledged;
    }

    private String encryptWebsiteData()
    {
        String websiteData = "";
        for (int i = 0; i < this.user.getWebsites().size(); i++)
        {
            websiteData += this.user.getWebsites().get(i).websiteToOutputFormat();

        }
        return Cipher.encryptString(websiteData, this.user.getFortKey());
    }

    private String decryptWebsiteData(String websiteData)
    {
        return Cipher.decryptString(websiteData, this.user.getFortKey());
    }

    private void displayPasswordCriteria()
    {
        ArrayList<String> passwordCriteria = new ArrayList<>();
        passwordCriteria.add("");
        passwordCriteria.add("• Should contain atleast one UPPER case letter.");
        passwordCriteria.add("• Should contain atleast one lower case letter.");
        passwordCriteria.add("• Should contain atleast one digit.");
        passwordCriteria.add("• Should contain atleast one special character [#?!@$%^&+-].");
        passwordCriteria.add("• Should be atleast eight characters in length.");
        passwordCriteria.add("• Note there is no defacto standard set for the creation of passwords. "
                + "\n  But, here at Password Fort this is what we believe makes a 'more sercure' Password!");
        passwordCriteria.add("");

        for (String criteria : passwordCriteria)
        {
            System.out.println(criteria);
        }
        Utilities.awaitForEnter();
    }

    private void getLargestWebsiteId()
    {
        int max = 0;
        for (int i = 0; i < this.user.getWebsites().size(); i++)
        {
            if (this.user.getWebsites().get(i).getId() > max)
            {
                max = this.user.getWebsites().get(i).getId();
            }
        }
        Website.setNextWebsiteNumber(max);
    }

    private static long unixTime()
    {
        return System.currentTimeMillis() / 1000L;
    }

    private static long calculateTimeoutSeconds(int attempts)
    {
        return attempts > 0
                ? (long) Math.pow(2, (attempts - 1))
                : 0;
    }

    //********************
    //PUBLIC FIELDS
    //********************
    /**
     * Checks to see if user.txt files exists. If not then a new user is
     * registered, if it does the existing user is allowed to login.
     */
    public void verifyUser()
    {
        checkUserExists("user.txt");
    }

    /**
     * Logs the user into the system if they have provided the correct password.
     * Else it will call the back off algorithm to slow down attempts.
     */
    public void logUserIn()
    {
        System.out.println(MESSAGE_WELCOME);
        String password = getPassword();
        System.out.println(MESSAGE_LOADING);
        this.user.setExistingUser("user.txt");
        this.user.setAttempts(1);
        this.user.getLastAttemptTime();
        this.user.setLastAttemptTime(unixTime());

        if (this.user.logUserIn(password))
        {
            this.user.generateFortKey(password);
            if (new File("passwordStore.txt").exists())
            {
                readWebsiteFromFile();
            }
        }
        else
        {
            checkAttempts(password);
        }

        runPasswordManager();
    }

    /**
     * Prevents user from making and attempt until time-limit has run out.
     * Please note the this exponential back-off algorithm and its helper
     * methods where taking from Shane Gavins lecture notes.
     *
     * @param password The users password.
     */
    public void checkAttempts(String password)
    {
        while (this.user.getAttempts() < QUIT_LOGIN && !this.user.logUserIn(password))
        {
            long currentTime = unixTime();
            long timeoutSeconds = calculateTimeoutSeconds(this.user.getAttempts());
            long nextAttemptTime = this.user.getLastAttemptTime() + timeoutSeconds;
            if (currentTime >= nextAttemptTime)
            {
                System.out.println(ERROR_CREDENTIALS_INCORRECT);
                password = getPassword();
                if (this.user.logUserIn(password))
                {
                    this.user.generateFortKey(password);
                    if (new File("passwordStore.txt").exists())
                    {
                        readWebsiteFromFile();
                    }
                }
                else
                {
                    this.user.setAttempts(this.user.getAttempts() + 1);
                    this.user.setLastAttemptTime(currentTime);
                }
            }
        }

    }

    /**
     * Gathers the information needed to create a new user and calls the
     * necessary methods needed to create one.
     */
    public void registerNewUser()
    {

        System.out.println(MESSAGE_NEW_USER);

        boolean isValidPassword = false;
        while (!isValidPassword)
        {
            String password = getPassword();
            try
            {
                password = confirmPassword(password);
                this.user = new User(password);
                isValidPassword = true;
            }
            catch (IllegalArgumentException e)
            {
                System.out.println(e.getMessage());
                displayPasswordCriteria();
            }
        }

        outputNewUserCredentials(user);

        System.out.println(MESSAGE_LOGIN);
        verifyUser();
    }

    /**
     * Outputs the users credentials once they have been registered. So that
     * they can login again in the future.
     *
     * @param user
     */
    public void outputNewUserCredentials(User user)
    {

        try (PrintWriter userOut = new PrintWriter(new File("user.txt")))
        {
            userOut.print(user.toOutputFormat());
        }
        catch (FileNotFoundException e)
        {
            System.out.println(ERROR_USER_OUTPUT_FAIL);
        }
    }

    /**
     * Starts the Password Manager once the users has logged in and runs the
     * applications main menu.
     */
    public void runPasswordManager()
    {
        this.running = true;
        while (this.running)
        {
            printMenuOptions();
            getMenuOption();
        }
    }

    /**
     * Prints the systems menu options.
     */
    public void printMenuOptions()
    {
        ArrayList<String> menuOptions = new ArrayList<>();
        menuOptions.add("");
        menuOptions.add("1. New Website");
        menuOptions.add("2. Edit Website");
        menuOptions.add("3. View All Website's");
        menuOptions.add("4. Search for Website");
        menuOptions.add("5. Delete A Website");
        menuOptions.add("6. Reset Master Password");
        menuOptions.add("7. Generate Password");
        menuOptions.add("8. Quit Password Fort ");
        menuOptions.add("");

        for (String option : menuOptions)
        {
            System.out.println(option);
        }

        System.out.print(MESSAGE_PROMPT_OPTION);
    }

    /**
     * Processes the input given by the user when navigating throughout the main
     * menu.
     */
    public void getMenuOption()
    {
        try
        {
            int selection = this.keyboard.nextInt();

            switch (selection)
            {
                case 1:
                    getWebsiteDetails();
                    break;
                case 2:
                    editWebsiteInformation();
                    break;
                case 3:
                    displayAllWebsites();
                    break;
                case 4:
                    runSearchByMenu();
                    break;
                case 5:
                    deleteWebsite();
                    break;
                case 6:
                    confirmReset();
                    break;
                case 7:
                    generatePassword();
                    break;
                case 8:
                    stopPasswordManager();
                    break;
                default:
                    System.out.println(ERROR_NOT_OPTION);
                    break;
            }
        }
        catch (InputMismatchException e)
        {
            System.out.println(ERROR_STRING_ENTERED);
            this.keyboard.nextLine();
        }
    }

    /**
     *
     */
    public void stopPasswordManager()
    {
        System.out.println(MESSAGE_GOODBYE);
        this.running = false;
        writeWebsitesToFile();
    }

    /**
     *
     */
    public void getWebsiteDetails()
    {
        this.keyboard.nextLine();
        System.out.print(MESSAGE_PROMPT_TITLE);
        String title = this.keyboard.nextLine();

        System.out.print(MESSAGE_PROMPT_URL);
        String url = this.keyboard.nextLine();
        url = Utilities.checkString(url);

        String password = getPassword();
        password = Utilities.checkString(password);

        addWebsite(title, url, password);

    }

    /**
     * Adds a users Website to the system.
     *
     * @param title The title of the website.
     * @param url The websites URL
     * @param password The password for the website.
     */
    public void addWebsite(String title, String url, String password)
    {
        if (!findMatchingPassword(password))
        {
            if (this.user.addWebsite(title, url, password))
            {
                System.out.println(MESSAGE_WEBSITE_ADDED);
                writeWebsitesToFile();
            }
            else
            {
                System.out.println(MESSAGE_WEBSITE_FAILED);
            }
        }
        else
        {
            System.out.println(MESSAGE_WEBSITE_CANCELLED);
        }
    }

    /**
     * Displays all the websites within the system.
     */
    public void displayAllWebsites()
    {

        this.user.displayAllWebsites();
        getPasswordForWebsite();
    }

    /**
     * Allows the user to select a website that they want to get the password
     * for.
     */
    public void getPasswordForWebsite()
    {

        String selection = "";
        this.keyboard.nextLine();

        while (!selection.equalsIgnoreCase("Q"))
        {
            System.out.print(MESSAGE_GET_PASSWORD);
            selection = this.keyboard.nextLine();

            if (Utilities.isNumeric(selection))
            {
                this.user.displayWebsitePassword(Integer.parseInt(selection));
            }
            else if (!selection.equalsIgnoreCase("Q"))
            {
                System.out.println(ERROR_BAD_ID);
            }
        }
    }

    /**
     * Deletes a website from the system.
     */
    public void deleteWebsite()
    {

        this.user.displayAllWebsites();
        this.keyboard.nextLine();

        System.out.print(MESSAGE_DELETE_WEBSITE);
        String id = keyboard.nextLine();

        if (Utilities.isNumeric(id))
        {
            if (confirmDelete())
            {
                this.user.deleteWebsite(Integer.parseInt(id));
                System.out.println(MESSAGE_WEBSITE_DELETED);
                writeWebsitesToFile();
            }
            else
            {
                System.out.println(MESSAGE_DELETE_FAILED);
            }
        }
        else
        {
            System.out.println(ERROR_NOT_OPTION);
        }
    }

    /**
     * Confirms the deletion of a website.
     *
     * @return Boolean Whether or not the user has confirmed the delete.
     */
    public boolean confirmDelete()
    {
        return Utilities.checkYesOrNo(MESSAGE_CONFIRM_DELETE);
    }

    /**
     * Allows the user to edit the details of each website within the system.
     */
    public void editWebsiteInformation()
    {

        this.user.displayAllWebsites();
        this.keyboard.nextLine();
        int websitePosition = getId();

        Website website = this.user.getWebsites().get(websitePosition);

        int id = website.getId();

        System.out.print("Please enter title: [" + website.getTitle() + "] > ");
        String title = keyboard.nextLine();

        System.out.print("Please enter URL: [" + website.getURL() + "] > ");
        String URL = keyboard.nextLine();

        System.out.print("Please enter password: [" + website.getPassword() + "] > ");
        String password = keyboard.nextLine();

        if (this.user.editWebsite(id, title, URL, password))
        {
            writeWebsitesToFile();
            System.out.println(MESSAGE_WEBSITE_EDITED);
        }
        else
        {
            System.out.println(MESSAGE_FAILED_EDIT);
        }
    }

    /**
     * Confirms the reset of the master password.
     */
    public void confirmReset()
    {
        this.keyboard.nextLine();
        boolean confirm = Utilities.checkYesOrNo(MESSAGE_RESET_MASTER);

        if (confirm)
        {
            boolean isChanged = false;
            while (!isChanged)
            {
                isChanged = resetMasterPassword();
            }
        }
    }

    /**
     * Resets the users master password for the system.
     *
     * @return Boolean Whether or not the change has been made.
     */
    public boolean resetMasterPassword()
    {
        System.out.print("\nPlease confirm old password > ");
        String oldPassword = this.keyboard.nextLine();

        System.out.print("Please enter new password > ");
        String newPassword = this.keyboard.nextLine();

        System.out.print("Please confirm new password > ");
        String confirmPassword = this.keyboard.nextLine();

        boolean isChanged = false;
        try
        {
            if (this.user.resetMasterPassword(oldPassword, newPassword, confirmPassword))
            {
                System.out.println(MESSAGE_PASSWORD_RESET);
                this.outputNewUserCredentials(this.user);
                writeWebsitesToFile();
                isChanged = true;
            }
            else
            {
                System.out.println(ERROR_RESET_FAILED);
            }
        }
        catch (IllegalArgumentException e)
        {
            System.out.println(e.getMessage());
            displayPasswordCriteria();
        }

        return isChanged;
    }

    /**
     * Display the different ways that a website can be searched for within the
     * system.
     */
    public void displaySearchByMenu()
    {

        ArrayList<String> searchByOptions = new ArrayList<>();
        searchByOptions.add("1. Search By Title");
        searchByOptions.add("2. Search By URL");

        for (String option : searchByOptions)
        {
            System.out.println(option);
        }

        System.out.print(MESSAGE_PROMPT_OPTION);
    }

    /**
     * Processes the input given while using the searchOptions menu.
     */
    public void runSearchByMenu()
    {

        displaySearchByMenu();

        try
        {
            int selection = this.keyboard.nextInt();

            switch (selection)
            {
                case 1:
                    searchByTitle();
                    break;
                case 2:
                    searchByURL();
                    break;
                case 3:
                    getMenuOption();
                    break;
                default:
                    System.out.println(ERROR_NOT_OPTION);
                    break;
            }
        }
        catch (InputMismatchException e)
        {
            System.out.println(ERROR_STRING_ENTERED);
            this.keyboard.nextLine();
        }
    }

    /**
     * Allows the user to search the system for a website by it's title.
     */
    public void searchByTitle()
    {
        this.keyboard.nextLine();
        System.out.print(MESSAGE_GET_TITLE);
        String input = this.keyboard.nextLine();

        ArrayList<Website> results = this.user.searchByName(input);

        if (results.size() > 0)
        {
            System.out.printf("\n%-15s%-35s%-20s%-15s\n", "Title", "URL", "Password", "Last Updated");
            Utilities.printLine("-", 85);
            for (int i = 0; i < results.size(); i++)
            {
                results.get(i).outputWebsiteResults();
            }
        }
        else
        {
            System.out.println(MESSAGE_CANT_FIND);
        }
    }

    /**
     * Allows the user to search the system for a website by it's URL.
     */
    public void searchByURL()
    {
        this.keyboard.nextLine();
        System.out.print(MESSAGE_GET_URL);
        String input = this.keyboard.nextLine();

        ArrayList<Website> results = this.user.searchByURL(input);

        if (results.size() > 0)
        {
            System.out.printf("\n%-15s%-35s%-20s%-15s\n", "Title", "URL", "Password", "Last Updated");
            Utilities.printLine("-", 85);
            for (int i = 0; i < results.size(); i++)
            {
                results.get(i).outputWebsiteResults();
            }
        }
        else
        {
            System.out.println(MESSAGE_CANT_FIND);
        }
    }

    /**
     * Writes all the website data out to file.
     */
    public void writeWebsitesToFile()
    {
        try (PrintWriter websitesOut = new PrintWriter(new File("passwordStore.txt")))
        {
            websitesOut.print(encryptWebsiteData());
        }
        catch (FileNotFoundException e)
        {
            System.out.println(ERROR_FILE_NOT_FOUND);
        }
    }

    /**
     * Reads all the website data in from file and stores said data in an
     * ArrayList contained within the users class.
     */
    public void readWebsiteFromFile()
    {
        try (Scanner sc = new Scanner(new File("passwordStore.txt")))
        {
            String websiteData = sc.nextLine();
            websiteData = decryptWebsiteData(websiteData);
            Scanner lineScanner = new Scanner(websiteData);
            lineScanner.useDelimiter("[,\r\n]");
            while (lineScanner.hasNext())
            {

                int id = lineScanner.nextInt();
                String title = lineScanner.next();
                String URL = lineScanner.next();
                String password = lineScanner.next();
                String lastUpdated = lineScanner.next();
                LocalDate updated = LocalDate.parse(lastUpdated);

                addWebsiteFromFile(id, title, URL, password, updated);
            }
            lineScanner.close();
            this.getLargestWebsiteId();
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
     * Generates different passwords for the user of varying length.
     */
    public void generatePassword()
    {
        boolean isQuit = false;
        keyboard.nextLine();
        while (!isQuit)
        {
            System.out.print(MESSAGE_PASSWORD_LENGTH);
            String input = this.keyboard.nextLine();

            if (Utilities.isNumeric(input))
            {
                int length = Integer.parseInt(input);
                System.out.println("\nNew Password: " + Password.generatePassword(length));
                System.out.println(MESSAGE_COPY_PASSWORD);
                Utilities.awaitForEnter();
            }
            else if (input.equalsIgnoreCase("q"))
            {
                isQuit = true;
            }
            else
            {
                System.out.println(ERROR_NOT_OPTION);
            }
        }
    }

    /**
     * Adds the users website from file.
     *
     * @param id The website id.
     * @param title The website title.
     * @param URL The website URL
     * @param password The website password.
     * @param updated The date the website was last updated.
     */
    public void addWebsiteFromFile(int id, String title, String URL, String password, LocalDate updated)
    {

        Website website = new Website(id, title, URL, password, updated);

        if (!this.user.getWebsites().contains(website))
        {
            this.user.getWebsites().add(website);
        }
    }

}
