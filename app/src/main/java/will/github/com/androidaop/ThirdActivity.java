package will.github.com.androidaop;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.opengl.opengltest.R;
import com.opengl.opengltest.widget.SHSlideLayout;

public class ThirdActivity extends BaseActivity {

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
