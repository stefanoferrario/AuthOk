package authorizer.gestoreRisorse;

import java.security.InvalidParameterException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class RisorsaLink extends Risorsa {
    private static final String URL = "(http(s)?:\\/\\/.)?(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)";
    private String link;
    RisorsaLink(int livello) {
        super(livello);
    }

    void setLink(String link) {
        Pattern pattern = Pattern.compile(URL);
        Matcher matcher = pattern.matcher(link);
        if (!matcher.matches()) {throw new InvalidParameterException("Invalid link");}
        this.link = link;
    }

    String getLink() {return link;}
}
