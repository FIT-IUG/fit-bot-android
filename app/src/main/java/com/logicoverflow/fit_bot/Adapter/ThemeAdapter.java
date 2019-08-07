package com.logicoverflow.fit_bot.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.logicoverflow.fit_bot.Model.Theme;
import com.logicoverflow.fit_bot.R;
import com.logicoverflow.fit_bot.Util.Const;

import java.util.ArrayList;

public class ThemeAdapter extends RecyclerView.Adapter<ThemeAdapter.ThemeAdapterViewHolder> {

  ArrayList<Theme> themes ;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditor;

  Context context;

  private int lastSelectedPosition =-1;
  public ThemeAdapter(ArrayList<Theme> themes , Context context){
      this.themes = themes;
      this.context = context;
      sharedPreferences = context.getSharedPreferences(Const.THEME_PREFERENCES, Context.MODE_PRIVATE);
      sharedPreferencesEditor = sharedPreferences.edit();

  }

    @NonNull
    @Override
    public ThemeAdapter.ThemeAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.theme_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;



        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new ThemeAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ThemeAdapter.ThemeAdapterViewHolder holder, final int position) {

               holder.bind(themes.get(position));


        }

    @Override
    public int getItemCount() {
        return themes.size();
    }


    public class ThemeAdapterViewHolder extends RecyclerView.ViewHolder  {


        ImageView imageOftheme;
        RadioButton themeIsselected;
        RelativeLayout relativeLayout;
        TextView nameOftheme;

        public ThemeAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            imageOftheme = itemView.findViewById(R.id.image_of_theme);
            themeIsselected = itemView.findViewById(R.id.is_theme_selected);
            nameOftheme = itemView.findViewById(R.id.name_of_theme);
            //relativeLayout = itemView.findViewById(R.id.list_item);

        }


        void bind(final Theme theme) {

            String currentTheme = sharedPreferences.getString(Const.THEME_SAVED, Const.DEFTHEME);

            if (currentTheme.equals(theme.getStyleOFtheme())) {

                //themeIsselected.setChecked(true);
                lastSelectedPosition = theme.getIdOftheme();
            }

            if (lastSelectedPosition == -1) {
                themeIsselected.setChecked(false);
            } else {
                if (lastSelectedPosition == getAdapterPosition()) {
                    themeIsselected.setChecked(true);
                } else {
                    themeIsselected.setChecked(false);
                }
            }
            nameOftheme.setText(theme.getNameOftheme());
            imageOftheme.setImageResource(theme.getImageOftheme());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    sharedPreferencesEditor.putString(Const.THEME_SAVED, theme.getStyleOFtheme());
                    sharedPreferencesEditor.apply();
                   // sharedPreferencesEditor.commit();

                    themeIsselected.setChecked(true);
                    if (lastSelectedPosition != getAdapterPosition()) {
                        notifyItemChanged(lastSelectedPosition);
                        lastSelectedPosition = getAdapterPosition();
                    }


                }


            });


        }
    }
}
