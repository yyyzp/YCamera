package will.github.com.androidaop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.opengl.opengltest.R;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
