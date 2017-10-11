package br.usjt.arqdesis.imagecap;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mukesh.image_processing.ImageProcessor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ImageInputHelper.ImageActionListener {

    private Bitmap bitmap;
    private int contador;
    private CheckBox[] filtros;
    private LinearLayout listFiltros;
    private ImageInputHelper imageInputHelper;
    private ImageView imagem1;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String[] nomeFiltros = {"doInvert", "doGreyScale", "applyGaussianBlur", "createShadow", "applyMeanRemoval", "emboss", "engrave",
                "applyFleaEffect", "applyBlackFilter", "applySnowEffect", "applyReflection"};

        filtros = new CheckBox[nomeFiltros.length];
        listFiltros = (LinearLayout) findViewById(R.id.listFiltrosLayout);
        imagem1 = (ImageView) findViewById(R.id.imagem1);
        contador = 0;

        for (contador = 0; contador < nomeFiltros.length; contador++) {
            filtros[contador] = new CheckBox(getApplicationContext());
            filtros[contador].setId(contador);
            filtros[contador].setText(nomeFiltros[contador]);
            filtros[contador].setVisibility(View.VISIBLE);
            filtros[contador].setTextColor(Color.BLACK);
            filtros[contador].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageProcessor imageProcessor = new ImageProcessor();
                    CheckBox filtro = (CheckBox) view;
                    if (filtro.getText().toString().equals("doInvert")) {
                        bitmap = imageProcessor.doInvert(bitmap);
                    } else if (filtro.getText().toString().equals("doGreyScale")) {
                        bitmap = imageProcessor.doGreyScale(bitmap);
                    } else if (filtro.getText().toString().equals("applyGaussianBlur")) {
                        bitmap = imageProcessor.applyGaussianBlur(bitmap);
                    } else if (filtro.getText().toString().equals("createShadow")) {
                        bitmap = imageProcessor.createShadow(bitmap);
                    } else if (filtro.getText().toString().equals("applyMeanRemoval")) {
                        bitmap = imageProcessor.applyMeanRemoval(bitmap);
                    } else if (filtro.getText().toString().equals("emboss")) {
                        bitmap = imageProcessor.emboss(bitmap);
                    } else if (filtro.getText().toString().equals("engrave")) {
                        bitmap = imageProcessor.engrave(bitmap);
                    } else if (filtro.getText().toString().equals("applyFleaEffect")) {
                        bitmap = imageProcessor.applyFleaEffect(bitmap);
                    } else if (filtro.getText().toString().equals("applyBlackFilter")) {
                        bitmap = imageProcessor.applyBlackFilter(bitmap);
                    } else if (filtro.getText().toString().equals("applySnowEffect")) {
                        bitmap = imageProcessor.applySnowEffect(bitmap);
                    } else if (filtro.getText().toString().equals("applyReflection")) {
                        bitmap = imageProcessor.applyReflection(bitmap);
                    }
                    imagem1.setImageBitmap(bitmap);
                }
            });
            LinearLayout.LayoutParams checkParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            checkParams.setMargins(10, 10, 10, 10);
            listFiltros.addView(filtros[contador], checkParams);
            Log.i("Checkbox", filtros[contador].getText().toString());
            Log.i("numchildsLinearLayout", listFiltros.getChildCount() + "");
        }

        checkAndRequestPermissions();

        imageInputHelper = new ImageInputHelper(this);
        imageInputHelper.setImageActionListener(this);

    }

    public void daGaleria(View view) {
        imageInputHelper.selectImageFromGallery();
    }

    public void daCamera(View view) {
        imageInputHelper.takePhotoWithCamera();
    }

    private boolean checkAndRequestPermissions() {
        int camera = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        int storage = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int loc = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int loc2 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int network = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE);
        int internet = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        //int myLoc = ContextCompat.checkSelfPermission(this, Manifest.permission.LOCATION_HARDWARE);


        List<String> listPermissionsNeeded = new ArrayList<>();

        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
        }
        if (storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (loc2 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);

        }
        if (loc != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (network != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_NETWORK_STATE);
        }
        if (internet != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.INTERNET);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageInputHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onImageSelectedFromGallery(Uri uri, File imageFile) {
        imageInputHelper.requestCropImage(uri, 400, 300, 4, 3); // x:16 y:9  (tamanho máximo | rate)
    }

    @Override
    public void onImageTakenFromCamera(Uri uri, File imageFile) {
        imageInputHelper.requestCropImage(uri, 400, 300, 4, 3);
    }

    @Override
    public void onImageCropped(Uri uri, File imageFile) {
        try {

            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            imagem1.setImageBitmap(bitmap);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
