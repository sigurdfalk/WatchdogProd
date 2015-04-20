package no.ntnu.idi.watchdogprod.recommender;

import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.math.DenseVector;
import org.apache.mahout.common.distance.EuclideanDistanceMeasure;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.SequentialAccessSparseVector;
import org.apache.mahout.math.Vector;

/**
 * Created by Wschive on 15/04/15.
 */
public class VectoredApp {



    RandomAccessSparseVector v1 = new RandomAccessSparseVector();
    RandomAccessSparseVector v2 = new RandomAccessSparseVector();
    EuclideanDistanceMeasure e = new EuclideanDistanceMeasure();
    double d = e.distance(v1,v2);
}
