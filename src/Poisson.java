import java.util.*;

/**
 * Created by n00430588 on 10/22/2014.
 */
public class Poisson {

    public static int getPoisson(double mean) {
        int r = 0;
        Random random = new Random();
        double a = random.nextDouble();
        double p = Math.exp(-mean);

        while (a > p) {
            r++;
            a = a - p;
            p = p * mean / r;
        }
        return r;
    }
}
