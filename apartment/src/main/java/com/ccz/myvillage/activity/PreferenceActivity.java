package com.ccz.myvillage.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ccz.myvillage.R;
import com.ccz.myvillage.common.Preferences;

public class PreferenceActivity extends AppCompatActivity {
    ImageView ivList, ivCard;
    boolean initialViewType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);

        ((TextView)findViewById(R.id.tvNick)).setText(Preferences.get(this, Preferences.USERNAME));

        ivList = (ImageView)findViewById(R.id.ivListType);
        ivCard = (ImageView)findViewById(R.id.ivCardType);
        ivList.setSelected(false);
        ivCard.setSelected(false);

        boolean isListType = Preferences.getBool(this, Preferences.VIEWTYPE);
        if(isListType)
            ivList.setSelected(true);
        else
            ivCard.setSelected(true);
        initialViewType = isListType;
    }

    @Override
    public void onBackPressed() {
        if(initialViewType != Preferences.getBool(this, Preferences.VIEWTYPE))
            setResult(1);
        else
            setResult(0);
        finish();
    }

    public void onClickExitSetting(View view) {
        onBackPressed();
    }

    public void onClickListType(View view) {
        Preferences.setBool(this, Preferences.VIEWTYPE, true);
        ivList.setSelected(true);
        ivCard.setSelected(false);
    }

    public void onClickCardType(View view) {
        Preferences.setBool(this, Preferences.VIEWTYPE, false);
        ivList.setSelected(false);
        ivCard.setSelected(true);
    }

    public void onClickChangeName(View view) {
        Toast.makeText(this, "Not Implemented!!!!", Toast.LENGTH_SHORT).show();
    }
}
