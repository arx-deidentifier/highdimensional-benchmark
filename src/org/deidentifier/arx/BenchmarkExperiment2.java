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

import de.linearbits.subframe.Benchmark;
import de.linearbits.subframe.analyzer.ValueBuffer;

/**
 * Performs the first experiment, which is a comparison of our approach with a globally-optimal algorithm.
 *  
 * @author Fabian Prasser
 */
public class BenchmarkExperiment2 {

    /** Repetitions for the FLASH algorithm */
    private static final int       REPETITIONS = 5;

    /** The benchmark instance */
    private static final Benchmark BENCHMARK         = new Benchmark(new String[] { "Utility measure", "Privacy model", "Suppression limit", "Dataset" });

    /** Time */
    public static final int        FLASH             = BENCHMARK.addMeasure("Flash");

    /** Time */
    public static final int        TOTAL             = BENCHMARK.addMeasure("Total");

    /** Time */
    public static final int        DISCOVERY         = BENCHMARK.addMeasure("Discovery");
    /** Label for result quality */
    public static final int        UTILITY           = BENCHMARK.addMeasure("Utility");
    /**
     * Main entry point
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        
        // Init
        BENCHMARK.addAnalyzer(FLASH, new ValueBuffer());
        BENCHMARK.addAnalyzer(TOTAL, new ValueBuffer());
        BENCHMARK.addAnalyzer(DISCOVERY, new ValueBuffer());
        BENCHMARK.addAnalyzer(UTILITY, new ValueBuffer());

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
                        BENCHMARK.getResults().write(new File("results/experiment2.csv"));
                    }
                }
            }
        }
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
     * Returns all datasets for this experiment
     * @return
     */
    private static BenchmarkDataset[] getDatasets() {
        return new BenchmarkDataset[] { 
                BenchmarkDataset.ADULT,
                BenchmarkDataset.CUP,
                BenchmarkDataset.FARS,
                BenchmarkDataset.ATUS,
                BenchmarkDataset.IHIS
        };
    }
    /**
     * Returns all suppression limits for this experiment
     * @return
     */
    private static double[] getSuppressionLimits() {
        return new double[]{0d, 1d};
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

        System.out.println("Performing experiment 2 - " + dataset + "/" + measure + "/" +criterion + "/" + suppressionLimit);
        
        // Measure execution time of FLASH
        double flash = Double.MAX_VALUE;
        for (int i = 0; i < REPETITIONS; i++) {
            flash = Math.min(flash, BenchmarkEnvironment.performRun(BenchmarkAlgorithm.FLASH, dataset, measure, criterion, 0, suppressionLimit).executionTime);
        }
        BENCHMARK.addValue(FLASH, flash);

        // Measure total time of own
        double total = Double.MAX_VALUE;
        for (int i = 0; i < REPETITIONS; i++) {
            total = Math.min(total, BenchmarkEnvironment.performRun(BenchmarkAlgorithm.LIGHTNING, dataset, measure, criterion, Integer.MAX_VALUE, suppressionLimit).executionTime);
        }
        BENCHMARK.addValue(TOTAL, total);
        
        // Measure quality of own when executed with flash's timeLimit and discovery time
        BenchmarkRun run = BenchmarkEnvironment.performRun(BenchmarkAlgorithm.LIGHTNING, dataset, measure, criterion, (int)flash, suppressionLimit);
        double utility = run.informationLoss;
        if (utility != -1) {
            double min = BenchmarkUtilityMetadata.getMinimalAndMaximalInformationLoss(dataset, measure, criterion, suppressionLimit)[0];
            double max = BenchmarkUtilityMetadata.getMinimalAndMaximalInformationLoss(dataset, measure, criterion, suppressionLimit)[1];
            utility = utility - min;
            utility /= max-min;
        }
        BENCHMARK.addValue(UTILITY, utility);
        BENCHMARK.addValue(DISCOVERY, run.discoveryTime);        
    }
}
