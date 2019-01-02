package will.github.com.androidaop;

import android.content.Intent;
import android.graphics.Color;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.opengl.opengltest.R;
import com.opengl.opengltest.widget.SHSlideLayout;

public class SecondActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        ImageView imageView=findViewById(R.id.image);
        imageView.setOnClickListener(v -> {startActivity(new Intent(this,ThirdActivity.class));});


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
