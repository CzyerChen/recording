package learningpattern.springdesignpattern.interclass;

import java.util.List;

public interface TagActionComposite extends TagAction {
    public List<TagAction> getTags();
    public void addTag(TagAction tagAction);
}
