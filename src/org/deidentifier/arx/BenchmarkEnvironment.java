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
import org.deidentifier.arx.framework.check.NodeChecker;
import org.deidentifier.arx.framework.check.distribution.DistributionAggregateFunction;
import org.deidentifier.arx.framework.data.DataManager;
import org.deidentifier.arx.framework.data.Dictionary;
import org.deidentifier.arx.framework.lattice.SolutionSpace;

/**
 * Creates a benchmarking environment consisting of a solution space, node checked, data manager etc. Furthermore,
 * initializes all configuration files
 * 
 * @author Fabian Prasser
 */
public class BenchmarkEnvironment {

    /** History size. */
    private static int    CONST_HISTORY_SIZE    = 200;

    /** Snapshot size. */
    private static double CONST_SNAPSHOT_SIZE_1 = 0.2d;

    /** Snapshot size snapshot. */
    private static double CONST_SNAPSHOT_SIZE_2 = 0.8d;

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