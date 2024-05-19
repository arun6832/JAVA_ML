package data;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.statistics.BoxAndWhiskerCalculator;
import org.jfree.data.statistics.BoxAndWhiskerItem;

import javax.swing.*;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

public class DataVisualization {

    public static void visualizeData(List<String[]> data, String chartType) {
        if (chartType.equalsIgnoreCase("bar")) {
            visualizeBarChart(data, "Tumour Stage", "Average Protein1 by Tumour Stage");
        } else if (chartType.equalsIgnoreCase("line")) {
            visualizeLineChart(data, "Histology", "Average Protein2 by Histology");
        } else if (chartType.equalsIgnoreCase("pie")) {
            visualizePieChart(data, "Surgery Type", "Distribution of Surgery Type");
        } else if (chartType.equalsIgnoreCase("scatter")) {
            visualizeScatterPlot(data, "Age", "Protein3", "Scatter Plot of Age vs Protein3");
        } else if (chartType.equalsIgnoreCase("box")) {
            visualizeBoxPlot(data, "ER status", "Box Plot of Protein4 by ER status");
        } else {
            System.out.println("Unsupported chart type: " + chartType);
        }
    }

    private static void visualizeBarChart(List<String[]> data, String category, String title) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, double[]> categoryMap = new HashMap<>();

        for (String[] row : data) {
            try {
                double value = Double.parseDouble(row[2]); // Protein1
                String categoryValue = row[6]; // Tumour_Stage
                categoryMap.putIfAbsent(categoryValue, new double[2]);
                categoryMap.get(categoryValue)[0] += value;
                categoryMap.get(categoryValue)[1] += 1;
            } catch (NumberFormatException e) {
                System.err.println("Error parsing value to double: " + e.getMessage());
            }
        }

        for (Map.Entry<String, double[]> entry : categoryMap.entrySet()) {
            String categoryValue = entry.getKey();
            double[] values = entry.getValue();
            double average = values[1] > 0 ? values[0] / values[1] : 0;
            dataset.addValue(average, "Protein1", categoryValue);
        }

        JFreeChart chart = ChartFactory.createBarChart(title, category, "Average Protein1", dataset);
        displayChart(chart, title);
    }

    private static void visualizeLineChart(List<String[]> data, String category, String title) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, double[]> categoryMap = new HashMap<>();

        for (String[] row : data) {
            try {
                double value = Double.parseDouble(row[3]); // Protein2
                String categoryValue = row[7]; // Histology
                categoryMap.putIfAbsent(categoryValue, new double[2]);
                categoryMap.get(categoryValue)[0] += value;
                categoryMap.get(categoryValue)[1] += 1;
            } catch (NumberFormatException e) {
                System.err.println("Error parsing value to double: " + e.getMessage());
            }
        }

        for (Map.Entry<String, double[]> entry : categoryMap.entrySet()) {
            String categoryValue = entry.getKey();
            double[] values = entry.getValue();
            double average = values[1] > 0 ? values[0] / values[1] : 0;
            dataset.addValue(average, "Protein2", categoryValue);
        }

        JFreeChart chart = ChartFactory.createLineChart(title, category, "Average Protein2", dataset);
        displayChart(chart, title);
    }

    private static void visualizePieChart(List<String[]> data, String category, String title) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        Map<String, Integer> categoryMap = new HashMap<>();

        for (String[] row : data) {
            String categoryValue = row[11]; // Surgery_type
            categoryMap.put(categoryValue, categoryMap.getOrDefault(categoryValue, 0) + 1);
        }

        for (Map.Entry<String, Integer> entry : categoryMap.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        JFreeChart chart = ChartFactory.createPieChart(title, dataset);
        displayChart(chart, title);
    }

    private static void visualizeScatterPlot(List<String[]> data, String xLabel, String yLabel, String title) {
        DefaultXYDataset dataset = new DefaultXYDataset();
        List<double[]> points = new ArrayList<>();

        for (String[] row : data) {
            try {
                double xValue = Double.parseDouble(row[0]); // Age
                double yValue = Double.parseDouble(row[4]); // Protein3
                points.add(new double[] { xValue, yValue });
            } catch (NumberFormatException e) {
                System.err.println("Error parsing value to double: " + e.getMessage());
            }
        }

        double[][] dataPoints = new double[2][points.size()];
        for (int i = 0; i < points.size(); i++) {
            dataPoints[0][i] = points.get(i)[0];
            dataPoints[1][i] = points.get(i)[1];
        }

        dataset.addSeries("Age vs Protein3", dataPoints);
        JFreeChart chart = ChartFactory.createScatterPlot(title, xLabel, yLabel, dataset, PlotOrientation.VERTICAL,
                true, true, false);
        displayChart(chart, title);
    }

    private static void visualizeBoxPlot(List<String[]> data, String category, String title) {
        DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
        Map<String, List<Double>> categoryMap = new HashMap<>();

        for (String[] row : data) {
            try {
                double value = Double.parseDouble(row[5]); // Protein4
                String categoryValue = row[8]; // ER status
                categoryMap.putIfAbsent(categoryValue, new ArrayList<>());
                categoryMap.get(categoryValue).add(value);
            } catch (NumberFormatException e) {
                System.err.println("Error parsing value to double: " + e.getMessage());
            }
        }

        for (Map.Entry<String, List<Double>> entry : categoryMap.entrySet()) {
            String categoryValue = entry.getKey();
            List<Double> values = entry.getValue();
            BoxAndWhiskerItem item = BoxAndWhiskerCalculator.calculateBoxAndWhiskerStatistics(values);
            dataset.add(item, "Protein4", categoryValue);
        }

        JFreeChart chart = ChartFactory.createBoxAndWhiskerChart(title, category, "Protein4", dataset, false);
        displayChart(chart, title);
    }

    private static void displayChart(JFreeChart chart, String title) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame(title);
            frame.setSize(800, 600);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setVisible(true);

            ChartPanel chartPanel = new ChartPanel(chart);
            frame.setContentPane(chartPanel);
        });
    }

    public static List<String[]> readCSV(String filePath) {
        List<String[]> data = new ArrayList<>();
        try (Reader in = new FileReader(filePath)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);
            for (CSVRecord record : records) {
                String[] row = new String[record.size()];
                for (int i = 0; i < record.size(); i++) {
                    row[i] = record.get(i);
                }
                data.add(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

}