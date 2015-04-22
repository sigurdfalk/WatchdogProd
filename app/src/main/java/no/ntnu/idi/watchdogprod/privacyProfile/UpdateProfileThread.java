package no.ntnu.idi.watchdogprod.privacyProfile;

import android.content.res.Configuration;
import android.graphics.Path;

import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.VectorWritable;
import org.apache.mahout.math.function.DoubleDoubleFunction;
import org.apache.mahout.math.function.DoubleFunction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import no.ntnu.idi.watchdogprod.domain.ExtendedPackageInfo;

/**
 * Created by Wschive on 22/04/15.
 */
public class UpdateProfileThread implements Runnable {
    ArrayList<ExtendedPackageInfo> apps;
    Profile profile;
    public UpdateProfileThread(ArrayList<ExtendedPackageInfo> apps){
        this.apps = apps;
        this.profile = Profile.getInstance();
    }
    @Override
    public void run() {
        DenseVector v = new DenseVector();
        for (ExtendedPackageInfo app : apps) {
            v.addAll(app.getVector());
        }
        final int numberOfApps = apps.size();
        DoubleFunction function = new DoubleFunction() {
            @Override
            public double apply(double v) {
                return v/numberOfApps;
            }
        };
        v.assign(function);
        profile.setProfileVector(v);

    }


}
