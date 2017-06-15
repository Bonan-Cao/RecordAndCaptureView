package tusdk.bonan.recordandcaptureview;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

    RecordCaptureView mRecordCaptureView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecordCaptureView = (RecordCaptureView) findViewById(R.id.ly_recordCaptureView);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mRecordCaptureView.onResume();
    }
}
