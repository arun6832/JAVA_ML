package data;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class DescriptiveStatistics {
    public static void main(String[] args) {
        // Example CSV file path
        String csvFile = "BRCA.csv";

        // Read CSV data and calculate statistics
        try {
            List<String[]> dataset = readCSV(csvFile);
            if (dataset != null && !dataset.isEmpty()) {
                // Identify numerical columns
                List<Integer> numericalColumns = identifyNumericalColumns(dataset);

                // Calculate and print statistics for each numerical column
                for (int columnIndex : numericalColumns) {
                    List<Double> columnData = extractColumn(dataset, columnIndex);
                    System.out.println("Statistics for Column " + columnIndex + ":");
                    printStatistics(columnData);
                }

                // Calculate and print correlations between numerical columns
                for (int i = 0; i < numericalColumns.size(); i++) {
                    for (int j = i + 1; j < numericalColumns.size(); j++) {
                        List<Double> columnData1 = extractColumn(dataset, numericalColumns.get(i));
                        List<Double> columnData2 = extractColumn(dataset, numericalColumns.get(j));
                        Double correlation = calculateCorrelation(columnData1, columnData2);
                        if (correlation != null) {
                            System.out.println("Correlation between Column " + numericalColumns.get(i) + " and Column " + numericalColumns.get(j) + ": " + correlation);
                        } else {
                            System.out.println("Correlation between Column " + numericalColumns.get(i) + " and Column " + numericalColumns.get(j) + ": Cannot be calculated (constant values in one of the columns).");
                        }
                    }
                }
            } else {
                System.out.println("No data found in the CSV file.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Read CSV file and parse data into a list of string arrays
    public static List<String[]> readCSV(String csvFile) throws IOException {
        List<String[]> dataset = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            // Read the header line
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] row = line.split(","); // Assuming CSV delimiter is comma
                dataset.add(row);
            }
        }
        return dataset;
    }

    // Identify numerical columns in the dataset
    public static List<Integer> identifyNumericalColumns(List<String[]> dataset) {
        List<Integer> numericalColumns = new ArrayList<>();
        for (int i = 0; i < dataset.get(0).length; i++) {
            try {
                Double.parseDouble(dataset.get(0)[i]);
                numericalColumns.add(i);
            } catch (NumberFormatException e) {
                // Not a numerical column
            }
        }
        return numericalColumns;
    }

    // Extract data from a specific column within the dataset
    public static List<Double> extractColumn(List<String[]> dataset, int columnIndex) {
        List<Double> columnData = new ArrayList<>();
        for (String[] row : dataset) {
            if (columnIndex < row.length) {
                String columnValue = row[columnIndex];
                try {
                    double value = Double.parseDouble(columnValue);
                    columnData.add(value);
                } catch (NumberFormatException e) {
                    // Handle invalid or non-numeric values in the column
                    System.out.println("Invalid value in the column: " + columnValue);
                }
            }
        }
        return columnData;
    }

    // Method to calculate the mean of a list of numbers
    public static double calculateMean(List<Double> data) {
        if (data.isEmpty()) {
            return 0; // or any other default value
        }
        double sum = 0;
        for (double num : data) {
            sum += num;
        }
        return sum / data.size();
    }

    // Method to calculate the median of a list of numbers
    public static double calculateMedian(List<Double> data) {
        if (data.isEmpty()) {
            return 0; // or any other default value
        }
        data.sort(null); // Sort the data in ascending order
        int size = data.size();
        if (size % 2 == 1) {
            return data.get(size / 2);
        } else {
            return (data.get(size / 2 - 1) + data.get(size / 2)) / 2.0;
        }
    }

    // Method to calculate the mode of a list of numbers
    public static double calculateMode(List<Double> data) {
        if (data.isEmpty()) {
            return 0; // or any other default value
        }
        Map<Double, Integer> frequencyMap = new HashMap<>();
        for (double num : data) {
            frequencyMap.put(num, frequencyMap.getOrDefault(num, 0) + 1);
        }
        int maxFrequency = 0;
        double mode = 0;
        for (Map.Entry<Double, Integer> entry : frequencyMap.entrySet()) {
            if (entry.getValue() > maxFrequency) {
                maxFrequency = entry.getValue();
                mode = entry.getKey();
            }
        }
        return mode;
    }

    // Method to calculate the standard deviation of a list of numbers
    public static double calculateStandardDeviation(List<Double> data) {
        if (data.isEmpty()) {
            return 0; // or any other default value
        }
        double mean = calculateMean(data);
        double sum = 0;
        for (double value : data) {
            sum += Math.pow(value - mean, 2);
        }
        return Math.sqrt(sum / data.size());
    }

    // Method to calculate the variance of a list of numbers
    public static double calculateVariance(List<Double> data) {
        if (data.isEmpty()) {
            return 0; // or any other default value
        }
        double mean = calculateMean(data);
        double sum = 0;
        for (double value : data) {
            sum += Math.pow(value - mean, 2);
        }
        return sum / data.size();
    }

    // Method to calculate the correlation between two lists of numbers
    public static Double calculateCorrelation(List<Double> data1, List<Double> data2) {
        if (data1.isEmpty() || data2.isEmpty() || data1.size() != data2.size()) {
            return null; // or any other default value
        }
        double mean1 = calculateMean(data1);
        double mean2 = calculateMean(data2);
        double sumXY = 0;
        double sumX2 = 0;
        double sumY2 = 0;
        for (int i = 0; i < data1.size(); i++) {
            double x = data1.get(i);
            double y = data2.get(i);
            sumXY += (x - mean1) * (y - mean2);
            sumX2 += Math.pow(x - mean1, 2);
            sumY2 += Math.pow(y - mean2, 2);
        }
        double stdX = Math.sqrt(sumX2 / data1.size());
        double stdY = Math.sqrt(sumY2 / data2.size());

        if (stdX == 0 || stdY == 0) {
            return null; // correlation is not defined when standard deviation is zero
        }

        return sumXY / Math.sqrt(sumX2 * sumY2);
    }

    // Method to calculate quartiles of a list of numbers
    public static double[] calculateQuartiles(List<Double> data) {
        if (data.isEmpty()) {
            return new double[]{0, 0, 0}; // or any other default value
        }
        data.sort(null); // Sort the data in ascending order
        int size = data.size();
        double[] quartiles = new double[3];
        quartiles[0] = calculateMedian(data.subList(0, size / 2)); // Q1 (25th percentile)
        quartiles[1] = calculateMedian(data); // Q2 (50th percentile, median)
        if (size % 2 == 0) {
            quartiles[2] = calculateMedian(data.subList(size / 2, size)); // Q3 (75th percentile)
        } else {
            quartiles[2] = calculateMedian(data.subList(size / 2 + 1, size)); // Q3 (75th percentile)
        }
        return quartiles;
    }

    // Method to print statistics
    public static void printStatistics(List<Double> data) {
        System.out.println("Mean: " + calculateMean(data));
        System.out.println("Median: " + calculateMedian(data));
        System.out.println("Mode: " + calculateMode(data));
        System.out.println("Standard Deviation: " + calculateStandardDeviation(data));
        System.out.println("Variance: " + calculateVariance(data));
        double[] quartiles = calculateQuartiles(data);
        System.out.println("Q1 (25th percentile): " + quartiles[0]);
        System.out.println("Q2 (50th percentile, median): " + quartiles[1]);
        System.out.println("Q3 (75th percentile): " + quartiles[2]);
    }
}
