package master;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Reducer {

    public static List<String> reduceResponses(List<String> rawResponses) {
        List<String> cleanResults = new ArrayList<>();
        for (String line : rawResponses) {
            if (line != null && !line.trim().isEmpty() && !line.startsWith("❌")) {
                cleanResults.add(line.trim());
            }
        }

        // alphabetic merge
        Collections.sort(cleanResults);

        return cleanResults;
    }
}