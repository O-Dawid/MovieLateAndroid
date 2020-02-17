package com.example.movielateandroid;

import android.util.Log;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import androidx.annotation.NonNull;

public class FireStore {
	FirebaseFirestore database;
	ArrayList<Flashcard> listOfFlashcards, repetition;
	int loadedCategory = 1;
	private static final String TAG = "FireStore";

	public FireStore() {
		database = FirebaseFirestore.getInstance();
		listOfFlashcards = new ArrayList<>();
		repetition = new ArrayList<>();
	}

	public void readLastLog(final FirestoreDateListener firestoreDateListener) {
		DocumentReference docRef = database.collection("Log").document("DScVoOeQBWAXkladcR42");
		docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
			@Override
			public void onComplete(@NonNull Task<DocumentSnapshot> task) {
				if (task.isSuccessful()) {
					DocumentSnapshot document = task.getResult();
					if (document.exists()) {
						firestoreDateListener.lastLogDate(String.valueOf(document.getData().get("LastLog")));
					} else {
						Log.d(TAG, "No such document");
					}
				} else {
					Log.d(TAG, "get failed with ", task.getException());
				}
			}
		});

	}


	void readRepetition(final FirestoreFlashcardsListener firestoreFlashcardsListener){
		database.collection("Flashcards")
				.whereEqualTo("repeatAgain", true)
				.get()
				.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
					@Override
					public void onComplete(@NonNull Task<QuerySnapshot> task) {
						int i=0;
						if (task.isSuccessful()) {

							for (QueryDocumentSnapshot document : task.getResult()) {
								addNewFlashcard(document);
								i++;

							}
						}
						if (i == task.getResult().size()){
							firestoreFlashcardsListener.flashcards(repetition);
						}
					}

					private void addNewFlashcard(QueryDocumentSnapshot document) {
						Flashcard flashcard = new Flashcard();
						flashcard.setEng(document.getData().get("eng").toString());
						flashcard.setPl(document.getData().get("pl").toString());
						flashcard.setCategory(document.getData().get("category").toString());
						flashcard.setDate(document.getData().get("date").toString());
						flashcard.setRepeatAgain((Boolean) document.getData().get("repeatAgain"));
						repetition.add(flashcard);
					}
				});
	}

	void readSingleBoxOfFlashcards(final ArrayList<String> categoriesNeedRepeat, final String date, final FirestoreFlashcardsListener firestoreFlashcardsListener) throws InterruptedException, ExecutionException {
		for (final String category : categoriesNeedRepeat) {
			database.collection("Flashcards")
					.whereEqualTo("category", category)
					.get()
					.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
						@Override
						public void onComplete(@NonNull Task<QuerySnapshot> task) {
							if (task.isSuccessful()) {
								for (QueryDocumentSnapshot document : task.getResult()) {
/////////////////// WARUNKI PRZERZUCIC DO LEITNERSYSTEM ///////////////////////////////////
									if (document.getData().get("date").toString().equals(date) && document.getData().get("category").toString().equals("0")){
										addNewFlashcard(document);
									} else if(document.getData().get("date").toString().equals(date)){

									}else{
										addNewFlashcard(document);
										Log.d(TAG, document.getId() + " => " + document.getData());
									}
/////////////////// WARUNKI PRZERZUCIC DO LEITNERSYSTEM ///////////////////////////////////
								}
							}
							if (loadedCategory == categoriesNeedRepeat.size()) {
								Log.d(TAG, "CZEKY IF ");
								firestoreFlashcardsListener.flashcards(listOfFlashcards);
							}
							loadedCategory++;
							}

						private void addNewFlashcard(QueryDocumentSnapshot document) {
							Flashcard flashcard = new Flashcard();
							flashcard.setEng(document.getData().get("eng").toString());
							flashcard.setPl(document.getData().get("pl").toString());
							flashcard.setCategory(document.getData().get("category").toString());
							flashcard.setDate(document.getData().get("date").toString());
							flashcard.setRepeatAgain((Boolean) document.getData().get("repeatAgain"));
							listOfFlashcards.add(flashcard);
						}
					});

		}
	}

	public void putRow(ArrayList<Flashcard> arrayList){
		for (int i = 0; i < arrayList.size(); i++) {
			final String category = arrayList.get(i).getCategory();
			final String date = arrayList.get(i).getDate();
			final Boolean repeatAgain = arrayList.get(i).getRepeatAgain();
			database.collection("Flashcards")
					.whereEqualTo("eng", arrayList.get(i).getEng())
					.get()
					.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
						@Override
						public void onComplete(@NonNull Task<QuerySnapshot> task) {
							if (task.isSuccessful()) {
								for (QueryDocumentSnapshot document : task.getResult()) {
									DocumentReference changedFlashcard = database.collection("Flashcards").document(document.getId());
									changedFlashcard
											.update("category", category, "date", date, "repeatAgain", repeatAgain)
											.addOnSuccessListener(new OnSuccessListener<Void>() {
												@Override
												public void onSuccess(Void aVoid) {
													Log.d(TAG, "DocumentSnapshot successfully updated!");
												}
											})
											.addOnFailureListener(new OnFailureListener() {
												@Override
												public void onFailure(@NonNull Exception e) {
													Log.w(TAG, "Error updating document", e);
												}
											});
								}
							} else {
								Log.d(TAG, "Error getting documents: ", task.getException());
							}
						}
					});
		}
	}

	public void addRow(ArrayList<Flashcard> arrayList) {
		for (int i = 0; i < arrayList.size(); i++) {
			String category = arrayList.get(i).getCategory();
			database.collection(category)
					.add(arrayList.get(i))
					.addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
						@Override
						public void onSuccess(DocumentReference documentReference) {
						}
					})
					.addOnFailureListener(new OnFailureListener() {
						@Override
						public void onFailure(@NonNull Exception e) {
							Log.w(TAG, "Error adding document", e);
						}
					});
		}
	}

	public void deleteRow(ArrayList<Flashcard>arrayList) {
		for (int i = 0; i < arrayList.size(); i++) {
			final String category = arrayList.get(i).getCategory();
			database.collection(category)
					.whereEqualTo("eng", arrayList.get(i).getEng())
					.get()
					.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
						@Override
						public void onComplete(@NonNull Task<QuerySnapshot> task) {
							if (task.isSuccessful()) {
								for (QueryDocumentSnapshot document : task.getResult()) {
									database.collection(category).document(document.getId())
											.delete()
											.addOnSuccessListener(new OnSuccessListener<Void>() {
												@Override
												public void onSuccess(Void aVoid) {
													Log.d(TAG, "DocumentSnapshot successfully deleted!");
												}
											})
											.addOnFailureListener(new OnFailureListener() {
												@Override
												public void onFailure(@NonNull Exception e) {
													Log.w(TAG, "Error deleting document", e);
												}
											});
								}
							} else {
								Log.d(TAG, "Error getting documents: ", task.getException());
							}
						}
					});
		}
	}

//	void readSingleBoxOfFlashcards(ArrayList<String> categoriesNeedRepeat, final FirestoreFlashcardsListener firestoreFlashcardsListener) throws InterruptedException, ExecutionException {
//
//
//		String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
//		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
//
//		for (final String category : categoriesNeedRepeat) {
//			try {
//				Date date2 = format.parse(currentDate);
//
//				database.collection(category)
//						.whereLessThan("date", dateFormat.format(date2))
//						.get()
//						.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//							@Override
//							public void onComplete(@NonNull Task<QuerySnapshot> task) {
//								if (task.isSuccessful()) {
//									for (QueryDocumentSnapshot document : task.getResult()) {
//
//										Flashcard flashcard = new Flashcard();
//										flashcard.setEng(document.getData().get("eng").toString());
//										flashcard.setPl(document.getData().get("pl").toString());
//										flashcard.setCategory(category);
//										listOfFlashcards.add(flashcard);
//
//
//										Log.d("ELKO", document.getId() + " => " + document.getData());
//									}
//								} else {
//									Log.d("ELKO", "Error getting documents: ", task.getException());
//								}
//								if (loadedCategory == sizeOfRepeation) {
////									FirestoreFlashcardsListener.flashcards(listOfFlashcards);
//									firestoreFlashcardsListener.flashcards(listOfFlashcards);
//								}
//							}
//						});
//
//			} catch (ParseException e) {
//				e.printStackTrace();
//			}
//
//		}
//
//	}

}
