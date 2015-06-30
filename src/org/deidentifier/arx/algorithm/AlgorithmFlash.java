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

package org.deidentifier.arx.algorithm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

import org.deidentifier.arx.algorithm.FLASHPhaseConfiguration.PhaseAnonymityProperty;
import org.deidentifier.arx.framework.check.NodeChecker;
import org.deidentifier.arx.framework.check.groupify.HashGroupify;
import org.deidentifier.arx.framework.lattice.DependentAction;
import org.deidentifier.arx.framework.lattice.SolutionSpace;
import org.deidentifier.arx.framework.lattice.Transformation;
import org.deidentifier.arx.metric.InformationLossWithBound;

import cern.colt.GenericSorting;
import cern.colt.Swapper;
import cern.colt.function.IntComparator;
import de.linearbits.jhpl.PredictiveProperty;

/**
 * This class implements the FLASH algorithm.
 *
 * @author Fabian Prasser
 * @author Florian Kohlmayer
 */
public class AlgorithmFlash extends AbstractAlgorithm {

    /** Configuration for the algorithm's phases. */
    protected final FLASHConfiguration config;

    /** Are the pointers for a node with id 'index' already sorted?. */
    private final int[][]              sortedSuccessors;

    /** The strategy. */
    private final FLASHStrategy        strategy;

    /** The number of checked transformations */
    private int                        checked = 0;

    /**
     * Creates a new instance.
     *
     * @param solutionSpace
     * @param checker
     * @param strategy
     */
    public AlgorithmFlash(SolutionSpace solutionSpace, NodeChecker checker, FLASHStrategy strategy) {
        super(solutionSpace, checker);
        
        if (solutionSpace.getSize() > Integer.MAX_VALUE) {
            throw new IllegalArgumentException();
        }
        this.checked = 0;
        this.config = ((FLASHAlgorithmImpl)FLASHAlgorithm.create(solutionSpace, checker, strategy)).config;
        this.solutionSpace.setAnonymityPropertyPredictable(config.isAnonymityPropertyPredicable());
        this.strategy = strategy;
        this.sortedSuccessors = new int[(int)solutionSpace.getSize()][];
    }

    @Override
    public void traverse() {

        // Determine configuration for the outer loop
        FLASHPhaseConfiguration outerLoopConfiguration;
        if (config.isBinaryPhaseRequired()) {
            outerLoopConfiguration = config.getBinaryPhaseConfiguration();
        } else {
            outerLoopConfiguration = config.getLinearPhaseConfiguration();
        }

        // Set some triggers
        checker.getHistory().setStorageStrategy(config.getSnapshotStorageStrategy());

        // Initialize
        PriorityQueue<Integer> queue = new PriorityQueue<Integer>(solutionSpace.getTop().getLevel(), strategy);
        Transformation bottom = solutionSpace.getBottom();
        Transformation top = solutionSpace.getTop();

        // Check bottom for speed and remember the result to prevent repeated checks
        NodeChecker.Result result = checker.check(bottom);
        bottom.setProperty(solutionSpace.getPropertyForceSnapshot());
        bottom.setData(result);

        // For each node in the lattice
        for (int level = bottom.getLevel(); level <= top.getLevel(); level++) {
            for (int transformation : getSortedUnprocessedNodes(level, outerLoopConfiguration.getTriggerSkip())) {

                // Run the correct phase
                if (config.isBinaryPhaseRequired()) {
                    binarySearch(transformation, queue);
                } else {
                    linearSearch(transformation);
                }
            }
        }

        // Potentially allows to better estimate utility in the lattice
        computeUtilityForMonotonicMetrics(bottom);
        computeUtilityForMonotonicMetrics(top);

        // Remove the associated result information to leave the lattice in a consistent state
        bottom.setData(null);
    }

    /**
     * Implements the FLASH algorithm (without outer loop).
     *
     * @param start
     * @param queue
     */
    private void binarySearch(int start, PriorityQueue<Integer> queue) {

        // Obtain node action
        DependentAction triggerSkip = config.getBinaryPhaseConfiguration().getTriggerSkip();

        // Add to queue
        queue.add(start);

        // While queue is not empty
        while (!queue.isEmpty()) {

            // Remove head and process
            Integer head = queue.poll();
            if (!skip(triggerSkip, solutionSpace.getTransformation(head))) {

                // First phase
                List<Integer> path = findPath(head, triggerSkip);
                head = checkPath(path, triggerSkip, queue);

                // Second phase
                if (config.isLinearPhaseRequired() && (head != -1)) {

                    // Run linear search on head
                    linearSearch(head);
                }
            }
        }
    }

    /**
     * Checks and tags the given transformation.
     *
     * @param transformation
     * @param configuration
     */
    private void checkAndTag(Transformation transformation, FLASHPhaseConfiguration configuration) {

        // Check or evaluate
        if (configuration.getTriggerEvaluate().appliesTo(transformation)) {
            InformationLossWithBound<?> loss = checker.getMetric().getInformationLoss(transformation, (HashGroupify)null);
            transformation.setInformationLoss(loss.getInformationLoss());
            transformation.setLowerBound(loss.getLowerBound());
            if (loss.getLowerBound() == null) {
                transformation.setLowerBound(checker.getMetric().getLowerBound(transformation));
            }
        } else if (configuration.getTriggerCheck().appliesTo(transformation)) {
            transformation.setChecked(checker.check(transformation));
            progress((double)++checked / (double)solutionSpace.getSize());
        }

        // Store optimum
        trackOptimum(transformation);

        // Tag
        configuration.getTriggerTag().apply(transformation);
    }

    /**
     * Checks a path binary.
     *
     * @param path The path
     * @param triggerSkip
     * @param queue
     * @return
     */
    private int checkPath(List<Integer> path, DependentAction triggerSkip, PriorityQueue<Integer> queue) {

        // Obtain anonymity property
        PredictiveProperty anonymityProperty = config.getBinaryPhaseConfiguration().getAnonymityProperty() == PhaseAnonymityProperty.ANONYMITY ?
                                               solutionSpace.getPropertyAnonymous() : solutionSpace.getPropertyKAnonymous();

        // Init
        int low = 0;
        int high = path.size() - 1;
        int lastAnonymousIdentifier = -1;

        // While not done
        while (low <= high) {

            // Init
            final int mid = (low + high) / 2;
            final int identifier = path.get(mid);
            Transformation transformation = solutionSpace.getTransformation(identifier);

            // Skip
            if (!skip(triggerSkip, transformation)) {

                // Check and tag
                checkAndTag(transformation, config.getBinaryPhaseConfiguration());

                // Add nodes to queue
                if (!transformation.hasProperty(anonymityProperty)) {
                    for (final int up : getSortedSuccessors(identifier)) {
                        if (!skip(triggerSkip, solutionSpace.getTransformation(up))) {
                            queue.add(up);
                        }
                    }
                }

                // Binary search
                if (transformation.hasProperty(anonymityProperty)) {
                    lastAnonymousIdentifier = identifier;
                    high = mid - 1;
                } else {
                    low = mid + 1;
                }
            } else {
                high = mid - 1;
            }
        }
        
        return lastAnonymousIdentifier;
    }

    /**
     * Greedily finds a path to the top node.
     *
     * @param current The node to start the path with. Will be included
     * @param triggerSkip All nodes to which this trigger applies will be skipped
     * @return The path as a list
     */
    private List<Integer> findPath(Integer current, DependentAction triggerSkip) {
        List<Integer> path = new ArrayList<Integer>();
        path.add(current);
        boolean found = true;
        while (found) {
            found = false;
            for (final int id : getSortedSuccessors(current)) {
                if (!skip(triggerSkip, solutionSpace.getTransformation(id))) {
                    current = id;
                    path.add(id);
                    found = true;
                    break;
                }
            }
        }
        return path;
    }

    /**
     * Returns all transformations that do not have the given property and sorts the resulting array
     * according to the strategy.
     *
     * @param level The level which is to be sorted
     * @param triggerSkip The trigger to be used for limiting the number of nodes to be sorted
     * @return A sorted array of nodes remaining on this level
     */
    private int[] getSortedUnprocessedNodes(int level, DependentAction triggerSkip) {

        // Create
        List<Integer> result = new ArrayList<Integer>();
        for (Iterator<Long> iter = solutionSpace.unsafeGetLevel(level); iter.hasNext();) {
            int id = iter.next().intValue();
            if (!skip(triggerSkip, solutionSpace.getTransformation(id))) {
                result.add(id);
            }
        }

        // Copy & sort
        int[] resultArray = new int[result.size()];
        for (int i=0; i<result.size(); i++) {
            resultArray[i] = result.get(i);
        }
        sort(resultArray);
        return resultArray;
    }

    /**
     * Implements a depth-first search with predictive tagging.
     *
     * @param start
     */
    private void linearSearch(int start) {

        // Obtain node action
        DependentAction triggerSkip = config.getLinearPhaseConfiguration().getTriggerSkip();

        // Skip this node
        Transformation transformation = solutionSpace.getTransformation(start);
        if (!skip(triggerSkip, transformation)) {

            // Check and tag
            checkAndTag(transformation, config.getLinearPhaseConfiguration());

            // DFS
            for (final int child : getSortedSuccessors(start)) {
                if (!skip(triggerSkip, solutionSpace.getTransformation(child))) {
                    linearSearch(child);
                }
            }
        }

        // Mark as successors pruned
        transformation.setProperty(solutionSpace.getPropertySuccessorsPruned());
    }

    /**
     * Returns whether a node should be skipped.
     *
     * @param transformation
     * @param identifier
     * @return
     */
    private boolean skip(DependentAction trigger, Transformation transformation) {

        // If the trigger applies, skip
        if (trigger.appliesTo(transformation)) {
            return true;
        }

        // We need to process this node
        return false;
    }

    /**
     * Sorts a given array of transformation identifiers.
     * 
     * @param array
     */
    private void sort(final int[] array) {
        GenericSorting.quickSort(0, array.length, new IntComparator(){
            @Override
            public int compare(int arg0, int arg1) {
                return strategy.compare(array[arg0], array[arg1]);
            }
        }, new Swapper(){
            @Override
            public void swap(int arg0, int arg1) {
                int temp = array[arg0];
                array[arg0] = array[arg1];
                array[arg1] = temp;
            }
            
        });
    }

    /**
     * Sorts pointers to successor nodes according to the strategy.
     *
     * @param transformation
     */
    private int[] getSortedSuccessors(final int transformation) {
        
        if (sortedSuccessors[transformation] == null) {
            List<Long> list = new ArrayList<Long>();
            for (Iterator<Long> iter = solutionSpace.getSuccessors(transformation); iter.hasNext();){
                list.add(iter.next());
            }
            int[] result = new int[list.size()];
            for (int i=0; i<result.length; i++) {
                result[i] = list.get(i).intValue();
            }
            sort(result);
            sortedSuccessors[transformation] = result;
        }
        return sortedSuccessors[transformation];
    }
}
