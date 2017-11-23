package br.com.bossini.analisesentimentofotoazure1;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;


public class FotoFragment extends Fragment{
    public FotoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        View view = inflater.inflate(R.layout.fragment_foto, container, false);
        ImageView fotoImageView = view.findViewById(R.id.fotoImageView);
        TextView sentimentoNaFotoTextView = view.findViewById(R.id.sentimentoNaFotoTextView);
        fotoImageView.setImageBitmap((Bitmap)arguments.get("data"));
        sentimentoNaFotoTextView.setText("It works");
        return view;
    }
}
