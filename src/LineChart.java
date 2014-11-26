import java.util.*;
import javax.swing.*;
import java.awt.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYErrorRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


/**
 * Created by n00430588 on 10/30/2014.
 */
public class LineChart extends JFrame {


    public LineChart(String applicationTitle, String chartTitle, Integer testsize, ArrayList<ArrayList<PacketTracer>> tests) {
        super(applicationTitle);
        XYDataset dataset = LoadData(testsize, tests);
        JFreeChart chart = createChart(dataset, applicationTitle);
        // we put the chart into a panel
        ChartPanel chartPanel = new ChartPanel(chart);
        // default size
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        // add it to our application
        setContentPane(chartPanel);


    }

    private XYDataset LoadData(Integer testsize, ArrayList<ArrayList<PacketTracer>> tests){
        //final XYSeries seriesNoCache = new XYSeries("NoCache");
        final XYSeries seriesLRUCache = new XYSeries("LRU Cache Average Hops");
        final XYSeries seriesFIFOCache = new XYSeries("FIFO Cache Average Hops");
        final XYSeries seriesRandomCache = new XYSeries("Random Cache Average Hops");

        ArrayList<PacketTracer> singleTest = new ArrayList<PacketTracer>();

        //Combine all results
        for(int t=0; t< tests.size(); t++) {
            singleTest = tests.get(t);
            double[] result = new double[6];
            for (int i = 0; i < singleTest.size(); i++) {
                result[singleTest.get(i).cacheSize / 10] += singleTest.get(i).averageHops;
            }
            //Divide the result sums by the number of tests and add to series
            for (int j = 0; j < result.length; j++) {
                //LRU
                if(t==0)
                seriesLRUCache.add(j * 10, result[j] / testsize);
                //FIFO
                if(t==1)
                seriesFIFOCache.add(j * 10, result[j] / testsize);
                //Random
                if(t==2)
                    seriesRandomCache.add(j * 10, result[j] / testsize);
            }
        }


        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(seriesLRUCache);
        dataset.addSeries(seriesFIFOCache);
        dataset.addSeries(seriesRandomCache);

        //dataset.addSeries(seriesNoCache);
        return dataset;

    }

    private JFreeChart createChart(XYDataset dataset, String applicationTitle) {

        JFreeChart chart = ChartFactory.createXYLineChart(applicationTitle,
                "Cache Size",
                "Average Hops per Request",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);

        chart.setBackgroundPaint(Color.white);

        final XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);

        //final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        XYErrorRenderer t = new XYErrorRenderer();
        t.setBaseLinesVisible(true);
        t.setBaseShapesVisible(false);
        t.setDrawYError(true);
        t.setDrawXError(true);
        t.setSeriesPaint(0, Color.black);
        t.setSeriesPaint(1, Color.BLUE);
        t.setSeriesPaint(2, Color.red);
        //renderer.setSeriesLinesVisible(0, true);
        //renderer.setSeriesShapesVisible(1, false);
        plot.setRenderer(t);
        //plot.setRenderer(renderer);

        return chart;

    }
}
