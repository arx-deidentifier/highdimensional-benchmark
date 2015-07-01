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

package org.deidentifier.arx.metric.v2;

import org.deidentifier.arx.ARXConfiguration;
import org.deidentifier.arx.DataDefinition;
import org.deidentifier.arx.framework.check.groupify.HashGroupify;
import org.deidentifier.arx.framework.check.groupify.HashGroupifyEntry;
import org.deidentifier.arx.framework.data.Data;
import org.deidentifier.arx.framework.data.GeneralizationHierarchy;
import org.deidentifier.arx.framework.lattice.Transformation;
import org.deidentifier.arx.metric.MetricConfiguration;

/**
 * Metric for datafly
 *
 * @author Fabian Prasser
 */
public class MetricDataFly extends AbstractMetricSingleDimensional {

    /** SVUID */
    private static final long serialVersionUID = 5349440837330821732L;

    private GeneralizationHierarchy[] hierarchies;

    /**
     * Creates a new instance.
     */
    public MetricDataFly() {
        super(false, false);
    }

    @Override
    public ILSingleDimensional createMaxInformationLoss() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ILSingleDimensional createMinInformationLoss() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the configuration of this metric.
     *
     * @return
     */
    public MetricConfiguration getConfiguration() {
        return new MetricConfiguration(false, // monotonic
                                       0.5d, // gs-factor
                                       false, // precomputed
                                       0.0d, // precomputation threshold
                                       AggregateFunction.SUM // aggregate function
        );
    }

    @Override
    public String toString() {
        return "DataFly";
    }

    @Override
    protected ILSingleDimensionalWithBound getInformationLossInternal(final Transformation node, final HashGroupify g) {

        // Determine maximal number of distinct values
        int max = 0;
        int[] transformation = node.getGeneralization();
        for (int i = 0; i < transformation.length; i++) {
            max = Math.max(max, hierarchies[i].getDistinctValues(transformation[i]).length);
        }
        return new ILSingleDimensionalWithBound(-max);
    }

    @Override
    protected ILSingleDimensionalWithBound getInformationLossInternal(Transformation node, HashGroupifyEntry entry) {
        return new ILSingleDimensionalWithBound(entry.count);
    }

    @Override
    protected ILSingleDimensional getLowerBoundInternal(Transformation node) {
        return null;
    }

    @Override
    protected ILSingleDimensional getLowerBoundInternal(Transformation node, HashGroupify groupify) {
        return getInformationLossInternal(node, groupify).getLowerBound();
    }

    @Override
    protected void initializeInternal(final DataDefinition definition,
                                      final Data input,
                                      final GeneralizationHierarchy[] hierarchies,
                                      final ARXConfiguration config) {
        super.initializeInternal(definition, input, hierarchies, config);
        this.hierarchies = hierarchies;
    }
}