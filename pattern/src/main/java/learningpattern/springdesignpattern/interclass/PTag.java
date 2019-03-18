package learningpattern.springdesignpattern.interclass;

import java.util.ArrayList;
import java.util.List;

public class PTag implements TagActionComposite{
    private List<TagAction> list  = new ArrayList<>();

    @Override
    public List<TagAction> getTags() {
        return this.list;
    }

    @Override
    public void addTag(TagAction tagAction) {
         list.add(tagAction);
    }

    @Override
    public void startTag() {
         System.out.println("<p>");
    }

    @Override
    public void endTag() {
        System.out.println("</p>");
    }
}
