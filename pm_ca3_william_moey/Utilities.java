package pm_ca3_william_moey;

import java.util.Scanner;

/**
 *
 * @author willMoeyNotFoundException
 */
public class Utilities
{

    //***********************
    // STATIC FIELDS
    //***********************
    public static Scanner input = new Scanner(System.in);

    /**
     * Test to determine if input is Numeric.
     *
     * @param strNum The String to be checked.
     * @return boolean Whether or not the input is numeric.
     */
    public static boolean isNumeric(String strNum)
    {

        if (strNum == null)
        {
            return false;
        }
        try
        {
            Integer.parseInt(strNum);
            return true;
        }
        catch (NumberFormatException e)
        {
            return false;
        }
    }

    /**
     * print a symbol with a specific amount that pass in
     *
     * @param symbol the symbol pass in to print
     * @param numberOfSymbol the number of symbol print
     */
    public static void printLine(String symbol, int numberOfSymbol)
    {
        for (int i = 0; i < numberOfSymbol; i++)
        {
            System.out.print(symbol);
        }
        System.out.println("");
    }

    /**
     *
     */
    public static void awaitForEnter()
    {
        System.out.print("Press enter to continue.");
        input.nextLine();
    }

    /**
     * Checks whether the input given is Yes|yes|No|no
     *
     * @param input The String to be checked.
     * @return Int 0 if No|no, 1 if Yes|yes and -1 if String does not match at
     * all.
     */
    public static int yesOrNo(String input)
    {

        if (input.matches("yes|no|Yes|No"))
        {
            if (input.equalsIgnoreCase("yes"))
            {
                return 1;
            }
            else
            {
                return 0;
            }
        }
        else
        {
            return -1;
        }
    }

    /**
     * Converts a yes or no answer into a boolean true or false.
     *
     * @param message Custom message requesting user input.
     * @return Boolean Whether or not input results in true or false returned.
     */
    public static boolean checkYesOrNo(String message)
    {
        boolean isTrueFalse = false;
        boolean isValid = false;
        while (!isValid)
        {
            System.out.print(message);
            String selection = input.nextLine();
            switch (Utilities.yesOrNo(selection))
            {
                case 1:
                    isTrueFalse = true;
                    isValid = true;
                    break;
                case 0:
                    isTrueFalse = false;
                    isValid = true;
                    break;
                default:
                    System.out.println("You did not enter Yes or No. Try Again!");
                    break;
            }
        }
        return isTrueFalse;
    }

    /**
     * Checks whether or not a String is empty.
     *
     * @param toCheck The String to be checked.
     * @return String A string with length greater than 0.
     */
    public static String checkString(String toCheck)
    {
        Scanner sc = new Scanner(System.in);

        boolean isValid = false;

        while (!isValid)
        {
            if (toCheck.length() == 0)
            {
                System.out.print("You cannot enter a String length 0. Try Again! > ");
                toCheck = sc.nextLine();
            }
            else
            {
                isValid = true;
            }
        }
        return toCheck;
    }
}
