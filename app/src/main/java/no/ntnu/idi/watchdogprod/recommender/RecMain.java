package no.ntnu.idi.watchdogprod.recommender;

import android.support.v7.app.ActionBarActivity;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.GenericItemSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

/**
 * Created by Wschive on 08/04/15.
 */
public class RecMain extends ActionBarActivity{

    RecommenderBuilder recommenderBuilder = new RecommenderBuilder() {
        @Override
        public Recommender buildRecommender(DataModel model) throws TasteException {

            ItemSimilarity similarity = new PearsonCorrelationSimilarity(model);
            return new GenericItemBasedRecommender(model, similarity);
        }
    };

}
