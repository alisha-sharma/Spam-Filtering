import java.util.HashSet;
import java.util.Set;

/**
 * Data structure to store set of words contained in an email.
 * Additionally, this data structure also stores the information of whether the email is spam or not.
 */
public class Email {

    /**
     * The set of all words.
     */
    public final Set<String> words;
    /**
     * Flag that indicates whether this email is spam or not.
     */
    public final boolean isSpam;

    /**
     * Create an email data structure.
     *
     * @param content the content of the email.
     * @param isSpam  whether the email is spam or not.
     */
    public Email(String content, boolean isSpam) {
        words = new HashSet<>();
        for (String word : content.split("\\s+")) {
            if (word.length() > 2 && word.length() < 15) {
                words.add(word);
            }
        }
        this.isSpam = isSpam;
    }

}
