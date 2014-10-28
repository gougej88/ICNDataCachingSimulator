import java.io.PrintWriter;
import java.util.*;
import java.io.*;
import java.text.*;

/**
 * Created by Jeff on 10/15/2014.
 */

public class Search {

    public static void runTest(Graph g, int numTests){


        //Create searches for content on Poisson Distribution


        //Get all nodes that are not content custodians, thus requesters
        ArrayList<Node> requesters = new ArrayList<Node>();
        for(int j=0; j<g.size; j++)
        {
            //COMPUTE ALL PATHS FROM EACH SRC ONCE
            Dijkstra.ComputePaths(g, g.nodes.get(j));

            //Check if the node is a custodian. if not: add to requesters
            if(!g.localContentCustodians.contains(g.nodes.get(j)))
            {

                requesters.add(g.nodes.get(j));

            }
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Date d = new Date();
        String file_name = "C:\\temp\\manet\\"+dateFormat.format(d)+".txt";
        File file = new File("C:\\temp\\manet\\");
        file.mkdirs();
        Writer writer = null;
        try {
             writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file_name), "utf-8"));


        //Get a random requester (aka not a custodian)
        double temp = g.size * .2;
        int numCustodians = (int)temp;
        Random rand = new Random(g.size-numCustodians);
        //System.out.println("Get random requester: "+ requesters.get(rand.nextInt(requesters.size())));


        PacketTracer test = new PacketTracer();
        //Each numTests is a time step
        int cachehits = 0;
        int totalHops = 0;
        double percent = 0;
        int p = 0;
        int jump = 0;
        int maxtime = 0;


        for(int x=0; x<numTests; x++) {
            //Get the number of requests to create per time step
            jump = p;
            p= Poisson.getPoisson(1);
            test.addToTest(jump+p,g.getZipfContent(),g.nodes.get(requesters.get(rand.nextInt(requesters.size())).nodeID));
                Packet r = findContent(g, g.nodes.get(requesters.get(rand.nextInt(requesters.size())).nodeID), g.getZipfContent());
                if (r.cachehit)
                    cachehits++;

                totalHops+=r.hops;
                maxtime += p;
            writer.write("Test:"+x+" | Source:"+r.src.nodeID+" | Content:"+r.search.contentID+" | Destination:"+r.dest.nodeID+" | Number of hops:"+r.hops+" | Cache hit?:"+r.cachehit+"\n");

        }
        System.out.println(test.k.size());
        System.out.println("Maxtime: " + maxtime);
        percent = (double)cachehits/(double)numTests *100;
        System.out.println("Number of tests performed: "+ numTests);
        System.out.println("Number of hops in test: "+ totalHops);
        System.out.println("Number of cache hits in test: "+ cachehits);
        System.out.println("Percentage of cache hits: "+ percent+"%");
            writer.write("Number of requests:"+numTests+" | Total number of hops:"+totalHops+" | Number of cache hits:"+cachehits+" | Percentage cache hits:"+percent+"%");

        } catch (IOException ex) {
            System.out.println(ex);
            // report
        } finally {
            try {writer.close();} catch (Exception ex) {}
        }

    }

    public static Packet findContent(Graph g, Node n, Content k){

        System.out.println("Starting node:" + n.nodeID);
        System.out.println("Search for:" + k.contentID);
        Packet p = new Packet(n, k);
        p.dest = n.contentCustodians.get(k);
        System.out.println("Dest node for content: " + p.dest.nodeID);
        p.route = Dijkstra.getShortestPath(p.src,p.dest);
        System.out.println("Route" + p.route);
        int i = 0;
        while(p.found == false)
        {

            if(p.dest != p.src && i < p.route.size()) {
                p.next = p.route.get(i + 1);
                p = p.route.get(i).sendData(p);
            }else{
                //This should only occur if a content custodian makes a request
                System.out.println("Content is on the requesting node");
                break;
            }
                i++;
        }
        if(p.found) {

            System.out.println("Data found:" + p.data.toString() + " on Node:" + p.referrer.nodeID);
            System.out.println("Number of hops: " + p.hops.toString());
            System.out.println();


        }
        return p;
    }

}
