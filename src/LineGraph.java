import java.util.*;

/**
 * Created by Jeff on 3/28/2015.
 */
public class LineGraph {
    int length;
    int cacheSize;
    ArrayList<Node> nodes = new ArrayList<Node>();
    Hashtable<Content,Node> localContentCustodians = new Hashtable<Content, Node>();
    int numContentItems;
    int numRequestsPerTest;
    double alpha;
    int cacheType;
    int numAttackers;
    int totalNodes;
    int numUnpopularItemsPerAttacker;
    ArrayList<Node> custodians = new ArrayList<Node>();
    ArrayList<AttackerNode> attackers = new ArrayList<AttackerNode>();



    public LineGraph(int length, int cacheSize, double alpha, int cacheType, int numAttackers, int numUnpopularItems, int numContentItems, int numRequests) {
        this.length = length;

        this.cacheSize = cacheSize;
        this.totalNodes = length+numAttackers;
        this.numContentItems = numContentItems;
        this.alpha = alpha;
        this.cacheType = cacheType;
        this.numAttackers = numAttackers;
        this.numUnpopularItemsPerAttacker = numUnpopularItems;
        this.numRequestsPerTest = numRequests;

    }



    public void createLineGraph(){

        ArrayList<Integer> attackIndexes = new ArrayList<Integer>();
        int attackerindex = -1;

        //Assign random requester as attacker
        if(numAttackers >0) {
            for(int a = 0; a < numAttackers; a++){
                attackerindex = length+a;
                attackIndexes.add(attackerindex);
            }//end for

        }//end if



        for(int i = 0; i < totalNodes; i++)
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
        setEdges();


        //Make 20% of the nodes content custodians and assign content to each one
        createContentCustodians();

        //Pass a hashtable of all content and their respective custodian to all nodes
        //All nodes must know where content lives in order to route correctly
        distributeContentCustodians();

        //Use Distribution to assign popularity to each piece of content in the graph
        assignPopularityDistribution(alpha);


    }//end createLineGraph()

    public void setEdges(){

        for(int i=0; i<length;i++)
        {
            //Check if node has a neighbor to the right
            if(i%length < length-1) {
                //System.out.println("Set right edge:" + nodes.get(i).getNodeID()+ "to node: "+nodes.get(i+1).nodeID);
                nodes.get(i).setEdge(nodes.get(i+1), 1);

            }

            //Check if node has a neighbor to the left
            if(i%length > 0)
            {
                //System.out.println("Set left edge:" + nodes.get(i).getNodeID()+ "to node: "+nodes.get(i-1).nodeID);
                nodes.get(i).setEdge(nodes.get(i-1),1);
            }


        }//end for loop

        //Set Attacker edges
        for(int a = 0; a < numAttackers; a++)
        {
            attackers.get(a).setEdge(nodes.get(1),1);
        }
    }//end setEdges()

    public void createContentCustodians(){
        ArrayList<Integer> n = new ArrayList<Integer>();
        int numCustodians = 1;
        numContentItems = numContentItems / numCustodians;

        //Add attackers to local variable so they do not become custodians
        for(Node x : attackers) {
            n.add(x.nodeID);
        }
        //Loop and create random nodes in the graph as content custodians
        for(int i = 0; i<numCustodians; i++)
        {
            int contentCust = 4;

            if(!n.contains(contentCust)) {
                n.add(contentCust);
                custodians.add(nodes.get(contentCust));
                for(int c = 0; c < numContentItems; c++)
                {
                    nodes.get(contentCust).saveContent("This is content number " + c + " from node "+ contentCust);

                }


            }
        }//end for

    }//end CreateContentCustodians()

    public void assignPopularityDistribution(double Alpha){

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

    }//end assignPopularityDistribution()

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
    }//end getZipfContent()

    public void distributeContentCustodians() {
        //This method is used to pass a hashtable of all content and respective nodes to all nodes
        //It must be called after creating all content custodians

        //Start at each node and check the number of content items on that node
        for (int i = 0; i < length; i++)
        {
            int kPerNode = nodes.get(i).content.size();
            //Loop through each piece of content on that node and all it to the local variable hashtable
            for(int k = 0; k < kPerNode; k++) {
                localContentCustodians.put(nodes.get(i).getContent(k), nodes.get(i));
            }
        }
        //Loop through all the nodes and assign the local hashtable to the hashtable on each node
        for (int i = 0; i < length; i++) {
            nodes.get(i).contentCustodians = localContentCustodians;
        }

    }//end distributeContentCustodians()

}
