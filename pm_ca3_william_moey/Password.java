package pm_ca3_william_moey;

import java.io.File;
import java.io.FileNotFoundException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Scanner;

/**
 *
 * @author willMoeyNotFoundException
 */
public class Password
{

    // #######################################################
    // # Static constants. Default values.
    // #######################################################
    // Configuration values
    public static final String KEY_FACTORY_TYPE = "PBKDF2WithHmacSHA512";
    public static final int ITERATIONS = 65536;
    public static final int KEY_SIZE = 256;
    public static final int SALT_BYTES = 32;

    /**
     * The password pattern below was sourced from
     * https://stackoverflow.com/questions/19605150/regex-for-password-must-contain-at-least-eight-characters-at-least-one-number-a
     */
    public static final String PASSWORD_PATTERN = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$";

    public static final String ERROR_BAD_ALGORITHM = "[Hash error] bad algorithm";
    public static final String ERROR_BAD_SPEC = "[Hash error] bad key specification";
    public static final String ERROR_FILE_NOT_FOUND = "[File Not Found] Breached Password File Could Not be Loaded";
    public static final String ERROR_COMMON_PASSWORD = "[Common Password] This is a common password, try again!";
    public static final String ERROR_STRENGTH_FAIL = "\n[Invalid Password] This password fails to meet the systems criteria, try again!";
    public static final String ERROR_SALT_LENGTH = "[Invalid Salt] The Salt used is the incorrect number of bytes must be 32 bytes or longer!";

    // #######################################################
    // # Instance variables
    // #######################################################
    private String password;
    private String salt;

    // #######################################################
    // # Salt utilities
    // #######################################################
    /**
     * @return A random 32-byte (256 bit) salt value as a Base64-encoded string.
     */
    public static String generateRandomSalt()
    {
        SecureRandom random = new SecureRandom();
        byte[] saltBytes = new byte[SALT_BYTES];
        random.nextBytes(saltBytes);
        return Base64.getEncoder().encodeToString(saltBytes);
    }

    // #######################################################
    // # Constructors
    // #######################################################
    /**
     * Construct from a passed password and salt (short constructor). The
     * iterations and keySize fields are set to the class's defaults for these
     * values.
     *
     * @param password A plaintext password
     * @param salt A password salt value (as a string)
     */
    public Password(String password, String salt)
    {
        this.setPassword(password);
        this.setSalt(salt);
    }

    /**
     * Copy constructor. Construct from an existing PasswordConfig object.
     *
     * @param other Another PasswordConfig object to copy
     */
    public Password(Password other)
    {
        this.setPassword(other.getPassword());
        this.setSalt(other.getSalt());
    }

    // #######################################################
    // # Getters
    // #######################################################
    /**
     * @return The currently set plaintext password.
     */
    public String getPassword()
    {
        return this.password;
    }

    /**
     * @return The currently set password salt value.
     */
    public String getSalt()
    {
        return this.salt;
    }

    // #######################################################
    // # Setters
    // #######################################################
    /**
     * Set the raw (plaintext) password. This method should throw an exception
     * (the type of the exception is up to you) if there is an attempt to set an
     * WEAK password
     *
     * @param password A plaintext password.
     */
    public void setPassword(String password)
    {
        ArrayList<String> weakPasswords = getCommonPasswords();

        if (weakPasswords.contains(password))
        {
            throw new IllegalArgumentException(ERROR_COMMON_PASSWORD);
        }

        if (password.matches(PASSWORD_PATTERN))
        {
            this.password = password;
        }
        else
        {
            throw new IllegalArgumentException(ERROR_STRENGTH_FAIL);
        }
    }

    /**
     * Set the salt value to be used when hashing the current object's password.
     * This method should throw an exception (the type of the exception is up to
     * you) if there is an attempt to set an WEAK salt
     *
     * @param salt A password salt value (as a string)
     */
    public void setSalt(String salt)
    {

        if (Base64.getDecoder().decode(salt).length >= SALT_BYTES)
        {
            this.salt = salt;
        }
        else
        {
            throw new IllegalArgumentException(ERROR_SALT_LENGTH);
        }
    }

    // #######################################################
    // # Hashing Utilities
    // #######################################################
    /**
     * Returns a derived key (hash) that has been generated using PBKDF2
     * (configurable with the KEY_FACTORY_TYPE constant) and SHA512 over
     * {getIterations()} iterations. The resulting hash is returned as a
     * base64-encoded string.
     *
     * @return Hash as a base64-encoded string.
     * @throws PasswordException if the hashing function is not correctly
     * configured.
     */
    public String generateHash()
    {

        try
        {

            char[] passwordChars = this.getPassword().toCharArray();
            byte[] saltBytes = Base64.getDecoder().decode(this.getSalt());

            SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY_FACTORY_TYPE);
            PBEKeySpec spec = new PBEKeySpec(passwordChars, saltBytes, ITERATIONS, KEY_SIZE);
            SecretKey key = factory.generateSecret(spec);

            return Base64.getEncoder().encodeToString(key.getEncoded());
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new PasswordException(ERROR_BAD_ALGORITHM);
        }
        catch (InvalidKeySpecException e)
        {
            throw new PasswordException(ERROR_BAD_SPEC);
        }
    }

    /**
     * Returns true if the passed hash is equal to the result of hashing the
     * current password data (i.e. like calling this.generateHash() and
     * comparing its result to the passed hash)
     *
     * @param hash The pre-computed hash to compare.
     *
     * @return true if the passed and computed hashes match
     * @throws PasswordException if the hashing function is not correctly
     * configured.
     */
    public boolean matchesHash(String hash)
    {
        return this.generateHash().equals(hash);
    }

    private ArrayList<String> getCommonPasswords()
    {

        ArrayList<String> weakPasswords = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File("weakPasswordList.txt")))
        {
            while (scanner.hasNext())
            {
                String weakPassword = scanner.nextLine();
                weakPasswords.add(weakPassword);
            }

        }
        catch (FileNotFoundException e)
        {
            System.err.println(ERROR_FILE_NOT_FOUND);
        }
        return weakPasswords;
    }

    /**
     * Generates a password that is capable of meeting our systems password
     * criteria using SecureRandom.
     *
     * @param length length of the output password
     * @return a valid password with require length
     */
    public static String generatePassword(int length)
    {
        //Passwords Minimum length must be 8.
        if (length < 8)
        {
            length = 8;
        }
        /**
         * Use cryptographically secure random number generator. The idea to use
         * this type of random number generator was introduced in our security
         * classes and was used to produce a random salt in Shane Gavins code
         * above.
         */
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length - 4; i++)
        {
            password.append((char) (random.nextInt(94) + 33));
        }
        char lowerLetters = (char) (random.nextInt(26) + 65);
        char UpperLetters = (char) (random.nextInt(26) + 97);
        char numbers = (char) (random.nextInt(10) + 48);
        char symbols = (char) (random.nextInt(14) + 33); // not including {,|,},~
        // first argument = random position, second  argument = random char
        /**
         * Ensure password policy is met by inserting required random chars in
         * random positions, get 4 more char from each of the char categories
         */
        password.insert(random.nextInt(password.length()), lowerLetters);
        password.insert(random.nextInt(password.length()), UpperLetters);
        password.insert(random.nextInt(password.length()), numbers);
        password.insert(random.nextInt(password.length()), symbols);
        return password.toString();
    }
}
