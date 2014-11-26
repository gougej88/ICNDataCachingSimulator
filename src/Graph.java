import java.util.*;


/**
 * Created by n00430588 on 10/3/2014.
 */
public class Graph {
    int length;
    int width;
    int cacheSize;
    ArrayList<Node> nodes = new ArrayList<Node>();
    Hashtable<Content,Node> localContentCustodians = new Hashtable<Content, Node>();
    int size;
    double alpha;
    int cacheType;



    public Graph(int length, int width, int cacheSize, double alpha, int cacheType) {
        this.length = length;
        this.width = width;
        this.cacheSize = cacheSize;
        size = length*width;
        this.alpha = alpha;
        this.cacheType = cacheType;
    }

    public void createGraph(){

        for(int i = 0; i < size; i++)
        {
            Node a = new Node(i, cacheSize, cacheType);
            nodes.add(i,a);


        }

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


    }

    public void setEdges(){

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
        }
    }

    public void createContentCustodians(){
        ArrayList<Integer> n = new ArrayList<Integer>();
        //Get 20% of the graph size and make them content custodians
        double temp = size * .2;
        int numCustodians = (int)temp;
        Random rand = new Random();
        //Loop and create random nodes in the graph as content custodians
        for(int i = 0; i<numCustodians; i++)
        {
            int contentCust = rand.nextInt(size);
            if(!n.contains(contentCust)) {
                n.add(contentCust);
                nodes.get(contentCust).saveContent("This is content from node "+ contentCust);
                nodes.get(contentCust).saveContent("This is a second piece of content on node "+contentCust);
                nodes.get(contentCust).saveContent("This is a third piece of content on node "+contentCust);
                nodes.get(contentCust).saveContent("This is a fourth piece of content on node "+contentCust);
                nodes.get(contentCust).saveContent("This is a fifth piece of content on node "+contentCust);
                nodes.get(contentCust).saveContent("This is a sixth piece of content on node "+contentCust);
                nodes.get(contentCust).saveContent("This is a seventh piece of content on node "+contentCust);
                nodes.get(contentCust).saveContent("This is a eighth piece of content on node "+contentCust);
                nodes.get(contentCust).saveContent("This is a ninth piece of content on node "+contentCust);
                nodes.get(contentCust).saveContent("This is a tenth piece of content on node "+contentCust);

            }
        }

    }

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
            //System.out.print(" Zip prob:"+zip.getProbability(j+1));
            //test += zip.getProbability(j+1);
        }
        //System.out.println("Total prob sum: "+ test);

        //Shuffle the ranks
        Collections.shuffle(ranks);

        //Assign all the ranks to each piece of content
        Enumeration e = localContentCustodians.keys();
        int i = 0;
        while(e.hasMoreElements()){

            Content k = (Content) e.nextElement();
            k.probability = ranks.get(i);
            i++;
            //System.out.println(k);
        }

    }

    public Content getZipfContent()
    {
        //This method will grab a random piece of content based on its popularity
        double totalSum = 0.0;
        double x = Math.random();
        Content r = new Content();
        //ArrayList<Content> allContent = new ArrayList<Content>();
        //for(int i=0; i<size;i++) {
            //allContent.add(i * 2, nodes.get(i).content.get(0));
           // allContent.add(i * 2 + 1, nodes.get(i).content.get(1));
       // }
        ArrayList<Content> allContent = new ArrayList<Content>();
        Enumeration e = localContentCustodians.keys();
        //int i =0;
        while(e.hasMoreElements()){

            Content t = (Content) e.nextElement();
            allContent.add(t);
            //System.out.println("Adding to allcontent: "+ allContent.get(i).contentID);
            //i++;
        }

        //Test for popularity search
        //System.out.println("Random x = " + x);
        for(Content k : allContent )
        {
            totalSum += k.probability;
            //System.out.print(" k prob : "+ k.probability + "content id " + k.contentID);
            if(x <= totalSum)
            {
                r = k;
                break;
            }
        }
       return r;
    }

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

    }









}
