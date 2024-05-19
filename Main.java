import data.LinearRegression;
import data.AverageProteinLevelsByAgeGroup;
import data.DataVisualization;
import data.DescriptiveStatistics;
import data.MissingValuesCount;
import data.CorrelationCalculator;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String filePath = "BRCA.csv";
        List<String[]> data = DataVisualization.readCSV(filePath);

        while (true) {
            clearConsole();
            System.out.println("-------Main Menu-------");
            System.out.println("1. Visualize Data");
            System.out.println("2. Mean Protein Levels Grouped By Age Group");
            System.out.println("3. Descriptive Statistics");
            System.out.println("4. Count Missing Values");
            System.out.println("5. Calculate Correlation Matrix");
            System.out.println("6. Run Linear Regression");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    visualizeDataMenu(data);
                    break;
                case 2:
                    AverageProteinLevelsByAgeGroup.calculateAverageProteinLevels(filePath);
                    break;
                case 3:
                    DescriptiveStatistics.main(args);
                    break;
                case 4:
                    countMissingValues(filePath);
                    break;
                case 5:
                    calculateCorrelations(filePath);
                    break;
                case 6:
                    LinearRegression.run(filePath);
                    break;
                case 0:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine(); // Consume newline left after nextInt()
            scanner.nextLine(); // Wait for user to press Enter before continuing
        }
    }

    private static void visualizeDataMenu(List<String[]> data) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            clearConsole();
            System.out.println("--- Data Visualization Menu ---");
            System.out.println("1. Bar Chart");
            System.out.println("2. Line Chart");
            System.out.println("3. Pie Chart");
            System.out.println("4. Scatter Plot");
            System.out.println("5. Box Plot");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    DataVisualization.visualizeData(data, "bar");
                    break;
                case 2:
                    DataVisualization.visualizeData(data, "line");
                    break;
                case 3:
                    DataVisualization.visualizeData(data, "pie");
                    break;
                case 4:
                    DataVisualization.visualizeData(data, "scatter");
                    break;
                case 5:
                    DataVisualization.visualizeData(data, "box");
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine(); // Consume newline left after nextInt()
            scanner.nextLine(); // Wait for user to press Enter before continuing
        }
    }

    private static void countMissingValues(String filePath) {
        MissingValuesCount missingValuesCounter = new MissingValuesCount();
        Map<Integer, Integer> missingValueCounts = missingValuesCounter.countMissingValues(filePath);

        // Print the count of missing values in each column
        for (Map.Entry<Integer, Integer> entry : missingValueCounts.entrySet()) {
            int columnIndex = entry.getKey();
            int missingCount = entry.getValue();
            System.out.println("Column " + (columnIndex + 1) + " - Missing Values Count: " + missingCount);
        }
        System.out.println("\nPress Enter to continue...");
        new Scanner(System.in).nextLine(); // Wait for user to press Enter before continuing
    }

    private static void calculateCorrelations(String filePath) {
        try {
            Map<String, Map<String, Double>> correlationMap = CorrelationCalculator.calculateCorrelations(filePath);
            System.out.println("Pearson Correlation Matrix:");
            for (Map.Entry<String, Map<String, Double>> entry : correlationMap.entrySet()) {
                String heading1 = entry.getKey();
                Map<String, Double> correlations = entry.getValue();
                for (Map.Entry<String, Double> correlationEntry : correlations.entrySet()) {
                    String heading2 = correlationEntry.getKey();
                    double correlation = correlationEntry.getValue();
                    System.out.println(heading1 + " <-> " + heading2 + ": " + correlation);
                }
            }

            // Find and print the best pair of column headings
            Map.Entry<String, String> bestPair = CorrelationCalculator.findBestPair(correlationMap);
            if (bestPair != null) {
                System.out.println("\nBest Pair of Column Headings:");
                System.out.println(bestPair.getKey() + " <-> " + bestPair.getValue() + ": "
                        + correlationMap.get(bestPair.getKey()).get(bestPair.getValue()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("\nPress Enter to continue...");
        new Scanner(System.in).nextLine(); // Wait for user to press Enter before continuing
    }

    private static void clearConsole() {
        try {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                Runtime.getRuntime().exec("clear");
            }
        } catch (final Exception e) {
            System.out.println("Error clearing console: " + e.getMessage());
        }
    }
}
