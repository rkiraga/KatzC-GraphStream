package katz;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.graphstream.algorithm.measure.AbstractCentrality;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

/**
 * Created by Radek2 on 23.04.2018.
 */
public class KatzCentrality extends AbstractCentrality {

    private Graph graph;
    private RealVector katzCentralityVector;
    private double alfa = 0.1;
    private double beta = 1.0;
    private RealVector betas = null;
    private boolean isComputed=false;
    public KatzCentrality(String attribute, NormalizationMode normalize) {
        super(attribute, normalize);
    }

    public void init(Graph graph){
        this.graph = graph;
    }
    public void init(Graph graph, double alfa){
        this.graph = graph;
    }
    public void init(Graph graph, double alfa, double beta){
        this.graph = graph;
        this.beta = beta;
    }
    public void init(Graph graph, double alfa, double[] betas){
        this.graph = graph;
        this.betas = MatrixUtils.createRealVector(betas);
    }
    public void init(Graph graph, double[] betas){
        this.graph = graph;
        this.betas = MatrixUtils.createRealVector(betas);
    }

    public void setAlfa(double alfa){
        this.alfa = alfa;
    }
    public void computeCentrality(){
        this.calculateKatzCentralityVectorIterationMode();
    }

    public Graph getGraphWithKatzCentrality(){
        getKatzCentralityVector();
        setKatzAttribute();
        return graph;
    }

    public void setKatzAttribute(){
        int i=0;
        for(Node node: graph) {
            node.setAttribute("katz", katzCentralityVector.getEntry(i++));
        }
    }

    public RealVector getKatzCentralityVector() {
        if(!this.isComputed){
            this.computeCentrality();
        }
        if(this.getNormalizationMode().equals(NormalizationMode.MAX_1_MIN_0)){

            double max= this.katzCentralityVector.getMaxValue(),
                   min=this.katzCentralityVector.getMinValue();
            double maxMinusMin = max-min;
            for (int i=0; i<this.katzCentralityVector.getDimension(); i++) {
                this.katzCentralityVector.setEntry(i, ( (this.katzCentralityVector.getEntry(i)-min) / maxMinusMin) );
            }
            return katzCentralityVector;

        } else if(this.getNormalizationMode().equals(NormalizationMode.SUM_IS_1)){

            double sum=0;
            for (int i=0; i<this.katzCentralityVector.getDimension(); i++) {
                sum+=this.katzCentralityVector.getEntry(i);
            }
            this.katzCentralityVector = this.katzCentralityVector.mapMultiply((1/sum));
            return katzCentralityVector;
        } else{
            return katzCentralityVector;
        }

    }

    private void calculateKatzCentralityVectorIterationMode(){

        int n = graph.getNodeCount();
        double adjacencyMatrix[][] = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                adjacencyMatrix[i][j] = (graph.getNode(i).hasEdgeBetween(j) ? 1 : 0);
            }
        }

        RealMatrix adj = new Array2DRowRealMatrix(adjacencyMatrix);
        //adj = adj.transpose();

        double[] eigenvalues = null;
        eigenvalues = new Jama.Matrix(adjacencyMatrix).eig().getRealEigenvalues();

        double maxEigenValue=eigenvalues[0];
        for(int i=1; i<n; i++){
            if(maxEigenValue < eigenvalues[i]) maxEigenValue = eigenvalues[i];
        }
        if(maxEigenValue > 0 && alfa==0.1){
            alfa = (1/maxEigenValue) * 0.9;
        }

        double[] d = new double[n];
        for(int i=0; i<n; i++) d[i]=1;

        //x(0) = e~, ~x(t + 1) = αA~x(t) + βe~

        RealVector xi = MatrixUtils.createRealVector(d); //x0=e

        //x(t+1) = alfa * A * x(t) + beta*e
        RealMatrix adjalfa = adj.scalarMultiply(alfa);
        RealVector xiPlus1=null;
        for(int i=1; i<100; i++){

            xiPlus1 = adjalfa.preMultiply(xi); // αA~x(t)
            xiPlus1 = xiPlus1.add(MatrixUtils.createRealVector(d).mapMultiply(beta)); //+ beta*e
            xi = xiPlus1;
            //System.out.println(xiPlus1);
        }
        this.katzCentralityVector=xiPlus1;
        //System.out.println("alfa="+this.alfa);

    }


}
