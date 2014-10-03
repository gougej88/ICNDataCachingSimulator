

public class Main {

    public static void main(String[] args) throws java.io.IOException, java.lang.Exception {
        // write your code here

        //Create graph grid of nodes
        Node a = new Node(1);
        a.saveContent("This is content from node A.");
        Node b = new Node(2);
        b.saveContent("This is content from node B.");
        Node c = new Node(3);
        c.saveContent("This is content from node C.");
        Node d = new Node(4);
        d.saveContent("This is content from node D.");

        //Setup Routes
        Route ab = new Route(a.nodeID, b.nodeID);
        Route bc = new Route(b.nodeID, c.nodeID);
        Route cd = new Route(c.nodeID, d.nodeID);

        //Test show the content
        a.getContent();
        b.getContent();
        c.getContent();



        //Start at outside node

        //Search grid for content custodian

        //Need to know where content found

        //Print where content is found
    }
}
