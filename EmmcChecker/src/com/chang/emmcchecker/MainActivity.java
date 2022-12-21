package com.chang.emmcchecker;

import com.chang.emmcchecker.R;
import android.app.Activity;
import android.os.ServiceManager;
import android.os.Bundle;
import android.os.IEmmcDoctorService;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity implements OnClickListener {
    private final static String LOG_TAG = "EmmcChecker";

    private IEmmcDoctorService emmcDoctor = null;

    private EditText valueText = null;
    private Button readButton = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        emmcDoctor = IEmmcDoctorService.Stub.asInterface(
                ServiceManager.getService("EmmcDoctorService"));

        valueText = (EditText)findViewById(R.id.edit_value);
        readButton = (Button)findViewById(R.id.button_read);

        readButton.setOnClickListener(this);
        Log.i(LOG_TAG, "Hello Activity Created");
    }

    @Override
    public void onClick(View v) {
        if(v.equals(readButton)) {
            try {
                int val = emmcDoctor.getMlcLife();
                String text = String.valueOf(val);
                valueText.setText(text);
            } catch (RemoteException e) {
                Log.e(LOG_TAG, "Remote Exception while reading value from device.");
            }		
        }
    }
}
