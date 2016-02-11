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

package org.deidentifier.arx;

import java.io.IOException;
import java.util.HashMap;

import org.deidentifier.arx.BenchmarkSetup.BenchmarkAlgorithm;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkPrivacyModel;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkDataset;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkQualityMeasure;
import org.deidentifier.arx.algorithm.AlgorithmBenchmark;
import org.deidentifier.arx.algorithm.AlgorithmFlash;
import org.deidentifier.arx.algorithm.AlgorithmLightning;
import org.deidentifier.arx.algorithm.AlgorithmMinimal;
import org.deidentifier.arx.algorithm.FLASHStrategy;
import org.deidentifier.arx.framework.check.NodeChecker;
import org.deidentifier.arx.framework.check.NodeChecker.Result;
import org.deidentifier.arx.framework.check.distribution.DistributionAggregateFunction;
import org.deidentifier.arx.framework.data.DataManager;
import org.deidentifier.arx.framework.data.Dictionary;
import org.deidentifier.arx.framework.lattice.SolutionSpace;
import org.deidentifier.arx.metric.v2.ILMultiDimensionalGeometricMean;

import cern.colt.list.DoubleArrayList;

/**
 * Creates a benchmarking environment consisting of a solution space, 
 * node checked, data manager etc. Furthermore, initializes all configuration files
 * 
 * @author Fabian Prasser
 */
public class BenchmarkEnvironment {

    /**
     * The result of one benchmark run
     * @author Fabian Prasser
     */
    public static final class BenchmarkResults {

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
        public BenchmarkResults(double executionTime, double informationLoss, double discoveryTime, DoubleArrayList trackRecord) {
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
     * Internal method
     * 
     * @param algorithm
     * @param dataset
     * @param measure
     * @param criterion
     * @param timeLimit
     * @param suppressionLimit
     * @return
     * @throws IOException
     */
    public static BenchmarkResults getBenchmarkResults(BenchmarkAlgorithm algorithm,
                                                       BenchmarkDataset dataset,
                                                       BenchmarkQualityMeasure measure,
                                                       BenchmarkPrivacyModel criterion,
                                                       int timeLimit,
                                                       double suppressionLimit) throws IOException {

        // Create environment
        BenchmarkEnvironment environment = new BenchmarkEnvironment(algorithm, dataset, measure, criterion, suppressionLimit);

        // Create an algorithm instance
        AlgorithmBenchmark implementation;
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

        // Define the resulting information loss
        double iloss = -1;
        
        // If no result was found, return immediately
        if (implementation.getGlobalOptimum() == null) {
            return new BenchmarkResults(time, iloss, discovery, trackRecord); 
        }

        // Potentially convert results
        if (algorithm == BenchmarkAlgorithm.IGREEDY || algorithm == BenchmarkAlgorithm.DATAFLY) {
            
            // If IGreedy or DataFly, compute information loss in terms of the given quality model
            int[] optimum = implementation.getGlobalOptimum().getGeneralization();
            iloss = getInformationLoss(dataset, measure, criterion, suppressionLimit, optimum);
        } else {
            
            // If lightning or flash
            iloss = Double.valueOf( implementation.getGlobalOptimum().getInformationLoss().toString());
        }
        
        // Return result
        return new BenchmarkResults(time, iloss, discovery, trackRecord);
    }

    /**
     * Returns the resulting utility value as a double
     * @param dataset
     * @param measure
     * @param criterion
     * @param suppressionLimit
     * @return
     * @throws IOException 
     */
    public static double[] getMinimalAndMaximalInformationLoss(BenchmarkDataset dataset,
                                                                BenchmarkQualityMeasure measure,
                                                                BenchmarkPrivacyModel criterion,
                                                                double suppressionLimit) throws IOException {

        // Create environment
        BenchmarkEnvironment environment = new BenchmarkEnvironment(BenchmarkAlgorithm.FLASH, dataset, measure, criterion, suppressionLimit);
        
        // For each transformation
        double min = Double.MAX_VALUE;
        double max = - Double.MAX_VALUE;
        
        for (int i = 0; i <= environment.solutions.getTop().getIdentifier(); i++) {
            Result result = environment.checker.check(environment.solutions.getTransformation(i));
            if (result.privacyModelFulfilled) {
                double value = 0d;
                if (result.informationLoss instanceof ILMultiDimensionalGeometricMean) {
                    value = Double.valueOf(((ILMultiDimensionalGeometricMean) result.informationLoss).toString());
                } else {
                    value = (Double) result.informationLoss.getValue();
                }
                min = Math.min(min, value);
                max = Math.max(max, value);
            }
        }
        return new double[]{min, max};
    }
    
    /**
     * Returns the information loss for the given transformation
     * @param dataset
     * @param measure
     * @param criterion
     * @param suppressionLimit
     * @param transformation
     * @return
     * @throws IOException 
     */
    private static double getInformationLoss(BenchmarkDataset dataset,
                                             BenchmarkQualityMeasure measure,
                                             BenchmarkPrivacyModel criterion,
                                             double suppressionLimit,
                                             int[] transformation) throws IOException {

        BenchmarkEnvironment environment = new BenchmarkEnvironment(BenchmarkAlgorithm.FLASH, dataset, measure, criterion, suppressionLimit);
        return Double.valueOf(environment.checker.check(environment.solutions.getTransformation(transformation)).informationLoss.toString());
    }

    /** Variable*/
    private final SolutionSpace solutions;

    /** Variable*/
    private final NodeChecker checker;

    /** Variable*/
    private final DataManager manager;

    /**
     * Creates a new instance
     * @param algorithm
     * @param dataset
     * @param measure
     * @param criterion
     * @param suppressionLimit
     * @throws IOException
     */
    private BenchmarkEnvironment(BenchmarkAlgorithm algorithm,
                                BenchmarkDataset dataset,
                                BenchmarkQualityMeasure measure,
                                BenchmarkPrivacyModel criterion,
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
