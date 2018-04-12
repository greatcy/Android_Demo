package com.eli.simplestdemo.download;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

import com.eli.simplestdemo.R;

/**
 * Created by chenjunheng on 2018/4/12.
 * magnet creator
 */

public class MagnetCreator {

    public void createMagnet(Context context, final ICallBack callBack) {
        /*@setView 装入一个EditView
     */
        final EditText editText = new EditText(context);
        AlertDialog.Builder inputDialog =
                new AlertDialog.Builder(context);
        inputDialog.setTitle(context.getString(R.string.add_magnet_title)).setView(editText);
        inputDialog.setPositiveButton(context.getString(R.string.sure),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (callBack != null) {
                            callBack.onCreateMagnet(editText.getText().toString());
                        }
                    }
                }).show();
    }

    public interface ICallBack {
        void onCreateMagnet(String magnetUrl);
    }
}
