package com.si20b.crud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;


public class ListDataActivity extends AppCompatActivity implements RecyclerViewAdapter.dataListener {
    //deklarasi variable untuk recycleview
    private RecyclerView recyclerView;
//    private RecyclerView.Adapter adapter;
    private RecyclerViewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    //deklarasi variable database reference dan arraylist dengan parameter class model kita.
    private DatabaseReference reference;
    private ArrayList<data_mahasiswa> dataMahasiswa;

    private FloatingActionButton fab;
    //Deklarasi Variabel untuk SearchView
    private EditText searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_data);

        recyclerView = findViewById(R.id.datalist);
        fab = findViewById(R.id.fab);

        GetData("");
        searchView =findViewById(R.id.cari);
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().isEmpty()){
                    GetData(s.toString());
                }else {
                    adapter.getFilter().filter(s);
                }
            }
        });

        MyRecycleView();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListDataActivity.this, DaftarMhs.class);
                startActivity(intent);
            }
        });
    }

    private void GetData(String data){
        Toast.makeText(getApplicationContext(), "Mohon tunggu sebentar...", Toast.LENGTH_LONG).show();
        //mendapatkan referensi database
        reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Admin").child("Mahasiswa")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //inisialisasi arraylist
                        dataMahasiswa = new ArrayList<>();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            //mapping data pada datasnapshot kedalam objek mahasiswa
                            data_mahasiswa mahasiswa = snapshot.getValue(data_mahasiswa.class);

                            //mengambil primary key, digunakan untuk proses update dan delete
                            mahasiswa.setKey(snapshot.getKey());
                            dataMahasiswa.add(mahasiswa);
                        }

                        // inisialisasi adapter dan data mahasiswa dalam bentuk array
                        adapter = new RecyclerViewAdapter(dataMahasiswa, ListDataActivity.this);

                        //memasang adapter dalam recycle view
                        recyclerView.setAdapter(adapter);

                        Toast.makeText(getApplicationContext(), "Data berhasil dimuat", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        /*
                        kode ini akan dijalankan ketika ada error dan
                        pengambilan data error tersebut lalu memprint error nya ke logcat
                         */
                        Toast.makeText(getApplicationContext(),"Data gagal dimuat!", Toast.LENGTH_LONG).show();
                        Log.e("MyListActivity", databaseError.getDetails()+" "+databaseError.getMessage());
                    }
                });
    }

    private void MyRecycleView() {
        //menggunakan layout manager dan membuat list secara vertical
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        //membuat underline pada setiap item didalam list
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.line));
        recyclerView.addItemDecoration(itemDecoration);
    }

    @Override
    public void onDeleteData(data_mahasiswa data, int position) {
        /*
        kode ini akan dipanggil ketika method ondeletedata
        dipanggil dari adaoter pada recycleview melalui interface.
        kemudia akan menghapus data berdasarkan primary key dari data tersebut
        jika berhasil maka akan memunculkan toast
         */
        if (reference!= null) {
            reference.child("Admin")
                    .child("Mahasiswa")
                    .child(data.getKey())
                    .removeValue()
                    .addOnSuccessListener(new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {
                            Toast.makeText(ListDataActivity.this, "Data berhasil dihapus", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}