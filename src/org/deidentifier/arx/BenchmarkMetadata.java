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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deidentifier.arx.BenchmarkSetup.BenchmarkPrivacyModel;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkDataset;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkQualityMeasure;

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
        private final BenchmarkQualityMeasure measure;
        /** Field */
        private final BenchmarkPrivacyModel      criterion;
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
                                     BenchmarkQualityMeasure measure,
                                     BenchmarkPrivacyModel criterion,
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
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.K_ANONYMITY, 0.0, 942.5625, 30162.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.K_ANONYMITY, 0.0, 3172.05, 63441.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.K_ANONYMITY, 0.0, 266.3245382585752, 100937.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.K_ANONYMITY, 0.0, 621.2592165898618, 539253.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.K_ANONYMITY, 0.0, 133.4418604651163, 1193504.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.L_DIVERSITY, 0.0, 754.05, 30162.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.L_DIVERSITY, 0.0, 906.3, 63441.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.L_DIVERSITY, 0.0, 305.869696969697, 100937.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.L_DIVERSITY, 0.0, 7095.434210526316, 539253.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.L_DIVERSITY, 0.0, 318.77777777777777, 1193504.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.T_CLOSENESS, 0.0, 6032.4, 30162.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.T_CLOSENESS, 0.0, 6344.1, 63441.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.T_CLOSENESS, 0.0, 5046.85, 100937.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.T_CLOSENESS, 0.0, 44937.75, 539253.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.T_CLOSENESS, 0.0, 2869.0, 1193504.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.D_PRESENCE, 0.0, 150.8, 3016.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.D_PRESENCE, 0.0, 906.2857142857143, 6344.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.D_PRESENCE, 0.0, 152.92424242424244, 10093.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.D_PRESENCE, 0.0, 162.42469879518072, 53925.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.D_PRESENCE, 0.0, 86.73691860465117, 119350.0));

        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.P_UNIQUENESS, 0.0, 2.571136305515301, 30162.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.P_UNIQUENESS, 0.0, 1.0078798951465566, 63441.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.P_UNIQUENESS, 0.0, 3.42310170583647, 100937.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.P_UNIQUENESS, 0.0, 11.034438305709024, 539253.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.P_UNIQUENESS, 0.0, 2.9739015164728926, 1193504.0));
        
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.K_ANONYMITY, 0.1, 26.597883597883598, 30162.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.K_ANONYMITY, 0.1, 16.57721452835119, 63441.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.K_ANONYMITY, 0.1, 25.65108005082592, 100937.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.K_ANONYMITY, 0.1, 68.09609799217073, 539253.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.K_ANONYMITY, 0.1, 15.813236170917522, 1193504.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.L_DIVERSITY, 0.1, 24.09105431309904, 30162.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.L_DIVERSITY, 0.1, 10.173348300192432, 63441.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.L_DIVERSITY, 0.1, 15.37970440347402, 100937.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.L_DIVERSITY, 0.1, 167.7303265940902, 539253.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.L_DIVERSITY, 0.1, 29.493994958730788, 1193504.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.T_CLOSENESS, 0.1, 569.0943396226415, 30162.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.T_CLOSENESS, 0.1, 104.17241379310344, 63441.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.T_CLOSENESS, 0.1, 176.15532286212914, 100937.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.T_CLOSENESS, 0.1, 3434.732484076433, 539253.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.T_CLOSENESS, 0.1, 1417.4631828978622, 1193504.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.D_PRESENCE, 0.1, 97.29032258064517, 3016.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.D_PRESENCE, 0.1, 333.89473684210526, 6344.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.D_PRESENCE, 0.1, 63.879746835443036, 10093.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.D_PRESENCE, 0.1, 82.07762557077625, 53925.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.D_PRESENCE, 0.1, 27.411575562700964, 119350.0));

        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.P_UNIQUENESS, 0.1, 2.571136305515301, 30162.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.P_UNIQUENESS, 0.1, 1.0078798951465566, 63441.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.P_UNIQUENESS, 0.1, 3.42310170583647, 100937.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.P_UNIQUENESS, 0.1, 11.034438305709024, 539253.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.P_UNIQUENESS, 0.1, 2.9739015164728926, 1193504.0));
        
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.K_ANONYMITY, 1.0, 21.37632884479093, 30162.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.K_ANONYMITY, 1.0, 13.255536982866694, 63441.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.K_ANONYMITY, 1.0, 23.975534441805227, 100937.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.K_ANONYMITY, 1.0, 68.09609799217073, 539253.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.K_ANONYMITY, 1.0, 13.677091092444678, 1193504.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.L_DIVERSITY, 1.0, 17.63859649122807, 30162.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.L_DIVERSITY, 1.0, 7.364016250725479, 63441.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.L_DIVERSITY, 1.0, 15.335308416894561, 100937.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.L_DIVERSITY, 1.0, 63.95315464895636, 539253.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.L_DIVERSITY, 1.0, 14.05859002296955, 1193504.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.T_CLOSENESS, 1.0, 192.11464968152868, 30162.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.T_CLOSENESS, 1.0, 76.34296028880867, 63441.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.T_CLOSENESS, 1.0, 135.30428954423593, 100937.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.T_CLOSENESS, 1.0, 2304.5, 539253.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.T_CLOSENESS, 1.0, 1417.4631828978622, 1193504.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.D_PRESENCE, 1.0, 97.29032258064517, 3016.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.D_PRESENCE, 1.0, 333.89473684210526, 6344.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.D_PRESENCE, 1.0, 63.879746835443036, 10093.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.D_PRESENCE, 1.0, 82.07762557077625, 53925.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.D_PRESENCE, 1.0, 27.411575562700964, 119350.0));
        
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.P_UNIQUENESS, 1.0, 2.571136305515301, 30162.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.P_UNIQUENESS, 1.0, 1.0078798951465566, 63441.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.P_UNIQUENESS, 1.0, 3.42310170583647, 100937.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.P_UNIQUENESS, 1.0, 11.034438305709024, 539253.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.AECS, BenchmarkPrivacyModel.P_UNIQUENESS, 1.0, 2.9739015164728926, 1193504.0));
        
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.K_ANONYMITY, 0.0, 0.542210825407941, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.K_ANONYMITY, 0.0, 0.7480258231683719, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.K_ANONYMITY, 0.0, 0.640670712015275, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.K_ANONYMITY, 0.0, 0.36971453484577976, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.K_ANONYMITY, 0.0, 0.30259109865708345, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.L_DIVERSITY, 0.0, 0.542210825407941, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.L_DIVERSITY, 0.0, 0.6631604870499719, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.L_DIVERSITY, 0.0, 0.640670712015275, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.L_DIVERSITY, 0.0, 0.45561039917016566, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.L_DIVERSITY, 0.0, 0.31102151375348486, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.T_CLOSENESS, 0.0, 0.749497378559133, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.T_CLOSENESS, 0.0, 0.8114473285278123, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.T_CLOSENESS, 0.0, 0.8217263638509449, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.T_CLOSENESS, 0.0, 0.6817928305074292, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.T_CLOSENESS, 0.0, 0.41421356237309515, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.D_PRESENCE, 0.0, 0.6041403987002945, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.D_PRESENCE, 0.0, 0.8114473285278123, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.D_PRESENCE, 0.0, 0.7289394816203074, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.D_PRESENCE, 0.0, 0.4409948631213094, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.D_PRESENCE, 0.0, 0.31683595541068077, 1.0000000000000004));
        
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.P_UNIQUENESS, 0.0, 0.0049634194198648895, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.P_UNIQUENESS, 0.0, 0.00710363579774187, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.P_UNIQUENESS, 0.0, 0.02539784838239978, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.P_UNIQUENESS, 0.0, 0.0, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.P_UNIQUENESS, 0.0, 0.0, 1.0000000000000004));
        
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.K_ANONYMITY, 0.1, 0.16273358184004305, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.K_ANONYMITY, 0.1, 0.1287966152224742, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.K_ANONYMITY, 0.1, 0.10147063173897553, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.K_ANONYMITY, 0.1, 0.04559864251535917, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.K_ANONYMITY, 0.1, 0.05757878033850372, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.L_DIVERSITY, 0.1, 0.16873621678753303, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.L_DIVERSITY, 0.1, 0.11574270797256259, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.L_DIVERSITY, 0.1, 0.09987780486248954, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.L_DIVERSITY, 0.1, 0.15235467818065307, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.L_DIVERSITY, 0.1, 0.054058173821677435, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.T_CLOSENESS, 0.1, 0.5603677705988503, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.T_CLOSENESS, 0.1, 0.215580067870204, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.T_CLOSENESS, 0.1, 0.30258618481494604, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.T_CLOSENESS, 0.1, 0.48254652304742196, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.T_CLOSENESS, 0.1, 0.32682762642510355, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.D_PRESENCE, 0.1, 0.5450826720309907, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.D_PRESENCE, 0.1, 0.736121634703844, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.D_PRESENCE, 0.1, 0.6824600738509348, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.D_PRESENCE, 0.1, 0.4229456039455204, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.D_PRESENCE, 0.1, 0.3101938163248257, 1.0000000000000004));
        
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.P_UNIQUENESS, 0.1, 0.0049634194198648895, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.P_UNIQUENESS, 0.1, 0.00710363579774187, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.P_UNIQUENESS, 0.1, 0.018461399150697666, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.P_UNIQUENESS, 0.1, 0.0, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.P_UNIQUENESS, 0.1, 0.0, 1.0000000000000004));
        
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.K_ANONYMITY, 1.0, 0.16273358184004305, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.K_ANONYMITY, 1.0, 0.1287966152224742, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.K_ANONYMITY, 1.0, 0.10147063173897553, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.K_ANONYMITY, 1.0, 0.04559864251535917, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.K_ANONYMITY, 1.0, 0.05757878033850372, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.L_DIVERSITY, 1.0, 0.16873621678753303, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.L_DIVERSITY, 1.0, 0.11574270797256259, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.L_DIVERSITY, 1.0, 0.09987780486248954, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.L_DIVERSITY, 1.0, 0.15235467818065307, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.L_DIVERSITY, 1.0, 0.054058173821677435, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.T_CLOSENESS, 1.0, 0.5049846167763927, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.T_CLOSENESS, 1.0, 0.215580067870204, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.T_CLOSENESS, 1.0, 0.24818069546787958, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.T_CLOSENESS, 1.0, 0.4074998927362343, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.T_CLOSENESS, 1.0, 0.31929315436224326, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.D_PRESENCE, 1.0, 0.5450826720309907, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.D_PRESENCE, 1.0, 0.736121634703844, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.D_PRESENCE, 1.0, 0.6824600738509348, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.D_PRESENCE, 1.0, 0.4229456039455204, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.D_PRESENCE, 1.0, 0.3101938163248257, 1.0000000000000004));
        
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.P_UNIQUENESS, 1.0, 0.0049634194198648895, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.P_UNIQUENESS, 1.0, 0.00710363579774187, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.P_UNIQUENESS, 1.0, 0.018461399150697666, 0.9999999999999987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.P_UNIQUENESS, 1.0, 0.0, 1.0000000000000004));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.LOSS, BenchmarkPrivacyModel.P_UNIQUENESS, 1.0, 0.0, 1.0000000000000004));
        
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.K_ANONYMITY, 0.0, 5.5170356E7, 9.09746244E8));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.K_ANONYMITY, 0.0, 3.01506905E8, 4.024760481E9));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.K_ANONYMITY, 0.0, 1.63184963E8, 1.0188277969E10));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.K_ANONYMITY, 0.0, 5.695103509E9, 2.90793798009E11));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.K_ANONYMITY, 0.0, 1.88718942E8, 1.424451798016E12));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.L_DIVERSITY, 0.0, 5.5170356E7, 9.09746244E8));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.L_DIVERSITY, 0.0, 9.2264547E7, 4.024760481E9));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.L_DIVERSITY, 0.0, 1.63184963E8, 1.0188277969E10));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.L_DIVERSITY, 0.0, 2.1209722019E10, 2.90793798009E11));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.L_DIVERSITY, 0.0, 4.99428552E8, 1.424451798016E12));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.T_CLOSENESS, 0.0, 2.90180796E8, 9.09746244E8));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.T_CLOSENESS, 0.0, 5.88189923E8, 4.024760481E9));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.T_CLOSENESS, 0.0, 7.80794309E8, 1.0188277969E10));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.T_CLOSENESS, 0.0, 2.8735900831E10, 2.90793798009E11));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.T_CLOSENESS, 0.0, 3.823303854E9, 1.424451798016E12));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.D_PRESENCE, 0.0, 1613190.0, 9096256.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.D_PRESENCE, 0.0, 6145304.0, 4.0246336E7));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.D_PRESENCE, 0.0, 7819409.0, 1.01868649E8));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.D_PRESENCE, 0.0, 6.8886459E7, 2.907905625E9));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.D_PRESENCE, 0.0, 1.3618414E7, 1.42444225E10));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.P_UNIQUENESS, 0.0, 511040.0, 9.09746244E8));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.P_UNIQUENESS, 0.0, 64439.0, 4.024760481E9));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.P_UNIQUENESS, 0.0, 9430437.0, 1.0188277969E10));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.P_UNIQUENESS, 0.0, 2.241709429E9, 2.90793798009E11));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.P_UNIQUENESS, 0.0, 9642324.0, 1.424451798016E12));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.K_ANONYMITY, 0.1, 9268952.0, 9.09746244E8));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.K_ANONYMITY, 0.1, 1.8075903E7, 4.024760481E9));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.K_ANONYMITY, 0.1, 5.1226981E7, 1.0188277969E10));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.K_ANONYMITY, 0.1, 3.668618921E9, 2.90793798009E11));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.K_ANONYMITY, 0.1, 1.51003054E8, 1.424451798016E12));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.L_DIVERSITY, 0.1, 8865882.0, 9.09746244E8));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.L_DIVERSITY, 0.1, 1.5284651E7, 4.024760481E9));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.L_DIVERSITY, 0.1, 4.7189649E7, 1.0188277969E10));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.L_DIVERSITY, 0.1, 1.5927518025E10, 2.90793798009E11));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.L_DIVERSITY, 0.1, 4.99428552E8, 1.424451798016E12));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.T_CLOSENESS, 0.1, 1.05354606E8, 9.09746244E8));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.T_CLOSENESS, 0.1, 4.9347313E7, 4.024760481E9));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.T_CLOSENESS, 0.1, 1.80641111E8, 1.0188277969E10));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.T_CLOSENESS, 0.1, 2.4552278877E10, 2.90793798009E11));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.T_CLOSENESS, 0.1, 3.823303854E9, 1.424451798016E12));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.D_PRESENCE, 0.1, 925344.0, 9096256.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.D_PRESENCE, 0.1, 3058166.0, 4.0246336E7));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.D_PRESENCE, 0.1, 7819409.0, 1.01868649E8));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.D_PRESENCE, 0.1, 6.6235941E7, 2.907905625E9));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.D_PRESENCE, 0.1, 1.349541E7, 1.42444225E10));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.P_UNIQUENESS, 0.1, 511040.0, 9.09746244E8));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.P_UNIQUENESS, 0.1, 64439.0, 4.024760481E9));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.P_UNIQUENESS, 0.1, 9430437.0, 1.0188277969E10));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.P_UNIQUENESS, 0.1, 2.241709429E9, 2.90793798009E11));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.P_UNIQUENESS, 0.1, 9642324.0, 1.424451798016E12));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.K_ANONYMITY, 1.0, 9268952.0, 9.09746244E8));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.K_ANONYMITY, 1.0, 1.8075903E7, 4.024760481E9));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.K_ANONYMITY, 1.0, 5.1226981E7, 1.0188277969E10));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.K_ANONYMITY, 1.0, 3.668618921E9, 2.90793798009E11));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.K_ANONYMITY, 1.0, 1.51003054E8, 1.424451798016E12));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.L_DIVERSITY, 1.0, 8865882.0, 9.09746244E8));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.L_DIVERSITY, 1.0, 1.5284651E7, 4.024760481E9));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.L_DIVERSITY, 1.0, 4.7189649E7, 1.0188277969E10));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.L_DIVERSITY, 1.0, 1.5927518025E10, 2.90793798009E11));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.L_DIVERSITY, 1.0, 4.99428552E8, 1.424451798016E12));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.T_CLOSENESS, 1.0, 1.05354606E8, 9.09746244E8));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.T_CLOSENESS, 1.0, 4.9347313E7, 4.024760481E9));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.T_CLOSENESS, 1.0, 1.80641111E8, 1.0188277969E10));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.T_CLOSENESS, 1.0, 2.4552278877E10, 2.90793798009E11));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.T_CLOSENESS, 1.0, 3.823303854E9, 1.424451798016E12));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.D_PRESENCE, 1.0, 925344.0, 9096256.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.D_PRESENCE, 1.0, 3058166.0, 4.0246336E7));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.D_PRESENCE, 1.0, 7819409.0, 1.01868649E8));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.D_PRESENCE, 1.0, 6.6235941E7, 2.907905625E9));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.D_PRESENCE, 1.0, 1.349541E7, 1.42444225E10));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.P_UNIQUENESS, 1.0, 511040.0, 9.09746244E8));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.P_UNIQUENESS, 1.0, 64439.0, 4.024760481E9));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.P_UNIQUENESS, 1.0, 9430437.0, 1.0188277969E10));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.P_UNIQUENESS, 1.0, 2.241709429E9, 2.90793798009E11));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.DISCERNIBILITY, BenchmarkPrivacyModel.P_UNIQUENESS, 1.0, 9642324.0, 1.424451798016E12));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.K_ANONYMITY, 0.0, 324620.5269918692, 455884.9365769584));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.K_ANONYMITY, 0.0, 1961244.4822559545, 2207518.9993976974));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.K_ANONYMITY, 0.0, 1201007.0880104562, 1828114.5308318876));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.K_ANONYMITY, 0.0, 3478926.4429797237, 7631154.350031186));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.K_ANONYMITY, 0.0, 9503511.261799559, 2.5025416064416602E7));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.L_DIVERSITY, 0.0, 324620.5269918692, 455884.9365769584));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.L_DIVERSITY, 0.0, 1849069.8831038165, 2207518.9993976974));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.L_DIVERSITY, 0.0, 1201007.0880104562, 1828114.5308318876));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.L_DIVERSITY, 0.0, 4449205.5963389445, 7631154.350031186));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.L_DIVERSITY, 0.0, 1.110578692891293E7, 2.5025416064416602E7));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.T_CLOSENESS, 0.0, 398400.0741806447, 455884.9365769584));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.T_CLOSENESS, 0.0, 2023751.243421626, 2207518.9993976974));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.T_CLOSENESS, 0.0, 1429434.050891089, 1828114.5308318876));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.T_CLOSENESS, 0.0, 5760138.103541854, 7631154.350031186));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.T_CLOSENESS, 0.0, 1.4719292081181683E7, 2.5025416064416602E7));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.D_PRESENCE, 0.0, 37223.2491248282, 45489.51725469279));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.D_PRESENCE, 0.0, 195829.0291224279, 213334.24047076472));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.D_PRESENCE, 0.0, 142414.2491462392, 182250.0061611987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.D_PRESENCE, 0.0, 400542.9546949434, 761596.0125393161));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.D_PRESENCE, 0.0, 1287579.5821546589, 2502463.58155805));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.P_UNIQUENESS, 0.0, 14525.4525605146, 455884.9365769584));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.P_UNIQUENESS, 0.0, 378629.2356952147, 2207518.9993976974));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.P_UNIQUENESS, 0.0, 224136.17203017522, 1828114.5308318876));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.P_UNIQUENESS, 0.0, 0.0, 7631154.350031186));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.P_UNIQUENESS, 0.0, 0.0, 2.5025416064416602E7));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.K_ANONYMITY, 0.1, 170242.48182758508, 455884.9365769584));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.K_ANONYMITY, 0.1, 1252060.0068011207, 2207518.9993976974));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.K_ANONYMITY, 0.1, 524588.4631206655, 1828114.5308318876));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.K_ANONYMITY, 0.1, 1147007.6735407393, 7631154.350031186));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.K_ANONYMITY, 0.1, 5752032.059741599, 2.5025416064416602E7));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.L_DIVERSITY, 0.1, 165784.0187082871, 455884.9365769584));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.L_DIVERSITY, 0.1, 1201665.8941423893, 2207518.9993976974));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.L_DIVERSITY, 0.1, 482229.19553293573, 1828114.5308318876));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.L_DIVERSITY, 0.1, 2955724.7848877655, 7631154.350031186));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.L_DIVERSITY, 0.1, 6385211.111222374, 2.5025416064416602E7));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.T_CLOSENESS, 0.1, 293760.385098631, 455884.9365769584));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.T_CLOSENESS, 0.1, 1407619.3716609064, 2207518.9993976974));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.T_CLOSENESS, 0.1, 906074.1283192673, 1828114.5308318876));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.T_CLOSENESS, 0.1, 5004909.21264682, 7631154.350031186));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.T_CLOSENESS, 0.1, 1.4383391697090292E7, 2.5025416064416602E7));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.D_PRESENCE, 0.1, 33660.3063277646, 45489.51725469279));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.D_PRESENCE, 0.1, 188698.52093140973, 213334.24047076472));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.D_PRESENCE, 0.1, 130481.14757714301, 182250.0061611987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.D_PRESENCE, 0.1, 348509.5903491556, 761596.0125393161));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.D_PRESENCE, 0.1, 1091154.322219155, 2502463.58155805));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.P_UNIQUENESS, 0.1, 14525.4525605146, 455884.9365769584));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.P_UNIQUENESS, 0.1, 378629.2356952147, 2207518.9993976974));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.P_UNIQUENESS, 0.1, 205390.37654595572, 1828114.5308318876));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.P_UNIQUENESS, 0.1, 0.0, 7631154.350031186));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.P_UNIQUENESS, 0.1, 0.0, 2.5025416064416602E7));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.K_ANONYMITY, 1.0, 168349.3384320104, 455884.9365769584));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.K_ANONYMITY, 1.0, 1252060.0068011207, 2207518.9993976974));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.K_ANONYMITY, 1.0, 513815.1613051364, 1828114.5308318876));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.K_ANONYMITY, 1.0, 1147007.6735407393, 7631154.350031186));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.K_ANONYMITY, 1.0, 5752032.059741599, 2.5025416064416602E7));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.L_DIVERSITY, 1.0, 165784.0187082871, 455884.9365769584));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.L_DIVERSITY, 1.0, 1201665.8941423893, 2207518.9993976974));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.L_DIVERSITY, 1.0, 478257.59255078644, 1828114.5308318876));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.L_DIVERSITY, 1.0, 2689752.332576472, 7631154.350031186));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.L_DIVERSITY, 1.0, 6385211.111222374, 2.5025416064416602E7));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.T_CLOSENESS, 1.0, 267829.68447463156, 455884.9365769584));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.T_CLOSENESS, 1.0, 1407619.3716609064, 2207518.999397698));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.T_CLOSENESS, 1.0, 900654.1343434566, 1828114.5308318876));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.T_CLOSENESS, 1.0, 4882032.028492338, 7631154.350031186));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.T_CLOSENESS, 1.0, 1.4383391697090292E7, 2.5025416064416606E7));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.D_PRESENCE, 1.0, 33660.3063277646, 45489.51725469279));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.D_PRESENCE, 1.0, 188698.52093140973, 213334.24047076472));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.D_PRESENCE, 1.0, 130481.14757714301, 182250.0061611987));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.D_PRESENCE, 1.0, 348509.5903491556, 761596.0125393161));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.D_PRESENCE, 1.0, 1091154.322219155, 2502463.58155805));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.P_UNIQUENESS, 1.0, 14525.4525605146, 455884.9365769584));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.P_UNIQUENESS, 1.0, 378629.2356952147, 2207518.9993976974));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.P_UNIQUENESS, 1.0, 205390.37654595572, 1828114.5308318876));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.P_UNIQUENESS, 1.0, 0.0, 7631154.350031186));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.ENTROPY, BenchmarkPrivacyModel.P_UNIQUENESS, 1.0, 0.0, 2.5025416064416602E7));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.K_ANONYMITY, 0.0, 0.625, 1.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.K_ANONYMITY, 0.0, 0.857142857142857, 0.9999999999999998));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.K_ANONYMITY, 0.0, 0.7142857142857142, 0.9999999999999998));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.K_ANONYMITY, 0.0, 0.5625, 1.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.K_ANONYMITY, 0.0, 0.4, 1.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.L_DIVERSITY, 0.0, 0.625, 1.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.L_DIVERSITY, 0.0, 0.8214285714285714, 0.9999999999999998));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.L_DIVERSITY, 0.0, 0.7142857142857142, 0.9999999999999998));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.L_DIVERSITY, 0.0, 0.625, 1.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.L_DIVERSITY, 0.0, 0.4375, 1.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.T_CLOSENESS, 0.0, 0.8125, 1.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.T_CLOSENESS, 0.0, 0.857142857142857, 0.9999999999999998));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.T_CLOSENESS, 0.0, 0.8857142857142857, 0.9999999999999998));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.T_CLOSENESS, 0.0, 0.75, 1.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.T_CLOSENESS, 0.0, 0.5, 1.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.D_PRESENCE, 0.0, 0.6875, 1.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.D_PRESENCE, 0.0, 0.857142857142857, 0.9999999999999998));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.D_PRESENCE, 0.0, 0.8095238095238093, 0.9999999999999998));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.D_PRESENCE, 0.0, 0.625, 1.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.D_PRESENCE, 0.0, 0.4375, 1.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.P_UNIQUENESS, 0.0, 0.03125, 1.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.P_UNIQUENESS, 0.0, 0.09999999999999999, 0.9999999999999998));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.P_UNIQUENESS, 0.0, 0.0761904761904762, 0.9999999999999998));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.P_UNIQUENESS, 0.0, 0.0, 1.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.P_UNIQUENESS, 0.0, 0.0, 1.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.K_ANONYMITY, 0.1, 0.24123621333686973, 1.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.K_ANONYMITY, 0.1, 0.3309720842987973, 0.9999999999999998));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.K_ANONYMITY, 0.1, 0.1718668457505554, 0.9999999999999998));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.K_ANONYMITY, 0.1, 0.09231214290880163, 1.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.K_ANONYMITY, 0.1, 0.14369860197368423, 1.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.L_DIVERSITY, 0.1, 0.2677748491479345, 1.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.L_DIVERSITY, 0.1, 0.3062989909634824, 0.9999999999999998));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.L_DIVERSITY, 0.1, 0.1605750310070827, 0.9999999999999998));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.L_DIVERSITY, 0.1, 0.2602035593682372, 1.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.L_DIVERSITY, 0.1, 0.13779322272903988, 1.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.T_CLOSENESS, 0.1, 0.6554853789536503, 1.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.T_CLOSENESS, 0.1, 0.4025341881207961, 0.9999999999999998));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.T_CLOSENESS, 0.1, 0.4407982914377992, 0.9999999999999998));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.T_CLOSENESS, 0.1, 0.5494860482927308, 1.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.T_CLOSENESS, 0.1, 0.42679863243022226, 1.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.D_PRESENCE, 0.1, 0.626865053050398, 1.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.D_PRESENCE, 0.1, 0.8214567195099981, 0.9999999999999998));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.D_PRESENCE, 0.1, 0.7619991224469576, 0.9999999999999998));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.D_PRESENCE, 0.1, 0.5625730180806675, 1.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.D_PRESENCE, 0.1, 0.4320648826979472, 1.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.P_UNIQUENESS, 0.1, 0.03125, 1.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.P_UNIQUENESS, 0.1, 0.09999999999999999, 0.9999999999999998));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.P_UNIQUENESS, 0.1, 0.06282221300698172, 0.9999999999999998));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.P_UNIQUENESS, 0.1, 0.0, 1.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.P_UNIQUENESS, 0.1, 0.0, 1.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.K_ANONYMITY, 1.0, 0.23929944963861813, 1.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.K_ANONYMITY, 1.0, 0.3309720842987973, 0.9999999999999998));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.K_ANONYMITY, 1.0, 0.1718668457505554, 0.9999999999999998));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.K_ANONYMITY, 1.0, 0.09231214290880163, 1.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.K_ANONYMITY, 1.0, 0.14369860197368423, 1.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.L_DIVERSITY, 1.0, 0.2564871913887231, 1.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.L_DIVERSITY, 1.0, 0.3062989909634824, 0.9999999999999998));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.L_DIVERSITY, 1.0, 0.1605750310070827, 0.9999999999999998));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.L_DIVERSITY, 1.0, 0.2602035593682372, 1.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.L_DIVERSITY, 1.0, 0.13779322272903988, 1.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.T_CLOSENESS, 1.0, 0.5968157366885485, 1.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.T_CLOSENESS, 1.0, 0.4025341881207961, 0.9999999999999998));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.T_CLOSENESS, 1.0, 0.3346453256793371, 0.9999999999999998));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.T_CLOSENESS, 1.0, 0.4687709201432352, 1.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.T_CLOSENESS, 1.0, 0.42679863243022226, 1.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.D_PRESENCE, 1.0, 0.626865053050398, 1.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.D_PRESENCE, 1.0, 0.8214567195099981, 0.9999999999999998));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.D_PRESENCE, 1.0, 0.7619991224469576, 0.9999999999999998));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.D_PRESENCE, 1.0, 0.5625730180806675, 1.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.D_PRESENCE, 1.0, 0.4320648826979472, 1.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.P_UNIQUENESS, 1.0, 0.03125, 1.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.P_UNIQUENESS, 1.0, 0.09999999999999999, 0.9999999999999998));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.P_UNIQUENESS, 1.0, 0.06282221300698172, 0.9999999999999998));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.P_UNIQUENESS, 1.0, 0.0, 1.0));
        list.add(new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkQualityMeasure.PRECISION, BenchmarkPrivacyModel.P_UNIQUENESS, 1.0, 0.0, 1.0));
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
                                                                BenchmarkQualityMeasure measure,
                                                                BenchmarkPrivacyModel criterion,
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
        for (BenchmarkQualityMeasure measure : getUtilityMeasures()) {
            for (double suppressionLimit : getSuppressionLimits()) {
                for (BenchmarkPrivacyModel criterion : getCriteria()) {
                    for (BenchmarkDataset dataset : getDatasets()) {
                        
                        // Compute
                        double[] minmax = BenchmarkEnvironment.getMinimalAndMaximalInformationLoss(dataset, measure, criterion, suppressionLimit);

                        // Print
                        System.out.print("list.add(new UtilityMetadataEntry(");
                        System.out.print("BenchmarkDataset." + dataset.name() + ", ");
                        System.out.print("BenchmarkQualityMeasure." + measure.name() + ", ");
                        System.out.print("BenchmarkPrivacyModel." + criterion.name() + ", ");
                        System.out.print(suppressionLimit + ", ");
                        System.out.print(minmax[0] + ", ");
                        System.out.print(minmax[1] + "));\n");
                    }
                }
            }
        }
    }

    /**
     * Returns all criteria
     * @return
     */
    private static BenchmarkPrivacyModel[] getCriteria() {
        return new BenchmarkPrivacyModel[]{
            BenchmarkPrivacyModel.K_ANONYMITY,
            BenchmarkPrivacyModel.L_DIVERSITY,
            BenchmarkPrivacyModel.T_CLOSENESS,
            BenchmarkPrivacyModel.D_PRESENCE,
            BenchmarkPrivacyModel.P_UNIQUENESS
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
    private static BenchmarkQualityMeasure[] getUtilityMeasures() {
        return new BenchmarkQualityMeasure[] {
                // BenchmarkQualityMeasure.AECS,
                // BenchmarkQualityMeasure.LOSS,
                // BenchmarkQualityMeasure.DISCERNIBILITY,
                BenchmarkQualityMeasure.ENTROPY,
                BenchmarkQualityMeasure.PRECISION
        };
    }
}
