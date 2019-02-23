package changhua.com.showimage;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.ImageView;
import changhua.com.showimage.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        ImageView imageView = (ImageView) findViewById(R.id.image);
        binding.setHandler(new MyButtonHandler(this, imageView));

    }







}
