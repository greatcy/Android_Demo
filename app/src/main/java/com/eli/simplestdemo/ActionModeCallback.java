package com.eli.simplestdemo;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Created by chenjunheng on 2018/3/6.
 */

public class ActionModeCallback implements ActionMode.Callback {
    private ActionModeReCallBack actionModeReCallBack;

    public ActionModeCallback(ActionModeReCallBack reCallBack) {
        this.actionModeReCallBack = reCallBack;
    }

    // Called when the action mode is created; startActionMode() was called
    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        // Inflate a menu resource providing context menu items
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.long_press_menu, menu);
        return true;
    }

    // Called each time the action mode is shown. Always called after onCreateActionMode, but
    // may be called multiple times if the mode is invalidated.
    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        this.actionModeReCallBack.onEnterActionMode();
        return false; // Return false if nothing is done
    }

    // Called when the user selects a contextual menu item
    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            default:
                mode.finish(); // Action picked, so close the CAB
                return false;
        }
    }

    // Called when the user exits the action mode
    @Override
    public void onDestroyActionMode(ActionMode mode) {
        this.actionModeReCallBack.onExitActionMode();
    }

    public interface ActionModeReCallBack {
        void onExitActionMode();

        void onEnterActionMode();
    }
}
