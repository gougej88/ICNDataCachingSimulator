import java.util.*;

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
    ArrayList<Content> unpopularContent = new ArrayList<Content>();
    boolean donePolling;
    boolean readyToAttack;
    boolean allPacketsFromCustodian;
    int indexInList;
    int characteristicTimeStatus;
    int startWait;

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
        allPacketsFromCustodian = true;
        characteristicTimeStatus = 1;

    }

    public Packet receiveData(Packet p)
    {
        //Attacker counts number of requests received
        numRequestsServed++;

        //Add content being searched for to attacker lists
        if(!donePolling) {
            //What defines popular or not?
            if (unpopularContent.contains(p.search)) {
                unpopularContent.remove(p.search);

            } else {
                unpopularContent.add(p.search);
            }
        }//end if not done polling

        //If this node is the dest
        //This should not happen on the attacker node.
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
                allPacketsFromCustodian = true;
                //estimate Characteristic Time
                //characteristicTimeGuess = GuessCharacteristicTime(target,cacheSizeGuess,characteristicTimeGuess);

            }
            return p;
        }

    }//end receive data

    public Packet sendData(Packet p)
    {
        //Check if done polling, if so time to attack or guess characteristic time
        //If not just send content along as usual
        if(donePolling && p.src == this) {

                //if not ready to attack then still need to guess characteristic time
                if(!readyToAttack) {

                    p = GuessCharacteristicTime(target,cacheSizeGuess, p);
                }//end if not ready to attack
                //else ready to attack send attack request
                else {
                    p = sendAttack(p);

                }//end else

            }//end if done polling

        else {
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

    public Packet GuessCharacteristicTime(Node target, int CacheSizeGuess, Packet p) {

        //Need to know where in the process we are
        //Possible values characteristicsTimeStatus
        //1 - request unpopular content first run
        //2 - waiting phase between requesting lists
        //3 - request unpopular content second run
        if(characteristicTimeStatus == 2) {
            startWait++;
            //if number of requests seen more than cache size guess time to request again
            if(startWait >= CacheSizeGuess){
                characteristicTimeStatus = 3;
            }

            //Send content to next route
            p = p.next.receiveData(p);

        }//end if waiting phase
        else {
            if (characteristicTimeStatus == 1 || characteristicTimeStatus == 3) {
                //Alter the request and request an unpopular file
                p.search = unpopularContent.get(indexInList);
                p.dest = this.contentCustodians.get(p.search);
                p.route = Dijkstra.getShortestPath(this, p.dest);
                p.next = p.route.get(1);

                p = p.next.receiveData(p);
            }
            //increment indexInList to request new file on next run
            if (indexInList < (unpopularContent.size() - 1)) {
                indexInList++;
            }
            //else we made through entire list of unpopular content
            //start waiting or set ready to attack
            else {
                //if done with phase 1, start waiting
                if (characteristicTimeStatus == 1) {
                    startWait = 0;
                    characteristicTimeStatus = 2;
                    indexInList = 0;
                }
                //Else done guessing characteristic time
                else {
                    //ready to attack?
                    //all from custodian check done outside of class in search class

                    startWait = 0;
                    indexInList = 0;


                    //if all requests returned from custodian, then reduce T* guess
                    if (allPacketsFromCustodian) {
                        characteristicTimeGuess = characteristicTimeGuess / 2;
                        characteristicTimeStatus = 1;
                    }//end if all packets from custodian
                    else {
                        finalCharTimeGuess = characteristicTimeGuess + 2;
                        readyToAttack = true;
                    }//end else

                }//end else. done with second request
            }//end else

        }//end else for not in waiting phase
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
            pack.search = unpopularContent.get(indexInList);
            pack.dest = this.contentCustodians.get(pack.search);
            pack.route = Dijkstra.getShortestPath(this, pack.dest);
            pack.next = pack.route.get(1);

            pack = pack.next.receiveData(pack);

            //increment indexInList to request new file on next run
            if(indexInList < (unpopularContent.size()-1)) {
                indexInList++;
            }else{
                indexInList = 0;
            }//end else for increment

        }//end else for request decision
        return pack;

    }//end sendAttack()




}
