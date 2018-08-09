package com.ccz.myvillage.activity.dialog;

public interface IDialogListResultListener {
    void onDialogResult(boolean yesOrNo, int type); //true - YES, false - NO or LATER if type 1
    void onItemSelected(int which);
}
