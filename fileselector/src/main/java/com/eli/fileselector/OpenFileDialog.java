package com.eli.fileselector;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OpenFileDialog extends AlertDialog.Builder {
    public static final String ANDROID_STORAGE = "ANDROID_STORAGE";
    public static final String DEFAULT_STORAGE_PATH = "/storage";
    public static final Pattern DEFAULT_STORAGE_PATTERN = Pattern.compile("\\w\\w\\w\\w-\\w\\w\\w\\w");

    public static final String[] PERMISSIONS_RO = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
    public static final String[] PERMISSIONS_RW = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public static final String UP = "[..]";
    public static final String ROOT = "/";

    public enum DIALOG_TYPE {
        FILE_DIALOG,
        FOLDER_DIALOG,
        BOOTH
    }

    protected File currentPath;
    protected TextView title;
    protected TextView message;
    protected ListView listView;
    protected FilenameFilter filenameFilter;
    protected int folderIcon;
    protected int fileIcon;
    protected int btFileIcon;
    protected FileAdapter adapter;
    protected DialogInterface.OnShowListener onshow;
    protected DialogInterface.OnClickListener neutral;

    protected int paddingLeft;
    protected int paddingRight;
    protected int paddingBottom;
    protected int paddingTop;
    protected int iconSize;
    protected Runnable changeFolder;

    // file / folder readonly dialog selection or output directory? also shows readonly folder tooltip.
    protected boolean readonly = false;
    // allow select files, or just select directory
    protected DIALOG_TYPE type = DIALOG_TYPE.BOOTH;

    protected Button positive; // enable / disable OK

    // cache folders, keep folder visible, when android shows none
    protected static Map<File, Set<File>> cache = new TreeMap<>();

    protected static void cache(Context context) {
        cache(context.getExternalCacheDir());
        if (Build.VERSION.SDK_INT >= 19) {
            File[] ff = context.getExternalCacheDirs();
            for (File f : ff) {
                cache(f);
            }
        }
    }

    protected static void cache(File path) {
        while (path != null && path.isFile()) // skip file links, go up folder
            path = path.getParentFile();
        if (path == null)
            return;
        File old = path;
        path = path.getParentFile();
        while (path != null) {
            TreeSet<File> list = new TreeSet<>();
            list.add(old);
            Set<File> tmp = cache.put(path, list);
            if (tmp != null) {
                for (File f : tmp) {
                    list.add(f);
                }
            }
            old = path;
            path = path.getParentFile();
        }
    }

    protected static List<File> cache(File path, FilenameFilter filter) {
        Set<File> list = null;
        File[] ff = path.listFiles();
        if (ff != null) {
            list = new TreeSet<>(Arrays.asList(ff));
        }
        if (list == null)
            list = new TreeSet<>();
        Set<File> old = cache.get(path);
        if (old != null) {
            for (File f : old) {
                if (f.exists()) { // purge cache from non existen files
                    list.add(f);
                }
            }
        }
        cache.put(path, list);

        ArrayList<File> files = new ArrayList<>();
        for (File f : list) {
            String s = f.getName();
            if ((filter == null) || filter.accept(path, s))
                files.add(f);
        }
        return files;
    }

    public static boolean isFile(File f) {
        return !isDirectory(f);
    }

    public static boolean isDirectory(File f) {
        if (f.isDirectory())
            return true;
        if (cache.get(f) != null)
            return true;
        return false;
    }

    public static boolean isBTFile(File f) {
        if (f != null && f.getName().endsWith(".torrent"))
            return true;
        return false;
    }

    protected static class SortFiles implements Comparator<File> {
        @Override
        public int compare(File f1, File f2) {
            if (isDirectory(f1) && isFile(f2))
                return -1;
            else if (isFile(f1) && isDirectory(f2))
                return 1;
            else
                return f1.getPath().compareTo(f2.getPath());
        }
    }

    public static File getLocalInternal(Context context) {
        return context.getFilesDir();
    }

    public static File[] getLocalExternals(Context context, boolean readonly) {
        return ContextCompat.getExternalFilesDirs(context, "");
    }

    public class StorageAdapter implements ListAdapter {
        ArrayList<File> list = new ArrayList<>();

        public StorageAdapter() {
            File ext = Environment.getExternalStorageDirectory();
            cache(ext);
            ext = getLocalInternal(getContext());
            add(ext);
            File data = new File(getContext().getApplicationInfo().dataDir);
            File datae = getContext().getExternalCacheDir();
            if (datae != null)
                datae = datae.getParentFile();
            if (Build.VERSION.SDK_INT >= 19) {
                File[] ff = getLocalExternals(getContext(), readonly);
                if (ff != null) {
                    for (File f : ff) {
                        if (f == null)
                            continue; // if storage unmounted null file here

                        cache(f);

                        {
                            ArrayList<File> pp = new ArrayList<>();
                            File a = f;
                            StatFs stat = new StatFs(f.getPath());
                            File p = f;
                            while (f != null) {
                                pp.add(f);
                                StatFs fs = new StatFs(f.getPath());
                                if (fs.getTotalBytes() != stat.getTotalBytes()) {
                                    add(p); // add sdcard root
                                }
                                p = f;
                                f = f.getParentFile();
                            }
                            if (!readonly) { // help user find writable root if algorithm above failed
                                for (int i = pp.size() - 1; i >= 0; i--) {
                                    p = pp.get(i);
                                    if (data != null && p.getAbsolutePath().startsWith(data.getAbsolutePath())) { // skip default /storage/.../data
                                        continue;
                                    }
                                    if (datae != null && p.getAbsolutePath().startsWith(datae.getAbsolutePath())) { // skip default /storage/.../data
                                        continue;
                                    }
                                    if (add(p))
                                        break; // add first root
                                }
                            }
                            if (data != null && a.getAbsolutePath().startsWith(data.getAbsolutePath())) { // skip default /storage/.../files
                                continue;
                            }
                            if (datae != null && a.getAbsolutePath().startsWith(datae.getAbsolutePath())) { // skip default /storage/.../files
                                continue;
                            }
                            add(a);
                        }
                    }
                }
            }
            String path = System.getenv(OpenFileDialog.ANDROID_STORAGE);
            if (path == null || path.isEmpty())
                path = OpenFileDialog.DEFAULT_STORAGE_PATH;
            File storage = new File(path);
            File[] ff = storage.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    String name = file.getName();
                    Matcher m = OpenFileDialog.DEFAULT_STORAGE_PATTERN.matcher(name);
                    return m.matches();
                }
            });
            if (ff == null)
                return;
            for (File f : ff) {
                add(f);
            }
        }

        boolean add(File f) {
            if (f == null)
                return false;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getAbsolutePath().equals(f.getAbsolutePath()))
                    return true;
            }
            list.add(f);
            return true;
        }

        public int find(File c) {
            for (int i = 0; i < list.size(); i++) {
                File f = list.get(i);
                if (c.getAbsolutePath().startsWith(f.getAbsolutePath()))
                    return i;
            }
            return -1;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return true;
        }

        @Override
        public boolean isEnabled(int position) {
            return true;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {
        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());

                final LinearLayout titlebar = new LinearLayout(getContext());
                titlebar.setOrientation(LinearLayout.HORIZONTAL);
                titlebar.setPadding(paddingLeft, 0, paddingRight, 0);
                titlebar.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                TextView title = (TextView) inflater.inflate(android.R.layout.simple_list_item_1, null);
                title.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                title.setPadding(0, paddingTop, 0, paddingBottom);
                title.setTag("text");

                PathMax textMax = new PathMax(getContext(), title);
                textMax.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                titlebar.addView(textMax);

                convertView = titlebar;
            }

            File f = list.get(position);
            TextView title = (TextView) convertView.findViewWithTag("text");
            title.setText(f.getAbsolutePath());

            return convertView;
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return list.isEmpty();
        }
    }

    public class FileAdapter extends ArrayAdapter<File> {
        int selectedIndex = -1;
        int colorSelected;
        int colorTransparent;
        File currentPath;

        public FileAdapter(Context context) {
            super(context, android.R.layout.simple_list_item_1);

            if (Build.VERSION.SDK_INT >= 23) {
                colorSelected = getContext().getResources().getColor(android.R.color.holo_blue_dark, getContext().getTheme());
                colorTransparent = getContext().getResources().getColor(android.R.color.transparent, getContext().getTheme());
            } else {
                colorSelected = getContext().getResources().getColor(R.color.colorAccent);
                colorTransparent = getContext().getResources().getColor(android.R.color.transparent);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getView(position, convertView, parent);
            File file = getItem(position);
            if (view != null) {
                view.setText(file.getName());
                if (isDirectory(file)) {
                    setDrawable(view, getDrawable(folderIcon));
                } else if (isBTFile(file)) {
                    setDrawable(view, getDrawable(btFileIcon));
                } else {
                    setDrawable(view, getDrawable(fileIcon));
                }
                if (selectedIndex == position)
                    view.setBackgroundColor(colorSelected);
                else
                    view.setBackgroundColor(colorTransparent);
            }
            return view;
        }

        protected void setDrawable(TextView view, Drawable drawable) {
            if (view != null) {
                if (drawable != null) {
                    drawable.setBounds(0, 0, iconSize, iconSize);
                    view.setCompoundDrawables(drawable, null, null, null);
                } else {
                    view.setCompoundDrawables(null, null, null, null);
                }
            }
        }

        public void scan(File dir) {
            cache(dir);
            updateSelected(-1);
            currentPath = dir;

            if (dir.exists() && !isDirectory(dir)) { // file or symlink
                currentPath = currentPath.getParentFile();
            }

            if (currentPath == null) {
                currentPath = new File(ROOT);
            }

            clear();

            List<File> files = cache(currentPath, filenameFilter);

            if (files == null)
                return;

            if (Build.VERSION.SDK_INT < 11) {
                for (File f : files) {
                    add(f);
                }
            } else {
                addAll(files);
            }

            sort(new SortFiles());

            if (dir.exists() && !isDirectory(dir)) { // file or symlink
                updateSelected(getPosition(dir));
            }

            notifyDataSetChanged();
        }
    }

    public static class EditTextDialog extends AlertDialog.Builder {
        EditText input;

        public EditTextDialog(Context context) {
            super(context);

            input = new EditText(getContext());

            input.setSingleLine(true);

            setPositiveButton(new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
            setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    hide();
                }
            });

            setView(input);
        }

        public AlertDialog.Builder setPositiveButton(final DialogInterface.OnClickListener listener) {
            return setPositiveButton(android.R.string.ok, listener);
        }

        @Override
        public AlertDialog.Builder setPositiveButton(int textId, final DialogInterface.OnClickListener listener) {
            return super.setPositiveButton(textId, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    listener.onClick(dialog, which);
                    hide();
                }
            });
        }

        @Override
        public AlertDialog.Builder setPositiveButton(CharSequence text, final DialogInterface.OnClickListener listener) {
            return super.setPositiveButton(text, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    listener.onClick(dialog, which);
                    hide();
                }
            });
        }

        void hide() {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(input.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

        @Override
        public AlertDialog create() {
            AlertDialog d = super.create();

            Window w = d.getWindow();
            w.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

            return d;
        }

        public void setText(String s) {
            input.setText(s);
            input.setSelection(s.length());
        }

        public String getText() {
            return input.getText().toString();
        }
    }

    public OpenFileDialog(Context context, DIALOG_TYPE type) {
        super(context);

        this.type = type;
        currentPath = Environment.getExternalStorageDirectory();
        paddingLeft = dp2px(14);
        paddingRight = dp2px(14);
        paddingTop = dp2px(14);
        paddingBottom = dp2px(14);
        iconSize = dp2px(30);

        folderIcon = R.drawable.ic_fs_folder;
        fileIcon = R.drawable.ic_fs_file;
        btFileIcon = R.drawable.ic_fs_bt_file;

        cache(context);
    }

    public OpenFileDialog(Context context, DIALOG_TYPE type, boolean readonly) {
        this(context, type);
        setReadonly(readonly);
    }

    public int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getContext().getResources().getDisplayMetrics());
    }

    void toast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public AlertDialog create() {
        LayoutInflater inflater = LayoutInflater.from(getContext());

        int dp2 = ThemeUtils.dp2px(getContext(), 2);

        // title layout
        final LinearLayout titlebar = new LinearLayout(getContext());
        titlebar.setOrientation(LinearLayout.HORIZONTAL);
        titlebar.setPadding(paddingLeft, 0, paddingRight, 0);
        titlebar.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        title = (TextView) inflater.inflate(android.R.layout.simple_list_item_1, null);
        title.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        title.setPadding(0, paddingTop, dp2, paddingBottom);

        PathMax textMax = new PathMax(getContext(), title);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        lp.gravity = Gravity.CENTER;
        textMax.setLayoutParams(lp);
        titlebar.addView(textMax);

        setCustomTitle(titlebar);

        // main view, linearlayout
        final LinearLayout main = new LinearLayout(getContext());
        main.setOrientation(LinearLayout.VERTICAL);
        main.setMinimumHeight(getLinearLayoutMinHeight(getContext()));
        main.setPadding(paddingLeft, 0, paddingRight, 0);

        // add toolbar (UP / NEWFOLDER)
        {
            LinearLayout toolbar = new LinearLayout(getContext());
            toolbar.setOrientation(LinearLayout.HORIZONTAL);
            toolbar.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            {
                TextView textView = (TextView) LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, null);
                textView.setText(UP);
                Drawable icon = getDrawable(folderIcon);
                icon.setBounds(0, 0, iconSize, iconSize);
                textView.setCompoundDrawables(icon, null, null, null);
                textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        File parentDirectory = currentPath;
                        if (isDirectory(parentDirectory) || !parentDirectory.exists()) { // allow virtual up
                            parentDirectory = parentDirectory.getParentFile();
                        } else {
                            parentDirectory = parentDirectory.getParentFile();
                            if (parentDirectory != null)
                                parentDirectory = parentDirectory.getParentFile();
                        }

                        if (parentDirectory == null)
                            parentDirectory = new File(ROOT);

                        if (parentDirectory != null) {
                            currentPath = parentDirectory;
                            rebuildFiles();
                        }
                    }
                });
                toolbar.addView(textView);
            }

            main.addView(toolbar);

            message = new TextView(getContext());
            message.setGravity(Gravity.CENTER);
            message.setBackgroundColor(0x22222222);
            message.setVisibility(View.GONE);
            main.addView(message);
        }

        // ADD FILES LIST
        listView = new ListView(getContext());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                File file = adapter.getItem(index);

                currentPath = file;

                if (isDirectory(file)) {
                    rebuildFiles();
                } else {
                    switch (type) {
                        case FILE_DIALOG:
                        case BOOTH:
                            if (index != adapter.selectedIndex) {
                                updateSelected(index);
                            } else {
                                currentPath = file.getParentFile();
                                updateSelected(-1);
                            }
                            adapter.notifyDataSetChanged();
                            break;
                    }
                }
            }
        });
        main.addView(listView);


        {
            TextView text = (TextView) LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, null);
            text.setText(getContext().getString(R.string.filedialog_empty));
            text.setVisibility(View.GONE);
            listView.setEmptyView(text);
            main.addView(text);
        }

        setView(main);
        setNegativeButton(android.R.string.cancel, null);

        adapter = new FileAdapter(getContext());
        listView.setAdapter(adapter);

        final AlertDialog d = super.create();

        onshow = new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                positive = d.getButton(DialogInterface.BUTTON_POSITIVE);
                rebuildFiles();
                // scroll to selected item
                listView.post(new Runnable() {
                    @Override
                    public void run() {
                        listView.setSelection(adapter.selectedIndex);
                    }
                });
                if (neutral != null) {
                    Button n = d.getButton(DialogInterface.BUTTON_NEUTRAL);
                    n.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            neutral.onClick(d, DialogInterface.BUTTON_NEUTRAL);
                        }
                    });
                }
            }
        };

        d.setOnShowListener(onshow);
        return d;
    }

    void updateSelected(int i) {
        if (positive != null) {
            switch (type) {
                case FILE_DIALOG:
                    positive.setEnabled(i != -1);
                    break;
                default:
                    positive.setEnabled(true);
            }
        }
        adapter.selectedIndex = i;
    }

    public OpenFileDialog setFilter(final String filter) {
        filenameFilter = new FilenameFilter() {
            @Override
            public boolean accept(File file, String fileName) {
                File tempFile = new File(file.getPath(), fileName);
                if (isFile(tempFile))
                    return tempFile.getName().matches(filter);
                return true;
            }
        };
        return this;
    }

    public OpenFileDialog setFolderIcon(int drawable) {
        this.folderIcon = drawable;
        return this;
    }

    // dialog to set output directory / file or readonly dialog?
    public void setReadonly(boolean b) {
        readonly = b;
    }

    // file select dialog or directory select dialog?
    public void setSelectFiles(DIALOG_TYPE type) {
        this.type = type;
    }

    public void setChangeFolderListener(Runnable r) {
        this.changeFolder = r;
    }

    @Override
    public AlertDialog.Builder setNeutralButton(CharSequence text, DialogInterface.OnClickListener listener) {
        neutral = listener;
        return super.setNeutralButton(text, listener);
    }

    @Override
    public AlertDialog.Builder setNeutralButton(int textId, DialogInterface.OnClickListener listener) {
        neutral = listener;
        return super.setNeutralButton(textId, listener);
    }

    public void setCurrentPath(File path) {
        currentPath = path;
        if (adapter != null) // created?
            rebuildFiles();
    }

    public File getCurrentPath() {
        return currentPath;
    }

    public OpenFileDialog setFileIcon(int drawable) {
        this.fileIcon = drawable;
        return this;
    }

    protected static Display getDefaultDisplay(Context context) {
        return ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    }

    Drawable getDrawable(int resid) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getContext().getDrawable(resid);
        } else {
            return ContextCompat.getDrawable(getContext(), resid);
        }
    }

    protected static Point getScreenSize(Context context) {
        Display d = getDefaultDisplay(context);
        if (Build.VERSION.SDK_INT < 13) {
            return new Point(d.getWidth(), d.getHeight());
        } else {
            Point screeSize = new Point();
            d.getSize(screeSize);
            return screeSize;
        }
    }

    protected static int getLinearLayoutMinHeight(Context context) {
        return getScreenSize(context).y;
    }

    public void rebuildFiles() {
        adapter.scan(currentPath);
        listView.setSelection(0);
        title.setText(adapter.currentPath.getPath());
        if (changeFolder != null) {
            changeFolder.run();
        }
    }

    protected boolean mkdirs(File f) {
        return f.mkdirs();
    }
}
