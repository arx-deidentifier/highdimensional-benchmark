/*
 * Source code of the experiments from our 2015 paper 
 * "Utility-driven anonymization of high-dimensional data"
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

package org.deidentifier.arx;

import java.io.File;
import java.io.IOException;

import org.deidentifier.arx.BenchmarkEnvironment.BenchmarkRun;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkAlgorithm;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkDataset;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkUtilityMeasure;

import cern.colt.list.DoubleArrayList;
import de.linearbits.subframe.Benchmark;
import de.linearbits.subframe.analyzer.ValueBuffer;

/**
 * Performs the first experiment, which is a comparison of our approach with a globally-optimal algorithm.
 *  
 * @author Fabian Prasser
 */
public class BenchmarkExperiment3 {

    /**
     * Returns all datasets for this experiment
     * @return
     */
    private static BenchmarkDataset[] getDatasets() {
        return new BenchmarkDataset[] { 
                BenchmarkDataset.SS13ACS_15,
                BenchmarkDataset.SS13ACS_20,
                BenchmarkDataset.SS13ACS_25,
                BenchmarkDataset.SS13ACS_30
        };
    }

    /**
     * Returns all criteria for this experiment
     * @return
     */
    private static BenchmarkCriterion[] getCriteria() {
        return new BenchmarkCriterion[]{
            BenchmarkCriterion.K_ANONYMITY,
            BenchmarkCriterion.P_UNIQUENESS
        };
    }

    /**
     * Returns all utility measures for this experiment
     * @return
     */
    private static BenchmarkUtilityMeasure[] getUtilityMeasures() {
        return new BenchmarkUtilityMeasure[] { 
                BenchmarkUtilityMeasure.AECS,
                BenchmarkUtilityMeasure.LOSS
        };
    }

    /**
     * Returns all suppression limits for this experiment
     * @return
     */
    private static double[] getSuppressionLimits() {
        return new double[]{0d, 1d};
    }

    /** The benchmark instance */
    private static final Benchmark BENCHMARK         = new Benchmark(new String[] { "Utility measure", "Privacy model", "Suppression limit", "Dataset" });
    /** Time */
    public static final int        TIME              = BENCHMARK.addMeasure("Time");
    /** Utility */
    public static final int        UTILITY           = BENCHMARK.addMeasure("Utility");
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
        BENCHMARK.addAnalyzer(UTILITY, new ValueBuffer());
        BENCHMARK.addAnalyzer(COMPLETE, new ValueBuffer());

        // For each relevant combination
        for (BenchmarkUtilityMeasure measure : getUtilityMeasures()) {
            for (BenchmarkCriterion criterion : getCriteria()) {
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
                                          BenchmarkUtilityMeasure measure,
                                          BenchmarkCriterion criterion,
                                          double suppressionLimit) throws IOException {
        
        System.out.println("Performing experiment 3 - " + dataset + "/" + measure + "/" +criterion + "/" + suppressionLimit);
        
        BenchmarkRun run = BenchmarkEnvironment.performRun(BenchmarkAlgorithm.LIGHTNING, dataset, measure, criterion, 600 * 1000, suppressionLimit);
        DoubleArrayList trackRecord = run.trackRecord;
        boolean complete = run.executionTime < 600 * 1000;
        
        double min = trackRecord.get(1);
        double max = trackRecord.get(trackRecord.size()-1);

        for (int i = 0; i < trackRecord.size(); i += 2) {
            double utility = min == max ? 1d : (trackRecord.get(i + 1) - min) / (max - min);
            BENCHMARK.addValue(TIME, trackRecord.get(i));
            BENCHMARK.addValue(UTILITY, utility);
            BENCHMARK.addValue(COMPLETE, complete);
            if (i < trackRecord.size() - 2) {
                BENCHMARK.addRun(measure.toString(), criterion.toString(), String.valueOf(suppressionLimit), dataset.toString());
            }
        }
    }
}
