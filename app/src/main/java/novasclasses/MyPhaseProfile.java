package novasclasses;



public class MyPhaseProfile {
    private String title;
    private String tip;
    private String hint;
    private String answer;
    private String almostAnswer1;
    private String almostAnswer2;
    private String almostAnswer3;
    private String almostAnswerTip1;
    private String almostAnswerTip2;
    private String almostAnswerTip3;

    public MyPhaseProfile(){}

    public MyPhaseProfile(String title, String tip, String hint, String answer, String almostAnswer1, String almostAnswer2, String almostAnswer3, String almostAnswerTip1, String almostAnswerTip2, String almostAnswerTip3) {
        this.title = title;
        this.tip = tip;
        this.hint = hint;
        this.answer = answer;
        this.almostAnswer1 = almostAnswer1;
        this.almostAnswer2 = almostAnswer2;
        this.almostAnswer3 = almostAnswer3;
        this.almostAnswerTip1 = almostAnswerTip1;
        this.almostAnswerTip2 = almostAnswerTip2;
        this.almostAnswerTip3 = almostAnswerTip3;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAlmostAnswer1() {
        return almostAnswer1;
    }

    public void setAlmostAnswer1(String almostAnswer1) {
        this.almostAnswer1 = almostAnswer1;
    }

    public String getAlmostAnswer2() {
        return almostAnswer2;
    }

    public void setAlmostAnswer2(String almostAnswer2) {
        this.almostAnswer2 = almostAnswer2;
    }

    public String getAlmostAnswer3() {
        return almostAnswer3;
    }

    public void setAlmostAnswer3(String almostAnswer3) {
        this.almostAnswer3 = almostAnswer3;
    }

    public String getAlmostAnswerTip1() {
        return almostAnswerTip1;
    }

    public void setAlmostAnswerTip1(String almostAnswerTip1) {
        this.almostAnswerTip1 = almostAnswerTip1;
    }

    public String getAlmostAnswerTip2() {
        return almostAnswerTip2;
    }

    public void setAlmostAnswerTip2(String almostAnswerTip2) {
        this.almostAnswerTip2 = almostAnswerTip2;
    }

    public String getAlmostAnswerTip3() {
        return almostAnswerTip3;
    }

    public void setAlmostAnswerTip3(String almostAnswerTip3) {
        this.almostAnswerTip3 = almostAnswerTip3;
    }
}
