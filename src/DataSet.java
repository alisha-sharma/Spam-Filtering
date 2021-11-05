import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * A dataset of spam and ham emails.
 */
public class DataSet {

    /**
     * List of all emails
     */
    public List<Email> emails;
    /**
     * Set of all words that exist in this dataset.
     */
    public Set<String> allWords;
    /**
     * Mapping of every word in the dataset to the probability of that word occurring in spam emails.
     */
    public Map<String, Double> spamProbabilityMap;
    /**
     * Mapping of every word in the dataset to the probability of that word occurring in ham emails.
     */
    public Map<String, Double> hamProbabilityMap;

    /**
     * Total number of spam emails
     */
    public int spamCount;
    /**
     * Total number of spam emails
     */
    public int hamCount;
    /**
     * Probability of an email being a spam
     */
    public double spamProbability;
    /**
     * Probability of an email being not spam
     */
    public double hamProbability;

    /**
     * Create a dataset by parsing data contained in a CSV file.
     *
     * @param dataFile name or path of CSV file.
     * @throws IOException if any error occurs while reading the CSV file.
     */
    public DataSet(String dataFile) throws IOException {
        emails = readEmails(dataFile);
        spamProbability = (double) spamCount / (spamCount + hamCount);
        hamProbability = (double) hamCount / (spamCount + hamCount);
        collectWords();
        countWords();
    }

    /**
     * Internal email to parse a csv file and create list of emails.
     *
     * @param dataFile name of path of CSV file.
     * @return the list of parsed emails.
     * @throws IOException if any error occurs while reading the CSV file.
     */
    private List<Email> readEmails(String dataFile) throws IOException {
        FileReader reader = new FileReader(dataFile);
        Scanner in = new Scanner(reader);
        List<Email> emails = new ArrayList<>();
        int spamCount = 0, hamCount = 0;
        in.nextLine();
        while (in.hasNextLine()) {
            try {
                String line = in.nextLine().trim();
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String content = parts[0];
                    String spamStr = parts[1];
                    boolean isSpam;
                    if (spamStr.equals("1")) {
                        isSpam = true;
                        spamCount++;
                    } else if (spamStr.equals("0")) {
                        isSpam = false;
                        hamCount++;
                    } else continue;

                    emails.add(new Email(content, isSpam));
                }

            } catch (RuntimeException e) {
                System.err.println(e.getMessage());
            }
        }
        this.spamCount = spamCount;
        this.hamCount = hamCount;
        return emails;
    }

    /**
     * Internal method to collect words from all emails to create a single set of words.
     */
    private void collectWords() {
        allWords = new HashSet<>();
        for (Email email : emails) {
            allWords.addAll(email.words);
        }
    }

    /**
     * Internal method to count the occurrence of a word in spam and ham emails.
     * After counting is done, the probabilities of occurrence of each word for both spam and ham emails are calculated.
     */
    private void countWords() {
        spamProbabilityMap = new HashMap<>();
        hamProbabilityMap = new HashMap<>();
        for (String word : allWords) { // for each word
            int countInSpam = 0, countInHam = 0;
            for (Email email : emails) { // for each email
                // if email contains the word
                if (email.words.contains(word)) {
                    // if email is spam, increment spam count.
                    if (email.isSpam) countInSpam++;
                        // otherwise, increment ham count.
                    else countInHam++;
                }
            }
            // Store P(word|Spam)
            spamProbabilityMap.put(word, (countInSpam + 1.0) / (spamCount + 2.0));
            // Store P(word|Ham)
            hamProbabilityMap.put(word, (countInHam + 1.0) / (hamCount + 2.0));
        }
    }

    /**
     * Calculate whether an email is spam or not. This method uses logarithm to avoid floating point underflow.
     *
     * @param email the email to check
     * @return true if the email is spam, otherwise false.
     */
    public boolean predictSpam(Email email) {
        double a = 1, b = 1;
        for (String word : email.words) {
            if (!allWords.contains(word)) continue;
            a += Math.log(spamProbabilityMap.get(word));
            b += Math.log(hamProbabilityMap.get(word));
        }

        double spamPart = Math.log(spamProbability) + a;
        double hamPart = Math.log(hamProbability) + b;

        return spamPart > hamPart;
    }

    /**
     * Calculate whether an email is spam or not. (Normal approach without logarithm)
     *
     * @param email the email to check
     * @return true if the email is spam, otherwise false.
     */
    public boolean predictSpam2(Email email) {
        double a = 1, b = 1;
        for (String word : email.words) {
            if (!allWords.contains(word)) continue;
            double oldA = a;
            double oldB = b;
            a *= spamProbabilityMap.get(word);
            b *= hamProbabilityMap.get(word);
            if (a == 0 || b == 0) {
                a = oldA;
                b = oldB;
                break;
            }
        }
        double probability = spamProbability * a / (spamProbability * a + hamProbability * b);
        return probability > 0.5;
    }

    @Override
    public String toString() {
        return "DataSet{" +
                "spamCount=" + spamCount +
                ", hamCount=" + hamCount +
                ", spamProbability=" + spamProbability +
                ", hamProbability=" + hamProbability +
                '}';
    }

}
