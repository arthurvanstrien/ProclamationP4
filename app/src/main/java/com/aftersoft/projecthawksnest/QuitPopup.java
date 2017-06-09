package com.aftersoft.projecthawksnest;

/**
 * Created by Gijs on 1-6-2017.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class QuitPopup extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quitpopup);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*0.43),(int)(height*0.36));
        ImageView saveicon =  (ImageView) this.findViewById(R.id.saveicon);
        TextView savetitle = (TextView) findViewById(R.id.savetitle);
        savetitle.setText("Photo's saved");
        TextView savetext = (TextView) findViewById(R.id.savetext);
        savetext.setText("Your Pictures are saved.");

        Button okayButton = (Button) this.findViewById(R.id.okaybutton);
        okayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);

            }
        });
    }
}
