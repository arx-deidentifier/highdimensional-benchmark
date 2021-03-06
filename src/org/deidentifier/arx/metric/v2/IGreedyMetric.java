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

package org.deidentifier.arx.metric.v2;

import org.deidentifier.arx.ARXConfiguration;
import org.deidentifier.arx.DataDefinition;
import org.deidentifier.arx.framework.check.groupify.HashGroupify;
import org.deidentifier.arx.framework.check.groupify.HashGroupifyEntry;
import org.deidentifier.arx.framework.data.Data;
import org.deidentifier.arx.framework.data.GeneralizationHierarchy;
import org.deidentifier.arx.framework.lattice.Transformation;
import org.deidentifier.arx.metric.InformationLoss;
import org.deidentifier.arx.metric.InformationLossWithBound;
import org.deidentifier.arx.metric.Metric;

/**
 * Metric for imporved greedy heuristic
 *
 * @author Fabian Prasser
 */
public class IGreedyMetric extends Metric<IGreedyInformationLoss> {
   
    /** SVUID */
    private static final long         serialVersionUID = -2464970491333541188L;

    /** Hierarchies*/
    private GeneralizationHierarchy[] hierarchies;

    /**
     * Creates a new instance.
     *
     * @param monotonic
     * @param independent
     */
    public IGreedyMetric() {
        super(false, false);
    }

    @Override
    public InformationLoss<?> createMaxInformationLoss() {
        throw new UnsupportedOperationException();
    }

    @Override
    public InformationLoss<?> createMinInformationLoss() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return "IGreedy";
    }

    @Override
    protected InformationLossWithBound<IGreedyInformationLoss> getInformationLossInternal(Transformation node, HashGroupify g) {

        // Determine minimal equivalence class size (worst case per generalization)
        int greedy = Integer.MAX_VALUE;
        HashGroupifyEntry m = g.getFirstEquivalenceClass();
        while (m != null) {
            greedy = Math.min(greedy, m.count);
            m = m.nextOrdered;
        }
        // Greater sizes are better when comparing with other generalizations
        greedy = -greedy;

        // Determine maximal number of distinct values (DataFly strategy)
        int datafly = 0;
        int[] transformation = node.getGeneralization();
        for (int i = 0; i < transformation.length; i++) {
            datafly = Math.max(datafly, hierarchies[i].getDistinctValues(transformation[i]).length);
        }
        datafly = -datafly;

        double[] array = new double[] { greedy, datafly };
        return new InformationLossWithBound<IGreedyInformationLoss>(new IGreedyInformationLoss(array), new IGreedyInformationLoss(array));
    }

    @Override
    protected InformationLossWithBound<IGreedyInformationLoss> getInformationLossInternal(Transformation node, HashGroupifyEntry entry) {
        return new InformationLossWithBound<IGreedyInformationLoss>(
                new IGreedyInformationLoss(new double[]{entry.count, entry.count}), new IGreedyInformationLoss(new double[]{entry.count, entry.count}));
    }

    @Override
    protected IGreedyInformationLoss getLowerBoundInternal(Transformation node) {
        return null;
    }

    @Override
    protected IGreedyInformationLoss getLowerBoundInternal(Transformation node, HashGroupify groupify) {
        return getInformationLossInternal(node, groupify).getLowerBound();
    }

    @Override
    protected void initializeInternal(final DataDefinition definition,
                                      final Data input,
                                      final GeneralizationHierarchy[] hierarchies,
                                      final ARXConfiguration config) {
        this.hierarchies = hierarchies;
    }
}