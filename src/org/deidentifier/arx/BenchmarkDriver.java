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

import org.deidentifier.arx.BenchmarkSetup.BenchmarkAlgorithm;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkDataset;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkUtilityMeasure;
import org.deidentifier.arx.algorithm.AbstractAlgorithm;
import org.deidentifier.arx.algorithm.AlgorithmFlash;
import org.deidentifier.arx.algorithm.AlgorithmLightning;
import org.deidentifier.arx.algorithm.AlgorithmMinimal;
import org.deidentifier.arx.algorithm.FLASHStrategy;
import org.deidentifier.arx.framework.check.NodeChecker.Result;

/**
 * This class implements the main benchmark driver. It re-implements some methods from the
 * class <code>ARXAnonymizer</code>
 * 
 * @author Fabian Prasser
 * @author Raffael Bild
 * @author Johanna Eicher
 * @author Helmut Spengler
 * @author Florian Kohlmayer
 */
public class BenchmarkDriver {

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
    public static double[] getExecutionTimeAndInformationLoss(BenchmarkAlgorithm algorithm,
                                                              BenchmarkDataset dataset,
                                                              BenchmarkUtilityMeasure measure,
                                                              BenchmarkCriterion criterion,
                                                              int timeLimit,
                                                              double suppressionLimit) throws IOException {

        // Create environment
        BenchmarkEnvironment environment = new BenchmarkEnvironment(algorithm, dataset, measure, criterion, suppressionLimit);

        // Create an algorithm instance
        AbstractAlgorithm implementation;
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

        // Return if possible
        if (algorithm != BenchmarkAlgorithm.DATAFLY && algorithm != BenchmarkAlgorithm.IGREEDY) { 
            return new double[]{time, (Double) implementation.getGlobalOptimum().getInformationLoss().getValue()}; 
        }

        // Else repeat to convert
        int[] optimum = implementation.getGlobalOptimum().getGeneralization();
        environment = new BenchmarkEnvironment(BenchmarkAlgorithm.FLASH, dataset, measure, criterion, suppressionLimit);
        return new double[]{time, (Double) environment.checker.check(environment.solutions.getTransformation(optimum)).informationLoss.getValue()};
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
                                                               BenchmarkUtilityMeasure measure,
                                                               BenchmarkCriterion criterion,
                                                               double suppressionLimit) throws IOException {

        // Create environment
        BenchmarkEnvironment environment = new BenchmarkEnvironment(BenchmarkAlgorithm.FLASH, dataset, measure, criterion, suppressionLimit);
        
        // For each transformation
        double min = Double.MAX_VALUE;
        double max = - Double.MAX_VALUE;
        for (int i = 0; i < environment.solutions.getTop().getIdentifier(); i++) {
            Result result = environment.checker.check(environment.solutions.getTransformation(i));
            if (result.privacyModelFulfilled) {
                min = Math.min(min, (Double)result.informationLoss.getValue());
                max = Math.max(max, (Double)result.informationLoss.getValue());
            }
        }
        return new double[] { min, max };
    }
}
