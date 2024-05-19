package data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class LinearRegression {
    private static final int TRAINING_PERCENTAGE = 70;
    private static final int MAX_ITERATIONS = 2000;
    private static final double LEARNING_RATE = 0.05; // Adjust learning rate
    private static final double REGULARIZATION_PARAM = 0.05; // Adjust regularization parameter

    public static void run(String filePath) {
        try {
            List<Map<String, Double>> data = new ArrayList<>();
            readData(filePath, data);
            System.out.println("Total data size: " + data.size());

            // Split data into training and testing sets
            List<Map<String, Double>> trainData = new ArrayList<>();
            List<Map<String, Double>> testData = new ArrayList<>();
            splitData(data, trainData, testData);
            System.out.println("Training data size: " + trainData.size());
            System.out.println("Testing data size: " + testData.size());

            // Normalize data
            standardizeData(trainData);
            standardizeData(testData);

            // Train the linear regression model
            Map<String, Double> coefficients = trainLinearRegression(trainData);

            // Test the model and calculate regression metrics
            calculateRegressionMetrics(coefficients, testData);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readData(String filename, List<Map<String, Double>> data) throws IOException {
        FileReader file = new FileReader(filename);
        BufferedReader br = new BufferedReader(file);

        String line;
        boolean headerSkipped = false;
        List<String> headers = new ArrayList<>();

        while ((line = br.readLine()) != null) {
            if (!headerSkipped) {
                headerSkipped = true;
                String[] headerValues = line.split(",");
                for (String header : headerValues) {
                    headers.add(header.trim());
                }
                System.out.println("Headers: " + headers); // Debug print
                continue;
            }

            String[] values = line.split(",");
            if (values.length >= headers.size()) {
                Map<String, Double> entry = new HashMap<>();
                boolean validEntry = true;
                for (int i = 0; i < headers.size(); i++) {
                    String header = headers.get(i);
                    String value = values[i].trim();
                    if (value.isEmpty() || value.equals("?")) {
                        validEntry = false;
                        break; // Skip this row
                    }
                    if (!header.equals("Surgery_type") && isNumeric(value)) {
                        entry.put(header, Double.parseDouble(value));
                    }
                }
                if (validEntry) {
                    try {
                        entry.put("Surgery_type", Double.parseDouble(values[headers.indexOf("Surgery_type")].trim()));
                        data.add(entry);
                    } catch (NumberFormatException e) {
                        System.out
                                .println("Invalid value for Surgery_type: " + values[headers.indexOf("Surgery_type")]);
                        // Skip rows where Surgery_type is not numeric
                    }
                }
            } else {
                System.out.println("Incomplete data in row: " + line); // Debug print
            }
        }

        br.close();
        file.close();

        System.out.println("Total data size after reading: " + data.size()); // Debug print
    }

    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?"); // Regex to check if the string is numeric
    }

    public static void splitData(List<Map<String, Double>> data, List<Map<String, Double>> trainData,
            List<Map<String, Double>> testData) {
        Random random = new Random();
        for (Map<String, Double> entry : data) {
            if (random.nextInt(100) < TRAINING_PERCENTAGE) {
                trainData.add(entry);
            } else {
                testData.add(entry);
            }
        }
        System.out.println("Training data size: " + trainData.size());
        System.out.println("Testing data size: " + testData.size());
    }

    public static void standardizeData(List<Map<String, Double>> data) {
        if (data.isEmpty())
            return;

        Map<String, Double> means = new HashMap<>();
        Map<String, Double> stdDevs = new HashMap<>();

        for (String key : data.get(0).keySet()) {
            means.put(key, 0.0);
            stdDevs.put(key, 0.0);
        }

        for (Map<String, Double> entry : data) {
            for (Map.Entry<String, Double> feature : entry.entrySet()) {
                String key = feature.getKey();
                means.put(key, means.get(key) + feature.getValue());
            }
        }

        for (String key : means.keySet()) {
            means.put(key, means.get(key) / data.size());
        }

        for (Map<String, Double> entry : data) {
            for (Map.Entry<String, Double> feature : entry.entrySet()) {
                String key = feature.getKey();
                stdDevs.put(key, stdDevs.get(key) + Math.pow(feature.getValue() - means.get(key), 2));
            }
        }

        for (String key : stdDevs.keySet()) {
            stdDevs.put(key, Math.sqrt(stdDevs.get(key) / data.size()));
        }

        for (Map<String, Double> entry : data) {
            for (Map.Entry<String, Double> feature : entry.entrySet()) {
                String key = feature.getKey();
                double value = feature.getValue();
                double mean = means.get(key);
                double stdDev = stdDevs.get(key);
                if (stdDev != 0) {
                    entry.put(key, (value - mean) / stdDev);
                } else {
                    entry.put(key, 0.0); // If standard deviation is zero
                }
            }
        }
    }

    public static Map<String, Double> trainLinearRegression(List<Map<String, Double>> trainData) {
        if (trainData.isEmpty()) {
            throw new IllegalArgumentException("Training data is empty.");
        }

        Map<String, Double> coefficients = new HashMap<>();
        coefficients.put("intercept", 0.0);

        for (String key : trainData.get(0).keySet()) {
            if (!key.equals("Surgery_type")) {
                coefficients.put(key, 0.0);
            }
        }

        for (int iteration = 0; iteration < MAX_ITERATIONS; iteration++) {
            Map<String, Double> gradient = new HashMap<>();
            gradient.put("intercept", 0.0);

            for (Map<String, Double> entry : trainData) {
                double y = entry.get("Surgery_type");
                double yPred = coefficients.get("intercept");
                for (Map.Entry<String, Double> feature : entry.entrySet()) {
                    String featureName = feature.getKey();
                    if (!featureName.equals("Surgery_type")) {
                        yPred += coefficients.get(featureName) * feature.getValue();
                    }
                }

                for (Map.Entry<String, Double> feature : entry.entrySet()) {
                    String featureName = feature.getKey();
                    if (!featureName.equals("Surgery_type")) {
                        double featureValue = feature.getValue();
                        double featureGrad = gradient.getOrDefault(featureName, 0.0);
                        gradient.put(featureName, featureGrad + (yPred - y) * featureValue);
                    }
                }
                gradient.put("intercept", gradient.get("intercept") + (yPred - y));
            }

            for (Map.Entry<String, Double> gradEntry : gradient.entrySet()) {
                String featureName = gradEntry.getKey();
                double grad = gradEntry.getValue();
                double coeff = coefficients.get(featureName);
                coeff -= LEARNING_RATE * grad / trainData.size();
                if (!featureName.equals("intercept")) {
                    coeff -= LEARNING_RATE * REGULARIZATION_PARAM * coeff / trainData.size(); // Ridge regression
                                                                                              // regularization term
                }
                coefficients.put(featureName, coeff);
            }
        }

        return coefficients;
    }

    public static void calculateRegressionMetrics(Map<String, Double> coefficients,
            List<Map<String, Double>> testData) {
        if (testData.isEmpty()) {
            System.out.println("Test data is empty.");
            return;
        }

        double sumSquaredErrors = 0.0;
        double sumAbsoluteErrors = 0.0;
        double totalSumSquares = 0.0;
        double sumY = 0.0;
        double sumYPred = 0.0;
        double sumYPredSquared = 0.0;
        double sumYSquared = 0.0;
        int correctPredictions = 0;
        int totalPredictions = 0;

        for (Map<String, Double> entry : testData) {
            double y = entry.get("Surgery_type");
            double yPred = coefficients.get("intercept");
            for (Map.Entry<String, Double> feature : entry.entrySet()) {
                if (!feature.getKey().equals("Surgery_type")) {
                    yPred += coefficients.getOrDefault(feature.getKey(), 0.0) * feature.getValue();
                }
            }

            double error = yPred - y;
            sumSquaredErrors += error * error;
            sumAbsoluteErrors += Math.abs(error);
            sumY += y;
            sumYPred += yPred;
            sumYSquared += y * y;
            sumYPredSquared += yPred * yPred;

            totalPredictions++;
            if (Math.abs(y - yPred) < 0.5) { // Assuming a threshold for correct prediction
                correctPredictions++;
            }
        }

        double meanY = sumY / testData.size();
        for (Map<String, Double> entry : testData) {
            double y = entry.get("Surgery_type");
            totalSumSquares += (y - meanY) * (y - meanY);
        }

        double r2 = 1 - (sumSquaredErrors / totalSumSquares);
        double mse = sumSquaredErrors / testData.size();
        double mae = sumAbsoluteErrors / testData.size();
        double rmse = Math.sqrt(mse);
        double accuracyPercentage = ((double) correctPredictions / totalPredictions) * 100 ;

        System.out.println("R² Score: " + r2);
        System.out.println("Mean Squared Error (MSE): " + mse);
        System.out.println("Mean Absolute Error (MAE): " + mae);
        System.out.println("Root Mean Squared Error (RMSE): " + rmse);
        System.out.println("Accuracy Percentage: " + accuracyPercentage + "%");
    }

}
