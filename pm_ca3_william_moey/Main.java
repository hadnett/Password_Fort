package pm_ca3_william_moey;

/**
 *
 * @author willMoeyNotFoundException
 */
public class Main
{

    public static void main(String[] args)
    {
        try
        {
            PasswordManager passwordFort = new PasswordManager();
            passwordFort.verifyUser();
        }
        catch (Exception e)
        {
            System.out.println("A fatal error has occured please restart!");
        }
    }
}
