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


    public LineChart(String applicationTitle, String chartTitle, ArrayList<PacketTracer> tests) {
        super(applicationTitle);
        XYDataset dataset = LoadData(tests);
        JFreeChart chart = createChart(dataset);
        // we put the chart into a panel
        ChartPanel chartPanel = new ChartPanel(chart);
        // default size
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        // add it to our application
        setContentPane(chartPanel);


    }

    private XYDataset LoadData(ArrayList<PacketTracer> tests){
        //final XYSeries seriesNoCache = new XYSeries("NoCache");
        final XYSeries seriesCache = new XYSeries("Average Hops");

        for(int i =0; i <tests.size(); i++)
        {
            //if(tests.get(i).cacheEnabled)
            //{
                seriesCache.add(tests.get(i).cacheSize, tests.get(i).averageHops);
            //}
            /*else{
                seriesNoCache.add(tests.get(i).cacheSize, tests.get(i).averageHops);
            }
*/
        }
        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(seriesCache);
        //dataset.addSeries(seriesNoCache);
        return dataset;

    }

    private JFreeChart createChart(XYDataset dataset) {

        JFreeChart chart = ChartFactory.createXYLineChart("Average Hops per Request with cache size changes",
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
