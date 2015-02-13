/**
 * Created by Jeff on 2/10/2015.
 */
import java.util.*;

public class Attack {

    public static ArrayList<Content> OrderTargetContent(Node target)
    {
        //Attacker node has list of all content on network
        //Order content by popularity - This is done with zipfian distribution, but in real tests we would search
        //and find content that is archived, dated, or sorted by last accessed time.

        //Use content custodians to order content by popularity

        //Grab
        ArrayList<Content> contentordered = Collections.list(target.contentCustodians.keys());
        return contentordered;
    }

    public static Node FindBestTarget(Node attacker, ArrayList<Node> custodians)
    {
        Random rand = new Random(custodians.size());
        Node target = custodians.get(rand.nextInt(custodians.size()));
        return target;
    }

    public static int GuessCacheSize(Node attacker, Node target, List<Content> K, int cacheSizeGuess){

        int SumCacheHits = 0;
        //Request unpopular files in order
        for(Content distinctFile : K)
        {
            Packet p = new Packet(attacker,distinctFile);
            p.dest = attacker.contentCustodians.get(distinctFile);
            p.route = Dijkstra.getShortestPath(p.src,p.dest);
            Packet r = Search.findContent(p);

        }

        //Request same files in reverse order, recording number of cache hits
        Collections.reverse(K);

        for(Content distinctFile : K)
        {
            Packet p = new Packet(attacker,distinctFile);
            p.dest = attacker.contentCustodians.get(distinctFile);
            p.route = Dijkstra.getShortestPath(p.src,p.dest);
            Packet r = Search.findContent(p);
            if(r.cachehit)
                SumCacheHits++;
        }

        Collections.reverse(K);

        if(SumCacheHits == cacheSizeGuess)
        {
            SumCacheHits = 2* SumCacheHits;
            GuessCacheSize(attacker, target, K, SumCacheHits);
        }else{
            return SumCacheHits;
        }
        //If this number is returned then there is an error
        return 1;
    }

    public static int GuessCharacteristicTime(Node target, int CacheSizeGuess, int CharacteristicTimeGuess) {
        return 1;
    }

    public static void RunAttack(Node attacker, Node target){

    }

}
