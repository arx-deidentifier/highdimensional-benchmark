/*
 * Source code of our CBMS 2014 paper "A benchmark of globally-optimal 
 *      methods for the de-identification of biomedical data"
 *      
 * Copyright (C) 2014 Florian Kohlmayer, Fabian Prasser
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.deidentifier.arx.benchmark;

import java.io.IOException;

import org.deidentifier.arx.ARXConfiguration;
import org.deidentifier.arx.ARXPopulationModel;
import org.deidentifier.arx.ARXPopulationModel.Region;
import org.deidentifier.arx.AttributeType;
import org.deidentifier.arx.AttributeType.Hierarchy;
import org.deidentifier.arx.Data;
import org.deidentifier.arx.DataSubset;
import org.deidentifier.arx.criteria.DPresence;
import org.deidentifier.arx.criteria.HierarchicalDistanceTCloseness;
import org.deidentifier.arx.criteria.KAnonymity;
import org.deidentifier.arx.criteria.PopulationUniqueness;
import org.deidentifier.arx.criteria.RecursiveCLDiversity;
import org.deidentifier.arx.metric.Metric;
import org.deidentifier.arx.metric.Metric.AggregateFunction;
import org.deidentifier.arx.metric.v2.MetricSDDataFly;
import org.deidentifier.arx.metric.v2.MetricSDIGreedy;

/**
 * This class encapsulates most of the parameters of a benchmark run
 * @author Fabian Prasser
 * @author Raffael Bild
 * @author Johanna Eicher
 * @author Helmut Spengler
 * @author Florian Kohlmayer
 */
public class BenchmarkSetup {

    public static enum BenchmarkUtilityMeasure {
        AECS {
            @Override
            public String toString() {
                return "AECS";
            }
        },
        LOSS {
            @Override
            public String toString() {
                return "Loss";
            }
        },
    }
    
    public static enum BenchmarkAlgorithm {
        FLASH {
            @Override
            public String toString() {
                return "Flash";
            }
        },
        LIGHTNING {
            @Override
            public String toString() {
                return "Lightning";
            }
        },
        LIGHTNIG_MINIMAL {
            @Override
            public String toString() {
                return "LightningMinimal";
            }
        },
        DATAFLY {
            @Override
            public String toString() {
                return "DataFly";
            }
        },
        IGREEDY {
            @Override
            public String toString() {
                return "IGreedy";
            }
        }
    }

    public static enum BenchmarkCriterion {
        K_ANONYMITY {
            @Override
            public String toString() {
                return "k";
            }
        },
        L_DIVERSITY {
            @Override
            public String toString() {
                return "l";
            }
        },
        T_CLOSENESS {
            @Override
            public String toString() {
                return "t";
            }
        },
        D_PRESENCE {
            @Override
            public String toString() {
                return "d";
            }
        },
        RISK_BASED {
            @Override
            public String toString() {
                return "r";
            }
        }
    }

    public static enum BenchmarkDataset {
        ADULT {
            @Override
            public String toString() {
                return "ADULT";
            }
        },
        CUP {
            @Override
            public String toString() {
                return "CUP";
            }
        },
        FARS {
            @Override
            public String toString() {
                return "FARS";
            }
        },
        ATUS {
            @Override
            public String toString() {
                return "ATUS";
            }
        },
        IHIS {
            @Override
            public String toString() {
                return "IHIS";
            }
        },
        SS13ACS_15 {
            @Override
            public String toString() {
                return "SS13ACS_15";
            }
        },
        SS13ACS_20 {
            @Override
            public String toString() {
                return "SS13ACS_10";
            }
        },
        SS13ACS_25 {
            @Override
            public String toString() {
                return "SS13ACS_25";
            }
        },
        SS13ACS_30 {
            @Override
            public String toString() {
                return "SS13ACS_30";
            }
        }
    }

    /**
     * Returns a configuration for the ARX framework
     * @param dataset
     * @param utility
     * @param algorithm
     * @param criteria
     * @return
     * @throws IOException
     */
    public static ARXConfiguration getConfiguration(BenchmarkDataset dataset, 
                                                    BenchmarkUtilityMeasure utility,
                                                    BenchmarkAlgorithm algorithm,
                                                    BenchmarkCriterion... criteria) throws IOException {
        
        ARXConfiguration config = ARXConfiguration.create();
        config.setMetric(Metric.createEntropyMetric(true));
        config.setMaxOutliers(0d);
        
        for (BenchmarkCriterion c : criteria) {
            switch (c) {
            case D_PRESENCE:
                config.addCriterion(new DPresence(0.05d, 0.15d, getResearchSubset(dataset)));
                break;
            case K_ANONYMITY:
                config.addCriterion(new KAnonymity(5));
                break;
            case L_DIVERSITY:
                String sensitive = getSensitiveAttribute(dataset);
                config.addCriterion(new RecursiveCLDiversity(sensitive, 4, 3));
                break;
            case T_CLOSENESS:
                sensitive = getSensitiveAttribute(dataset);
                config.addCriterion(new HierarchicalDistanceTCloseness(sensitive, 0.2d, getHierarchy(dataset, sensitive)));
                break;
            case RISK_BASED:
                config.addCriterion(new PopulationUniqueness(0.01d, ARXPopulationModel.create(Region.USA)));
                break;
            default:
                throw new RuntimeException("Invalid criterion");
            }
        }
        
        if (algorithm == BenchmarkAlgorithm.DATAFLY) {
            config.setMetric(new MetricSDDataFly());
        } else if (algorithm == BenchmarkAlgorithm.IGREEDY) {
            config.setMetric(new MetricSDIGreedy());
        } else if (utility == BenchmarkUtilityMeasure.LOSS){
            config.setMetric(Metric.createLossMetric(AggregateFunction.GEOMETRIC_MEAN));
        } else if (utility == BenchmarkUtilityMeasure.AECS){
            config.setMetric(Metric.createAECSMetric());
        } else {
            throw new IllegalArgumentException("Unknown utility measure");
        }

        return config;
    }
    /**
     * Configures and returns the dataset 
     * @param dataset
     * @param criteria
     * @return
     * @throws IOException
     */
    public static Data getData(BenchmarkDataset dataset, 
                               BenchmarkCriterion[] criteria) throws IOException {
        return getData(dataset, criteria, Integer.MAX_VALUE);
    }
    
    /**
     * Configures and returns the dataset 
     * @param dataset
     * @return
     * @throws IOException
     */
    public static Data getData(BenchmarkDataset dataset) throws IOException {
        return getData(dataset, null, Integer.MAX_VALUE);
    }
    
    /**
     * Configures and returns the dataset 
     * @param dataset
     * @param criteria
     * @param qiCount
     * @return
     * @throws IOException
     */
    @SuppressWarnings("incomplete-switch")
    public static Data getData(BenchmarkDataset dataset, 
                               BenchmarkCriterion[] criteria,
                               int qiCount) throws IOException {
        Data data = null;
        switch (dataset) {
        case ADULT:
            data = Data.create("data/adult.csv", ';');
            break;
        case ATUS:
            data = Data.create("data/atus.csv", ';');
            break;
        case CUP:
            data = Data.create("data/cup.csv", ';');
            break;
        case FARS:
            data = Data.create("data/fars.csv", ';');
            break;
        case IHIS:
            data = Data.create("data/ihis.csv", ';');
            break;
        case SS13ACS_15:
        case SS13ACS_20:
        case SS13ACS_25:
        case SS13ACS_30:
            data = Data.create("data/ss13acs.csv", ';');
            break;
        default:
            throw new RuntimeException("Invalid dataset");
        }

        if (criteria != null) {
            int count = 0;
            for (String qi : getQuasiIdentifyingAttributes(dataset)) {
                data.getDefinition().setAttributeType(qi, getHierarchy(dataset, qi));
                count++;
                if (count > qiCount) {
                    break;
                }
            }
            for (BenchmarkCriterion c : criteria) {
                switch (c) {
                case L_DIVERSITY:
                case T_CLOSENESS:
                    String sensitive = getSensitiveAttribute(dataset);
                    data.getDefinition().setAttributeType(sensitive, AttributeType.SENSITIVE_ATTRIBUTE);
                    break;
                }
            }
        }

        return data;
    }

    /**
     * Returns the generalization hierarchy for the dataset and attribute
     * @param dataset
     * @param attribute
     * @return
     * @throws IOException
     */
    public static Hierarchy getHierarchy(BenchmarkDataset dataset, String attribute) throws IOException {
        switch (dataset) {
        case ADULT:
            return Hierarchy.create("hierarchies/adult_hierarchy_" + attribute + ".csv", ';');
        case ATUS:
            return Hierarchy.create("hierarchies/atus_hierarchy_" + attribute + ".csv", ';');
        case CUP:
            return Hierarchy.create("hierarchies/cup_hierarchy_" + attribute + ".csv", ';');
        case FARS:
            return Hierarchy.create("hierarchies/fars_hierarchy_" + attribute + ".csv", ';');
        case IHIS:
            return Hierarchy.create("hierarchies/ihis_hierarchy_" + attribute + ".csv", ';');
        case SS13ACS_15:
        case SS13ACS_20:
        case SS13ACS_25:
        case SS13ACS_30:
            return Hierarchy.create("hierarchies/ss13acs_hierarchy_o_" + attribute + ".csv", ';');
        default:
            throw new RuntimeException("Invalid dataset");
        }
    }

    /**
     * Returns the quasi-identifiers for the dataset
     * @param dataset
     * @return
     */
    public static String[] getQuasiIdentifyingAttributes(BenchmarkDataset dataset) {
        switch (dataset) {
        case ADULT:
            return new String[] {   "age",
                                    "education",
                                    "marital-status",
                                    "native-country",
                                    "race",
                                    "salary-class",
                                    "sex",
                                    "workclass" };
        case ATUS:
            return new String[] {   "Age",
                                    "Birthplace",
                                    "Citizenship status",
                                    "Labor force status",
                                    "Marital status",
                                    "Race",
                                    "Region",
                                    "Sex" };
        case CUP:
            return new String[] {   "AGE",
                                    "GENDER",
                                    "INCOME",
                                    "MINRAMNT",
                                    "NGIFTALL",
                                    "STATE",
                                    "ZIP" };
        case FARS:
            return new String[] {   "iage",
                                    "ideathday",
                                    "ideathmon",
                                    "ihispanic",
                                    "iinjury",
                                    "irace",
                                    "isex" };
        case IHIS:
            return new String[] {   "AGE",
                                    "MARSTAT",
                                    "PERNUM",
                                    "QUARTER",
                                    "RACEA",
                                    "REGION",
                                    "SEX",
                                    "YEAR" };
        case SS13ACS_15:
            return new String[] {   "AGEP",
                                    "CIT",
                                    "COW",
                                    "DDRS",
                                    "DEAR",
                                    "DEYE",
                                    "DOUT",
                                    "DPHY",
                                    "DREM",
                                    "FER",
                                    "GCL",
                                    "HINS1",
                                    "HINS2",
                                    "HINS3",
                                    "HINS4"};
            
        case SS13ACS_20:
            return new String[] {   "AGEP",
                                    "CIT",
                                    "COW",
                                    "DDRS",
                                    "DEAR",
                                    "DEYE",
                                    "DOUT",
                                    "DPHY",
                                    "DREM",
                                    "FER",
                                    "GCL",
                                    "HINS1",
                                    "HINS2",
                                    "HINS3",
                                    "HINS4",
                                    "HINS5",
                                    "HINS6",
                                    "HINS7",
                                    "INTP",
                                    "MAR"};
            
        case SS13ACS_25:
            return new String[] {   "AGEP",
                                    "CIT",
                                    "COW",
                                    "DDRS",
                                    "DEAR",
                                    "DEYE",
                                    "DOUT",
                                    "DPHY",
                                    "DREM",
                                    "FER",
                                    "GCL",
                                    "HINS1",
                                    "HINS2",
                                    "HINS3",
                                    "HINS4",
                                    "HINS5",
                                    "HINS6",
                                    "HINS7",
                                    "INTP",
                                    "MAR",
                                    "MARHD",
                                    "MARHM",
                                    "MARHW",
                                    "MIG",
                                    "MIL" };
            
        case SS13ACS_30:
            return new String[] {   "AGEP",
                                    "CIT",
                                    "COW",
                                    "DDRS",
                                    "DEAR",
                                    "DEYE",
                                    "DOUT",
                                    "DPHY",
                                    "DREM",
                                    "FER",
                                    "GCL",
                                    "HINS1",
                                    "HINS2",
                                    "HINS3",
                                    "HINS4",
                                    "HINS5",
                                    "HINS6",
                                    "HINS7",
                                    "INTP",
                                    "MAR",
                                    "MARHD",
                                    "MARHM",
                                    "MARHW",
                                    "MIG",
                                    "MIL",
                                    "PWGTP",
                                    "RELP",
                                    "SCHG",
                                    "SCHL",
                                    "SEX" };
            
        default:
            throw new RuntimeException("Invalid dataset");
        }
    }

    /**
     * Returns the research subset for the dataset
     * @param dataset
     * @return
     * @throws IOException
     */
    public static DataSubset getResearchSubset(BenchmarkDataset dataset) throws IOException {
        switch (dataset) {
        case ADULT:
            return DataSubset.create(getData(dataset, null), Data.create("data/adult_subset.csv", ';'));
        case ATUS:
            return DataSubset.create(getData(dataset, null), Data.create("data/atus_subset.csv", ';'));
        case CUP:
            return DataSubset.create(getData(dataset, null), Data.create("data/cup_subset.csv", ';'));
        case FARS:
            return DataSubset.create(getData(dataset, null), Data.create("data/fars_subset.csv", ';'));
        case IHIS:
            return DataSubset.create(getData(dataset, null), Data.create("data/ihis_subset.csv", ';'));
        case SS13ACS_15:
        case SS13ACS_20:
        case SS13ACS_25:
        case SS13ACS_30:
            return DataSubset.create(getData(dataset, null), Data.create("data/ss13acs_subset.csv", ';'));
        default:
            throw new RuntimeException("Invalid dataset");
        }
    }

    /**
     * Returns the sensitive attribute for the dataset
     * @param dataset
     * @return
     */
    public static String getSensitiveAttribute(BenchmarkDataset dataset) {
        switch (dataset) {
        case ADULT:
            return "occupation";
        case ATUS:
            return "Highest level of school completed";
        case CUP:
            return "RAMNTALL";
        case FARS:
            return "istatenum";
        case IHIS:
            return "EDUC";
        default:
            throw new RuntimeException("Invalid dataset");
        }
    }
}
