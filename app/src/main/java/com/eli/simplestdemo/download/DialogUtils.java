package com.eli.simplestdemo.download;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.eli.simplestdemo.R;

/**
 * Created by chenjunheng on 2018/4/12.
 */

public class DialogUtils {
    public static void showConfirmDeleteDLG(Context context,
                                            DialogInterface.OnClickListener confirmListener,
                                            DialogInterface.OnClickListener cancelListener) {
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(context);
        normalDialog.setTitle(context.getString(R.string.dlg_delete_title));
        normalDialog.setMessage(context.getString(R.string.dlg_delete_content));
        normalDialog.setPositiveButton(R.string.dlg_confirm, confirmListener);
        normalDialog.setNegativeButton(R.string.dlg_cancel, cancelListener);
        normalDialog.show();
    }
}
