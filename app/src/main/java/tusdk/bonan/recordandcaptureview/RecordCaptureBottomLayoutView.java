package tusdk.bonan.recordandcaptureview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by bonan on 25/06/2017.
 */
public class RecordCaptureBottomLayoutView extends RelativeLayout {

    private CaptureButton mCaptureButton;

    public RecordCaptureBottomLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initViews();
    }

    private void initViews() {
        mCaptureButton = new CaptureButton(this.getContext(), 160);

        RelativeLayout.LayoutParams btn_capture_param = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        btn_capture_param.addRule(CENTER_IN_PARENT, TRUE);
        btn_capture_param.setMargins(0, 20, 0, 20);

        addView(mCaptureButton, btn_capture_param);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(widthMeasureSpec, 200);
    }
}
