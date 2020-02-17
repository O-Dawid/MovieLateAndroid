package com.example.movielateandroid;

public class Flashcard {
    String eng, pl;
    String category;
    String date;
    Boolean repeatAgain = false;

    public Boolean getRepeatAgain() {
        return repeatAgain;
    }

    public void setRepeatAgain(Boolean repeatAgain) {
        this.repeatAgain = repeatAgain;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEng() {
        return eng;
    }

    public void setEng(String eng) {
        this.eng = eng;
    }

    public String getPl() {
        return pl;
    }

    public void setPl(String pl) {
        this.pl = pl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
