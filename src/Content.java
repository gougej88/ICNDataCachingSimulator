
/**
 * Created by n00430588 on 9/24/2014.
 */
public class Content {
    //right now each node can only hold 1 piece of content
    int contentID;
    String content;

    public Content()
    {

    }

    public void addContent(int contentID, String content)
    {
        this.contentID = contentID;
        this.content = content;
    }


    public String showContent(int ContentID)
    {
        //Show the content stored on the node
        return content;
    }
}
