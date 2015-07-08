package org.deidentifier.arx.analysis;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Iterator;

import org.deidentifier.arx.benchmark.BenchmarkExperiment1;
import org.deidentifier.arx.benchmark.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.benchmark.BenchmarkSetup.BenchmarkUtilityMeasure;

import de.linearbits.objectselector.Selector;
import de.linearbits.subframe.analyzer.Analyzer;
import de.linearbits.subframe.io.CSVFile;
import de.linearbits.subframe.io.CSVLine;

public class BenchmarkAnalysis1 {
    public static void main(String[] args) throws IOException, ParseException {
        
        // Prepare
        CSVFile file = new CSVFile(new File("results/experiment1.csv"));

        // For each plot
        for (BenchmarkUtilityMeasure measure : BenchmarkExperiment1.getUtilityMeasures()) {
            System.out.println("Measure: " + measure);
            for (double suppression : BenchmarkExperiment1.getSuppressionLimits()) {
                System.out.println(" - Suppression: " + String.valueOf(suppression));
                System.out.format("   - %-30s%-30s%-30s%-30s\n", new Object[]{"Criterion", "Own", "DataFly", "IGreedy"});
                for (BenchmarkCriterion criterion : BenchmarkExperiment1.getCriteria()) {
                    analyzeMean(file, measure, suppression, criterion); 
                }
            }
        }
        
        // For each plot
        for (BenchmarkUtilityMeasure measure : BenchmarkExperiment1.getUtilityMeasures()) {
            System.out.println("Measure: " + measure);
            for (double suppression : BenchmarkExperiment1.getSuppressionLimits()) {
                System.out.println(" - Suppression: " + String.valueOf(suppression));
                System.out.format("   - %-30s%-30s%-30s%-30s%-30s\n", new Object[]{"Dataset", "Criterion", "Own", "DataFly", "IGreedy"});
                for (BenchmarkCriterion criterion : BenchmarkExperiment1.getCriteria()) {
                    analyze(file, measure, suppression, criterion); 
                }
            }
        }
    }

    /**
     * Analyzes the results from one experiment
     * @param file
     * @param measure
     * @param suppression
     * @param criterion
     * @throws ParseException 
     */
    private static void analyzeMean(CSVFile file,
                                BenchmarkUtilityMeasure measure,
                                double suppression,
                                BenchmarkCriterion criterion) throws ParseException {

        // Select
        Selector<String[]> selector = file.getSelectorBuilder()
                                          .field("Suppression limit").equals(String.valueOf(suppression)).and()
                                          .field("Utility measure").equals(measure.toString()).and()
                                          .field("Privacy model").equals(criterion.toString())
                                          .build();

        // Iterate
        double own = 1d;
        double datafly = 1d;
        double igreedy = 1d;
        for (Iterator<CSVLine> iter = file.iterator(); iter.hasNext(); ) {
            CSVLine line = iter.next();
            if (selector.isSelected(line.getData())) {
                own *= Double.valueOf(line.get("Own", Analyzer.VALUE)) + 1d;
                datafly *= Double.valueOf(line.get("DataFly", Analyzer.VALUE)) + 1d;
                igreedy *= Double.valueOf(line.get("IGreedy", Analyzer.VALUE)) + 1d; 
            }
        }
        
        own = Double.valueOf(format((Math.pow(own, 1d/5d) - 1d) * 100d));
        datafly = Double.valueOf(format((Math.pow(datafly, 1d/5d) - 1d) * 100d));
        igreedy = Double.valueOf(format((Math.pow(igreedy, 1d/5d) - 1d) * 100d));
        
        Object[] output = new String[]{criterion.toString(),
                                       format(own, own, datafly, igreedy),
                                       format(datafly, own, datafly, igreedy),
                                       format(igreedy, own, datafly, igreedy)};
        System.out.format("   - %-30s%-30s%-30s%-30s\n", output);
        
    }

    /**
     * Analyzes the results from one experiment
     * @param file
     * @param measure
     * @param suppression
     * @param criterion
     * @throws ParseException 
     */
    private static void analyze(CSVFile file,
                                BenchmarkUtilityMeasure measure,
                                double suppression,
                                BenchmarkCriterion criterion) throws ParseException {

        // Select
        Selector<String[]> selector = file.getSelectorBuilder()
                                          .field("Suppression limit").equals(String.valueOf(suppression)).and()
                                          .field("Utility measure").equals(measure.toString()).and()
                                          .field("Privacy model").equals(criterion.toString())
                                          .build();

        // Iterate
        for (Iterator<CSVLine> iter = file.iterator(); iter.hasNext(); ) {
            CSVLine line = iter.next();
            if (selector.isSelected(line.getData())) {
                double own = Double.valueOf(format(Double.valueOf(line.get("Own", Analyzer.VALUE)) * 100d));
                double datafly = Double.valueOf(format(Double.valueOf(line.get("DataFly", Analyzer.VALUE)) * 100d));
                double igreedy = Double.valueOf(format(Double.valueOf(line.get("IGreedy", Analyzer.VALUE)) * 100d));
                Object[] output = new String[]{criterion.toString(),
                                               line.get("", "Dataset"),
                                               format(own, own, datafly, igreedy),
                                               format(datafly, own, datafly, igreedy),
                                               format(igreedy, own, datafly, igreedy)};
                System.out.format("   - %-30s%-30s%-30s%-30s%-30s\n", output);
            }
        }
    }

    /**
     * Formats the given output
     * @param value
     * @param value1
     * @param value2
     * @param value3
     * @return
     */
    private static String format(double value, double value1, double value2, double value3) {
        final DecimalFormat format = new DecimalFormat("###.###");
        String result = format.format(value).replace(',', '.');
        if (value == value1 && value == value2 && value == value3) {
            result = "(-) " + result;
        } else  if (value <= value1 && value <= value2 && value <= value3) {
            result = "(+) " + result;
        }
        return result;
    }

    /**
     * Formats the given output
     * @param value
     * @return
     */
    private static String format(double value) {
        final DecimalFormat format = new DecimalFormat("###.###");
        return format.format(value).replace(',', '.');
    }
}
