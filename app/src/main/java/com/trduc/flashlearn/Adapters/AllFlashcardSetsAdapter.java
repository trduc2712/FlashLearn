package com.trduc.flashlearn.Adapters;

import static android.content.Intent.getIntent;
import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.trduc.flashlearn.Controllers.ChangeFlashcardSetsNameActivity;
import com.trduc.flashlearn.Controllers.CreateFlashcardsActivity;
import com.trduc.flashlearn.Controllers.LearnActivity;
import com.trduc.flashlearn.Controllers.MainActivity;
import com.trduc.flashlearn.Controllers.SearchActivity;
import com.trduc.flashlearn.Models.FlashcardSets;
import com.trduc.flashlearn.R;

import java.util.ArrayList;

public class AllFlashcardSetsAdapter extends BaseAdapter {

    final ArrayList<FlashcardSets> flashcardSetsList;
    FirebaseFirestore db;
    FirebaseAuth auth;

    public AllFlashcardSetsAdapter(ArrayList<FlashcardSets> flashcardSetsList) {
        this.flashcardSetsList = flashcardSetsList;
    }

    @Override
    public int getCount() {
        return flashcardSetsList.size();
    }

    @Override
    public Object getItem(int position) {
        return flashcardSetsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            view = View.inflate(parent.getContext(), R.layout.flashcard_sets, null);
        } else {
            view = convertView;
        }

        FlashcardSets flashcardSets = (FlashcardSets) getItem(position);
        Button bFlashcardSetsName = view.findViewById(R.id.bFlashcardSetsName);
        bFlashcardSetsName.setText(String.format("%s", flashcardSets.getName()));

        SharedPreferences preferences = parent.getContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String choice = preferences.getString("choice", "");

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        bFlashcardSetsName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                Intent intent;

                switch (choice) {
                    case "Learn flashcard sets":
                        intent = new Intent(context, LearnActivity.class);
                        intent.putExtra("flashcardSetsId", flashcardSets.getId());
                        context.startActivity(intent);
                        break;
                    case "Change flashcard sets's name":
                        intent = new Intent(context, ChangeFlashcardSetsNameActivity.class);
                        intent.putExtra("flashcardSetsName", flashcardSets.getName());
                        intent.putExtra("flashcardSetsId", flashcardSets.getId());
                        context.startActivity(intent);
                        break;
                    case "Delete a flashcard sets":
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setView(R.layout.dialog_delete_flashcard_sets);

                        final AlertDialog dialog = builder.create();

                        dialog.show();

                        Button bYes = dialog.findViewById(R.id.bYes);
                        Button bNo = dialog.findViewById(R.id.bNo);

                        System.out.println("Ban dang muon xoa " + flashcardSets.getId());
                        System.out.println("Ban dang muon xoa " + flashcardSets.getName());

                        bYes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deleteAllFlashcards(flashcardSets.getId());
                                deleteFlashcardSet(flashcardSets.getId());
                                ((Activity) view.getContext()).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        flashcardSetsList.remove(position);
                                        notifyDataSetChanged();
                                    }
                                });
                                System.out.println("Da xoa");
                                dialog.dismiss();
                            }
                        });

                        bNo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        break;
                    case "Search a flashcard sets":
                        intent = new Intent(context, SearchActivity.class);
                        context.startActivity(intent);
                        break;
                }
            }
        });

        return view;
    }

    private void deleteFlashcardSet(String flashcardSetsId) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            db.collection("users")
                    .document(currentUser.getEmail())
                    .collection("flashcard_sets")
                    .document(flashcardSetsId)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }
    }

    private void deleteAllFlashcards(String flashcardSetsId) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            db.collection("users")
                    .document(currentUser.getEmail())
                    .collection("flashcard_sets")
                    .document(flashcardSetsId)
                    .collection("flashcards")
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                document.getReference().delete();
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }
    }


}
