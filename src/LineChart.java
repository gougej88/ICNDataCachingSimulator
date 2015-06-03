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


    public LineChart(String applicationTitle, String chartTitle, Integer cacheSizesTested, Integer cacheSizeIncrement, ArrayList<ArrayList<PacketTracer>> tests, ArrayList<Integer> attacksPerTest) {
        super(applicationTitle);
        XYDataset dataset = LoadData(cacheSizesTested, cacheSizeIncrement, attacksPerTest, tests);
        JFreeChart chart = createChart(dataset, applicationTitle);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);


    }

    private XYDataset LoadData(Integer cacheSizesTested, Integer cacheSizeIncrement, ArrayList<Integer> attackers, ArrayList<ArrayList<PacketTracer>> tests){
        final XYSeries seriesLRUCache = new XYSeries("LRU Cache Average Hops");
        final XYSeries seriesFIFOCache = new XYSeries("FIFO Cache Average Hops");
        final XYSeries seriesRandomCache = new XYSeries("Random Cache Average Hops");
        int testsPerSize= 0;
        double numTestsKept= 0;
        int AttacksPerTest = attackers.size();

        ArrayList<PacketTracer> singleTest = new ArrayList<PacketTracer>();

        //Print space for final numbers to console
        System.out.println();
        System.out.println("Final Results");

        //Combine all results
        //Loop on cache type (t)
        for(int t=0; t< tests.size(); t++) {

            //change all values back to 6
            singleTest = tests.get(t);
            double[] max = new double[cacheSizesTested*AttacksPerTest];
            double[] min = new double[cacheSizesTested*AttacksPerTest];
            double[] result = new double[cacheSizesTested*AttacksPerTest];
            int[] numattackers = new int[cacheSizesTested*AttacksPerTest];
            Arrays.fill(max,0);
            //Fill the min array with values that are as large as the most number of hops a test can take. Starting with 10
            Arrays.fill(min,10);
            testsPerSize =(singleTest.size()/cacheSizesTested)/AttacksPerTest;

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
                        seriesLRUCache.add(j*cacheSizeIncrement, result[j] / testsPerSize);
                        seriesLRUCache.add(j*cacheSizeIncrement,max[j]);
                        seriesLRUCache.add(j*cacheSizeIncrement,min[j]);

                        //Print results to the console
                        System.out.println("Average hops for "+ numattackers[j]+" Attackers and LRU with cache size "+(j/attackers.size())*cacheSizeIncrement+" = "+result[j]/testsPerSize);
                        System.out.println("Max hops for LRU with cache size "+(j/attackers.size())*cacheSizeIncrement+" = "+max[j]);
                        System.out.println("Min hops for LRU with cache size "+(j/attackers.size())*cacheSizeIncrement+" = "+min[j]);
                    }//end if lru cache
                    //FIFO
                    if(t==1) {
                        seriesFIFOCache.add(j*cacheSizeIncrement, result[j] / testsPerSize);
                        seriesFIFOCache.add(j*cacheSizeIncrement,max[j]);
                        seriesFIFOCache.add(j*cacheSizeIncrement,min[j]);
                        //Print results to the console
                        System.out.println("Average hops for "+ numattackers[j]+" Attackers and FIFO with cache size "+(j/attackers.size())*cacheSizeIncrement+" = "+result[j]/testsPerSize);
                        System.out.println("Max hops for FIFO with cache size "+(j/attackers.size())*cacheSizeIncrement+" = "+max[j]);
                        System.out.println("Min hops for FIFO with cache size "+(j/attackers.size())*cacheSizeIncrement+" = "+min[j]);
                    }//end if fifo cache
                    //Random
                    if(t==2) {
                        seriesRandomCache.add(j*cacheSizeIncrement, result[j] / testsPerSize);
                        seriesRandomCache.add(j*cacheSizeIncrement,max[j]);
                        seriesRandomCache.add(j*cacheSizeIncrement,min[j]);

                        //Print results to the console
                        System.out.println("Average hops for "+ numattackers[j]+" Attackers and Random with cache size "+(j/attackers.size())*cacheSizeIncrement+" = "+result[j]/testsPerSize);
                        System.out.println("Max hops for Random with cache size "+(j/attackers.size())*cacheSizeIncrement+" = "+max[j]);
                        System.out.println("Min hops for Random with cache size "+(j/attackers.size())*cacheSizeIncrement+" = "+min[j]);
                    }//end if random cache
                }//end for j



        }//end for t

        //Print percentage of tests kept to console
        System.out.println("All results collected had a 70% warm up phase.");


        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(seriesLRUCache);
        dataset.addSeries(seriesFIFOCache);
        dataset.addSeries(seriesRandomCache);

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

        XYErrorRenderer t = new XYErrorRenderer();
        t.setBaseLinesVisible(true);
        t.setBaseShapesVisible(false);
        t.setDrawYError(true);
        t.setDrawXError(false);
        t.setSeriesPaint(0, Color.black);
        t.setSeriesPaint(1, Color.BLUE);
        t.setSeriesPaint(2, Color.red);
        plot.setRenderer(t);

        return chart;

    }
}
