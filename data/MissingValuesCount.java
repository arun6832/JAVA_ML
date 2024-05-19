package data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class MissingValuesCount {
    public Map<Integer, Integer> countMissingValues(String filePath) {
        Map<Integer, Integer> missingValueCounts = new HashMap<>();
        try {
            FileReader file = new FileReader(filePath);
            BufferedReader br = new BufferedReader(file);

            // Read the header line to determine the number of columns
            String line;
            if ((line = br.readLine()) != null) {
                String[] headers = line.split(",");
                for (int i = 0; i < headers.length; i++) {
                    missingValueCounts.put(i, 0); // Initialize count for each column
                }
            }

            // Read each line of the CSV file
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                for (int i = 0; i < values.length; i++) {
                    if (values[i].trim().isEmpty()) {
                        missingValueCounts.put(i, missingValueCounts.get(i) + 1);
                    }
                }
            }

            br.close();
            file.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return missingValueCounts;
    }
}
