package com.si20b.crud;

import static android.text.TextUtils.isEmpty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

public class ActivityUpdate extends AppCompatActivity {

    //dekarasi variable
    private DatePickerDialog datePickerDialog;
    private ProgressBar progressBar;
    private SimpleDateFormat dateFormatter, Formatdatalama;
    private EditText nimBaru, namaBaru, tglaktifBaru;
    private Button update, btn_gantifoto;
    private DatabaseReference database;
    private String getNIM, getNama, getOrma,outputjabatan,cekgolongan,
            cekjeniskelamin,outputgender,getTglaktif;
    private CheckBox Ketua,Anggota;
    private RadioButton Pria,Wanita;
    private String[] ListOrma;
    private Spinner ormaBaru;
    private ImageView fotolama;
    public Uri imageUrl;
    FirebaseStorage storage;
    private StorageReference storageReference;

    private static final int REQUEST_CODE_CAMERA = 1;
    private static final int REQUEST_CODE_GALERI = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        getSupportActionBar().setTitle("Update Data");
        nimBaru = findViewById(R.id.new_nim);
        namaBaru = findViewById(R.id.new_nama);
        ormaBaru = findViewById(R.id.new_orma);
        Ketua = findViewById( R.id.new_ketua);
        Anggota = findViewById(R.id.new_anggota);
        Pria = findViewById(R.id.new_p);
        Wanita = findViewById(R.id.new_w);
        tglaktifBaru = findViewById(R.id.new_tglaktif);
        update = findViewById(R.id.update);

        //Gambar image view
        fotolama = findViewById(R.id.foto_lama);
        //btn ganti foto
        btn_gantifoto = findViewById(R.id.btn_gantifoto);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        storageReference = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance().getReference();
        getData();

        btn_gantifoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getimage();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mendapatkan data mahasiswa yang akan dicek
                getNIM = nimBaru.getText().toString();
                getNama = namaBaru.getText().toString();
                getOrma = ormaBaru.getSelectedItem().toString();
                getTglaktif = tglaktifBaru.getText().toString();

                //mengecek agar tidak ada data yang kosong saat proses update
                if(isEmpty(getNIM) || isEmpty(getNama) || isEmpty(getOrma)
                || outputjabatan==null || outputgender==null || isEmpty(getTglaktif)){
                    Toast.makeText(ActivityUpdate.this, "Data tidak boleh ada yang kosong",
                            Toast.LENGTH_SHORT).show();
                }else {
                    /*
                    menjalankan proses update data.
                    method setter digunakan untuk mendapatkan data baru yang diinputkan user.
                     */
                    //mendapatkan data dari Image View sebagai Bytes
                    progressBar.setVisibility(View.VISIBLE);
                    fotolama.setDrawingCacheEnabled(true);
                    fotolama.buildDrawingCache();
                    Bitmap bitmap = ((BitmapDrawable) fotolama.getDrawable()).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();

                    //mengkompress Bitmap menjadi JPG
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
                    byte[] bytes = stream.toByteArray();

                    //Lokasi Lengkap dimana gambar disimpan
                    String namaFile = UUID.randomUUID()+".jpg";
                    final String pathImage = "foto/"+namaFile;
                    UploadTask uploadTask = storageReference.child(pathImage).putBytes(bytes);
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    data_mahasiswa setMahasiswa = new data_mahasiswa();
                                    setMahasiswa.setNim(nimBaru.getText().toString());
                                    setMahasiswa.setNama(namaBaru.getText().toString());
                                    setMahasiswa.setOrma(ListOrma[ormaBaru.getSelectedItemPosition()]);
                                    setMahasiswa.setJabatan(outputjabatan.trim());
                                    setMahasiswa.setGender(outputgender.trim());
                                    setMahasiswa.setTglaktif(tglaktifBaru.getText().toString());
                                    setMahasiswa.setGambar(uri.toString().trim());
                                    updateMahasiswa(setMahasiswa);
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ActivityUpdate.this, "Update Gagal", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            progressBar.setVisibility(View.VISIBLE);
                            double progress = (100.0 * snapshot.getBytesTransferred())/ snapshot.getTotalByteCount();
                            progressBar.setProgress((int) progress);
                        }
                    });
                }
            }
        });
    }
    private boolean isEmpty(String s){
        return TextUtils.isEmpty(s);
    }
    private void getData(){
        final String getNIM = getIntent().getExtras().getString("dataNIM");
        final String getNama = getIntent().getExtras().getString("dataNama");
        final String getOrma = getIntent().getExtras().getString("dataOrma");
        final String getJabatan = getIntent().getExtras().getString("dataJabatan");
        final String getGender = getIntent().getExtras().getString("dataGender");
        final String getTglaktif = getIntent().getExtras().getString("dataTglaktif");
        final String getGambar =getIntent().getExtras().getString("data_gambar");

        //mengatur tampilan gambar
        if (isEmpty(getGambar)){
            //foto lama = nama imageview
            fotolama.setImageResource(R.drawable.ic_school);
        }else {
            Glide.with(ActivityUpdate.this)
                    .load(getGambar)
                    .into(fotolama);
        }

        Ketua.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    Anggota.setChecked(false);
                    outputjabatan = "Ketua";
                }
            }
        });
        Anggota.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    Ketua.setChecked(false);
                    outputjabatan = "Anggota";
                }
            }
        });

        Pria.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    outputgender = "Pria";
                }
            }
        });
        Wanita.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    outputgender = "Wanita";
                }
            }
        });

        if(getJabatan.trim().toString().equals("Ketua")) {
            Ketua.setChecked(true);
        } else if(getJabatan.trim().toString().equals("Anggota")) {
            Anggota.setChecked(true);
        }

        if (getGender.trim().equals("Pria")){
            Pria.setChecked(true);
        } else if (getGender.trim().equals("Wanita")){
            Wanita.setChecked(true);
        }

        ListOrma = new String[]{"Senat Mahasiswa","Badan Eksekutif Mahasiswa"};
        ArrayAdapter<String> fakultasadapter = new ArrayAdapter<String>(this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,ListOrma);
        ormaBaru.setAdapter(fakultasadapter);
        ormaBaru.setSelection(fakultasadapter.getPosition(getOrma.trim()));

        //Mengatur DatePicker
        dateFormatter = new SimpleDateFormat("dd MMM yyyy");
        tglaktifBaru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
            }
        });

        nimBaru.setText(getNIM);
        namaBaru.setText(getNama);
        tglaktifBaru.setText(getTglaktif);

    }
    private void getimage() {
        Intent imageIntenGallery = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(imageIntenGallery, REQUEST_CODE_GALERI);
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        switch (requestCode){
            case REQUEST_CODE_CAMERA:
                break;
            case REQUEST_CODE_GALERI:
                if (resultCode==RESULT_OK){
                    fotolama.setVisibility(View.VISIBLE);
                    Uri uri = data.getData();
                    fotolama.setImageURI(uri);

                }
                break;
        }
    }

    private void showDateDialog() {
        Calendar calendar = Calendar.getInstance();

        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                Calendar newDate = Calendar.getInstance();
                newDate.set(year,month,dayOfMonth);
                tglaktifBaru.setText(dateFormatter.format(newDate.getTime()));
            }
        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
    private void updateMahasiswa(data_mahasiswa mahasiswa) {
        String getKey = getIntent().getExtras().getString("getPrimaryKey");
        database.child("Admin")
                .child("Mahasiswa")
                .child(getKey)
                .setValue(mahasiswa)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        nimBaru.setText("");
                        namaBaru.setText("");
                        tglaktifBaru.setText("");
                        Toast.makeText(ActivityUpdate.this, "Data Berhasil diubah", Toast.LENGTH_SHORT).show();
                        finish();;
                    }
                });
    }
}