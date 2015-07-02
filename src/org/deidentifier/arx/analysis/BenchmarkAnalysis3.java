package org.deidentifier.arx.analysis;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.deidentifier.arx.benchmark.BenchmarkExperiment3;
import org.deidentifier.arx.benchmark.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.benchmark.BenchmarkSetup.BenchmarkUtilityMeasure;

import de.linearbits.objectselector.Selector;
import de.linearbits.subframe.analyzer.Analyzer;
import de.linearbits.subframe.graph.Field;
import de.linearbits.subframe.graph.Labels;
import de.linearbits.subframe.graph.Plot;
import de.linearbits.subframe.graph.PlotLinesClustered;
import de.linearbits.subframe.graph.Point3D;
import de.linearbits.subframe.graph.Series3D;
import de.linearbits.subframe.io.CSVFile;
import de.linearbits.subframe.render.GnuPlotParams;
import de.linearbits.subframe.render.GnuPlotParams.KeyPos;
import de.linearbits.subframe.render.LaTeX;
import de.linearbits.subframe.render.PlotGroup;

public class BenchmarkAnalysis3 {
    public static void main(String[] args) throws IOException, ParseException {
        
        // Prepare
        CSVFile file = new CSVFile(new File("results/experiment3.csv"));
        
        // Create on file with multiple plots
        List<PlotGroup> groups = new ArrayList<PlotGroup>();

        // For each plot
        for (BenchmarkCriterion criterion : BenchmarkExperiment3.getCriteria()) {
            for (BenchmarkUtilityMeasure measure : BenchmarkExperiment3.getUtilityMeasures()) {
                for (double suppression : BenchmarkExperiment3.getSuppressionLimits()) {
                    groups.add(plot(file, criterion, measure, suppression));
                }
            }
        }

        // Plot
        LaTeX.plot(groups, "results/experiment3", false);
    }

    /**
     * Plots the results from one experiment
     * @param file
     * @param criterion
     * @param measure
     * @param suppression
     * @return
     * @throws ParseException 
     */
    private static PlotGroup plot(CSVFile file,
                                  BenchmarkCriterion criterion,
                                  BenchmarkUtilityMeasure measure,
                                  double suppression) throws ParseException {

        // Plotting params
        GnuPlotParams params = new GnuPlotParams();
        params.rotateXTicks = 0;
        params.printValues = false;
        params.size = 0.8;
        params.logX = false;
        params.logY = false;
        params.ratio = 0.2d;
        params.minY = 0d;
        params.printValuesFormatString = "%.0f";
        params.maxY = 100d;
        params.keypos = KeyPos.TOP_RIGHT;
        params.colorize = true;

        // Select
        Selector<String[]> selector = file.getSelectorBuilder()
                                          .field("Suppression limit").equals(String.valueOf(suppression)).and()
                                          .field("Utility measure").equals(measure.toString()).and()
                                          .field("Privacy model").equals(criterion.toString())
                                          .build();

        // Create series
        Series3D series = new Series3D(file,
                                       selector,
                                       new Field("Time", Analyzer.VALUE),
                                       new Field("", "Dataset"),
                                       new Field("Utility", Analyzer.VALUE));
        
        // Pre-process
        makeClusterable(series);
        convertUnits(series);

        // Define plot group
        Labels labels = new Labels("Execution time [s]", measure.toString()+" Utility [%]");
        List<Plot<?>> plots = new ArrayList<Plot<?>>();
        plots.add(new PlotLinesClustered("", labels, series));
        String caption = "Development of the utility of the SS13ACS dataset over time for " + criterion.toString()+ " with " + (suppression * 100d) + "\\% suppression (higher is better)";
        return new PlotGroup(caption, plots, params, 1.0d);
    }
    
    /**
     * Converts milliseconds to seconds and [0,1] to [0,100]
     * @param series
     */
    private static void convertUnits(Series3D series) {
        
        List<Point3D> list = new ArrayList<Point3D>();
        for (Point3D point : series.getData()) {
            
            list.add(new Point3D(String.valueOf(Double.valueOf(point.x) / 1000d),
                                 point.y.replace("_", "-"),
                                 String.valueOf(Double.valueOf(point.z) * 100d)));
        }
        
        series.getData().clear();
        series.getData().addAll(list);
    }

    /**
     * We need to make sure that for every x-value there exists a y-value for each cluster
     * @param series
     */
    private static void makeClusterable(Series3D series) {
        
        // Prepare
        List<Double> xvalues = new ArrayList<Double>();
        Map<String, Map<Double, Double>> data = new HashMap<String, Map<Double, Double>>();
        for (Point3D point : series.getData()) {
            if (!data.containsKey(point.y)) {
                data.put(point.y, new HashMap<Double, Double>());
            }
            Double x = Double.valueOf(point.x);
            Double y = Double.valueOf(point.z);
            data.get(point.y).put(x, y);
            if (!xvalues.contains(x)) {
                xvalues.add(x);
            }
        }
        
        // For each cluster entry
        for (String cluster : data.keySet()) {
            
            // For each x-value
            for (double x : xvalues) {
                if (!data.get(cluster).containsKey(x)) {
                    series.getData().add(new Point3D(String.valueOf(x), cluster, String.valueOf(getYValueFor(x, data.get(cluster)))));
                }
            }
        }
        
        // Sort
        Collections.sort(series.getData(), new Comparator<Point3D>() {
            @Override
            public int compare(Point3D o1, Point3D o2) {
                int cluster = o1.y.compareTo(o2.y);
                if (cluster != 0) {
                    return cluster;
                }
                return Double.valueOf(o1.x).compareTo(Double.valueOf(o2.x));
            }            
        });
    }

    /**
     * Returns a missing y value
     * @param x
     * @param data
     * @return
     */
    private static Double getYValueFor(double x, Map<Double, Double> data) {
        double max = - Double.MAX_VALUE;
        double value = 0d;
        for (Entry<Double, Double> entry : data.entrySet()) {
            if (entry.getKey() <= x && entry.getKey() >= max) {
                max = entry.getKey();
                value = entry.getValue();
            }
        }
        return value;
    }
}
