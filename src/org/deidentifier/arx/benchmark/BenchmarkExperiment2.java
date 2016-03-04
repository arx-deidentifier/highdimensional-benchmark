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
import org.deidentifier.arx.BenchmarkMetadata;

import de.linearbits.subframe.Benchmark;
import de.linearbits.subframe.analyzer.ValueBuffer;

/**
 * Performs the second experiment, which is a comparison of our approach with a globally-optimal algorithm.
 *  
 * @author Fabian Prasser
 */
public class BenchmarkExperiment2 {

    /** Repetitions for the FLASH algorithm */
    private static final int       REPETITIONS = 5;

    /** The benchmark instance */
    private static final Benchmark BENCHMARK   = new Benchmark(new String[] { "Quality measure", "Privacy model", "Suppression limit", "Dataset" });

    /** Time */
    public static final int        FLASH       = BENCHMARK.addMeasure("Flash");
    /** Time */
    public static final int        LIGHTNING   = BENCHMARK.addMeasure("Lightning");
    /** Time */
    public static final int        DISCOVERY   = BENCHMARK.addMeasure("Discovery");
    /** Label for result quality */
    public static final int        QUALITY     = BENCHMARK.addMeasure("Quality");

    /**
     * Main entry point
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        
        // Init
        BENCHMARK.addAnalyzer(FLASH, new ValueBuffer());
        BENCHMARK.addAnalyzer(LIGHTNING, new ValueBuffer());
        BENCHMARK.addAnalyzer(DISCOVERY, new ValueBuffer());
        BENCHMARK.addAnalyzer(QUALITY, new ValueBuffer());

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
                        BENCHMARK.getResults().write(new File("results/experiment2.csv"));
                    }
                }
            }
        }
    }
    /**
     * Returns all privacy models for this experiment
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
    public static double[] getSuppressionLimits() {
        return new double[]{0d, 1d};
    }

    /**
     * Returns all quality measures for this experiment
     * @return
     */
    public static BenchmarkQualityMeasure[] getQualityMeasures() {
        return new BenchmarkQualityMeasure[] { 
                BenchmarkQualityMeasure.AECS,
                BenchmarkQualityMeasure.LOSS,
                BenchmarkQualityMeasure.DISCERNIBILITY,
                BenchmarkQualityMeasure.ENTROPY,
                BenchmarkQualityMeasure.PRECISION 
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

        System.out.println("Performing experiment 2 - " + dataset + "/" + measure + "/" +criterion + "/" + suppressionLimit);
        
        // Measure execution time of FLASH
        double flash = Double.MAX_VALUE;
        for (int i = 0; i < REPETITIONS; i++) {
            flash = Math.min(flash, BenchmarkEnvironment.getBenchmarkResults(BenchmarkAlgorithm.FLASH, dataset, measure, criterion, 0, suppressionLimit).executionTime);
        }
        BENCHMARK.addValue(FLASH, flash);

        // Measure total time of lightning
        double lightning = Double.MAX_VALUE;
        for (int i = 0; i < REPETITIONS; i++) {
            lightning = Math.min(lightning, BenchmarkEnvironment.getBenchmarkResults(BenchmarkAlgorithm.LIGHTNING, dataset, measure, criterion, Integer.MAX_VALUE, suppressionLimit).executionTime);
        }
        BENCHMARK.addValue(LIGHTNING, lightning);
        
        // Measure performance of lightning when executed with flash's time limit
        double quality = -1;
        double discovery = Double.MAX_VALUE;
        for (int i = 0; i < REPETITIONS; i++) {
            BenchmarkResults run = BenchmarkEnvironment.getBenchmarkResults(BenchmarkAlgorithm.LIGHTNING, dataset, measure, criterion, (int)flash, suppressionLimit);
            if (run.informationLoss != -1 && run.discoveryTime < discovery) {
                discovery = run.discoveryTime;
                quality = run.informationLoss;
            }
        }
        if (quality != -1) {
            double min = BenchmarkMetadata.getMinimalAndMaximalInformationLoss(dataset, measure, criterion, suppressionLimit)[0];
            double max = BenchmarkMetadata.getMinimalAndMaximalInformationLoss(dataset, measure, criterion, suppressionLimit)[1];
            quality = quality - min;
            quality /= max-min;
        }
        BENCHMARK.addValue(QUALITY, quality);
        BENCHMARK.addValue(DISCOVERY, discovery);        
    }
}
