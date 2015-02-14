/**
 * Created by Jeff on 2/10/2015.
 */
import java.util.*;

public class Attack {

    public static ArrayList<Content> OrderTargetContent(Node target)
    {
        //Attacker needs to poll traffic it sees to get popular list
        //HOW LONG SHOULD THIS POLL?
        //Use content custodians to order content by popularity

        //Grab
        ArrayList<Content> contentordered = Collections.list(target.contentCustodians.keys());
        return contentordered;
    }

    public static Node FindBestTarget(Node attacker, ArrayList<Node> custodians)
    {
        //Could also look at picking node with most hops or distance from attacker

        //Start by getting random custodian
        Random rand = new Random(custodians.size());
        Node target = custodians.get(rand.nextInt(custodians.size()));
        return target;
    }





    public static void RunAttack(Node attacker, Node target){

    }

}
