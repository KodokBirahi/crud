package com.si20b.crud;

import static android.text.TextUtils.isEmpty;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

//class adapter ini digunakan untuk mengatur bagaimana data akan ditampilkan
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements Filterable {

    //deklarasi variable
    private ArrayList<data_mahasiswa> listMahasiswa;
    private Context context;
    private ArrayList<data_mahasiswa> listMahasiswaSearch;
    private Filter setSearch = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<data_mahasiswa> filterMahasiswa = new ArrayList<>();
            if (constraint == null || constraint.length()==0){
                filterMahasiswa.addAll(listMahasiswaSearch);
            }else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (data_mahasiswa item : listMahasiswaSearch){
                    if (item.getNama().toLowerCase().contains(filterPattern)){
                        filterMahasiswa.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filterMahasiswa;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            listMahasiswa.clear();
            listMahasiswa.addAll((List) results.values);
            notifyDataSetChanged();
        }

    };


    public RecyclerViewAdapter(ArrayList<data_mahasiswa> listMahasiswa, Context context) {
        this.listMahasiswa = listMahasiswa;
        this.context = context;
        listener = (ListDataActivity)context;
        this.listMahasiswaSearch = listMahasiswa;
    }

    @Override
    public Filter getFilter() {
        return setSearch;
    }


    //membuat interface
    public interface dataListener{
        void onDeleteData(data_mahasiswa data, int position);
    }

    //deklarasi objek dari interface
    dataListener listener;


    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //membuat view untuk menyiapkan dan memasang layout yang akan digunakan pada recycleview
        View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_design, parent, false);
        return new ViewHolder(V);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewAdapter.ViewHolder holder,
                                 @SuppressLint("RecyclerView") final int position) {
        //mengambil nilai /value yang terdapat pada recycleview berdasarkan posisi tertentu
        final String NIM = listMahasiswa.get(position).getNim();
        final String Nama = listMahasiswa.get(position).getNama();
        final String Orma = listMahasiswa.get(position).getOrma();
        final String Jabatan = listMahasiswa.get(position).getJabatan();
        final String Gender = listMahasiswa.get(position).getGender();
        final String Tglaktif = listMahasiswa.get(position).getTglaktif();
        final String Gambar = listMahasiswa.get(position).getGambar();

        //memasukan nilai/value kedalam view (textview: NIM, Nama, Jeniskelamin, Tujuan, Fakultas)
        holder.NIM.setText("NIM : "+NIM);
        holder.Nama.setText("Nama : "+Nama);
        holder.Orma.setText("Organisasi : "+Orma);
        holder.Jabatan.setText("Jabatan : "+Jabatan);
        holder.Gender.setText("Gender: "+Gender);
        holder.Tglaktif.setText("Tanggal Aktif: "+Tglaktif);

        if(isEmpty(Gambar)) {
            holder.Gambar.setImageResource(R.drawable.ic_school);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(Gambar.trim())
                    .into(holder.Gambar);
        }

        holder.ListItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                final String[] action = {"Update","Delete"};
                AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                alert.setItems(action, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        switch (i){
                            case 0:
                                /*
                                berpindah Activity pada halaman layout updatedata
                                dan mengambil data pada listmahasiswa, berdasarkan posisinya
                                untuk dikirim pada activity selanjutnya
                                 */
                                Bundle bundle = new Bundle();
                                bundle.putString("dataNIM", listMahasiswa.get(position).getNim());
                                bundle.putString("dataNama", listMahasiswa.get(position).getNama());
                                bundle.putString("dataOrma",listMahasiswa.get(position).getOrma());
                                bundle.putString("dataJabatan",listMahasiswa.get(position).getJabatan());
                                bundle.putString("dataGender",listMahasiswa.get(position).getGender());
                                bundle.putString("dataTglaktif",listMahasiswa.get(position).getTglaktif());
                                bundle.putString("data_gambar",listMahasiswa.get(position).getGambar());
                                bundle.putString("getPrimaryKey", listMahasiswa.get(position).getKey());
                                Intent intent = new Intent(v.getContext(), ActivityUpdate.class);
                                intent.putExtras(bundle);
                                context.startActivity(intent);
                                break;

                            case 1:
                                //menggunakan interface untuk mengirim data mahasiswa, yang akan dihapus
                                listener.onDeleteData(listMahasiswa.get(position), position);
                                    break;
                        }
                    }
                });
                                alert.create();
                                alert.show();
                                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return listMahasiswa.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //inisialisasi widget
        private TextView NIM, Nama, Orma, Jabatan, Gender, Tglaktif;
        private ImageView Gambar;
        private LinearLayout ListItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //menginisialisasi view-view yang terpasang pada layout recycleview kita
            NIM = itemView.findViewById(R.id.nim);
            Nama = itemView.findViewById(R.id.nama);
            Orma = itemView.findViewById(R.id.orma);
            Jabatan = itemView.findViewById(R.id.jabatan);
            Gender = itemView.findViewById(R.id.gender);
            Tglaktif = itemView.findViewById(R.id.tglaktif);
            Gambar = itemView.findViewById(R.id.gambar);
            ListItem = itemView.findViewById(R.id.list_item);
        }
    }
}
