package com.logicoverflow.fit_bot.Model;

import android.widget.ImageView;
import android.widget.RadioButton;

public class Theme {

   int imageOftheme;
   String nameOftheme;
   String styleOFtheme;
   Boolean isCurretnTheme;
   int id ;
    public Theme(int imageOftheme , String nameOftheme ,int id , String styleOFtheme , Boolean isCurretnTheme) {
        this.imageOftheme = imageOftheme;
        this.nameOftheme = nameOftheme;
        this.id = id;
        this.styleOFtheme = styleOFtheme;
        this.isCurretnTheme = isCurretnTheme;
    }


    public String getNameOftheme() {
        return nameOftheme;
    }

    public void setNameOftheme(String nameOftheme) {
        this.nameOftheme = nameOftheme;
    }

    public int getImageOftheme() {
        return imageOftheme;
    }

    public void setImageOftheme(int imageOftheme) {
        this.imageOftheme = imageOftheme;
    }

    public int getIdOftheme() {
        return id;
    }

    public String getStyleOFtheme() {
        return styleOFtheme;
    }

    public Boolean getIsCurretnTheme() {
        return isCurretnTheme;
    }

    public void setIsCurretnTheme(Boolean curretnTheme) {
        isCurretnTheme = curretnTheme;
    }
}
