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

import java.util.Iterator;

import org.deidentifier.arx.framework.check.NodeChecker;
import org.deidentifier.arx.framework.check.history.History.StorageStrategy;
import org.deidentifier.arx.framework.lattice.SolutionSpace;
import org.deidentifier.arx.framework.lattice.Transformation;

import com.carrotsearch.hppc.LongArrayList;

import de.linearbits.jhpl.PredictiveProperty;

/**
 * 
 * @author Fabian Prasser
 * @author Raffael Bild
 * @author Johanna Eicher
 * @author Helmut Spengler
 */
public class AlgorithmMinimal extends AbstractAlgorithm{
   
    /** Property */
    private final PredictiveProperty propertyChecked;
    
    /**
    * Constructor
    * @param space
    * @param checker
    */
    public AlgorithmMinimal(SolutionSpace space, NodeChecker checker) {
        super(space, checker);
        this.propertyChecked = space.getPropertyChecked();
        this.checker.getHistory().setStorageStrategy(StorageStrategy.ALL);
        this.solutionSpace.setAnonymityPropertyPredictable(false);
    }
    
    /**
    * Makes sure that the given Transformation has been checked
    * @param transformation
    */
    private void assureChecked(final Transformation transformation) {
        if (!transformation.hasProperty(propertyChecked)) {
            transformation.setChecked(checker.check(transformation, true));
            trackOptimum(transformation);
        }
    }

    @Override
    public void traverse() {
        Transformation bottom = solutionSpace.getBottom();
        dfs(bottom);
    }
    
    /**
    * Performs a depth first search without backtracking
    * @param transformation
    */
    private void dfs(Transformation transformation) {
        assureChecked(transformation);
        if (getGlobalOptimum() != null) {
            return;
        }
        Transformation next = expand(transformation);
        if (getGlobalOptimum() != null) {
            return;
        }
        if (next != null) {
            dfs(next);
        }
    }
    /**
    * Returns the successor with minimal information loss, if any, null otherwise.
    * @param transformation
    * @return
    */
    private Transformation expand(Transformation transformation) {
        Transformation result = null;
        
        LongArrayList list = new LongArrayList();
        for (Iterator<Long> iter = solutionSpace.getSuccessors(transformation.getIdentifier()); iter.hasNext();) {
            list.add(iter.next());
        }
        for (long id : list.toArray()) {
            Transformation successor = solutionSpace.getTransformation(id);
            if (!successor.hasProperty(propertyChecked)) {
                assureChecked(successor);
                if (result == null || successor.getInformationLoss().compareTo(result.getInformationLoss()) < 0) {
                    result = successor;
                }
            }
            if (getGlobalOptimum() != null) {
                return result;
            }
        }
        return result;
    }
}
