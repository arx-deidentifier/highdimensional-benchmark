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

package org.deidentifier.arx.benchmark;

import java.io.File;
import java.io.IOException;

import org.deidentifier.arx.BenchmarkEnvironment;
import org.deidentifier.arx.BenchmarkEnvironment.BenchmarkResults;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkAlgorithm;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkPrivacyModel;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkDataset;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkQualityMeasure;

import cern.colt.list.DoubleArrayList;
import de.linearbits.subframe.Benchmark;
import de.linearbits.subframe.analyzer.ValueBuffer;

/**
 * Performs the third experiment, which is an evaluation of our approach with high-dimensional data.
 *  
 * @author Fabian Prasser
 */
public class BenchmarkExperiment3 {

    /** The benchmark instance */
    private static final Benchmark BENCHMARK         = new Benchmark(new String[] { "Quality measure", "Privacy model", "Suppression limit", "Dataset" });

    /** Time */
    public static final int        TIME              = BENCHMARK.addMeasure("Time");

    /** Utility */
    public static final int        QUALITY           = BENCHMARK.addMeasure("Quality");

    /** Complete search performed */
    public static final int        COMPLETE          = BENCHMARK.addMeasure("Complete");

    /**
     * Main entry point
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        
        // Init
        BENCHMARK.addAnalyzer(TIME, new ValueBuffer());
        BENCHMARK.addAnalyzer(QUALITY, new ValueBuffer());
        BENCHMARK.addAnalyzer(COMPLETE, new ValueBuffer());

        // For each relevant combination
        for (BenchmarkQualityMeasure measure : getQualityMeasures()) {
            for (BenchmarkPrivacyModel criterion : getPrivacyModels()) {
                for (double suppressionLimit : getSuppressionLimits()) {
                    for (BenchmarkDataset dataset : getDatasets()) {

                        // Run
                        BENCHMARK.addRun(measure.toString(), criterion.toString(), String.valueOf(suppressionLimit), dataset.toString());
                        
                        // Measurements
                        performExperiment(dataset, measure, criterion, suppressionLimit);
                        
                        // Write after each experiment
                        BENCHMARK.getResults().write(new File("results/experiment3.csv"));
                    }
                }
            }
        }
    }
    /**
     * Returns all criteria for this experiment
     * @return
     */
    public static BenchmarkPrivacyModel[] getPrivacyModels() {
        return new BenchmarkPrivacyModel[]{
            BenchmarkPrivacyModel.K_ANONYMITY,
            BenchmarkPrivacyModel.P_UNIQUENESS
        };
    }
    /**
     * Returns all datasets for this experiment
     * @return
     */
    public static BenchmarkDataset[] getDatasets() {
        return new BenchmarkDataset[] { 
                BenchmarkDataset.SS13ACS_15,
                BenchmarkDataset.SS13ACS_20,
                BenchmarkDataset.SS13ACS_25,
                BenchmarkDataset.SS13ACS_30
        };
    }
    /**
     * Returns all suppression limits for this experiment
     * @return
     */
    public static double[] getSuppressionLimits() {
        return new double[]{0d, 1d};
    }

    /**
     * Returns all utility measures for this experiment
     * @return
     */
    public static BenchmarkQualityMeasure[] getQualityMeasures() {
        return new BenchmarkQualityMeasure[] { 
                BenchmarkQualityMeasure.AECS,
                BenchmarkQualityMeasure.LOSS
        };
    }

    /**
     * Performs one experiment
     * @param algorithm
     * @param benchmarkMeasure
     * @param dataset
     * @param measure
     * @param criterion
     * @param suppressionLimit
     * @throws IOException
     */
    private static void performExperiment(BenchmarkDataset dataset,
                                          BenchmarkQualityMeasure measure,
                                          BenchmarkPrivacyModel criterion,
                                          double suppressionLimit) throws IOException {
        
        System.out.println("Performing experiment 3 - " + dataset + "/" + measure + "/" +criterion + "/" + suppressionLimit);
        
        // Perform
        BenchmarkResults run = BenchmarkEnvironment.getBenchmarkResults(BenchmarkAlgorithm.LIGHTNING, dataset, measure, criterion, 600 * 1000, suppressionLimit);
        DoubleArrayList trackRecord = run.trackRecord;
        
        // Check if completed
        boolean complete = run.executionTime < 600 * 1000;
        
        // Min and max
        double min = trackRecord.get(1);
        double max = trackRecord.get(trackRecord.size()-1);

        // For each step
        double previous = Double.MAX_VALUE;
        for (int i = 0; i < trackRecord.size(); i += 2) {
            
            // Normalize
            double utility = min == max ? 1d : (trackRecord.get(i + 1) - min) / (max - min);
            
            // Ignore steps in which utility did not change
            if (utility == -0d) utility = +0d;
            if (utility != previous) {
                previous = utility; 
                BENCHMARK.addValue(TIME, trackRecord.get(i));
                BENCHMARK.addValue(QUALITY, utility);
                BENCHMARK.addValue(COMPLETE, complete);
                BENCHMARK.addRun(measure.toString(), criterion.toString(), String.valueOf(suppressionLimit), dataset.toString());
            }
        }
    }
}
