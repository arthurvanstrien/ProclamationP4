package com.aftersoft.projecthawksnest;

/**
 * Created by lars on 08-06-17.
 */

public interface DataTaskListener {

    void onGetDone(Double xAxis, Double yAxis, Double zAxis);
    void hasError();

}
