package learningpattern.springdesignpattern.interclass;

public class SpanTag implements TagAction{
    @Override
    public void startTag() {
        System.out.println("<span>");
    }

    @Override
    public void endTag() {
        System.out.println("</span>");
    }
}
