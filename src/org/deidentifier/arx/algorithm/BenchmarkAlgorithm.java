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

import org.deidentifier.arx.framework.check.NodeChecker;
import org.deidentifier.arx.framework.lattice.SolutionSpace;
import org.deidentifier.arx.framework.lattice.Transformation;

import cern.colt.list.DoubleArrayList;

/**
 * Abstract base class
 * 
 * @author Fabian Prasser
 */
public abstract class BenchmarkAlgorithm extends AbstractAlgorithm {

    /** Start time of the search process */
    private long            time        = 0;

    /** Time at which the optimum was discovered */
    private int             discovery   = 0;

    /** The track record */
    private DoubleArrayList trackRecord = new DoubleArrayList();

    /**
     * Creates a new instance
     * @param arg0
     * @param arg1
     */
    protected BenchmarkAlgorithm(SolutionSpace arg0, NodeChecker arg1) {
        super(arg0, arg1);
    }

    /**
     * Returns the time at which the optimum was discovered
     * @return
     */
    public int getDiscoveryTime() {
        return discovery;
    }

    /**
     * Returns a track record of the previous run. List containing tuples <time, utility>.
     * @return
     */
    public DoubleArrayList getTrackRecord() {
        return this.trackRecord;
    }

    @Override
    public void traverse() {
        this.time = System.currentTimeMillis();
        this.search();
    }
    
    /**
     * Search method
     */
    protected abstract void search();
    
    @Override
    protected void trackOptimum(Transformation arg0) {

        long previousId = getGlobalOptimum() == null ? -1 : getGlobalOptimum().getIdentifier();
        super.trackOptimum(arg0);
        long newId = getGlobalOptimum() == null ? -1 : getGlobalOptimum().getIdentifier();
        if ((this instanceof AlgorithmLightning) && previousId != newId) {
            double utility = Double.valueOf(getGlobalOptimum().getInformationLoss().toString());
            this.discovery = (int) (System.currentTimeMillis() - time);
            this.trackRecord.add(this.discovery);
            this.trackRecord.add(utility);
        }
    }
}
