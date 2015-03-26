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
    Map<Content,Integer> allContent;
    int numRequestsTotal;
    int numUnpopularItems;
    boolean donePolling;
    boolean readyToAttack;
    boolean allPacketsFromCustodian;
    int indexInList;
    int characteristicTimeStatus;
    int attackStatus;
    int startWait;
    int numattacks = 0;

    public AttackerNode(int NodeID, int cacheSize, int cacheType, int numUnpopularItems, int numRequests){
        super(NodeID, cacheSize, cacheType);
        maxCacheSize = cacheSize;
        numRequestsServed = 0;
        this.numRequestsTotal = numRequests;
        this.numUnpopularItems = numUnpopularItems;
        allContent = new HashMap<Content, Integer>();
        //Guess Cache using theory that all nodes same size cache
        cacheSizeGuess = cacheSize;
        //Guess Characteristic Time to large value
        characteristicTimeGuess = cacheSize*numUnpopularItems;
        donePolling = false;
        readyToAttack = false;
        allPacketsFromCustodian = true;
        characteristicTimeStatus = 1;
        attackStatus = 1;

    }

    public Packet receiveData(Packet p)
    {
        //Attacker counts number of requests received
        numRequestsServed++;

        //Add content being searched for to attacker lists
            //What defines popular or not?
        if(allContent.containsKey(p.search))
        {
            int num = allContent.get(p.search);
            allContent.replace(p.search,num+1);
        }else {
            allContent.put(p.search, 1);
        }


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

        }else {
            //Not found in cache, add to cache and forward to next hop
            //DISABLE/ENABLE CACHE HERE
            if (p.cacheEnabled)
                addToCache(p.search);
            p.hops++;
            powerDrain(1);
        }
            //Before return, check if poll done
            //Check if warmup phase is complete.
            //Warmup phase = Whats the best way for this?
            if(numRequestsServed == 500)
            {
                donePolling = true;
                target = FindBestTarget(this, custodians);
                //estimate cacheSize
                cacheSizeGuess = maxCacheSize;
                allPacketsFromCustodian = true;

                //Sort all content and put least popular in unpopular list
                //Map sorted = sortByValue(allContent);
                //List<Map.Entry<Content,Integer>> sortedList = new LinkedList<Map.Entry<Content, Integer>>(sorted.entrySet());

                //Grab most unpopular in graph
                Map<Content, Double> popContent = new HashMap<Content, Double>();
                ArrayList<Content> all = new ArrayList<Content>();
                Enumeration e = contentCustodians.keys();
                while(e.hasMoreElements()){
                    Content d = (Content) e.nextElement();
                    popContent.put(d,d.probability);
                }
                Map sorted = sortByValue(popContent);
                List<Map.Entry<Content,Integer>> sortedList = new LinkedList<Map.Entry<Content, Integer>>(sorted.entrySet());

                //Add only up to size specified to unpopular content list
                for(int s = 0; s < numUnpopularItems; s++){
                    //Add unpopular files to variable
                    Map.Entry<Content,Integer> currentEntry = sortedList.get(s);
                    unpopularContent.add(currentEntry.getKey());
                }//end for



            }
            return p;


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
        //4 - ready to decide on value
        if(characteristicTimeStatus == 2) {
            startWait++;
            //if number of requests seen more than characteristic time guess, then time to request again
            if(startWait >= characteristicTimeGuess){
                characteristicTimeStatus = 3;
            }

            //Send content to next route
            p = p.next.receiveData(p);

        }//end if waiting phase
        else {
            if (characteristicTimeStatus == 1 || characteristicTimeStatus == 3) {
                //Alter the request and request an unpopular file
                p.search = unpopularContent.get(0);
                p.dest = this.contentCustodians.get(p.search);
                p.route = Dijkstra.getShortestPath(this, p.dest);
                p.next = p.route.get(1);

                p = p.next.receiveData(p);
            }

            //start waiting or set ready to attack
                //if done with phase 1, start waiting
                if (characteristicTimeStatus == 1) {
                    startWait = 0;
                    characteristicTimeStatus = 2;
                }
                //Else done guessing characteristic time
                if(characteristicTimeStatus == 3) {
                    //ready to attack?
                    //all from custodian check done outside of class in search class

                    startWait = 0;
                    characteristicTimeStatus = 4;
                }//end if
                if(characteristicTimeStatus == 4){
                    //if all requests returned from custodian, then reduce T* guess
                    if (allPacketsFromCustodian) {
                        characteristicTimeGuess = characteristicTimeGuess / 2;
                        characteristicTimeStatus = 1;
                    }//end if all packets from custodian
                    else {
                        finalCharTimeGuess = characteristicTimeGuess + 2;
                        characteristicTimeStatus = 1;
                        readyToAttack = true;
                    }//end else

                }//end if

        }//end else for not in waiting phase
        return p;
    }//end guessCharacteristicTime

    public Packet sendAttack(Packet pack){

        //Need to know where in the process we are
        //Possible values attackStatus
        //1 - attacking
        //2 - waiting phase between attack requests

        numattacks++;
        if(attackStatus==2){
            startWait++;
            //if number of requests seen more than characteristic time guess, then time to request again
            if(startWait >= finalCharTimeGuess){
                attackStatus = 1;
            }

            pack = pack.next.receiveData(pack);
        }//end waiting
        else {
            //Run attack
            //Every 10 requests, request a popular file i.e. leave it unaltered
            if (indexInList % 10 == 0 && indexInList != 0) {

                pack = pack.next.receiveData(pack);

                //increment indexInList to request new file on next run
                if (indexInList < (unpopularContent.size() - 1)) {
                    indexInList++;
                } else {
                    startWait = 0;
                    attackStatus = 2;
                    indexInList = 0;
                }//end else for increment
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
                if (indexInList < (unpopularContent.size() - 1)) {
                    indexInList++;
                } else {
                    startWait = 0;
                    attackStatus = 2;
                    indexInList = 0;
                }//end else for increment

            }//end else for request decision
        }//end else for waiting phase
        return pack;

    }//end sendAttack()

    public static <K, V extends Comparable<? super V>> Map<K, V>
    sortByValue( Map<K, V> map )
    {
        List<Map.Entry<K, V>> list =
                new LinkedList<Map.Entry<K, V>>( map.entrySet() );
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        } );

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list)
        {
            result.put( entry.getKey(), entry.getValue() );
        }
        return result;
    }


}//end attackernode class


