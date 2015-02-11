/**
 * Created by Jeff on 2/10/2015.
 */
import java.util.*;

public class Attack {

    public static void OrderAllContent(Node n)
    {

    }

    public static Node FindBestTarget(Node attacker, ArrayList<Node> custodians)
    {
        Random rand = new Random(custodians.size());
        Node target = custodians.get(rand.nextInt(custodians.size()));
        return target;
    }
}
