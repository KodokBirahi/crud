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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

public class DaftarMhs extends AppCompatActivity {

    //deklarasi variable
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;
    private RadioGroup rg;
    private RadioButton rb;
    private ProgressBar progressBar;
    private EditText NIM, Nama, Tglaktif;
    private Spinner Orma;
    private Button Simpan, ShowData, getfoto;
    private String getNIM, getNama, getORMA, getJabatan,outputjabatan,getGender,
            getTglaktif, getGambar;
    private TextView Jabatan;
    private CheckBox Ketua, Anggota;
    private ImageView ImageContainer;
    private ArrayList<String> hasilJabatan;
    public Uri imageUrl,uri;
    public Bitmap bitmap;
    private StorageReference reference;

    DatabaseReference getReference;
    FirebaseStorage storage;
    DatabaseReference database;
    StorageReference storageReference;

    private static final int REQUEST_CODE_CAMERA = 1;
    private static final int  REQUEST_CODE_GALLERY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_mhs);

        //Inisialisasi ID (ProgressBar)
        progressBar = findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);

        //Inisialisasi ID (Button)
        Simpan = findViewById(R.id.save);
        ShowData = findViewById(R.id.showdata);

        //Inisialisasi Untuk Upload foto
        getfoto = findViewById(R.id.getfoto);
        ImageContainer = findViewById(R.id.imageContainer);

        //Inisialisasi ID (EditText)
        NIM = findViewById(R.id.nim);
        Nama = findViewById(R.id.nama);
        Orma = findViewById(R.id.orma);

        //inisialisasi ID (Check button)
        Ketua = findViewById(R.id.ketua);
        Anggota = findViewById(R.id.anggota);
        //checkbox untuk golongan darah
        Jabatan = findViewById(R.id.jabatan);
        hasilJabatan = new ArrayList<>();
        Jabatan.setEnabled(false);

        Ketua.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Anggota.setChecked(false);
                    outputjabatan = "Ketua";
                }
            }
        });
        Anggota.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Ketua.setChecked(false);
                    outputjabatan = "Anggota";
                }
            }
        });

        //inisialisasi ID kelamin
        rg = (RadioGroup) findViewById(R.id.gender);

        //mengatur Datepicker
        Tglaktif = findViewById(R.id.tglaktif);
        dateFormatter = new SimpleDateFormat("dd MMM yyyy");
        Tglaktif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
            }
        });

        //Mendapatkan Referensi dari Firebase Storage
        reference = FirebaseStorage.getInstance().getReference();

        //mendapatkan instance dari database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        getReference = database.getReference(); // mendapatkan referensi database

        //membuat fungsi tombol simpan
        Simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //menyimpan data yang diinputkan user kedalam variable
                getNIM = NIM.getText().toString();
                getNama = Nama.getText().toString();
                getORMA = Orma.getSelectedItem().toString();
                //jabatan
                if (outputjabatan == null){
                    getJabatan = null;
                }else {
                    getJabatan = outputjabatan.trim();
                }
                //gender
                if (rb ==null){
                    getGender = null;
                }else {
                    getGender = rb.getText().toString();
                }
                getTglaktif = Tglaktif.getText().toString();
                getGambar = ImageContainer.toString().trim();

                checkUser();
                progressBar.setVisibility(View.VISIBLE);
            }
        });

        ShowData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DaftarMhs.this, ListDataActivity.class);
                startActivity(intent);
            }
        });

        getfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getimage();
            }
        });

    }

    private void showDateDialog() {
        Calendar calendar = Calendar.getInstance();

        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year,month,dayOfMonth);
                Tglaktif.setText(dateFormatter.format(newDate.getTime()));
            }
        }, calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void getimage(){
        Intent imageIntentGallery = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(imageIntentGallery, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        //menghandle hasil data yang diambil dari kamera atau galeri pada ImageVIew
        switch (requestCode){
            case REQUEST_CODE_CAMERA:
                if (resultCode==RESULT_OK){
                    ImageContainer.setVisibility(View.VISIBLE);
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    ImageContainer.setImageBitmap(bitmap);
                }
                break;

            case REQUEST_CODE_GALLERY:
                if (resultCode==RESULT_OK){
                    ImageContainer.setVisibility(View.VISIBLE);
                    uri = data.getData();
                    ImageContainer.setImageURI(uri);
                }
                break;
        }

    }

    private void checkUser() {
        // mengecek apakah ada data yang kosong
        if (isEmpty(getNIM) && isEmpty(getNama) && isEmpty(getORMA)
        && TextUtils.isEmpty(getJabatan) && outputjabatan.equals("") && outputjabatan==null
        && TextUtils.isEmpty(getGender) && TextUtils.isEmpty(getTglaktif) && uri == null){
            //jika ada maka akan menampilkan pesan singkat seperti berikut ini
            Toast.makeText(DaftarMhs.this, "Data tidak boleh kosong", Toast.LENGTH_SHORT).show();
        }else {
            /*
            jika tidak maka data tidak dapat diproses dan menyimpannya ke database
            menyimpan data referensi pada database berdasarkan user id masing masing akun
             */
            //Mendapatkan data dari ImageView sebagai bytes
            ImageContainer.setDrawingCacheEnabled(true);
            ImageContainer.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) ImageContainer.getDrawable()).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            //mengkompres Bitmap menjadi JPG dengan kualitas gambar 100%
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
            byte[] bytes =stream.toByteArray();

            //Lokasi lengkap dimana gambar akan disimpan
            String namaFile = UUID.randomUUID()+".jpg";
            final String pathImage = "gambar/"+namaFile;
            UploadTask uploadTask = reference.child(pathImage).putBytes(bytes);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            getReference.child("Admin").child("Mahasiswa").push()
                                    .setValue(new data_mahasiswa(getNIM, getNama, getORMA,
                                            getJabatan, getGender, getTglaktif, uri.toString().trim()))
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            //peristiwa ini terjadi saat user berhasil menyimpan datanya kedalam database
                                            NIM.setText("");
                                            Nama.setText("");
                                            Tglaktif.setText("");
                                            Toast.makeText(DaftarMhs.this,"data tersimpan", Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    });

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(DaftarMhs.this, "Uploading Gagal", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    progressBar.setVisibility(View.VISIBLE);
                    double progress = (100.0 * snapshot.getBytesTransferred())/ snapshot.getTotalByteCount();
                    progressBar.setProgress((int) progress);
                }
            });
            //jika tidak maka data dapat diproses dan menyimpan pada database
            //Menyimpan data referensi pada Database berdasarkan User ID dari masing-masing Akun

        }
    }
    public void rbclick(View v){
        int radiobuttonid = rg.getCheckedRadioButtonId();
        rb =(RadioButton)findViewById(radiobuttonid);
    }
}