package com.example.library;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BookAdapter extends FirebaseRecyclerAdapter<Books, BookAdapter.BookViewHolder> {
    private ArrayList<String> addBooksList, endBookList;
    ProgressBar progressBar;
    private RecyclerView rvBooks;

    public BookAdapter(@NonNull FirebaseRecyclerOptions<Books> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull BookViewHolder holder, int position, @NonNull Books books) {
        String title = books.getBook_name();
        holder.textViewTitle.setText(books.getBook_name());
        holder.textViewAuthor.setText(books.getAuthor());
        holder.textViewCategory.setText(books.getCategory());
        holder.imageViewBook.setImageResource(R.mipmap.library_icon_foreground);
        holder.imageButtonAdded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference ref = database.getReference("users/Aley/endBook");
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Veri değiştiğinde bu metot çağırılır
                        List<String> data = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String value = snapshot.getValue(String.class);
                            data.add(value);

                        }
                        endBookList = (ArrayList<String>) data;
                        endBookList.add(title);
//                        addBooksList =(ArrayList<String>) data;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase", "Veri okuma hatası: " + error.getMessage());
                    }
                });
                if (!endBookList.contains(title)) {
                    FirebaseDatabase.getInstance().getReference().child("users").child("Aley").child("endBook").push().setValue(title);
                } else if (addBooksList.contains(title)) {
                    //

                }

            }
        });
        holder.imageButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference ref = database.getReference("users/Aley/addBook");
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Veri değiştiğinde bu metot çağırılır
                        List<String> data = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String value = snapshot.getValue(String.class);
                            data.add(value);
                        }

                        addBooksList = (ArrayList<String>) data;
                        addBooksList.add((title));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase", "Veri okuma hatası: " + error.getMessage());
                    }
                });

                if(!addBooksList.contains(title)){
                    FirebaseDatabase.getInstance().getReference().child("users").child("Aley").child("addBook").push().setValue(title);
                }else if(endBookList.contains(title)){
                    //
                }



//                 }
//                else
//                    FirebaseDatabase.getInstance().getReference().child("users").child("Aley").child("addBook").setValue('"'+addBooksList.toString()+'"');
            }
        });


        holder.imageButtonComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View commentContext = LayoutInflater.from(view.getContext()).inflate(R.layout.row_books_comment, null, false);
                EditText commentInputLayout = commentContext.findViewById(R.id.commentInput);
                ViewGroup parent = (ViewGroup) commentInputLayout.getParent();

                if (parent != null) {
                    parent.removeView(commentInputLayout);
                }

                AlertDialog.Builder ad = new AlertDialog.Builder(view.getContext());
                ad.setView(commentInputLayout);
                ad.setTitle("Yorum");
                ad.setMessage("Lütfen Yorumunuzu giriniz...");

                ad.setPositiveButton("Yorum yap", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(view.getContext(), "Okey anahtar değil " + commentInputLayout.getText().toString(), Toast.LENGTH_SHORT).show();
                        //firebase e gönderme komutları...
                        String inputComment = commentInputLayout.getText().toString();
                    }
                });
                ad.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(view.getContext(), "İşlem iptal edildi", Toast.LENGTH_SHORT).show();
                    }
                });
                ad.create().show();
            }
        });

        Glide.with(holder.imageViewBook.getContext())
                .load(books.getBook_image())
                .into(holder.imageViewBook);

    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        loadBookAddAndAdded();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_books, parent, false);
        return new BookViewHolder(view);
    }

    public class BookViewHolder extends RecyclerView.ViewHolder {

        ImageView imageViewBook;
        TextView textViewTitle, textViewAuthor, textViewCategory;
        ImageButton imageButtonAdd, imageButtonAdded, imageButtonComment;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);

            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBarId);

            imageViewBook = (ImageView) itemView.findViewById(R.id.imageViewBook);
            textViewTitle = (TextView) itemView.findViewById(R.id.textViewtitle);
            textViewAuthor = (TextView) itemView.findViewById(R.id.textViewAuthor);
            textViewCategory = (TextView) itemView.findViewById(R.id.textViewCategory);
            imageButtonAdd = (ImageButton) itemView.findViewById(R.id.imageButtonAdd);
            imageButtonAdded = (ImageButton) itemView.findViewById(R.id.imageButtonAdded);
            imageButtonComment = (ImageButton) itemView.findViewById(R.id.imageButtonComment);

        }
    }

    protected void loadBookAddAndAdded() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("users/Aley/addBook");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Veri değiştiğinde bu metot çağırılır
                List<String> data = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String value = snapshot.getValue(String.class);
                    data.add(value);
                }

                addBooksList = (ArrayList<String>) data;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Veri okuma hatası: " + error.getMessage());
            }
        });
        ref = database.getReference("users/Aley/endBook");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Veri değiştiğinde bu metot çağırılır
                List<String> data = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String value = snapshot.getValue(String.class);
                    data.add(value);
                    endBookList = (ArrayList<String>) data;
                }

//                        addBooksList =(ArrayList<String>) data;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Veri okuma hatası: " + error.getMessage());
            }
        });
    }


}


