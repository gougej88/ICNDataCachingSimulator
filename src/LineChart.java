import java.util.*;
import javax.swing.*;
import java.awt.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


/**
 * Created by n00430588 on 10/30/2014.
 */
public class LineChart extends JFrame {


    public LineChart(String applicationTitle, String chartTitle, Integer testsize, ArrayList<PacketTracer> tests) {
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

    private XYDataset LoadData(Integer testsize, ArrayList<PacketTracer> tests){
        //final XYSeries seriesNoCache = new XYSeries("NoCache");
        final XYSeries seriesCache = new XYSeries("LRU Average Hops (Zipfian alpha = 1)");
        double[] result = new double[6];

        //Combine all results
        for(int i =0; i <tests.size(); i++)
        {
                result[tests.get(i).cacheSize/10] += tests.get(i).averageHops;
        }
        //Divide the result sums by the number of tests and add to series
        for(int j = 0; j< result.length; j++)
        {
            seriesCache.add(j*10, result[j]/testsize);
        }
        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(seriesCache);
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

        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(0, true);
        renderer.setSeriesShapesVisible(1, false);
        plot.setRenderer(renderer);

        return chart;

    }
}
