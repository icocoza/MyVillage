package com.ccz.myvillage.activity.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.ccz.myvillage.R;

import java.util.List;

public class AlertManager {

    public static void showYes(@NonNull Context ctx, @Nullable String title, @NonNull String message, @Nullable final IDialogResultListener listener)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        if(title != null)
            builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(ctx.getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(listener!=null)
                            listener.onDialogResult(true, 0);
                    }
                });
        builder.show();
    }

    public static void showYesNo(@NonNull Context ctx, @Nullable String title, @NonNull String message, @NonNull final IDialogResultListener listener)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        if(title != null)
            builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(ctx.getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onDialogResult(true, 0);
                    }
                });
        builder.setNegativeButton(ctx.getString(R.string.no),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onDialogResult(false, 0);
                    }
                });
        builder.show();
    }

    public static void showYesNoLater(@NonNull Context ctx, @Nullable String title, @NonNull String message, @NonNull final IDialogResultListener listener)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        if(title != null)
            builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(ctx.getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onDialogResult(true, 0);
                    }
                });
        builder.setNegativeButton(ctx.getString(R.string.no),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onDialogResult(false, 0);
                    }
                });
        builder.setNeutralButton(ctx.getString(R.string.later),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onDialogResult(false, 1
                        );
                    }
                });

        builder.show();
    }

    public static void showList(@NonNull Context ctx, @Nullable String title, @NonNull List<String> list, @NonNull final IDialogListResultListener listener ) {
        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(
                ctx, // Context
                android.R.layout.simple_list_item_single_choice, // Layout
                list // List
        );
        showList(ctx, title, arrayAdapter, listener);
    }

    public static void showList(@NonNull Context ctx, @Nullable String title, @NonNull ArrayAdapter arrayAdapter, @NonNull final IDialogListResultListener listener ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        if(title != null)
            builder.setTitle(title);
        builder.setSingleChoiceItems(arrayAdapter, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onItemSelected(which);
            }
        });

        builder.setPositiveButton(ctx.getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onDialogResult(true, 0);
            }
        });
        builder.setNegativeButton(ctx.getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onDialogResult(false, 0);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
