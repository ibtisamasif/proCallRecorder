package lvc.pro.com.pro;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.callrecorder.pro.R;

import lvc.pro.com.pro.constants.Constants;
import lvc.pro.com.pro.service.CallDetectionService;


public class DialogActivity extends Activity {
    AlertDialog.Builder builder;
    AlertDialog mDialog;
    private TextView mYes, mNo, mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = this.getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        setContentView(R.layout.activity_dialog);
        mYes = (TextView) findViewById(R.id.dialog_text_yes);
        mNo = (TextView) findViewById(R.id.dialog_text_no);
        mTitle = (TextView) findViewById(R.id.text_title);
        if (getIntent().getExtras() != null) {
            if (getIntent().getBooleanExtra(Constants.sRECORDING_STARTED, false)) {
                mTitle.setText(getString(R.string.recording_started));
                mNo.setVisibility(View.GONE);
                mYes.setText(R.string.text_ok);
            } else {
                mTitle.setText(getString(R.string.error_while_uploading_to_cloud));
                mNo.setVisibility(View.GONE);
                mYes.setText(R.string.text_ok);
            }
        } else {
            // if (getIntent().getBooleanExtra(Constants.sRECORDING_STARTED, false)) {
            //    mTitle.setText(getString(R.string.recording_started));
            //    mNo.setVisibility(View.GONE);
            //     mYes.setText(R.string.text_ok);
            //  } else {
            mTitle.setText(getString(R.string.question_save_recording));
            // }
        }
        mYes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mYes.getText().toString().equalsIgnoreCase(getString(R.string.text_yes))) {
                    Toast.makeText(DialogActivity.this, getString(R.string.recording_saved), Toast.LENGTH_SHORT).show();
                }
                DialogActivity.this.finish();
            }
        });
        mNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CallDetectionService.deleteAudioFile(getApplicationContext());
                Toast.makeText(DialogActivity.this, getString(R.string.recording_not_saved), Toast.LENGTH_SHORT).show();
                DialogActivity.this.finish();
            }
        });
    }

}
