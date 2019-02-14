package il.co.wearabledevices.mudrasdkreference;

import MudraAndroidSDK.Feature;
import MudraAndroidSDK.GestureType;
import MudraAndroidSDK.Mudra;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import model.Graph;
import android.content.Context;
import android.content.res.Resources;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    public static final String SNC1 = "SNC1", SNC2 = "SNC2", SNC3 = "SNC3";
    // Sensor Colors
    public static final int SNC_1_COLOR = ColorTemplate.rgb("#1b7696");
    public static final int SNC_2_COLOR = ColorTemplate.rgb("#26a9d7");
    public static final int SNC_3_COLOR = ColorTemplate.rgb("#66c3e4");
    public static final int MIDDLE_TAP_ICON_BLUE = R.drawable.ic_mid_tap_blue_v1;
    public static final int MIDDLE_TAP_ICON_GREY = R.drawable.ic_mid_tap_grey_v1;
    public static final int INDEX_ICON_BLUE = R.drawable.ic_index_blue_v1;
    public static final int INDEX_ICON_GREY = R.drawable.ic_index_grey_v1;
    public static final int THUMB_ICON_BLUE = R.drawable.ic_thumb_blue_v1;
    public static final int THUMB_ICON_GREY = R.drawable.ic_thumb_grey_v1;
    public static final int ENTERIES_NUM = 512;

    int mScreenWidth, mScreenHeight;
    float mAirMousePosX, mAirMousePosY;
    final float HSPEED = 0.7f;
    final float VSPEED = 1.0f;
    private FloatingActionButton mFab;


    private Mudra mMudra;
    private ImageView mRecognizeTapImageView, mRecognizeIndexImageView, mRecognizeThumbImageView;

    private ConstraintLayout mCubeLayout;
    private Context mContext =this;
    final   String LICENSE = "Feature::RawData";
    private GLSurfaceView mIMUsurface;
    private OpenGLRenderer mGLRenderer;
    private Graph mSncGraph;
    private LineChart mSignalsChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Mudra.requestAccessLocationPermissions(this);
        // Required permission for Mudra - note we do not access any of your files/locationj!
        // Location is required for bluetooth,
        // Storage is required for reading gesture calibration file saved during callibration
        requestPermissions(new String[]{"android.permission.ACCESS_FINE_LOCATION",
                                       "android.permission.ACCESS_COARSE_LOCATION",
                                       "android.permission.READ_EXTERNAL_STORAGE",
                                       "android.permission.WRITE_EXTERNAL_STORAGE"},
                                       1);
        setContentView(R.layout.activity_main);
        mCubeLayout = findViewById(R.id.cubeLayout);
        mSignalsChart = findViewById(R.id.signalsChart);
        mSncGraph = new Graph(mSignalsChart);
        mSncGraph.setYaxis(1.1f, -1.1f, 1.1f, -1.1f);
        mSncGraph.addLine(SNC1, SNC_1_COLOR, ENTERIES_NUM, 1f);
        mSncGraph.addLine(SNC2, SNC_2_COLOR, ENTERIES_NUM, 1f);
        mSncGraph.addLine(SNC3, SNC_3_COLOR, ENTERIES_NUM, 1f);
        mRecognizeTapImageView = findViewById(R.id.recognizeTap_imageView);
        mRecognizeIndexImageView = findViewById(R.id.recognizeIndex_imageView);
        mRecognizeThumbImageView = findViewById(R.id.recognizeThumb_imageView);
        mScreenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        mScreenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        mAirMousePosX = mScreenWidth / 2.0f;
        mAirMousePosY = mScreenHeight / 2.0f;
        mFab = findViewById(R.id.fab);
        initCube();
        mMudra =  Mudra.autoConnectPaired(mContext);
        if (mMudra !=null) {
            mMudra.setLicense(Feature.RawData, LICENSE);
            mMudra.setOnGestureReady(onGestureReady);
            mMudra.setOnFingertipPressureReady(onFingertipPressureReady);
            mMudra.setOnSncReady(onSncReady);
            mMudra.setOnAirMousePositionChanged(OnAirMousePositionChanged);
            mMudra.setOnImuQuaternionReady(onImuQuaternionReady);
            mMudra.setOnDeviceStatusChanged(onDeviceStatusChanged);
            mMudra.onBatteryLevelChanged(onBatteryLevelChanged);
        }
    }

    Mudra.OnImuQuaternionReady onImuQuaternionReady = new Mudra.OnImuQuaternionReady() {
        @Override
        public void run(float[] floats) {
            mGLRenderer.setRotationQuaternion(floats);
            mIMUsurface.requestRender();
        }
    };

    Mudra.OnGestureReady onGestureReady = new Mudra.OnGestureReady() {
        @Override
        public void run(final GestureType gestureType) {
            switch (gestureType) {
                case Tap:
                    mRecognizeTapImageView.setImageDrawable(ContextCompat.getDrawable(mContext, MIDDLE_TAP_ICON_BLUE));
                    mRecognizeIndexImageView.setImageDrawable(ContextCompat.getDrawable(mContext, INDEX_ICON_GREY));
                    mRecognizeThumbImageView.setImageDrawable(ContextCompat.getDrawable(mContext, THUMB_ICON_GREY));
                    break;
                case Index:
                    mRecognizeTapImageView.setImageDrawable(ContextCompat.getDrawable(mContext, MIDDLE_TAP_ICON_GREY));
                    mRecognizeIndexImageView.setImageDrawable(ContextCompat.getDrawable(mContext, INDEX_ICON_BLUE));
                    mRecognizeThumbImageView.setImageDrawable(ContextCompat.getDrawable(mContext, THUMB_ICON_GREY));
                    break;
                case Thumb:
                    mRecognizeTapImageView.setImageDrawable(ContextCompat.getDrawable(mContext, MIDDLE_TAP_ICON_GREY));
                    mRecognizeIndexImageView.setImageDrawable(ContextCompat.getDrawable(mContext, INDEX_ICON_GREY));
                    mRecognizeThumbImageView.setImageDrawable(ContextCompat.getDrawable(mContext, THUMB_ICON_BLUE));
                    break;
            }
        }
            /*
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                  //  if (mRecognizeIndexImageView == null || mRecognizeTapImageView == null || mRecognizeThumbImageView == null)
                //        initConnectedDevices();
                    switch (gestureType) {
                        case Tap:
                            mRecognizeTapImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_mid_tap_blue_v1));
                            mRecognizeIndexImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_index_grey_v1));
                            mRecognizeThumbImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_thumb_grey_v1));
                            break;
                        case Index:
                            mRecognizeTapImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_mid_tap_grey_v1));
                            mRecognizeIndexImageView.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.ic_index_blue_v1));
                            mRecognizeThumbImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_thumb_grey_v1));
                            break;
                        case Thumb:
                            mRecognizeTapImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_mid_tap_grey_v1));
                            mRecognizeIndexImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_index_grey_v1));
                            mRecognizeThumbImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_thumb_blue_v1));
                            break;
                    }
                }
            });
        }*/
    };

    Mudra.OnFingertipPressureReady onFingertipPressureReady = new Mudra.OnFingertipPressureReady() {

        @Override
        public void run(final float v) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                        ((ProgressBar) findViewById(R.id.pressureBar)).setProgress((int) (v * 1000));
                }
            });
        }
    };

    Mudra.OnSncReady onSncReady = new Mudra.OnSncReady() {
        @Override

        public void run(float[] floats) {
            final int dataSize=18;
            mSncGraph.drawLine(SNC1, Arrays.copyOfRange(floats, 0, dataSize));
            mSncGraph.drawLine(SNC2, Arrays.copyOfRange(floats, dataSize, dataSize * 2));
            mSncGraph.drawLine(SNC3, Arrays.copyOfRange(floats, dataSize * 2, dataSize * 3));
        }
    };



    Mudra.OnAirMousePositionChanged OnAirMousePositionChanged = new Mudra.OnAirMousePositionChanged() {
        @Override
        public void run(float[] floats) {

            mAirMousePosX += floats[0] * mScreenWidth * HSPEED;
            mAirMousePosY += floats[1] * mScreenHeight * VSPEED;
            mAirMousePosX = Clamp(mAirMousePosX, 0, mScreenWidth);
            mAirMousePosY = Clamp(mAirMousePosY, 0, mScreenHeight);

            runOnUiThread(new Thread(new Runnable() {
                public void run() {
                    mFab.setX(mAirMousePosX);
                    mFab.setY(mAirMousePosY);
                }
            }));
        }
    };


    Mudra.OnDeviceStatusChanged onDeviceStatusChanged = new Mudra.OnDeviceStatusChanged() {
        @Override
        public void run(boolean b) {
            if(b)
            {
                runOnUiThread(new Thread(new Runnable() {
                    public void run() {
                        TextView device_name= findViewById(R.id.txtDevicesNumber);
                        device_name.setText(mMudra.getBluetoothDevice().getAddress());
                    }
                }));
            }
        }
    };

    Mudra.OnBatteryLevelChanged onBatteryLevelChanged = new Mudra.OnBatteryLevelChanged() {
        @Override
        public void run(int i) {
            runOnUiThread(new Thread(new Runnable() {
                public void run() {
                    TextView txtBattery=findViewById(R.id.txtBatLevel);
                    txtBattery.setText(mMudra.getBatteryLevel() + "%");
                }
            }));
        }
    };

    float Clamp(float v, float lo, float hi) {
        float res = v <= lo ? lo : v >= hi ? hi : v;
        return res;
    }

    private void initCube() {
        Log.d("initCube()", "MainActivity: initCube()");
        if (mIMUsurface == null) {
            mIMUsurface = new GLSurfaceView(this);
            mIMUsurface.setEGLContextClientVersion(1);
            mGLRenderer = new OpenGLRenderer();
            mIMUsurface.setRenderer(mGLRenderer);
            mIMUsurface.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
            mCubeLayout.addView(mIMUsurface);
        }
    }

}
