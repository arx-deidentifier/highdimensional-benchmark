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

import org.deidentifier.arx.BenchmarkSetup.BenchmarkAlgorithm;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkDataset;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkUtilityMeasure;

import de.linearbits.subframe.Benchmark;
import de.linearbits.subframe.analyzer.ValueBuffer;

/**
 * Performs the first experiment, which is a comparison of our approach with previous works
 * using the concept of minimal anonymity.
 *  
 * @author Fabian Prasser
 */
public class BenchmarkExperiment1 {

    /** The benchmark instance */
    private static final Benchmark BENCHMARK = new Benchmark(new String[] { "Utility measure", "Suppression limit", "Privacy model", "Dataset"});

    /** Label for result quality */
    public static final int        OWN       = BENCHMARK.addMeasure("Own");

    /** Label for result quality */
    public static final int        DATAFLY   = BENCHMARK.addMeasure("DataFly");

    /** Label for result quality */
    public static final int        IGREEDY   = BENCHMARK.addMeasure("IGreedy");

    /**
     * Main entry point
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        
        // Init
        BENCHMARK.addAnalyzer(OWN, new ValueBuffer());
        BENCHMARK.addAnalyzer(DATAFLY, new ValueBuffer());
        BENCHMARK.addAnalyzer(IGREEDY, new ValueBuffer());

        // For each relevant combination
        for (BenchmarkUtilityMeasure measure : getUtilityMeasures()) {
            for (double suppressionLimit : getSuppressionLimits()) {
                for (BenchmarkCriterion criterion : getCriteria()) {
                    for (BenchmarkDataset dataset : getDatasets()) {

                        // Run
                        BENCHMARK.addRun(measure.toString(), String.valueOf(suppressionLimit), criterion.toString(), dataset.toString());
                        
                        // Measurements
                        performExperiment(BenchmarkAlgorithm.LIGHTNIG_MINIMAL, OWN, dataset, measure, criterion, suppressionLimit);
                        performExperiment(BenchmarkAlgorithm.DATAFLY, DATAFLY, dataset, measure, criterion, suppressionLimit);
                        performExperiment(BenchmarkAlgorithm.IGREEDY, IGREEDY, dataset, measure, criterion, suppressionLimit);
                        
                        // Write after each experiment
                        BENCHMARK.getResults().write(new File("results/experiment1.csv"));
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
            BenchmarkCriterion.L_DIVERSITY,
            BenchmarkCriterion.T_CLOSENESS,
            BenchmarkCriterion.D_PRESENCE,
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
        return new double[]{0d, 0.1d};
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
    private static void performExperiment(BenchmarkAlgorithm algorithm,
                                         int benchmarkMeasure,
                                         BenchmarkDataset dataset,
                                         BenchmarkUtilityMeasure measure,
                                         BenchmarkCriterion criterion,
                                         double suppressionLimit) throws IOException {

        System.out.println("Performing experiment 1 - " + algorithm + "/" + dataset + "/" + measure + "/" +criterion + "/" + suppressionLimit);
        
        // Measure utility
        double value = BenchmarkEnvironment.performRun(algorithm, dataset, measure, criterion, 0, suppressionLimit).informationLoss;
        
        // Normalize
        double min = BenchmarkUtilityMetadata.getMinimalAndMaximalInformationLoss(dataset, measure, criterion, suppressionLimit)[0];
        double max = BenchmarkUtilityMetadata.getMinimalAndMaximalInformationLoss(dataset, measure, criterion, suppressionLimit)[1];
        value = value - min;
        value /= max-min;
        
        // Add
        BENCHMARK.addValue(benchmarkMeasure, value);
    }
}
