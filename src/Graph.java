import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


/**
 * Created by n00430588 on 10/3/2014.
 */
public class Graph {
    int graphType;
    int length;
    int width;
    int cacheSize;
    double percentCustodians;
    ArrayList<Node> nodes = new ArrayList<Node>();
    Hashtable<Content,Node> localContentCustodians = new Hashtable<Content, Node>();
    int size;
    int numContentItems;
    int numRequestsPerTest;
    double alpha;
    int cacheType;
    int numAttackers;
    int numUnpopularItemsPerAttacker;
    ArrayList<Node> possibleRequesters = new ArrayList<Node>();
    ArrayList<Node> possibleCustodians = new ArrayList<Node>();
    ArrayList<AttackerNode> attackers = new ArrayList<AttackerNode>();
    ArrayList<Integer> attackIndexes = new ArrayList<Integer>();
    ArrayList<Node> custodians = new ArrayList<Node>();
    ArrayList<Integer> custodianIndexes = new ArrayList<Integer>();
    Boolean useCharacteristicTimeAttack;
    Boolean dijkstraComputed;



    public Graph(int graphType, int size, int cacheSize, double alpha, int cacheType, int numAttackers, int numUnpopularItems, double percentCustodians, int numContentItems, int numRequests, Boolean useTargetedAttack) {
        this.graphType = graphType;
        this.cacheSize = cacheSize;
        this.percentCustodians = percentCustodians;
        this.size = size;
        this.numContentItems = numContentItems;
        this.alpha = alpha;
        this.cacheType = cacheType;
        this.numAttackers = numAttackers;
        this.numUnpopularItemsPerAttacker = numUnpopularItems;
        this.numRequestsPerTest = numRequests;
        this.useCharacteristicTimeAttack = useTargetedAttack;

    }

    public void createGraph(){

        dijkstraComputed = false;
        if(graphType == 1){
            length = (int)Math.sqrt(size);
            width = length;
        }

        int attackerindex = -1;
        Random rand = new Random(size);
        //Assign random requester as attacker
        if(numAttackers >0) {
            for(int a = 0; a < numAttackers; a++){
            attackerindex = rand.nextInt(size);
                if(attackIndexes.contains(attackerindex))
                {
                    a--;
                }else {
                    attackIndexes.add(attackerindex);
                }
            }//end for
        }//end if



        for(int i = 0; i < size; i++)
        {
            if(attackIndexes.contains(i)) {
                AttackerNode att = new AttackerNode(i, cacheSize, cacheType, numUnpopularItemsPerAttacker,numRequestsPerTest);
                attackers.add(att);
                nodes.add(i, att);
            }else {
                Node a = new Node(i, cacheSize, cacheType);
                nodes.add(i, a);
            }//end else

        }//end for

        //Setup the rest of the graph
        //Create all edges and weights
        if(graphType==1) {
            setEdgesSquareGraph();
        }
        if(graphType==2){
            setEdges();
        }
        //Make 20% of the nodes content custodians and assign content to each one
        createContentCustodians(percentCustodians);

        //Pass a hashtable of all content and their respective custodian to all nodes
        //All nodes must know where content lives in order to route correctly
        distributeContentCustodians();

        //Use Distribution to assign popularity to each piece of content in the graph
        assignPopularityDistribution(alpha);

        //Force attackers to attack from beginning
        if(!useCharacteristicTimeAttack) {
            for (AttackerNode att : attackers) {
                //Simulate polling done and ready to guess characteristic time.
                att.numRequestsServed = 500;
                att.donePolling = true;
                //att.target = att.FindBestTarget(att, custodians);
                //estimate cacheSize
                att.cacheSizeGuess = att.maxCacheSize;
                att.allPacketsFromCustodian = true;

                //Grab most unpopular in graph
                Map<Content, Double> popContent = new HashMap<Content, Double>();
                ArrayList<Content> all = new ArrayList<Content>();
                Enumeration e = att.contentCustodians.keys();
                while (e.hasMoreElements()) {
                    Content d = (Content) e.nextElement();
                    popContent.put(d, d.probability);
                }
                Map sorted = att.sortByValue(popContent);
                List<Map.Entry<Content, Integer>> sortedList = new LinkedList<Map.Entry<Content, Integer>>(sorted.entrySet());

                //Add only up to size specified to unpopular content list
                for (int s = 0; s < att.numUnpopularItems; s++) {
                    //Add unpopular files to variable
                    Map.Entry<Content, Integer> currentEntry = sortedList.get(s);
                    att.unpopularContent.add(currentEntry.getKey());
                }//end for

                att.finalCharTimeGuess = 0;
                att.readyToAttack = true;

            }//end for
        }//end if not using characteristic time attack


    }//end createGraph()

    public void setEdges(){
        //Open the file and set edges
        //One per line, fromNode [tab] toNode
        List<Map<Integer,Integer>> edges = new ArrayList<Map<Integer, Integer>>();
        try {

            String filename = "./graphs/p2p-Gnutella08.txt";
            BufferedReader bReader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = bReader.readLine()) != null) {

                String datavalue[] = line.split("\t");
                Map<Integer,Integer> edge = new HashMap<Integer, Integer>();
                edge.put(Integer.parseInt(datavalue[0]),Integer.parseInt(datavalue[1]));
                int fromNode = Integer.parseInt(datavalue[0]);
                int toNode = Integer.parseInt(datavalue[1]);
                nodes.get(fromNode).setEdge(nodes.get(toNode),1);
                nodes.get(toNode).setEdge(nodes.get(fromNode),1);
                edges.add(edge);
                if(!possibleRequesters.contains(nodes.get(fromNode))){
                    possibleRequesters.add(nodes.get(fromNode));
                }
                if(!possibleCustodians.contains(nodes.get(toNode))) {
                    possibleCustodians.add(nodes.get(toNode));
                }
            }
            bReader.close();
        } catch(IOException ex) {
            System.out.println(ex);
        }


    }//end setEdges()

    public void setEdgesSquareGraph(){

        for(int i=0; i<size;i++)
        {
                //Check if node has a neighbor to the right
                if(i%width < width-1) {
                    //System.out.println("Set right edge:" + nodes.get(i).getNodeID()+ "to node: "+nodes.get(i+1).nodeID);
                    nodes.get(i).setEdge(nodes.get(i+1), 1);

                }
                //Check if node has a neighbor to the bottom
                if(i < (size-width)) {
                    //System.out.println("Set bottom edge:" + nodes.get(i).getNodeID()+ "to node: "+nodes.get(i+width).nodeID);
                    nodes.get(i).setEdge(nodes.get(i+width), 1);
                }
                //Check if node has a neighbor to the top
                if(i >= length)
                {
                    //System.out.println("Set top edge:" + nodes.get(i).getNodeID()+ "to node: "+nodes.get(i-length).nodeID);
                    nodes.get(i).setEdge(nodes.get(i-length),1);
                }
                //Check if node has a neighbor to the left
                if(i%length > 0)
                {
                    //System.out.println("Set left edge:" + nodes.get(i).getNodeID()+ "to node: "+nodes.get(i-1).nodeID);
                    nodes.get(i).setEdge(nodes.get(i-1),1);
                }
            possibleRequesters.add(nodes.get(i));
            possibleCustodians.add(nodes.get(i));
        }//end for
    }

    public void createContentCustodians(double percent){
        ArrayList<Integer> n = new ArrayList<Integer>();
        //Get 20% of the graph size and make them content custodians
        double temp = size * percent;
        int numCustodians = (int)temp;
        numContentItems = numContentItems / numCustodians;
        Random rand = new Random();

        //Add attackers to local variable so they do not become custodians
        for(Node x : attackers) {
            n.add(x.nodeID);
        }
        //Loop and create random nodes in the graph as content custodians
        for(int i = 0; i<numCustodians; i++)
        {
            int contentCust = rand.nextInt(size);

            if(!n.contains(contentCust) && possibleCustodians.contains(nodes.get(contentCust))) {
                n.add(contentCust);
                custodians.add(nodes.get(contentCust));
                for(int c = 0; c < numContentItems; c++)
                {
                    nodes.get(contentCust).saveContent("This is content number " + c + " from node "+ contentCust);

                }


            }//end if
            else{
                i--;
            }

        }//end for

    }

    public void assignPopularityDistribution(double Alpha){

        //This is the popularity distribution for content using Zipfian
        int totalContent = localContentCustodians.size();
        //System.out.println("Number of content items: " + totalContent);


        //Create a zipfian distribution
        //Change Alpha here for Zipfian
        Zipf zip = new Zipf(totalContent,Alpha);


        ArrayList<Double> ranks = new ArrayList<Double>();
        double test = 0;
        //Assign the distribution probabilities to an array and shuffle them before assign popularity to each piece of content
        for(int j=0; j<totalContent; j++)
        {
            ranks.add(zip.getProbability(j+1));

        }

        //Shuffle the ranks
        Collections.shuffle(ranks);

        //Assign all the ranks to each piece of content
        Enumeration e = localContentCustodians.keys();
        int i = 0;
        while(e.hasMoreElements()){

            Content k = (Content) e.nextElement();
            k.probability = ranks.get(i);
            i++;
        }

    }

    public Content getZipfContent()
    {
        //This method will grab a random piece of content based on its popularity
        double totalSum = 0.0;
        double x = Math.random();
        Content r = new Content();

        ArrayList<Content> allContent = new ArrayList<Content>();
        Enumeration e = localContentCustodians.keys();

        while(e.hasMoreElements()){

            Content t = (Content) e.nextElement();
            allContent.add(t);
        }

        for(Content k : allContent )
        {
            totalSum += k.probability;

            if(x <= totalSum)
            {
                r = k;
                break;
            }
        }
       return r;
    }//end getZipfContent

    public void distributeContentCustodians() {
    //This method is used to pass a hashtable of all content and respective nodes to all nodes
    //It must be called after creating all content custodians

        //Start at each node and check the number of content items on that node
        for (int i = 0; i < size; i++)
        {
            int kPerNode = nodes.get(i).content.size();
            //Loop through each piece of content on that node and all it to the local variable hashtable
            for(int k = 0; k < kPerNode; k++) {
                localContentCustodians.put(nodes.get(i).getContent(k), nodes.get(i));
            }
        }
        //Loop through all the nodes and assign the local hashtable to the hashtable on each node
        for (int i = 0; i < size; i++) {
            nodes.get(i).contentCustodians = localContentCustodians;
        }

    }//end distribute content

    public void resetGraphStats() {

        for(Node n : nodes){
            //Reset stats for each node
            n.resetNodeStats();
        }
        for(AttackerNode a : attackers){
            //Reset all stats for each attacker
            a.resetAttackerStats();
        }
    }//end resetGraphStats()

    public void addAttackersToExistingGraph(int num){
        //Grab current number of attackers and compare to new value
        int currentAttackers = attackers.size();
        if(num>currentAttackers){
            numAttackers = num;
            int attackerindex = -1;
            Random rand = new Random();
            for(Node a : nodes){
                if(custodians.contains(a)){
                    custodianIndexes.add(a.nodeID);
                }
                a.clearEdges();
            }
            for(int a = 0; a < num-currentAttackers; a++){
                attackerindex = rand.nextInt(size);
                if(attackIndexes.contains(attackerindex) || custodianIndexes.contains(attackerindex) || !possibleRequesters.contains(nodes.get(attackerindex)))
                {
                    a--;
                }else {
                    attackIndexes.add(attackerindex);
                    AttackerNode act = new AttackerNode(attackerindex, cacheSize, cacheType, numUnpopularItemsPerAttacker,numRequestsPerTest);
                    attackers.add(act);
                    Node old = nodes.get(attackerindex);

                    act.edges = old.edges;
                    act.contentCustodians=old.contentCustodians;
                    nodes.set(attackerindex,act);
                    int index = possibleRequesters.indexOf(old);
                    possibleRequesters.set(index,act);
                    dijkstraComputed=false;
                }//end else
            }//end for

                    if(graphType==1) {
                        setEdgesSquareGraph();
                    }
                    if(graphType==2){
                        setEdges();
                    }

                    distributeContentCustodians();

                    //force attackers to attack from beginning
                    if(!useCharacteristicTimeAttack) {
                        for (AttackerNode att : attackers) {
                            //Simulate polling done and ready to guess characteristic time.
                            att.numRequestsServed = 500;
                            att.donePolling = true;
                            //att.target = att.FindBestTarget(att, custodians);
                            //estimate cacheSize
                            att.cacheSizeGuess = att.maxCacheSize;
                            att.allPacketsFromCustodian = true;

                            //Grab most unpopular in graph
                            Map<Content, Double> popContent = new HashMap<Content, Double>();
                            ArrayList<Content> all = new ArrayList<Content>();
                            Enumeration e = att.contentCustodians.keys();
                            while (e.hasMoreElements()) {
                                Content d = (Content) e.nextElement();
                                popContent.put(d, d.probability);
                            }
                            Map sorted = att.sortByValue(popContent);
                            List<Map.Entry<Content, Integer>> sortedList = new LinkedList<Map.Entry<Content, Integer>>(sorted.entrySet());

                            //Add only up to size specified to unpopular content list
                            for (int s = 0; s < att.numUnpopularItems; s++) {
                                //Add unpopular files to variable
                                Map.Entry<Content, Integer> currentEntry = sortedList.get(s);
                                att.unpopularContent.add(currentEntry.getKey());
                            }//end for

                            att.finalCharTimeGuess = 0;
                            att.readyToAttack = true;

                        }//end for
                    }//end if not using characteristic time attack


        }
    }//end addAttackersToExistingGraph



}//end graph
