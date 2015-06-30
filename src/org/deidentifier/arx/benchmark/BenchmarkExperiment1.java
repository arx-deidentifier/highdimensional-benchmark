package org.deidentifier.arx.benchmark;

import java.io.IOException;

import org.deidentifier.arx.benchmark.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.benchmark.BenchmarkSetup.BenchmarkDataset;

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
    private static BenchmarkCriterion[][] getCriteria() {
        BenchmarkCriterion[][] result = new BenchmarkCriterion[11][];
        result[0] = new BenchmarkCriterion[] { BenchmarkCriterion.K_ANONYMITY };
        result[1] = new BenchmarkCriterion[] { BenchmarkCriterion.L_DIVERSITY };
        result[2] = new BenchmarkCriterion[] { BenchmarkCriterion.T_CLOSENESS };
        result[3] = new BenchmarkCriterion[] { BenchmarkCriterion.D_PRESENCE };
        result[4] = new BenchmarkCriterion[] { BenchmarkCriterion.P_UNIQUENESS };
        return result;
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
