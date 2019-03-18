package learningpattern.springdesignpattern;

import learningpattern.springdesignpattern.interclass.PTag;
import learningpattern.springdesignpattern.interclass.SpanTag;
import learningpattern.springdesignpattern.interclass.TagAction;
import learningpattern.springdesignpattern.interclass.TagActionComposite;

public class CompositeModel {

    public static void main(String[] args){
        TagActionComposite tags = new PTag();
        tags.addTag(new SpanTag());
        tags.startTag();
        for(TagAction t : tags.getTags()){
            t.startTag();
            t.endTag();
        }
        tags.endTag();
    }
}
