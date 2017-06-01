package com.aftersoft.projecthawksnest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;

public class QRLoading extends AppCompatActivity {

    protected static final int TIMER_RUNTIME = 10000;
    protected boolean mActive;
    protected ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrloading);

        bar = (ProgressBar) findViewById(R.id.activity_main_progress_bar_id);

        final Thread timerThread = new Thread(){
            @Override
            public void run(){
                mActive = true;
                try {
                    int waited = 0;
                    while (mActive && (waited < TIMER_RUNTIME)) {
                        sleep(200);
                        if (mActive){
                            waited += 200;
                            updateProgess(waited);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    onContinue();
                }
            }
        };
        timerThread.start();
    }

    private void onContinue() {
        System.out.println("Verbinding gelukt!");
    }

    private void updateProgess(int timePassed) {
        if (null != bar){
            final int progress = bar.getMax() * timePassed / TIMER_RUNTIME;
            bar.setProgress(progress);
        }
    }
}
