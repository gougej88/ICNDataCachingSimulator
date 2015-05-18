import java.util.*;

/**
 * Created by n00430588 on 10/6/2014.
 */
public class Dijkstra {

    private static  Node GetMinDist(Queue<Node> g)
    {
        Node smallest = null;
        for(Node n : g)
        {
            if(smallest == null || n.minDistance < smallest.minDistance)
            {
                smallest = n;
            }
        }
        return  smallest;
    }

    public static void ComputePaths(Graph g, Node src)
    {
        src.minDistance = 0.;
        Queue<Node> queue1 = new LinkedList<Node>();

        queue1.add(src);
        for(Node n: g.nodes)
        {
            if(n.nodeID != src.nodeID)
            {
                n.minDistance = Double.POSITIVE_INFINITY;
                queue1.add(n);
            }

        }
        while(!queue1.isEmpty())
        {
            Node u = GetMinDist(queue1);
            queue1.remove(u);


            for(Edge e : u.edges)
            {
                Node x = e.target;
                double weight = e.weight;
                double distanceThru = u.minDistance + weight;
                if(distanceThru < x.minDistance) {
                    x.minDistance = distanceThru;

                    if(x.previous == null || !x.previous.containsKey(src))
                    {
                        x.previous.put(src,u);
                    }else {
                        x.previous.replace(src, u);
                    }

                }
            }
        }

    }

    public static List<Node> getShortestPath(Node src, Node target){
        List<Node> path = new ArrayList<Node>();
        for (Node vertex = target; vertex != null; vertex = vertex.previous.get(src)) {
            //System.out.println("Adding node to path: "+ vertex.nodeID);
            path.add(vertex);
        }
        Collections.reverse(path);
        return path;
    }



}
