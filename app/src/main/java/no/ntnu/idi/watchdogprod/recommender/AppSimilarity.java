package no.ntnu.idi.watchdogprod.recommender;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

import java.util.Collection;

/**
 * Created by Wschive on 08/04/15.
 */
public class AppSimilarity implements ItemSimilarity {
    @Override
    public double itemSimilarity(long l, long l2) throws TasteException {
        return 0;
    }

    @Override
    public double[] itemSimilarities(long l, long[] longs) throws TasteException {
        return new double[0];
    }

    @Override
    public long[] allSimilarItemIDs(long l) throws TasteException {
        return new long[0];
    }

    @Override
    public void refresh(Collection<Refreshable> refreshables) {

    }
}
