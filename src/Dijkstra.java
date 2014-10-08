import java.util.*;

/**
 * Created by n00430588 on 10/6/2014.
 */
public class Dijkstra {
    public static void ComputePaths(Node src)
    {
        src.minDistance = 0;
        PriorityQueue<Node> queue1 = new PriorityQueue<Node>();
        queue1.add(src);

        while(!queue1.isEmpty()){
            Node n = queue1.poll();

            for(Edge e : n.edges)
            {
                Node x = e.target;
                double weight = e.weight;
                double distanceThru = n.minDistance + weight;
                if(distanceThru < x.minDistance) {
                    queue1.remove(x);
                    x.minDistance = distanceThru;
                    x.previous = n;
                    queue1.add(x);
                }
            }
        }
    }

    public static List<Node> getShortestPath(Node target){
        List<Node> path = new ArrayList<Node>();
        for (Node vertex = target; vertex != null; vertex = vertex.previous)
            path.add(vertex);
        Collections.reverse(path);
        return path;
    }

    public static Packet findContent(Node n, Content k){
        Packet p = new Packet(n, k);
        p.dest = n.contentCustodians.get(k);
        p.route = getShortestPath(p.dest);
        int i =0;
        while(p.found == false)
        {

            p.route.get(i).sendData(p);
            i++;
        }

        return p;
    }

}
