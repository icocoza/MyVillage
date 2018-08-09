package com.ccz.myvillage.activity.dialog;

public interface IDialogResultListener {
    void onDialogResult(boolean yesOrNo, int type); //true - YES, false - NO or LATER if type 1
}
