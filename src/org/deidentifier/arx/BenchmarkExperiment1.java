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

import java.io.IOException;

import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkDataset;

import de.linearbits.subframe.Benchmark;
import de.linearbits.subframe.analyzer.buffered.BufferedArithmeticMeanAnalyzer;
import de.linearbits.subframe.analyzer.buffered.BufferedStandardDeviationAnalyzer;

public class BenchmarkExperiment1 {

    /**
     * Returns all datasets
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
     * Returns all sets of criteria
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

    /** Repetitions */
    private static final int       REPETITIONS       = 3;
    /** The benchmark instance */
    private static final Benchmark BENCHMARK         = new Benchmark(new String[] { "Algorithm", "Dataset", "Criteria" });
    /** Label for execution times */
    public static final int        EXECUTION_TIME    = BENCHMARK.addMeasure("Execution time");

    static {
        BENCHMARK.addAnalyzer(EXECUTION_TIME, new BufferedArithmeticMeanAnalyzer(REPETITIONS));
        BENCHMARK.addAnalyzer(EXECUTION_TIME, new BufferedStandardDeviationAnalyzer(REPETITIONS));
    }

    /**
     * Main entry point
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

//        BenchmarkDriver driver = new BenchmarkDriver(BENCHMARK);
//
//        // For each algorithm
//        for (BenchmarkAlgorithm algorithm : BenchmarkSetup.getAlgorithms()) {
//            
//            // For each dataset
//            for (BenchmarkDataset data : BenchmarkSetup.getDatasets()) {
//                
//                // For each combination of criteria
//                for (BenchmarkCriterion[] criteria : BenchmarkSetup.getCriteria()) {
//
//                    // Warmup run
//                    driver.anonymize(data, criteria, algorithm, true);
//
//                    // Print status info
//                    System.out.println("Running: " + algorithm.toString() + " / " + data.toString() + " / " + Arrays.toString(criteria));
//
//                    // Benchmark
//                    BENCHMARK.addRun(algorithm.toString(), data.toString(), Arrays.toString(criteria));
//                    
//                    // Repeat
//                    for (int i = 0; i < REPETITIONS; i++) {
//                        driver.anonymize(data, criteria, algorithm, false);
//                    }
//                    
//                    // Write results incrementally
//                    BENCHMARK.getResults().write(new File("results/results.csv"));
//                }
//            }
//        }
    }
}
