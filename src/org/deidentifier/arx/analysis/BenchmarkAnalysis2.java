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
import org.deidentifier.arx.benchmark.BenchmarkExperiment2;

import de.linearbits.objectselector.Selector;
import de.linearbits.subframe.analyzer.Analyzer;
import de.linearbits.subframe.io.CSVFile;
import de.linearbits.subframe.io.CSVLine;

/**
 * Analysis of the results of the 2nd set of experiments
 * @author Fabian Prasser
 */
public class BenchmarkAnalysis2 {

    /**
     * Main
     * @param args
     * @throws IOException
     * @throws ParseException
     */
    public static void main(String[] args) throws IOException, ParseException {
        
        // Prepare
        CSVFile file = new CSVFile(new File("results/experiment2.csv"));

        // For each plot
        for (BenchmarkQualityMeasure measure : BenchmarkExperiment2.getQualityMeasures()) {
            for (double suppression : BenchmarkExperiment2.getSuppressionLimits()) {
                for (BenchmarkPrivacyModel model : BenchmarkExperiment2.getPrivacyModels()) {
                    System.out.println("----------------------");
                    System.out.println("Privacy model: " + model);
                    System.out.println("Quality measure: " + measure);
                    System.out.println("Suppression limit: " + String.valueOf(suppression));
                    System.out.println("----------------------");
                    System.out.println("");
                    System.out.format("%-30s%-30s%-30s%-30s%-30s\n", new Object[]{"Dataset", "Flash", "Lightning", "Discovery", "Quality"});
                    analyze(file, measure, suppression, model);
                    System.out.println("");
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
                
                // Read values
                double _flash = getValue(line, "Flash", 1d / 1000d);
                double _lightning = getValue(line, "Lightning", 1d / 1000d);
                double _utility = getValue(line, "Quality", 100d);
                
                // Convert to strings
                String discovery = "--";
                String utility = "--"; 
                String flash = null;
                String lightning = null;
                if (_utility != -100d) {
                    utility = format(100d - _utility);
                    discovery = format(getValue(line, "Discovery", 1d / 1000d));
                }
                flash = format(_flash);
                lightning = format(_lightning);

                // Discovery and total time of lightning are potentially measured in different benchmark runs.
                // Due to fluctuations in the JVM execution times, discovery can thus be
                // a bit larger than total time, when they are actually identical. 
                // We handle this special case here.
                if (!discovery.equals("--") && Double.valueOf(discovery) > Double.valueOf(_lightning)) {
                    discovery = format(_lightning);
                }
                
                // Print
                Object[] output = new String[]{line.get("", "Dataset"),
                                               flash,
                                               lightning,
                                               discovery,
                                               utility};
                System.out.format("%-30s%-30s%-30s%-30s%-30s\n", output);
            }
        }
    }
    
    /**
     * Returns a field value multiplied with the given factor. Performs some rounding
     * @param line
     * @param field
     * @param factor
     * @return
     */
    private static double getValue(CSVLine line, String field, double factor) {
        return Double.valueOf(format(Double.valueOf(line.get(field, Analyzer.VALUE)) * factor));
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
