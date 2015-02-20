import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jeff on 2/14/2015.
 */
public class AttackerNode extends Node {

    int maxCacheSize;
    int numRequestsServed;
    int cacheSizeGuess;
    int characteristicTimeGuess;
    int finalCharTimeGuess;
    Node target;
    ArrayList<Node> custodians;
    ArrayList<Content> popularContent;
    ArrayList<Content> unpopularContent;
    boolean donePolling;
    boolean readyToAttack;
    int indexInList;





    public AttackerNode(int NodeID, int cacheSize, int cacheType){
        super(NodeID, cacheSize, cacheType);
        maxCacheSize = cacheSize;
        numRequestsServed = 0;
        //Guess Cache using theory that all nodes same size cache
        cacheSizeGuess = cacheSize;
        //Guess Characteristic Time to large value
        characteristicTimeGuess = cacheSize*4;
        donePolling = false;
        readyToAttack = false;

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
        if(p.cacheEnabled && (searchCache(p.search.contentID) && !p.found))
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
                donePolling = true;
                target = FindBestTarget(this, custodians);
                //estimate cacheSize
                cacheSizeGuess = maxCacheSize;
                //estimate Characteristic Time
                //characteristicTimeGuess = GuessCharacteristicTime(target,cacheSizeGuess,characteristicTimeGuess);

            }
            return p;
        }




    }

    public Packet sendData(Packet p)
    {
        //Check if done polling, if so time to attack or guess characteristic time
        //If not just send content along as usual
        if(donePolling) {
            //If this is the requester check what to poll or send instead
            if(p.src == this) {
                //if not ready to attack then still need to guess characteristic time
                if(!readyToAttack) {
                    GuessCharacteristicTime(target,cacheSizeGuess,characteristicTimeGuess, p);
                }
                //else ready to attack send attack request
                else {
                    p = sendAttack(p);

                }

            }

        }else {
            //Send content to next route
            p = p.next.receiveData(p);
            }//end else

        return p;
    }

    public void SetCustodians(ArrayList<Node> cust){
        this.custodians = cust;
    }

    public Node FindBestTarget(Node attacker, ArrayList<Node> custodians)
    {
        //Could also look at picking node with most hops or distance from attacker

        //Start by getting random custodian
        Random rand = new Random(custodians.size());
        Node t = custodians.get(rand.nextInt(custodians.size()));
        return t;
    }

    public int GuessCacheSize(Node attacker, Node target, List<Content> K, int cacheSizeGuess){

        int SumCacheHits = 0;
        //Request unpopular files in order
        for(Content distinctFile : K)
        {
            Packet p = new Packet(attacker,distinctFile);
            //Packet r = Search.findContent(p);

        }

        for(Content distinctFile : K)
        {
            Packet p = new Packet(attacker,distinctFile);
            //Packet r = Search.findContent(p);
            //if(r.cachehit)
                //SumCacheHits++;
        }


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

    public Packet GuessCharacteristicTime(Node target, int CacheSizeGuess, int CharacteristicTimeGuess, Packet p) {

        boolean allPacketsFromCustodian = true;

        //Alter the request and request an unpopular file
        p.data = unpopularContent.get(indexInList);
        p.dest = this.contentCustodians.get(p.data);
        p.route = Dijkstra.getShortestPath(this, p.dest);

        p = p.next.receiveData(p);

        //increment indexInList to request new file on next run
        if(indexInList < (unpopularContent.size()-1)) {
            indexInList++;
        }else{
            indexInList = 0;
        }

            //Request each item in unpopular content
            //Need to make sure items are distinct and unique


        //if all requests returned from custodian, then reduce T* guess
        if(allPacketsFromCustodian)
        {
            characteristicTimeGuess = CharacteristicTimeGuess/2;
        }//end if all packets from custodian
        else{
            finalCharTimeGuess = CharacteristicTimeGuess + 2;

        }//end else

        return p;
    }//end guessCharacteristicTime

    public Packet sendAttack(Packet pack){
        //Run attack
        //Every 10 requests, request a popular file i.e. leave it unaltered
        if(indexInList % 10 ==0) {
         pack = pack.next.receiveData(pack);
        }
        //Else request unpopular file
        else {

            //Alter the request and request an unpopular file
            pack.data = unpopularContent.get(indexInList);
            pack.dest = this.contentCustodians.get(pack.data);
            pack.route = Dijkstra.getShortestPath(this, pack.dest);

            pack = pack.next.receiveData(pack);

            //increment indexInList to request new file on next run
            if(indexInList < (unpopularContent.size()-1)) {
                indexInList++;
            }else{
                indexInList = 0;
            }//end else for increment

        }//end else for request decision
        return pack;

                /*
                int x = 0;
                while(x < attackDuration)
                {
                    int count = 0;
                    for(Content k : unpopularContent)
                    {
                        //Request Content

                        //Every 10 requests
                        if(count % 10 == 0)
                        {
                            //Requeset popular content
                            Packet pop = new Packet(this,popularContent.get(0));
                            Search.findContent(pop);
                        }
                    }//end for each

                    x++;
                }//end while
                */
    }




}
