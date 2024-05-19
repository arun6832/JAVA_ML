package data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class CorrelationCalculator {

    public static Map<String, Map<String, Double>> calculateCorrelations(String filePath) throws IOException {
        // Read the CSV file
        FileReader file = new FileReader(filePath);
        BufferedReader br = new BufferedReader(file);

        // Variables for data processing
        String line;
        List<String> columnHeadings = new ArrayList<>();
        List<List<Double>> columnData = new ArrayList<>();

        // Read the header line to determine column headings
        if ((line = br.readLine()) != null) {
            String[] headers = line.split(",");
            columnHeadings = Arrays.asList(headers);
            for (int i = 0; i < headers.length; i++) {
                columnData.add(new ArrayList<>()); // Initialize data list for each column
            }
        }

        // Read each line of the CSV file
        while ((line = br.readLine()) != null) {
            // Skip empty lines
            if (line.trim().isEmpty()) {
                continue;
            }

            // Split the line by comma to get individual values
            String[] values = line.split(",");
            if (values.length != columnHeadings.size()) {
                System.err.println("Error: Data format mismatch in CSV file at line: " + line);
                continue;
            }

            // Process each column's data
            for (int i = 0; i < values.length; i++) {
                try {
                    double value = Double.parseDouble(values[i].trim());
                    columnData.get(i).add(value); // Add value to the corresponding column's data list
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing value at row: " + line + ", column: " + columnHeadings.get(i));
                    return null;
                }
            }
        }

        // Calculate Pearson correlation coefficient for each pair of columns
        Map<String, Map<String, Double>> correlationMap = new HashMap<>();
        for (int i = 0; i < columnHeadings.size(); i++) {
            String heading1 = columnHeadings.get(i);
            List<Double> data1 = columnData.get(i);
            for (int j = i + 1; j < columnHeadings.size(); j++) {
                String heading2 = columnHeadings.get(j);
                List<Double> data2 = columnData.get(j);
                double correlation = calculatePearsonCorrelation(data1, data2);
                correlationMap.computeIfAbsent(heading1, k -> new HashMap<>()).put(heading2, correlation);
                correlationMap.computeIfAbsent(heading2, k -> new HashMap<>()).put(heading1, correlation);
            }
        }

        // Close the file reader
        br.close();
        file.close();

        return correlationMap;
    }

    public static Map.Entry<String, String> findBestPair(Map<String, Map<String, Double>> correlationMap) {
        Map.Entry<String, String> bestPair = null;
        double maxCorrelation = Double.MIN_VALUE;
        for (Map.Entry<String, Map<String, Double>> entry : correlationMap.entrySet()) {
            for (Map.Entry<String, Double> correlationEntry : entry.getValue().entrySet()) {
                if (correlationEntry.getValue() > maxCorrelation) {
                    maxCorrelation = correlationEntry.getValue();
                    bestPair = new AbstractMap.SimpleEntry<>(entry.getKey(), correlationEntry.getKey());
                }
            }
        }
        return bestPair;
    }

    // Method to calculate the Pearson correlation coefficient
    private static double calculatePearsonCorrelation(List<Double> xValues, List<Double> yValues) {
        if (xValues.size() != yValues.size()) {
            throw new IllegalArgumentException("Input lists must have the same size.");
        }

        double meanX = calculateMean(xValues);
        double meanY = calculateMean(yValues);

        double sumXY = 0;
        double sumX2 = 0;
        double sumY2 = 0;

        for (int i = 0; i < xValues.size(); i++) {
            double xMinusMean = xValues.get(i) - meanX;
            double yMinusMean = yValues.get(i) - meanY;
            sumXY += xMinusMean * yMinusMean;
            sumX2 += xMinusMean * xMinusMean;
            sumY2 += yMinusMean * yMinusMean;
        }

        return sumXY / (Math.sqrt(sumX2) * Math.sqrt(sumY2));
    }

    // Method to calculate the mean of a list of values
    private static double calculateMean(List<Double> values) {
        if (values.isEmpty()) {
            throw new IllegalArgumentException("Input list must not be empty.");
        }

        double sum = 0;
        for (double value : values) {
            sum += value;
        }
        return sum / values.size();
    }
}
