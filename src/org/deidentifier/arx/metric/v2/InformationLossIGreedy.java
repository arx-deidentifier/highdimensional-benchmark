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

import java.util.Arrays;

import org.deidentifier.arx.metric.InformationLoss;

/**
 * For the improved greedy heuristic
 * 
 * @author Fabian Prasser
 */
public class InformationLossIGreedy extends InformationLoss<double[]> {

    /** SVUID */
    private static final long serialVersionUID = 3193042896130786734L;
    /** Values. */
    private double[]          value;

    /**
     * Creates a new instance.
     * 
     * @param value
     */
    InformationLossIGreedy(final double value[]) {
        this.value = value;
    }

    @Override
    public InformationLoss<double[]> clone() {
        return new InformationLossIGreedy(value);

    }

    @Override
    public int compareTo(InformationLoss<?> other) {
        if (other == null) {
            throw new IllegalArgumentException("Argument must not be null");
        } else {
            int greedy = Double.valueOf(this.value[0])
                               .compareTo(Double.valueOf(((InformationLossIGreedy) other).value[0]));
            int datafly = Double.valueOf(this.value[1])
                                .compareTo(Double.valueOf(((InformationLossIGreedy) other).value[1]));

            // use comparison of greatest minimal equivalence class size if not equal
            if (greedy != 0) {
                return greedy;
            }
            // use number of distinct values
            else {
                return datafly;
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        InformationLossIGreedy other = (InformationLossIGreedy) obj;
        return Arrays.equals(this.value, other.value);
    }

    @Override
    public double[] getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }

    @Override
    public void max(final InformationLoss<?> other) {
        if (this.compareTo(other) < 0) {
            this.value = ((InformationLossIGreedy) other).value;
        }
    }

    @Override
    public void min(final InformationLoss<?> other) {
        if (this.compareTo(other) > 0) {
            this.value = ((InformationLossIGreedy) other).value;
        }
    }

    @Override
    public double relativeTo(InformationLoss<?> min, InformationLoss<?> max) {
        return 0;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }
}
