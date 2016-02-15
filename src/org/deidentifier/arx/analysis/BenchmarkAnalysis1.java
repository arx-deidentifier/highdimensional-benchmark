/*
 * Source code of the experiments from our 2016 paper 
 * "Lightning: Utility-driven anonymization of high-dimensional data"
 *      
 * Copyright (C) 2015 Fabian Prasser, Raffael Bild, Johanna Eicher, Helmut Spengler, Florian Kohlmayer
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.deidentifier.arx.analysis;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Iterator;

import org.deidentifier.arx.BenchmarkSetup.BenchmarkPrivacyModel;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkQualityMeasure;
import org.deidentifier.arx.benchmark.BenchmarkExperiment1;

import de.linearbits.objectselector.Selector;
import de.linearbits.subframe.analyzer.Analyzer;
import de.linearbits.subframe.io.CSVFile;
import de.linearbits.subframe.io.CSVLine;

/**
 * Analysis of the results of the 1st set of experiments
 * @author Fabian Prasser
 */
public class BenchmarkAnalysis1 {

    /**
     * Main
     * @param args
     * @throws IOException
     * @throws ParseException
     */
    public static void main(String[] args) throws IOException, ParseException {
        
        // Prepare
        CSVFile file = new CSVFile(new File("results/experiment1.csv"));

        // For each plot
        for (BenchmarkQualityMeasure measure : BenchmarkExperiment1.getUtilityMeasures()) {
            for (double suppression : BenchmarkExperiment1.getSuppressionLimits()) {
                System.out.println("----------------------");
                System.out.println("Quality measure: " + measure);
                System.out.println("Suppression limit: " + String.valueOf(suppression));
                System.out.println("----------------------");
                System.out.println("");
                System.out.format("%-30s%-30s%-30s%-30s\n", new Object[]{"Privacy model", "Lightning", "DataFly", "IGreedy"});
                for (BenchmarkPrivacyModel criterion : BenchmarkExperiment1.getCriteria()) {
                    analyzeMean(file, measure, suppression, criterion); 
                }
                System.out.println("");
            }
        }
        
        // For each plot
        for (BenchmarkQualityMeasure measure : BenchmarkExperiment1.getUtilityMeasures()) {
            for (double suppression : BenchmarkExperiment1.getSuppressionLimits()) {
                System.out.println("----------------------");
                System.out.println("Quality measure: " + measure);
                System.out.println("Suppression limit: " + String.valueOf(suppression));
                System.out.println("----------------------");
                System.out.println("");
                System.out.format("%-30s%-30s%-30s%-30s%-30s\n", new Object[]{"Dataset", "Privacy model", "Lightning", "DataFly", "IGreedy"});
                for (BenchmarkPrivacyModel criterion : BenchmarkExperiment1.getCriteria()) {
                    analyze(file, measure, suppression, criterion); 
                }
                System.out.println("");
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
                                BenchmarkQualityMeasure measure,
                                double suppression,
                                BenchmarkPrivacyModel criterion) throws ParseException {

        // Select
        Selector<String[]> selector = file.getSelectorBuilder()
                                          .field("Suppression limit").equals(String.valueOf(suppression)).and()
                                          .field("Quality measure").equals(measure.toString()).and()
                                          .field("Privacy model").equals(criterion.toString())
                                          .build();

        // Iterate
        double lightning = 1d;
        double datafly = 1d;
        double igreedy = 1d;
        for (Iterator<CSVLine> iter = file.iterator(); iter.hasNext(); ) {
            CSVLine line = iter.next();
            if (selector.isSelected(line.getData())) {
                lightning *= Double.valueOf(line.get("Lightning", Analyzer.VALUE)) + 1d;
                datafly *= Double.valueOf(line.get("DataFly", Analyzer.VALUE)) + 1d;
                igreedy *= Double.valueOf(line.get("IGreedy", Analyzer.VALUE)) + 1d; 
            }
        }
        
        lightning = Double.valueOf(format((Math.pow(lightning, 1d/5d) - 1d) * 100d));
        datafly = Double.valueOf(format((Math.pow(datafly, 1d/5d) - 1d) * 100d));
        igreedy = Double.valueOf(format((Math.pow(igreedy, 1d/5d) - 1d) * 100d));
        
        Object[] output = new String[]{criterion.toString(),
                                       format(lightning, lightning, datafly, igreedy),
                                       format(datafly, lightning, datafly, igreedy),
                                       format(igreedy, lightning, datafly, igreedy)};
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
                                BenchmarkQualityMeasure measure,
                                double suppression,
                                BenchmarkPrivacyModel criterion) throws ParseException {

        // Select
        Selector<String[]> selector = file.getSelectorBuilder()
                                          .field("Suppression limit").equals(String.valueOf(suppression)).and()
                                          .field("Quality measure").equals(measure.toString()).and()
                                          .field("Privacy model").equals(criterion.toString())
                                          .build();

        // Iterate
        for (Iterator<CSVLine> iter = file.iterator(); iter.hasNext(); ) {
            CSVLine line = iter.next();
            if (selector.isSelected(line.getData())) {
                double own = Double.valueOf(format(Double.valueOf(line.get("Lightning", Analyzer.VALUE)) * 100d));
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
