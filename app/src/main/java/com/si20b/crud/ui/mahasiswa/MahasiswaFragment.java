package com.si20b.crud.ui.mahasiswa;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.si20b.crud.DaftarMhs;
import com.si20b.crud.R;

public class MahasiswaFragment extends Fragment {

    public Button btn_show;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_mahasiswa, container, false);

        btn_show = root.findViewById(R.id.btn_show);

        btn_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), DaftarMhs.class);
                startActivity(intent);
            }
        });

        return root ;
    }
}