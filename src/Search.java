import java.util.*;
import java.io.*;
import java.text.*;

/**
 * Created by Jeff on 10/15/2014.
 */

public class Search {

    public static PacketTracer runTest(Graph g, int numTests, Boolean cacheEnabled){

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

        //Create a new log file of a test
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmssSSS");
        Date d = new Date();
        String file_name = "C:\\temp\\manet\\"+dateFormat.format(d)+".txt";
        File file = new File("C:\\temp\\manet\\");
        file.mkdirs();
        Writer writer = null;
        PacketTracer test = new PacketTracer(cacheEnabled);
        try {
             writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file_name), "utf-8"));

        //Get a random requester (aka not a custodian)
        double temp = g.size * .2;
        int numCustodians = (int)temp;
        Random rand = new Random(g.size-numCustodians);


        //Each numTests is a time step
        int cachehits = 0;
        int totalHops = 0;
        double percent = 0;
        double averagehops = 0;
        int p = 0;
        double jump = 0;
        double maxtime = 0;
        int cacheSize = g.cacheSize;

        for(int x=0; x<numTests; x++) {
            //Get the number of requests to create per time step
            jump = maxtime;

            //Mean is set here
            p= Poisson.getPoisson(1);

            Content k = g.getZipfContent();
            Node n = g.nodes.get(requesters.get(rand.nextInt(requesters.size())).nodeID);
            test.addToTest(jump+p,k,n);
            Packet pack = new Packet(n,k);
            pack.cacheEnabled = cacheEnabled;
            pack.dest = n.contentCustodians.get(k);
            pack.route = Dijkstra.getShortestPath(pack.src,pack.dest);
            //Perform the search
            Packet r = findContent(pack);
                if (r.cachehit)
                    cachehits++;

                totalHops+=r.hops;
                maxtime += p;
            //Write each query out to text file
            writer.write("Test:"+x+" | Time:"+maxtime+" | Source:"+r.src.nodeID+" | Content:"+r.search.contentID+" | Destination:"+r.dest.nodeID+" | Data found on:"+r.referrer.nodeID+" | Number of hops:"+r.hops+" | Cache hit?:"+r.cachehit+"\r\n");
        }
        System.out.println("Maxtime: " + maxtime);
        percent = (double)cachehits/(double)numTests *100;
        System.out.println("Number of tests performed: "+ numTests);
        System.out.println("Number of hops in test: "+ totalHops);
        System.out.println("Cache Size: "+ cacheSize);
        System.out.println("Number of cache hits in test: "+ cachehits);
        System.out.println("Percentage of cache hits: "+ percent+"%");
        averagehops = (double)totalHops/(double)numTests;
        System.out.println("Average hops per request: "+ averagehops);
        //Set totals in packetTracer
        test.setTotals(cacheSize,numTests,totalHops,cachehits,averagehops);

        //Write output to log file
        writer.write("Number of requests:"+numTests+" | Total number of hops:"+totalHops+" | Number of cache hits:"+cachehits+" | Percentage cache hits:"+percent+"%"+ " | Average hops per request"+ averagehops );
        } catch (IOException ex) {
            System.out.println(ex);
        } finally {
            try {writer.close();} catch (Exception ex) {System.out.println(ex);}
        }
    return test;
    }//end runTest

    public static Packet findContent(Packet p){

        //System.out.println("Route" + p.route);
        int i = 0;
        while(!p.found)
        {
            if(p.dest != p.src && i < p.route.size()) {
                p.next = p.route.get(i + 1);
                p = p.route.get(i).sendData(p);
            }else{
                //This should only occur if a content custodian makes a request, which should never happen
                //System.out.println("Content is on the requesting node");
                break;
            }
                i++;
        }
        if(p.found) {
            //System.out.println("Data found:" + p.data.toString() + " on Node:" + p.referrer.nodeID);
            //System.out.println("Number of hops: " + p.hops.toString());
            //System.out.println();
        }
        return p;
    }//end findContent
}
