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
                
                double flash = Double.valueOf(format(Double.valueOf(line.get("Flash", Analyzer.VALUE)) / 1000d));
                double total = Double.valueOf(format(Double.valueOf(line.get("Lightning", Analyzer.VALUE)) / 1000d));
                
                String discovery = "--";
                String utility = "--"; 
                double _utility = Double.valueOf(format(Double.valueOf(line.get("Quality", Analyzer.VALUE)) * 100d));
                if (_utility != -100d) {
                    utility = format(100d - _utility);
                    discovery = format(Double.valueOf(format(Double.valueOf(line.get("Discovery", Analyzer.VALUE)) / 1000d)));
                }
                
                Object[] output = new String[]{line.get("", "Dataset"),
                                               format(flash),
                                               format(total),
                                               discovery,
                                               utility};
                System.out.format("%-30s%-30s%-30s%-30s%-30s\n", output);
            }
        }
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
