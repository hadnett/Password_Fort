package pm_ca3_william_moey;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author willMoeyNotFoundException
 */
public class Website
{

    //***********************
    // STATIC FIELDS
    //***********************
    public static final String ERROR_MALFORMED_URL = "[Malformed URL] The URL given does not adhere to the correct format!";
    public static final String ERROR_DOMAIN_FAIL = "\n[Invalid Domain] Not a valid top-level domain! e.g. .com, .ie. Try Again!";
    public static final String ERROR_FILE_NOT_FOUND = "[File Not Found] Breached Password File Could Not be Loaded";

    //****************
    //FIELDS
    //****************
    public static int currentWebsiteNumber = 0;
    private int id;
    private String title;
    private String URL;
    private String password;
    private LocalDate lastUpdated;

    //**************
    //CONSTRUCTOR
    //**************
    /**
     * Constructors a new website object.
     *
     * @param id The website id.
     * @param title The website title.
     * @param URL The website URL.
     * @param password The website password.
     * @param lastUpdated The website lastUpdated.
     */
    public Website(int id, String title, String URL, String password, LocalDate lastUpdated)
    {

        this.id = id;
        setTitle(title);
        setURL(URL);
        setPassword(password);
        this.lastUpdated = lastUpdated;
    }

    /**
     * Constructors a new website object.
     *
     * @param title The website title.
     * @param URL The website URL.
     * @param password The website password.
     */
    public Website(String title, String URL, String password)
    {

        currentWebsiteNumber++;

        setId();
        setTitle(title);
        setURL(URL);
        setPassword(password);
        setLastUpdated();
    }

    //******************
    //GETTERS & SETTERS
    //******************
    /**
     * Gets the website id.
     *
     * @return int The website id.
     */
    public int getId()
    {
        return id;
    }

    /**
     * Sets the website id.
     */
    public void setId()
    {
        this.id = this.id + currentWebsiteNumber++;
    }

    /**
     * Gets the website title.
     *
     * @return String The website title.
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Sets the website title.
     *
     * @param title The website title.
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * Gets the website URL.
     *
     * @return String The website URL.
     */
    public String getURL()
    {
        return URL;
    }

    /**
     * Sanitises, normalises, validates and finally sets the website URL.
     *
     * @param URL The website URL.
     */
    public void setURL(String URL)
    {

        /**
         * Sanitisation, normalisation and finally validation of URL's takes
         * place here. Idea of matcher and pattern taken from StackOverflow, but
         * adapted to meet our needs
         * https://stackoverflow.com/questions/11725896/adding-http-before-my-address
         */
        Pattern urlPattern = Pattern.compile("https://www.");
        Matcher urlMatcher = urlPattern.matcher(URL);

        if (!urlMatcher.lookingAt())
        {
            URL = URL.toLowerCase();
            URL = URL.replaceAll("http://|www.", "");
            URL = "https://www." + URL;
        }

        if (!checkDomain(URL))
        {
            throw new IllegalArgumentException(ERROR_DOMAIN_FAIL);
        }

        /**
         * Myself and Moey where aware that Java supports certain network
         * features. Therefore, we suspected that there must be some
         * functionality surrounding URL's so after looking through the Java API
         * we discovered the Java.net library. This allows for the creation of a
         * URL object and validation of said URL. As a malformed URL exception
         * will be thrown is the URL given does not meet RFC2396 Generic Syntax
         * e.g https://www.google.com. Can be found at
         * https://docs.oracle.com/javase/7/docs/api/java/net/URL.html
         */
        try
        {
            this.URL = new URL(URL).toURI().toString();
        }
        catch (MalformedURLException | URISyntaxException e)
        {
            throw new IllegalArgumentException(ERROR_MALFORMED_URL);
        }
    }

    /**
     * Sets the website password.
     *
     * @param password The website password.
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

    /**
     * Gets the website password.
     *
     * @return String the website password.
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * Gets the Last Time the website was Updated.
     *
     * @return LocalDate The Date the website was updated.
     */
    public LocalDate getLastUpdated()
    {
        return lastUpdated;
    }

    /**
     * Sets the Last Time the website was Updated.
     */
    public void setLastUpdated()
    {

        this.lastUpdated = LocalDate.now();
    }

    /**
     * Sets the next website Id when reading in website data from a file.
     *
     * @param nextWebsiteNumber The website id.
     */
    public static void setNextWebsiteNumber(int nextWebsiteNumber)
    {
        currentWebsiteNumber = nextWebsiteNumber;
    }

    //****************
    // PUBLIC FIELDS
    //****************
    @Override
    public String toString()
    {
        return "Website{" + "id=" + id + ", URL=" + URL + ", password=" + password + ", lastUpdated Date=" + lastUpdated + '}';
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 79 * hash + this.id;
        hash = 79 * hash + Objects.hashCode(this.title);
        hash = 79 * hash + Objects.hashCode(this.URL);
        hash = 79 * hash + Objects.hashCode(this.password);
        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final Website other = (Website) obj;
        if (!Objects.equals(this.title, other.title))
        {
            return false;
        }
        if (!Objects.equals(this.URL, other.URL))
        {
            return false;
        }
        if (!Objects.equals(this.password, other.password))
        {
            return false;
        }
        return true;
    }

    /**
     * Outputs the website in a normalised format so that the information can be
     * read back in again.
     *
     * @return
     */
    public String websiteToOutputFormat()
    {
        return this.getId() + "," + this.getTitle() + "," + this.getURL() + "," + this.getPassword() + "," + this.getLastUpdated() + "\n";
    }

    /**
     * Checks to determine if a website password has been repeated.
     *
     * @param password The password to check.
     * @return boolean Whether or not the password is repeated.
     */
    public boolean cheakRepeatPassword(String password)
    {
        return this.getPassword().equals(password);
    }

    /**
     * Checks to see if one of the common top-level domains in contained within
     * the String URL.
     *
     * @param URL The URL to be checked
     * @return boolean Whether or not common top-level domain contained within
     * String
     */
    public boolean checkDomain(String URL)
    {
        ArrayList<String> commonDomains = getCommonDomains();

        for (int i = 0; i < commonDomains.size(); i++)
        {
            if (URL.contains(commonDomains.get(i)))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Reads in a list of the common top-level domains. Domains sourced from
     * https://en.wikipedia.org/wiki/List_of_Internet_top-level_domains
     *
     * @return ArrayList<String> Of Top-level domains.
     */
    public ArrayList<String> getCommonDomains()
    {

        ArrayList<String> commonDomains = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File("commonDomains.txt")))
        {
            while (scanner.hasNext())
            {
                String weakPassword = scanner.nextLine();
                commonDomains.add(weakPassword);
            }

        }
        catch (FileNotFoundException e)
        {
            System.err.println(ERROR_FILE_NOT_FOUND);
        }
        return commonDomains;
    }

    /**
     * Outputs the website in a formatted manner when displaying search results.
     */
    public void outputWebsiteResults()
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyy");
        String lastUpdatedFormatted = formatter.format(this.getLastUpdated());

        System.out.printf("%-15s%-35s%-20s%-15s\n\n", this.getTitle(), this.getURL(), this.getPassword(), lastUpdatedFormatted);

    }
}
