import java.util.*;

/**
 * Created by Jeff on 2/14/2015.
 */
public class AttackerNode extends Node {

    int maxCacheSize;
    int numRequestsServed;
    int cacheSizeGuess;
    int characteristicTimeGuess;
    Node target;
    ArrayList<Node> custodians;
    ArrayList<Content> popularContent;
    ArrayList<Content> unpopularContent;




    public AttackerNode(int NodeID, int cacheSize, int cacheType){
        super(NodeID, cacheSize, cacheType);
        maxCacheSize = cacheSize;
        numRequestsServed = 0;
        //Guess Cache using theory that all nodes same size cache
        cacheSizeGuess = cacheSize;
        //Guess Characteristic Time to large value
        characteristicTimeGuess = cacheSize*4;

    }

    public Packet receiveData(Packet p)
    {
        //Attacker counts number of requests received
        numRequestsServed++;

        //If this node is the dest
        if(p.dest.nodeID == this.nodeID)
        {
            p.hops++;
            p.referrer = this;
            UUID s = p.search.contentID;
            int f = content.indexOf(p.search);
            p.data = content.get(f);
            p.found=true;
            powerDrain(1);
            return p;
        }
        //Store content in cache then send along path if not on this node
        //Check in cache, if so sendData
        if(p.cacheEnabled && (searchCache(p.search.contentID) && p.found==false))
        {
            //content found in cache send back to src
            p.found=true;
            //LRU cache move to front of hashmap since used
            if(cacheType==1) {
                cache.remove(p.search.contentID);
                addToCache(p.search);
            }
            p.hops++;
            p.referrer = this;
            p.data = cache.get(p.search.contentID);
            powerDrain(1);
            //System.out.println("Content Found in Cache!!!");
            p.cachehit = true;
            return p;
        }else{
            //Not found in cache, add to cache and forward to next hop
            //DISABLE/ENABLE CACHE HERE
            if(p.cacheEnabled)
                addToCache(p.search);
            p.hops++;
            powerDrain(1);

            //Before return, check if poll done
            if(numRequestsServed == maxCacheSize*2)
            {

                target = FindBestTarget(this, custodians);
                //estimate cacheSize
                cacheSizeGuess = maxCacheSize;
                //estimate Characteristic Time
                characteristicTimeGuess = GuessCharacteristicTime(target,cacheSizeGuess,characteristicTimeGuess);
                //Run attack

            }
            return p;
        }


    }

    public void SetCustodians(ArrayList<Node> cust){
        this.custodians = cust;
    }

    public Node FindBestTarget(Node attacker, ArrayList<Node> custodians)
    {
        //Could also look at picking node with most hops or distance from attacker

        //Start by getting random custodian
        Random rand = new Random(custodians.size());
        Node target = custodians.get(rand.nextInt(custodians.size()));
        return target;
    }

    public int GuessCacheSize(Node attacker, Node target, List<Content> K, int cacheSizeGuess){

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

    public int GuessCharacteristicTime(Node target, int CacheSizeGuess, int CharacteristicTimeGuess) {
        return 1;
    }




}
