package data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class AverageProteinLevelsByAgeGroup {
    public static void calculateAverageProteinLevels(String datasetFilePath) {
        // Initialize a map to store age groups and their corresponding total protein3 and protein4 levels, and count
        Map<Integer, Double> ageProtein3Sum = new HashMap<>();
        Map<Integer, Double> ageProtein4Sum = new HashMap<>();
        Map<Integer, Integer> ageCount = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(datasetFilePath))) {
            String line;
            boolean headerSkipped = false;
            while ((line = br.readLine()) != null) {
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue; // Skip the header line
                }

                String[] columns = line.split(","); // Split the row into columns

                // Extract age, protein3, and protein4 levels from the columns
                int age = Integer.parseInt(columns[0]); // Assuming age is in the first column
                double protein3 = Double.parseDouble(columns[4]); // Protein3 column
                double protein4 = Double.parseDouble(columns[5]); // Protein4 column

                // Determine the age group (e.g., group every 10 years)
                int ageGroup = age / 10 * 10;

                // Update the sum of protein3, protein4, and count for the corresponding age group
                ageProtein3Sum.put(ageGroup, ageProtein3Sum.getOrDefault(ageGroup, 0.0) + protein3);
                ageProtein4Sum.put(ageGroup, ageProtein4Sum.getOrDefault(ageGroup, 0.0) + protein4);
                ageCount.put(ageGroup, ageCount.getOrDefault(ageGroup, 0) + 1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Sort ageProtein3Sum and ageProtein4Sum maps by keys
        Map<Integer, Double> sortedAgeProtein3Sum = new TreeMap<>(ageProtein3Sum);
        Map<Integer, Double> sortedAgeProtein4Sum = new TreeMap<>(ageProtein4Sum);

        // Calculate the average protein3 and protein4 levels for each age group and print the results
        System.out.println("Average Protein3 and Protein4 Levels by Age Group:");
        for (Map.Entry<Integer, Double> entry : sortedAgeProtein3Sum.entrySet()) {
            int ageGroup = entry.getKey();
            double sumProtein3 = entry.getValue();
            double sumProtein4 = sortedAgeProtein4Sum.get(ageGroup);
            int count = ageCount.get(ageGroup);
            double averageProtein3 = sumProtein3 / count;
            double averageProtein4 = sumProtein4 / count;
            System.out.println("Age Group: " + ageGroup + "-" + (ageGroup + 9) + ", Average Protein3: " + averageProtein3 + ", Average Protein4: " + averageProtein4);
        }
    }
}
