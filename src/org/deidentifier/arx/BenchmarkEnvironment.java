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
import java.util.HashMap;

import org.deidentifier.arx.BenchmarkSetup.BenchmarkAlgorithm;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkDataset;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkUtilityMeasure;
import org.deidentifier.arx.algorithm.AlgorithmFlash;
import org.deidentifier.arx.algorithm.AlgorithmLightning;
import org.deidentifier.arx.algorithm.AlgorithmMinimal;
import org.deidentifier.arx.algorithm.FLASHStrategy;
import org.deidentifier.arx.framework.check.NodeChecker;
import org.deidentifier.arx.framework.check.distribution.DistributionAggregateFunction;
import org.deidentifier.arx.framework.data.DataManager;
import org.deidentifier.arx.framework.data.Dictionary;
import org.deidentifier.arx.framework.lattice.SolutionSpace;

import cern.colt.list.DoubleArrayList;

/**
 * Creates a benchmarking environment consisting of a solution space, node checked, data manager etc. Furthermore,
 * initializes all configuration files
 * 
 * @author Fabian Prasser
 */
public class BenchmarkEnvironment {

    /**
     * The result of one benchmark run
     * @author Fabian Prasser
     */
    public static final class BenchmarkRun {

        /** Execution time */
        public final double          executionTime;
        /** Information loss: -1 means "no result" */
        public final double          informationLoss;
        /** Discovery time */
        public final double          discoveryTime;
        /** Track record */
        public final DoubleArrayList trackRecord;

        /**
         * Creates a new instance
         * @param executionTime
         * @param informationLoss
         * @param discoveryTime
         */
        public BenchmarkRun(double executionTime, double informationLoss, double discoveryTime, DoubleArrayList trackRecord) {
            this.executionTime = executionTime;
            this.informationLoss = informationLoss;
            this.discoveryTime = discoveryTime;
            this.trackRecord = trackRecord;
        }
    }
    
    /** History size. */
    private static int    CONST_HISTORY_SIZE    = 200;

    /** Snapshot size. */
    private static double CONST_SNAPSHOT_SIZE_1 = 0.2d;

    /** Snapshot size snapshot. */
    private static double CONST_SNAPSHOT_SIZE_2 = 0.8d;

    /**
     * Returns the resulting utility value as a double
     * @param algorithm
     * @param dataset
     * @param measure
     * @param criterion
     * @param timeLimit
     * @param suppressionLimit
     * @return
     * @throws IOException 
     */
    public static BenchmarkRun performRun(BenchmarkAlgorithm algorithm,
                                          BenchmarkDataset dataset,
                                          BenchmarkUtilityMeasure measure,
                                          BenchmarkCriterion criterion,
                                          int timeLimit,
                                          double suppressionLimit) throws IOException {
        
        // Perform 10 repetitions for p_uniqueness
        BenchmarkRun result = null;
        int repetitions = criterion == BenchmarkCriterion.P_UNIQUENESS ? 10 : 1;
        for (int j=0; j<repetitions; j++) {
            BenchmarkRun run = doPerformRun(algorithm, dataset, measure, criterion, timeLimit, suppressionLimit);
            if (result == null) {
                result = run;
            } else if (result.informationLoss == -1 && run.informationLoss != -1) {
                result = run;
            } else if (result.informationLoss > run.informationLoss) {
                result = run;
            }
        }
        return result;
    }
    
    /**
     * Internal method
     * @param algorithm
     * @param dataset
     * @param measure
     * @param criterion
     * @param timeLimit
     * @param suppressionLimit
     * @return
     * @throws IOException
     */
    private static BenchmarkRun doPerformRun(BenchmarkAlgorithm algorithm,
                                              BenchmarkDataset dataset,
                                              BenchmarkUtilityMeasure measure,
                                              BenchmarkCriterion criterion,
                                              int timeLimit,
                                              double suppressionLimit) throws IOException {

        // Create environment
        BenchmarkEnvironment environment = new BenchmarkEnvironment(algorithm, dataset, measure, criterion, suppressionLimit);

        // Create an algorithm instance
        org.deidentifier.arx.algorithm.BenchmarkAlgorithm implementation;
        switch (algorithm) {
        case DATAFLY:
        case IGREEDY:
            implementation = new AlgorithmMinimal(environment.solutions, environment.checker);
            break;
        case FLASH:
            FLASHStrategy strategy = new FLASHStrategy(environment.solutions, environment.manager.getHierarchies());
            implementation = new AlgorithmFlash(environment.solutions, environment.checker, strategy);
            break;
        case LIGHTNIG_MINIMAL:
            implementation = new AlgorithmLightning(environment.solutions, environment.checker, 0);
            break;
        case LIGHTNING:
            implementation = new AlgorithmLightning(environment.solutions, environment.checker, timeLimit);
            break;
        default:
            throw new RuntimeException("Invalid algorithm");
        }

        // Execute
        long time = System.currentTimeMillis();
        implementation.traverse();
        time = System.currentTimeMillis() - time;
        double discovery = implementation.getDiscoveryTime();
        DoubleArrayList trackRecord = implementation.getTrackRecord();

        // Return if possible
        if ((algorithm != BenchmarkAlgorithm.DATAFLY && algorithm != BenchmarkAlgorithm.IGREEDY) || implementation.getGlobalOptimum() == null) {
            double iloss = -1d;
            if (implementation.getGlobalOptimum() != null) {
                iloss = Double.valueOf( implementation.getGlobalOptimum().getInformationLoss().toString());
            }
            return new BenchmarkRun(time, iloss, discovery, trackRecord); 
        }

        // Else repeat process to convert information loss
        int[] optimum = implementation.getGlobalOptimum().getGeneralization();
        environment = new BenchmarkEnvironment(BenchmarkAlgorithm.FLASH, dataset, measure, criterion, suppressionLimit);
        double iloss = Double.valueOf(environment.checker.check(environment.solutions.getTransformation(optimum)).informationLoss.toString());
        return new BenchmarkRun(time, iloss, discovery, trackRecord);
    }

    /** Variable*/
    public final SolutionSpace solutions;

    /** Variable*/
    public final NodeChecker checker;

    /** Variable*/
    public final DataManager manager;

    /**
     * Creates a new instance
     * @param algorithm
     * @param dataset
     * @param measure
     * @param criterion
     * @param suppressionLimit
     * @throws IOException
     */
    public BenchmarkEnvironment(BenchmarkAlgorithm algorithm,
                                BenchmarkDataset dataset,
                                BenchmarkUtilityMeasure measure,
                                BenchmarkCriterion criterion,
                                double suppressionLimit) throws IOException {
        
        // Prepare
        Data data = BenchmarkSetup.getData(dataset, criterion);
        ARXConfiguration config = BenchmarkSetup.getConfiguration(dataset,
                                                                  measure,
                                                                  algorithm,
                                                                  criterion);
        config.setMaxOutliers(suppressionLimit);

        // Initialize
        DataHandle handle = data.getHandle();
        handle.getDefinition().materializeHierarchies(handle);
        handle.getRegistry().reset();
        handle.getRegistry().createInputSubset(config);
        DataDefinition definition = handle.getDefinition();

        // Encode
        String[] header = ((DataHandleInput) handle).header;
        int[][] dataArray = ((DataHandleInput) handle).data;
        Dictionary dictionary = ((DataHandleInput) handle).dictionary;
        manager = new DataManager(header,
                                              dataArray,
                                              dictionary,
                                              definition,
                                              config.getCriteria(),
                                              new HashMap<String, DistributionAggregateFunction>());

        // Attach arrays to data handle
        ((DataHandleInput) handle).update(manager.getDataGeneralized().getArray(),
                                          manager.getDataAnalyzed().getArray(),
                                          manager.getDataStatic().getArray());

        // Initialize
        config.initialize(manager);

        // Build or clean the lattice
        solutions = new SolutionSpace(manager.getHierarchiesMinLevels(),
                                                        manager.getHierarchiesMaxLevels());

        // Build a node checker
        checker = new NodeChecker(manager,
                                              config.getMetric(),
                                              config.getInternalConfiguration(),
                                              CONST_HISTORY_SIZE,
                                              CONST_SNAPSHOT_SIZE_1,
                                              CONST_SNAPSHOT_SIZE_2,
                                              solutions);

        // Initialize the metric
        config.getMetric().initialize(definition,
                                      manager.getDataGeneralized(),
                                      manager.getHierarchies(),
                                      config);
    }
}
