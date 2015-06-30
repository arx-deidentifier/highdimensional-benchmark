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
import java.util.Map;

import org.deidentifier.arx.BenchmarkSetup.BenchmarkAlgorithm;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkDataset;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkUtilityMeasure;
import org.deidentifier.arx.framework.check.NodeChecker.Result;

/**
 * This class computes the minimal and maximal information loss for all low-dimensional datasets
 * 
 * @author Fabian Prasser
 * @author Raffael Bild
 * @author Johanna Eicher
 * @author Helmut Spengler
 * @author Florian Kohlmayer
 */
public class BenchmarkUtilityMetadata {
    
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
    
    // Fill the map
    static {
        new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.K_ANONYMITY, 0.0, 942.5625, 15081.0);
        new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.K_ANONYMITY, 0.0, 3172.05, 31720.5);
        new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.K_ANONYMITY, 0.0, 266.3245382585752, 33645.666666666664);
        new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.K_ANONYMITY, 0.0, 621.2592165898618, 269626.5);
        new UtilityMetadataEntry(BenchmarkDataset.IHIS, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.K_ANONYMITY, 0.0, 133.4418604651163, 1193504.0);
        new UtilityMetadataEntry(BenchmarkDataset.ADULT, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.L_DIVERSITY, 0.0, 754.05, 15081.0);
        new UtilityMetadataEntry(BenchmarkDataset.CUP, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.L_DIVERSITY, 0.0, 906.3, 31720.5);
        new UtilityMetadataEntry(BenchmarkDataset.FARS, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.L_DIVERSITY, 0.0, 305.869696969697, 33645.666666666664);
        new UtilityMetadataEntry(BenchmarkDataset.ATUS, BenchmarkUtilityMeasure.AECS, BenchmarkCriterion.L_DIVERSITY, 0.0, 7095.434210526316, 269626.5);
        // TODO
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
        
        double[] suppression = new double[]{0, 0.1d, 1d};
        
        for (BenchmarkUtilityMeasure measure : getUtilityMeasures()) {
            for (double s : suppression) {
                for (BenchmarkCriterion criterion : getCriteria()) {
                    for (BenchmarkDataset dataset : getDatasets()) {
                        computeUtilityMetadata(dataset, measure, criterion, s);
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
    private static double[] computeMinimalAndMaximalInformationLoss(BenchmarkDataset dataset,
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

    /**
     * Computes and prints the according metadata
     * @param dataset
     * @param measure 
     * @param criterion
     * @param suppression
     * @throws IOException 
     */
    private static void computeUtilityMetadata(BenchmarkDataset dataset,
                                               BenchmarkUtilityMeasure measure, 
                                               BenchmarkCriterion criterion,
                                               double suppression) throws IOException {
        
        double[] values = computeMinimalAndMaximalInformationLoss(dataset, measure, criterion, suppression);
        
        System.out.print("new UtilityMetadataEntry(");
        System.out.print("BenchmarkDataset." + dataset.name()+", ");
        System.out.print("BenchmarkUtilityMeasure." + measure.name()+", ");
        System.out.print("BenchmarkCriterion." + criterion.name()+", ");
        System.out.print(suppression+", ");
        System.out.print(values[0]+", ");
        System.out.print(values[1]+");\n");
    }

    /**
     * Returns all sets of criteria
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
