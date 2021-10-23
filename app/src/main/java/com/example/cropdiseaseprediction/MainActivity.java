package com.example.cropdiseaseprediction;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cropdiseaseprediction.ml.Model;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity {
    private ImageView imgView;
    private Button select, predict;
    private Bitmap img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgView = (ImageView) findViewById(R.id.imageView);
        select = (Button) findViewById(R.id.button);
        predict = (Button) findViewById(R.id.button2);


        select.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View view) {

                                          Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                          intent.setType("image/*");
                                          startActivityForResult(intent, 100);




                                      }
                                  }

        );

        predict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                img = Bitmap.createScaledBitmap(img, 100, 100, true);

                try {
                    Model model = Model.newInstance(getApplicationContext());

                    // Creates inputs for reference.
                    TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 100, 100, 3}, DataType.FLOAT32);
                    TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
                    tensorImage.load(img);
                    ByteBuffer byteBuffer = tensorImage.getBuffer();
                    inputFeature0.loadBuffer(byteBuffer);

                    // Runs model inference and gets result.
                    Model.Outputs outputs = model.process(inputFeature0);
                    TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

                    // Releases model resources if no longer used.
                    model.close();


                    if (outputFeature0.getFloatArray()[0] == 1.0) {
                        openDisease0();


                    }
                    if (outputFeature0.getFloatArray()[1] == 1.0){
                        openDisease1();
                    }
                    if (outputFeature0.getFloatArray()[2] == 1.0){
                        openDisease2();
                    }
                } catch (IOException e) {
                    // TODO Handle the exception
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100)
        {
            imgView.setImageURI(data.getData());

            Uri uri = data.getData();
            try {
                img = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    protected void openDisease0(){
        Intent intent = new Intent(this, Disease0.class);
        startActivity(intent);
    }
    protected void openDisease1(){
        Intent intent = new Intent(this, Disease1.class);
        startActivity(intent);
    }
    protected void openDisease2(){
        Intent intent = new Intent(this, Disease2.class);
        startActivity(intent);
    }
}