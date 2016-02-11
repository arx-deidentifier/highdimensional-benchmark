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

package org.deidentifier.arx.algorithm;

import java.util.Comparator;
import java.util.PriorityQueue;

import org.deidentifier.arx.framework.check.NodeChecker;
import org.deidentifier.arx.framework.check.history.History.StorageStrategy;
import org.deidentifier.arx.framework.lattice.SolutionSpace;
import org.deidentifier.arx.framework.lattice.Transformation;

import cern.colt.list.LongArrayList;
import de.linearbits.jhpl.PredictiveProperty;


/**
 * This class implements the Lightning algorithm. 
 * Use a time limit of zero to search for a minimally anonymous solution.
 * 
 * @author Fabian Prasser
 * @author Raffael Bild
 * @author Johanna Eicher
 * @author Helmut Spengler
 */
public class AlgorithmLightning extends AlgorithmBenchmark{

    /** Property */
    private final PredictiveProperty propertyChecked;
    /** Property */
    private final PredictiveProperty propertyExpanded;
    /** The number indicating how often a depth-first-search will be performed */
    private final int                stepping;
    /** Time limit */
    private final int                timeLimit;
    /** The start time */
    private long                     timeStart;

    /**
    * Constructor
    * @param space
    * @param checker
    * @param timeLimit Set it to zero to search for a minimally anonymous solution
    */
    public AlgorithmLightning(SolutionSpace space, NodeChecker checker, int timeLimit) {
        super(space, checker);
        this.checker.getHistory().setStorageStrategy(StorageStrategy.ALL);
        int stepping = space.getTop().getLevel();
        this.stepping = stepping > 0 ? stepping : 1;
        this.propertyChecked = space.getPropertyChecked();
        this.propertyExpanded = space.getPropertyExpanded();
        this.solutionSpace.setAnonymityPropertyPredictable(false);
        this.timeLimit = timeLimit;
    }
    
    /**
    * Makes sure that the given Transformation has been checked
    * @param transformation
    */
    private void assureChecked(final Transformation transformation) {
        if (!transformation.hasProperty(propertyChecked)) {
            transformation.setChecked(checker.check(transformation, true));
            trackOptimum(transformation);
            progress((double)(System.currentTimeMillis() - timeStart) / (double)timeLimit);
        }
    }

    /**
    * Performs a depth first search (without backtracking) starting from the the given transformation
    * @param queue
    * @param transformation
    */
    private void dfs(PriorityQueue<Long> queue, Transformation transformation) {
        if ((timeLimit != 0 && getTime() > timeLimit) || timeLimit == 0 && getGlobalOptimum() != null) {
            return;
        }
        Transformation next = expand(queue, transformation);
        if (next != null) {
            queue.remove(next);
            dfs(queue, next);
        }
    }
    
    /**
    * Returns the successor with minimal information loss, if any, null otherwise.
    * @param queue
    * @param transformation
    * @return
    */
    private Transformation expand(PriorityQueue<Long> queue, Transformation transformation) {
        Transformation result = null;
        
        LongArrayList list = solutionSpace.getSuccessors(transformation.getIdentifier());
        for (int i=0; i<list.size(); i++) {
            long id = list.getQuick(i);
            Transformation successor = solutionSpace.getTransformation(id);
            if (!successor.hasProperty(propertyExpanded)) {
                assureChecked(successor);
                queue.add(successor.getIdentifier());
                if (result == null || successor.getInformationLoss().compareTo(result.getInformationLoss()) < 0) {
                    result = successor;
                }
            }
            if ((timeLimit != 0 && getTime() > timeLimit) || timeLimit == 0 && getGlobalOptimum() != null) {
                return null;
            }
        }
        transformation.setProperty(propertyExpanded);
        return result;
    }

    /**
     * Returns the current execution time
     * @return
     */
    private int getTime() {
        return (int)(System.currentTimeMillis() - timeStart);
    }
    /**
    * Returns whether we can prune this Transformation
    * @param transformation
    * @return
    */
    private boolean prune(Transformation transformation) {
        // A Transformation (and it's direct and indirect successors, respectively) can be pruned if
        // the information loss is monotonic and the nodes's IL is greater or equal than the IL of the
        // global maximum (regardless of the anonymity criterion's monotonicity)
        boolean metricMonotonic = checker.getMetric().isMonotonic() || checker.getConfiguration().getAbsoluteMaxOutliers() == 0;
        // Depending on monotony of metric we choose to compare either IL or monotonic subset with the global optimum
        boolean prune = false;
        if (getGlobalOptimum() != null) {
            if (metricMonotonic) prune = transformation.getLowerBound().compareTo(getGlobalOptimum().getInformationLoss()) >= 0;
        }
        return (prune || transformation.hasProperty(propertyExpanded));
    }
    
    @Override
    protected void search() {
        timeStart = System.currentTimeMillis();
        PriorityQueue<Long> queue = new PriorityQueue<Long>(stepping, new Comparator<Long>() {
            @Override
            public int compare(Long arg0, Long arg1) {
                return solutionSpace.getUtility(arg0).compareTo(solutionSpace.getUtility(arg1));
            }
        });
        Transformation bottom = solutionSpace.getBottom();
        assureChecked(bottom);
        queue.add(bottom.getIdentifier());
        Transformation next;
        int step = 0;
        Long nextId;
        while ((nextId = queue.poll()) != null) {
            next = solutionSpace.getTransformation(nextId);
            if (!prune(next)) {
                step++;
                if (step % stepping == 0) {
                    dfs(queue, next);
                } else {
                    expand(queue, next);
                }
                if ((timeLimit != 0 && getTime() > timeLimit) || timeLimit == 0 && getGlobalOptimum() != null) {
                    return;
                }
            }
        }
    }
}