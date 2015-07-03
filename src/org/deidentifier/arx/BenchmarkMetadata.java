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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deidentifier.arx.benchmark.BenchmarkSetup.BenchmarkAlgorithm;
import org.deidentifier.arx.benchmark.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.benchmark.BenchmarkSetup.BenchmarkDataset;
import org.deidentifier.arx.benchmark.BenchmarkSetup.BenchmarkUtilityMeasure;
import org.deidentifier.arx.framework.check.NodeChecker.Result;
import org.deidentifier.arx.metric.v2.ILMultiDimensionalGeometricMean;

/**
 * This class computes the minimal and maximal information loss for all low-dimensional datasets
 * 
 * @author Fabian Prasser
 * @author Raffael Bild
 * @author Johanna Eicher
 * @author Helmut Spengler
 * @author Florian Kohlmayer
 */
public class BenchmarkMetadata {
    
    /**
     * An entry into the metadata table
     * 
     * @author Fabian Prasser
     */
    private static class UtilityMetadataEntry {

        /** Field */
        private final BenchmarkDataset        dataset;
        /** Field */
        private final BenchmarkUtilityMeasure measure;
        /** Field */
        private final BenchmarkCriterion      criterion;
        /** Field */
        private final double                  suppressionLimit;
        /** Field */
        public final double                   minimum;
        /** Field */
        public final double                   maximum;

        /**
         * Creates a new entry
         * @param dataset
         * @param measure
         * @param criterion
         * @param suppressionLimit
         * @param minimum
         * @param maximum
         */
        private UtilityMetadataEntry(BenchmarkDataset dataset,
                                     BenchmarkUtilityMeasure measure,
                                     BenchmarkCriterion criterion,
                                     double suppressionLimit,
                                     double minimum,
                                     double maximum) {
            this.dataset = dataset;
            this.measure = measure;
            this.criterion = criterion;
            this.suppressionLimit = suppressionLimit;
            this.minimum = minimum;
            this.maximum = maximum;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            UtilityMetadataEntry other = (UtilityMetadataEntry) obj;
            if (criterion != other.criterion) return false;
            if (dataset != other.dataset) return false;
            if (measure != other.measure) return false;
            if (Double.doubleToLongBits(suppressionLimit) != Double.doubleToLongBits(other.suppressionLimit)) return false;
            return true;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((criterion == null) ? 0 : criterion.hashCode());
            result = prime * result + ((dataset == null) ? 0 : dataset.hashCode());
            result = prime * result + ((measure == null) ? 0 : measure.hashCode());
            long temp;
            temp = Double.doubleToLongBits(suppressionLimit);
            result = prime * result + (int) (temp ^ (temp >>> 32));
            return result;
        }
    }
    
    /** The backing map*/
    private static final Map<UtilityMetadataEntry, UtilityMetadataEntry> utility = new HashMap<UtilityMetadataEntry, UtilityMetadataEntry>();
    
    // Init
    static {
        
        // AECS
        List<UtilityMetadataEntry> list = new ArrayList<UtilityMetadataEntry>();
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.K_ANONYMITY, 0.0, 942.5625, 30162.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.K_ANONYMITY, 0.0, 3172.05, 63441.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.K_ANONYMITY, 0.0, 266.3245382585752, 100937.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.K_ANONYMITY, 0.0, 621.2592165898618, 539253.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.K_ANONYMITY, 0.0, 133.4418604651163, 1193504.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.L_DIVERSITY, 0.0, 754.05, 30162.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.L_DIVERSITY, 0.0, 906.3, 63441.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.L_DIVERSITY, 0.0, 305.869696969697, 100937.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.L_DIVERSITY, 0.0, 7095.434210526316, 539253.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.L_DIVERSITY, 0.0, 318.77777777777777, 1193504.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.T_CLOSENESS, 0.0, 6032.4, 30162.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.T_CLOSENESS, 0.0, 6344.1, 63441.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.T_CLOSENESS, 0.0, 5046.85, 100937.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.T_CLOSENESS, 0.0, 44937.75, 539253.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.T_CLOSENESS, 0.0, 2869.0, 1193504.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.D_PRESENCE, 0.0, 150.8, 3016.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.D_PRESENCE, 0.0, 906.2857142857143, 6344.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.D_PRESENCE, 0.0, 152.92424242424244, 10093.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.D_PRESENCE, 0.0, 162.42469879518072, 53925.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.D_PRESENCE, 0.0, 86.73691860465117, 119350.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.P_UNIQUENESS, 0.0, 2.571136305515301, 30162.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.P_UNIQUENESS, 0.0, 1.0042264222623232, 63441.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.P_UNIQUENESS, 0.0, 3.1120737497687614, 100937.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.P_UNIQUENESS, 0.0, 11.034438305709024, 539253.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.P_UNIQUENESS, 0.0, 2.9739015164728926, 1193504.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.K_ANONYMITY, 0.1, 26.597883597883598, 30162.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.K_ANONYMITY, 0.1, 16.57721452835119, 63441.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.K_ANONYMITY, 0.1, 25.65108005082592, 100937.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.K_ANONYMITY, 0.1, 68.09609799217073, 539253.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.K_ANONYMITY, 0.1, 15.813236170917522, 1193504.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.L_DIVERSITY, 0.1, 24.09105431309904, 30162.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.L_DIVERSITY, 0.1, 10.173348300192432, 63441.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.L_DIVERSITY, 0.1, 15.37970440347402, 100937.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.L_DIVERSITY, 0.1, 167.7303265940902, 539253.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.L_DIVERSITY, 0.1, 29.493994958730788, 1193504.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.T_CLOSENESS, 0.1, 569.0943396226415, 30162.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.T_CLOSENESS, 0.1, 104.17241379310344, 63441.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.T_CLOSENESS, 0.1, 176.15532286212914, 100937.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.T_CLOSENESS, 0.1, 3434.732484076433, 539253.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.T_CLOSENESS, 0.1, 1417.4631828978622, 1193504.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.D_PRESENCE, 0.1, 97.29032258064517, 3016.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.D_PRESENCE, 0.1, 333.89473684210526, 6344.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.D_PRESENCE, 0.1, 63.879746835443036, 10093.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.D_PRESENCE, 0.1, 82.07762557077625, 53925.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.D_PRESENCE, 0.1, 27.411575562700964, 119350.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.P_UNIQUENESS, 0.1, 2.571136305515301, 30162.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.P_UNIQUENESS, 0.1, 1.0042264222623232, 63441.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.P_UNIQUENESS, 0.1, 3.2135307226997774, 100937.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.P_UNIQUENESS, 0.1, 11.034438305709024, 539253.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.P_UNIQUENESS, 0.1, 2.9739015164728926, 1193504.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.K_ANONYMITY, 1.0, 21.37632884479093, 30162.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.K_ANONYMITY, 1.0, 13.255536982866694, 63441.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.K_ANONYMITY, 1.0, 23.975534441805227, 100937.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.K_ANONYMITY, 1.0, 68.09609799217073, 539253.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.K_ANONYMITY, 1.0, 13.677091092444678, 1193504.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.L_DIVERSITY, 1.0, 17.63859649122807, 30162.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.L_DIVERSITY, 1.0, 7.364016250725479, 63441.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.L_DIVERSITY, 1.0, 15.335308416894561, 100937.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.L_DIVERSITY, 1.0, 63.95315464895636, 539253.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.L_DIVERSITY, 1.0, 14.05859002296955, 1193504.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.T_CLOSENESS, 1.0, 192.11464968152868, 30162.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.T_CLOSENESS, 1.0, 76.34296028880867, 63441.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.T_CLOSENESS, 1.0, 135.30428954423593, 100937.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.T_CLOSENESS, 1.0, 2304.5, 539253.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.T_CLOSENESS, 1.0, 1417.4631828978622, 1193504.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.D_PRESENCE, 1.0, 97.29032258064517, 3016.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.D_PRESENCE, 1.0, 333.89473684210526, 6344.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.D_PRESENCE, 1.0, 63.879746835443036, 10093.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.D_PRESENCE, 1.0, 82.07762557077625, 53925.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.D_PRESENCE, 1.0, 27.411575562700964, 119350.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.P_UNIQUENESS, 1.0, 2.571136305515301, 30162.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.P_UNIQUENESS, 1.0, 1.0042264222623232, 63441.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.P_UNIQUENESS, 1.0, 3.1120737497687614, 100937.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.P_UNIQUENESS, 1.0, 11.034438305709024, 539253.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.P_UNIQUENESS, 1.0, 2.9739015164728926, 1193504.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.K_ANONYMITY, 0.0, 0.542210825407941, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.K_ANONYMITY, 0.0, 0.7480258231683719, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.K_ANONYMITY, 0.0, 0.640670712015275, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.K_ANONYMITY, 0.0, 0.36971453484577976, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.K_ANONYMITY, 0.0, 0.30259109865708345, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.L_DIVERSITY, 0.0, 0.542210825407941, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.L_DIVERSITY, 0.0, 0.6631604870499719, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.L_DIVERSITY, 0.0, 0.640670712015275, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.L_DIVERSITY, 0.0, 0.45561039917016566, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.L_DIVERSITY, 0.0, 0.31102151375348486, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.T_CLOSENESS, 0.0, 0.749497378559133, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.T_CLOSENESS, 0.0, 0.8114473285278123, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.T_CLOSENESS, 0.0, 0.8217263638509449, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.T_CLOSENESS, 0.0, 0.6817928305074292, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.T_CLOSENESS, 0.0, 0.41421356237309515, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.D_PRESENCE, 0.0, 0.6041403987002945, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.D_PRESENCE, 0.0, 0.8114473285278123, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.D_PRESENCE, 0.0, 0.7289394816203074, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.D_PRESENCE, 0.0, 0.4409948631213094, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.D_PRESENCE, 0.0, 0.31683595541068077, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.P_UNIQUENESS, 0.0, 0.0049634194198648895, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.P_UNIQUENESS, 0.0, 0.00710363579774187, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.P_UNIQUENESS, 0.0, 0.02539784838239978, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.P_UNIQUENESS, 0.0, 0.0, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.P_UNIQUENESS, 0.0, 0.0, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.K_ANONYMITY, 0.1, 0.16273358184004305, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.K_ANONYMITY, 0.1, 0.1287966152224742, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.K_ANONYMITY, 0.1, 0.10147063173897553, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.K_ANONYMITY, 0.1, 0.04559864251535917, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.K_ANONYMITY, 0.1, 0.05757878033850372, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.L_DIVERSITY, 0.1, 0.16873621678753303, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.L_DIVERSITY, 0.1, 0.11574270797256259, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.L_DIVERSITY, 0.1, 0.09987780486248954, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.L_DIVERSITY, 0.1, 0.15235467818065307, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.L_DIVERSITY, 0.1, 0.054058173821677435, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.T_CLOSENESS, 0.1, 0.5603677705988503, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.T_CLOSENESS, 0.1, 0.215580067870204, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.T_CLOSENESS, 0.1, 0.30258618481494604, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.T_CLOSENESS, 0.1, 0.48254652304742196, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.T_CLOSENESS, 0.1, 0.32682762642510355, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.D_PRESENCE, 0.1, 0.5450826720309907, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.D_PRESENCE, 0.1, 0.736121634703844, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.D_PRESENCE, 0.1, 0.6824600738509348, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.D_PRESENCE, 0.1, 0.4229456039455204, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.D_PRESENCE, 0.1, 0.3101938163248257, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.P_UNIQUENESS, 0.1, 0.0049634194198648895, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.P_UNIQUENESS, 0.1, 0.00710363579774187, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.P_UNIQUENESS, 0.1, 0.018461399150697666, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.P_UNIQUENESS, 0.1, 0.0, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.P_UNIQUENESS, 0.1, 0.0, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.K_ANONYMITY, 1.0, 0.16273358184004305, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.K_ANONYMITY, 1.0, 0.1287966152224742, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.K_ANONYMITY, 1.0, 0.10147063173897553, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.K_ANONYMITY, 1.0, 0.04559864251535917, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.K_ANONYMITY, 1.0, 0.05757878033850372, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.L_DIVERSITY, 1.0, 0.16873621678753303, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.L_DIVERSITY, 1.0, 0.11574270797256259, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.L_DIVERSITY, 1.0, 0.09987780486248954, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.L_DIVERSITY, 1.0, 0.15235467818065307, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.L_DIVERSITY, 1.0, 0.054058173821677435, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.T_CLOSENESS, 1.0, 0.5049846167763927, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.T_CLOSENESS, 1.0, 0.215580067870204, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.T_CLOSENESS, 1.0, 0.24818069546787958, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.T_CLOSENESS, 1.0, 0.4074998927362343, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.T_CLOSENESS, 1.0, 0.31929315436224326, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.D_PRESENCE, 1.0, 0.5450826720309907, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.D_PRESENCE, 1.0, 0.736121634703844, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.D_PRESENCE, 1.0, 0.6824600738509348, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.D_PRESENCE, 1.0, 0.4229456039455204, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.D_PRESENCE, 1.0, 0.3101938163248257, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.P_UNIQUENESS, 1.0, 0.0049634194198648895, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.P_UNIQUENESS, 1.0, 0.00710363579774187, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.P_UNIQUENESS, 1.0, 0.018461399150697666, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.P_UNIQUENESS, 1.0, 0.0, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkUtilityMeasure.LOSS, BenchmarkCriterion.P_UNIQUENESS, 1.0, 0.0, 1.0000000000000004));
        
        // Add to map
        for (UtilityMetadataEntry entry : list) {
            utility.put(entry, entry);
        }
        
    }

    /**
     * Returns the resulting utility value as a double
     * @param dataset
     * @param measure
     * @param criterion
     * @param suppressionLimit
     * @return
     */
    public static double[] getMinimalAndMaximalInformationLoss(BenchmarkDataset dataset,
                                                                BenchmarkUtilityMeasure measure,
                                                                BenchmarkCriterion criterion,
                                                                double suppressionLimit) throws IOException {
        
        UtilityMetadataEntry entry = utility.get(new UtilityMetadataEntry(dataset, measure, criterion, suppressionLimit, 0, 0));
        return new double[]{entry.minimum, entry.maximum};
    }
    
    /**
     * Main entry point
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        
        // For each relevant combination
        for (BenchmarkUtilityMeasure measure : getUtilityMeasures()) {
            for (double suppressionLimit : getSuppressionLimits()) {
                for (BenchmarkCriterion criterion : getCriteria()) {
                    for (BenchmarkDataset dataset : getDatasets()) {
                        computeMinimalAndMaximalInformationLoss(dataset, measure, criterion, suppressionLimit);
                    }
                }
            }
        }
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
    private static void computeMinimalAndMaximalInformationLoss(BenchmarkDataset dataset,
                                                                BenchmarkUtilityMeasure measure,
                                                                BenchmarkCriterion criterion,
                                                                double suppressionLimit) throws IOException {

        // Create environment
        BenchmarkEnvironment environment = new BenchmarkEnvironment(BenchmarkAlgorithm.FLASH, dataset, measure, criterion, suppressionLimit);
        
        // For each transformation
        double min = Double.MAX_VALUE;
        double max = - Double.MAX_VALUE;
        
        // Repeat 10 times for p_uniqueness
        int repetitions = criterion == BenchmarkCriterion.P_UNIQUENESS ? 10 : 1;
        for (int j=0; j<repetitions; j++) {
            for (int i = 0; i <= environment.solutions.getTop().getIdentifier(); i++) {
                Result result = environment.checker.check(environment.solutions.getTransformation(i));
                if (result.privacyModelFulfilled) {
                    double value = 0d;
                    if (result.informationLoss instanceof ILMultiDimensionalGeometricMean) {
                        value = Double.valueOf(((ILMultiDimensionalGeometricMean)result.informationLoss).toString());
                    } else {
                        value = (Double)result.informationLoss.getValue();
                    }
                    min = Math.min(min, value);
                    max = Math.max(max, value);
                }
            }
        }

        // Print
        System.out.print("list.add(new UtilityMetadataEntry(");
        System.out.print("BenchmarkDataset." + dataset.name() + ", ");
        System.out.print("BenchmarkUtilityMeasure." + measure.name() + ", ");
        System.out.print("BenchmarkCriterion." + criterion.name() + ", ");
        System.out.print(suppressionLimit + ", ");
        System.out.print(min + ", ");
        System.out.print(max + "));\n");
    }

    /**
     * Returns all criteria
     * @return
     */
    private static BenchmarkCriterion[] getCriteria() {
        return new BenchmarkCriterion[]{
            BenchmarkCriterion.K_ANONYMITY,
            BenchmarkCriterion.L_DIVERSITY,
            BenchmarkCriterion.T_CLOSENESS,
            BenchmarkCriterion.D_PRESENCE,
            BenchmarkCriterion.P_UNIQUENESS
        };
    }

    /**
     * Returns all datasets
     * @return
     */
    private static BenchmarkDataset[] getDatasets() {
        return new BenchmarkDataset[] { 
                BenchmarkDataset.ADULT,
                BenchmarkDataset.CUP,
                BenchmarkDataset.FARS,
                BenchmarkDataset.ATUS,
                BenchmarkDataset.IHIS
        };
    }

    /**
     * Returns all suppression limits
     * @return
     */
    private static double[] getSuppressionLimits() {
        return new double[]{0d, 0.1d, 1d};
    }

    /**
     * Returns all utility measures
     * @return
     */
    private static BenchmarkUtilityMeasure[] getUtilityMeasures() {
        return new BenchmarkUtilityMeasure[] { 
                BenchmarkUtilityMeasure.AECS,
                BenchmarkUtilityMeasure.LOSS
        };
    }
}
