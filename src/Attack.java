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

    public static int GuessCacheSize(Node attacker, Node target, List<Content> K, int cacheSizeGuess){

        int SumCacheHits = 0;
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

        return 1;
    }

    public static int GuessCharacteristicTime(Node target, int CacheSizeGuess) {
        return 1;
    }

    public static void RunAttack(Node attacker, Node target){

    }

}
