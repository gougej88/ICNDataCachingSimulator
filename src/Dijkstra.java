import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

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

            for(Node e : n.edges)
            {
                Node x = e;
                double distanceThru = n.minDistance + 1;
                if(distanceThru < x.minDistance) {
                    queue1.remove(x);
                    x.minDistance = 1;
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

}
