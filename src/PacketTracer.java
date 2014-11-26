import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.*;
/**
 * Created by n00430588 on 10/27/2014.
 */
public class PacketTracer {
    ArrayList<Double> timer = new ArrayList<Double>();
    ArrayList<Content> k = new ArrayList<Content>();
    ArrayList<Node> srcNodes = new ArrayList<Node>();
    Boolean cacheEnabled;
    Integer cacheSize;
    Integer cacheType;
    Integer totalRequests;
    Integer totalHops;
    Integer totalCacheHits;
    Double averageHops;

    public PacketTracer(Boolean cache){
        this.cacheEnabled = cache;
    }

    public void addToTest(Double t, Content content1, Node node1) {
        timer.add(t);
        k.add(content1);
        srcNodes.add(node1);
    }

    public void setTotals(Integer cacheType, Integer cacheSize, Integer tests, Integer hops, Integer cache, Double avgHops){
        this.cacheType = cacheType;
        this.cacheSize = cacheSize;
        this.totalRequests = tests;
        this.totalHops = hops;
        this.totalCacheHits = cache;
        this.averageHops = avgHops;
    }


}
