package com.aftersoft.projecthawksnest;

/**
 * Created by lars on 08-06-17.
 */

public interface DataTaskListener {

    void onDataReceived(Double xAxis, Double yAxis, Double zAxis);
    void onExceptionThrown();

}
