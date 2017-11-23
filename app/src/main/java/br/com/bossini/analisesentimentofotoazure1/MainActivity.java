package br.com.bossini.analisesentimentofotoazure1;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.FaceRectangle;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class MainActivity extends FragmentActivity {

    private FaceServiceClient faceServiceClient;
    private static final int  TIRAR_FOTO_REQ = 1;
    private List<Fragment> fragments;
    private FotosPagerAdapter adapter;
    private ViewPager fotosViewPager;
    private ImageButton rightImageButton, leftImageButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tirarFoto();
            }
        });
        fragments = new ArrayList<>();
        adapter = new FotosPagerAdapter(getSupportFragmentManager(), fragments);
        fotosViewPager = (ViewPager) findViewById(R.id.fotosViewPager);
        fotosViewPager.setAdapter(adapter);
        rightImageButton = (ImageButton)findViewById(R.id.rightImageButton);
        leftImageButton = (ImageButton) findViewById(R.id.leftImageButton);
        faceServiceClient = new FaceServiceRestClient(getString(R.string.face_detection_api));
    }

    private void tirarFoto (){
        Intent intent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, TIRAR_FOTO_REQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case TIRAR_FOTO_REQ:
                if (resultCode == Activity.RESULT_OK){
                    new DetectaFaces().execute((Bitmap)data.getExtras().get("data"));
                }
                break;
        }
    }
    public void navigationClick (View view){
        int fotoAtual = fotosViewPager.getCurrentItem();
        switch (view.getId()){
            case R.id.rightImageButton:
                fotosViewPager.setCurrentItem((fotoAtual + 1) % fotosViewPager.getAdapter().getCount());
                break;
            case R.id.leftImageButton:
                if (fotoAtual == 0)
                    fotosViewPager.setCurrentItem(fotosViewPager.getAdapter().getCount() - 1);
                else
                    fotosViewPager.setCurrentItem(fotoAtual - 1);
                break;
        }
    }

    private class DetectaFaces extends AsyncTask <Bitmap, Void, Face[]>{

        private Bitmap fotoOriginal;
        @Override
        protected Face [] doInBackground(Bitmap... bitmaps) {
            this.fotoOriginal = bitmaps[0];
            try{
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                fotoOriginal.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                return faceServiceClient.detect(bais, true, false,null);
            }
            catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(Face[] faces) {
            Bitmap copia = desenharRetangulos(fotoOriginal, faces);
            adicionarAoViewPager (copia);
        }
    }

    private void adicionarAoViewPager (Bitmap fotoOriginal){
        Fragment fragment = new FotoFragment();
        Bundle b = new Bundle ();
        b.putParcelable("data", fotoOriginal);
        fragment.setArguments(b);
        fragments.add(fragment);
        adapter.notifyDataSetChanged();
    }

    private Bitmap desenharRetangulos (Bitmap fotoOriginal, Face [] faces){
        Bitmap copia = fotoOriginal.copy(fotoOriginal.getConfig(), true);
        Canvas canvas = new Canvas(copia);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        int strokeWidth = 2;
        paint.setStrokeWidth(strokeWidth);
        if (faces != null) {
            for (Face face : faces) {
                FaceRectangle faceRectangle = face.faceRectangle;
                canvas.drawRect(faceRectangle.left, faceRectangle.top, faceRectangle.left + faceRectangle.width, faceRectangle.top + faceRectangle.height, paint);
                consultaSentimentos(face);
            }
        }
        return copia;
    }

    private void consultaSentimentos (Face face){
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String s = face.toString();
        RequestBody body = RequestBody.create(JSON, face.toString());
      //      Request request = new Request.Builder()
       //             .url(url)
       //             .post(body)
       //             .build();
       //     Response response = client.newCall(request).execute();
       //     return response.body().string();
    }
}
