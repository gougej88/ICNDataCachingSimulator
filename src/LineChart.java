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


    public LineChart(String applicationTitle, String chartTitle, Integer testsize, ArrayList<ArrayList<PacketTracer>> tests, ArrayList<Integer> attacksPerTest) {
        super(applicationTitle);
        XYDataset dataset = LoadData(testsize, attacksPerTest, tests);
        JFreeChart chart = createChart(dataset, applicationTitle);
        // we put the chart into a panel
        ChartPanel chartPanel = new ChartPanel(chart);
        // default size
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        // add it to our application
        setContentPane(chartPanel);


    }

    private XYDataset LoadData(Integer testsize, ArrayList<Integer> attackers, ArrayList<ArrayList<PacketTracer>> tests){
        //final XYSeries seriesNoCache = new XYSeries("NoCache");
        final XYSeries seriesLRUCache = new XYSeries("LRU Cache Average Hops");
        final XYSeries seriesFIFOCache = new XYSeries("FIFO Cache Average Hops");
        final XYSeries seriesRandomCache = new XYSeries("Random Cache Average Hops");
        int testsPerSize= 0;
        double numTestsKept= 0;
        int AttacksPerTest = attackers.size();

        ArrayList<PacketTracer> singleTest = new ArrayList<PacketTracer>();

        //Print space for final numbers to console
        System.out.println();
        System.out.println();
        System.out.println("Final Results");

        //Combine all results
        //Loop on cache type (t)
        for(int t=0; t< tests.size(); t++) {

            //change all values back to 6
            singleTest = tests.get(t);
            double[] max = new double[6*AttacksPerTest];
            double[] min = new double[6*AttacksPerTest];
            double[] result = new double[6*AttacksPerTest];
            int[] numattackers = new int[6*AttacksPerTest];
            Arrays.fill(max,0);
            Arrays.fill(min,10);
            testsPerSize =(singleTest.size()/6)/AttacksPerTest;

                //Loop on number of tests run
                for (int i = 0; i < singleTest.size(); i++) {
                    int x = i / testsPerSize;

                        if (singleTest.get(i).averageHops < min[x])
                            min[x] = singleTest.get(i).averageHops;
                        if (singleTest.get(i).averageHops > max[x])
                            max[x] = singleTest.get(i).averageHops;
                        result[x] += singleTest.get(i).averageHops;
                    numattackers[x] = singleTest.get(i).numAttackers;

                }//end for i

                //Divide the result sums by the number of tests and add to series
                //Loop on tests
                for (int j = 0; j < result.length; j++) {
                    //LRU
                    if(t==0) {
                        seriesLRUCache.add(j*10, result[j] / testsPerSize);
                        seriesLRUCache.add(j*10,max[j]);
                        seriesLRUCache.add(j*10,min[j]);

                        //Print results to the console
                        System.out.println("Average hops for "+ numattackers[j]+" Attackers and LRU with cache size "+(j/attackers.size())*10+" = "+result[j]/testsPerSize);
                        //System.out.println("Max hops for LRU with cache size "+j*10+" = "+max[j]);
                        //System.out.println("Min hops for LRU with cache size "+j*10+" = "+min[j]);
                    }//end if lru cache
                    //FIFO
                    if(t==1) {
                        seriesFIFOCache.add(j*10, result[j] / testsPerSize);
                        seriesFIFOCache.add(j*10,max[j]);
                        seriesFIFOCache.add(j*10,min[j]);
                        //Print results to the console
                        System.out.println("Average hops for "+ numattackers[j]+" Attackers and FIFO with cache size "+(j/attackers.size())*10+" = "+result[j]/testsPerSize);
                        //System.out.println("Max hops for FIFO with cache size "+j*10+" = "+max[j]);
                        //System.out.println("Min hops for FIFO with cache size "+j*10+" = "+min[j]);
                    }//end if fifo cache
                    //Random
                    if(t==2) {
                        seriesRandomCache.add(j*10, result[j] / testsPerSize);
                        seriesRandomCache.add(j*10,max[j]);
                        seriesRandomCache.add(j*10,min[j]);

                        //Print results to the console
                        System.out.println("Average hops for "+ numattackers[j]+" Attackers and Random with cache size "+(j/attackers.size())*10+" = "+result[j]/testsPerSize);
                        //System.out.println("Max hops for Random with cache size "+j*10+" = "+max[j]);
                        //System.out.println("Min hops for Random with cache size "+j*10+" = "+min[j]);
                    }//end if random cache
                }//end for j



        }//end for t

        //Print percentage of tests kept to console
        System.out.println("All results collected had a 70% warm up phase.");


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
        t.setDrawXError(false);
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
