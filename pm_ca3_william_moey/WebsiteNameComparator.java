package pm_ca3_william_moey;

import java.util.Comparator;

/**
 *
 * @author williamhadnett
 */
public class WebsiteNameComparator implements Comparator<Website>
{

    @Override
    public int compare(Website a, Website b)
    {
        return a.getTitle().compareTo(b.getTitle());
    }

}
