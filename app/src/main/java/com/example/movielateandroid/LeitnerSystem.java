package com.example.movielateandroid;

import android.util.Log;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;


public class LeitnerSystem {
    FireStore fireStore;
    LearningSystem learningSystem;
    int consecutiveDays;
    int[] allCategories = {1,2,3,4,5}; //1-5
    ArrayList<String> categoriesToRepeat = new ArrayList<String>();
    String tagDate = "";
    private static final String TAG = "LeitnerSystem";
    ArrayList<Flashcard> pack = new ArrayList<>();

    public LeitnerSystem(LearningSystem learningSystem, int option){
        this.learningSystem = learningSystem;
        prepareTagDate();
        fireStore = new FireStore();
        getDates(option);
    }

    public LeitnerSystem(LearningSystem learningSystem){
        this.learningSystem = learningSystem;
        prepareTagDate();
        fireStore = new FireStore();
        getDates(1);
    }

    void createCollection(final int option){
        if (option == 0){
            categoriesToRepeat.clear();
            categoriesToRepeat.add("0");
        }
            try {
                fireStore.readSingleBoxOfFlashcards(categoriesToRepeat, prepareTagDate(), new FirestoreFlashcardsListener() {
                    @Override
                    public void flashcards(ArrayList arrayList) {
                        for (Object doc:arrayList){
                            pack.add((Flashcard) doc);
                        }

                        fireStore.readRepetition(new FirestoreFlashcardsListener() {
                            @Override
                            public void flashcards(ArrayList arrayList) {
                                if (option != 0){
                                    for (Object doc:arrayList){
                                        pack.add((Flashcard) doc);
                                    }
                                }
                                learningSystem.flashcards(pack);
                            }
                        });



                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

                }

    String prepareTagDate() {
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        try {
            Date date2 = format.parse(currentDate);
            tagDate = dateFormat.format(date2);
            Log.d(TAG, "przed: " + tagDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  tagDate;
    }

    int calculateStreak(String lastLogDate) {
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date date1 = format.parse(lastLogDate);
            Date date2 = format.parse(currentDate);
            consecutiveDays = calculateDifferenceBetweenDays(date1, date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return consecutiveDays;
    }

    int calculateDifferenceBetweenDays(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : "+ endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        System.out.printf(
                "%d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);

        return (int) elapsedDays;
    }

    ArrayList<String> toRepeatPack(){
        for (int i = 0; i< allCategories.length; i++) {
            if (consecutiveDays % allCategories[i] == 0) {
                categoriesToRepeat.add(String.valueOf(allCategories[i]));
            }
        }
        return categoriesToRepeat;
    }

    void getDates(final int option){
        fireStore.readLastLog(new FirestoreDateListener() {
            @Override
            public void lastLogDate(String date) {
                calculateStreak(date);
                toRepeatPack();
                createCollection(option);
                learningSystem.lastLogDate(date);
                learningSystem.consecutiveDays(calculateStreak(date));
            }
        });
    }

    void moveUp(ArrayList <Flashcard> done, int increase) {
        for (int i = 0; i < done.size(); i++) {
            String previousCategory = done.get(i).getCategory();
            String newCategory = String.valueOf(Integer.parseInt(previousCategory) + increase);
            done.get(i).setDate(tagDate);
            if (done.get(i).getCategory().equals("5")){
                done.get(i).setRepeatAgain(false);
            }else {
                if (done.get(i).getRepeatAgain() == true){
                    done.get(i).setRepeatAgain(false);
                }else{
                    done.get(i).setCategory(newCategory);
                }

            }
        }
        fireStore.putRow(done);
        }

    void moveDown(ArrayList <Flashcard> wrong, int decrease){
        for (int i = 0; i < wrong.size(); i++) {
            Log.d(TAG, "moveDown: "+ wrong.get(i).getRepeatAgain());
            String previousCategory = wrong.get(i).getCategory();
            String newCategory = String.valueOf(Integer.parseInt(previousCategory) + decrease);
            if (wrong.get(i).getCategory().equals("0")){

            } else if(wrong.get(i).getCategory().equals("1")){
                wrong.get(i).setDate(tagDate);
                wrong.get(i).setRepeatAgain(true);
            }else {
                if (wrong.get(i).getRepeatAgain() == false){
                    wrong.get(i).setDate(tagDate);
                    wrong.get(i).setRepeatAgain(true);
                    wrong.get(i).setCategory(newCategory);
                } else{

                }



            }
        }
        fireStore.putRow(wrong);
        }

    public void updateFlashcards(ArrayList<Flashcard> done, ArrayList<Flashcard> wrong){
        moveDown(wrong, -1);
        moveUp(done, 1);
    }



//    void moveUp(ArrayList <Flashcard> done, int increase) {
//        for (int i = 0; i < done.size(); i++) {
//            fireStore.deleteRow(done);
//            String previousCategory = done.get(i).getCategory();
//            String newCategory = String.valueOf(Integer.parseInt(previousCategory) + increase);
//            done.get(i).setCategory(newCategory);
//            done.get(i).setDate(tagDate);
//        }
//        fireStore.addRow(done);
//    }

//    void moveDown(ArrayList <Flashcard> wrong, int decrease){
//        for (int i = 0; i < wrong.size(); i++) {
//            fireStore.deleteRow(wrong);
//            String previousCategory = wrong.get(i).getCategory();
//            String newCategory = String.valueOf(Integer.parseInt(previousCategory) + decrease);
//            wrong.get(i).setCategory(newCategory);
//            wrong.get(i).setDate(tagDate);
//        }
//        fireStore.addRow(wrong);
//    }



}
