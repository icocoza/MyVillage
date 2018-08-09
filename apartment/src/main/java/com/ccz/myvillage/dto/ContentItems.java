package com.ccz.myvillage.dto;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.ccz.myvillage.IConst;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ContentItems {

    private Map<String, ContentItem> map = new ConcurrentHashMap<>();
    private List<ContentItem> list = new ArrayList<>();
    private long totalSize = 0;

    private int currentPos = 0;

    public void setEditText(String key, EditText edtComment) {
        map.get(key).setTextView(edtComment);
    }

    public boolean add(String key, Bitmap bmp, boolean isCamera) throws IOException {
        String fileName = "";
        if(isCamera == true)
            fileName = (new File(key)).getName();
        else
            fileName = "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss",Locale.getDefault()).format(new Date())+".jpg";
        ContentItem item = new ContentItem(fileName, bmp, isCamera);
        Log.i("ContentItems", String.format("FileSize:%d, TotalSize: %d", item.getSize(), totalSize + item.getSize()));
        if(IConst.MAX_UPLOAD_SIZE < totalSize + item.getSize()) {
            return false;
        }
        totalSize += item.getSize();
        map.put(key, item);
        list.add(item);
        return true;
    }

    public void del(String key) {
        if(map.containsKey(key)==false)
            return;
        map.remove(key);
    }

    public byte[] getFileData(String key) {
        if(map.containsKey(key)==false)
            return null;
        return map.get(key).getData();
    }

    public long getFileSize(String key) {
        if(map.containsKey(key)==false)
            return -1;
        return map.get(key).getSize();
    }

    public long getTotalSize() {
        return totalSize;
    }

    public boolean isCamera(String key) {
        if(map.containsKey(key)==false)
            return false;
        return map.get(key).isCamera();
    }

    public void clear() {
        map.clear();
    }

    public ContentItem first() {
        currentPos = 0;
        return next();
    }

    public ContentItem next() {
        if(currentPos < list.size())
            return list.get(currentPos++);
        return null;
    }

    public ContentItem current() {
        if(currentPos-1>-1)
            return list.get(currentPos-1);
        return null;
    }

    public ContentItem get(int pos) {
        if(pos < list.size())
            return list.get(pos);
        return null;
    }

    public int size() {
        return list.size();
    }

    public int getCurrentId() {
        return currentPos;
    }

    public List<String> getFileIds() {
        return list.stream().map(x -> x.fileId).collect(Collectors.toList());
    }

    public class ContentItem {
        final byte[] fileData;
        final boolean validContent;
        final boolean isCamera;
        final String fileName;
        private EditText edtComment;

        private String fileId;

        private ContentItem(String fileName, Bitmap bmp, boolean isCamera) throws IOException {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            validContent = bmp.compress(Bitmap.CompressFormat.JPEG, 90, stream);
            fileData = stream.toByteArray();
            stream.close();
            this.isCamera = isCamera;
            this.fileName = fileName;
        }
        public void setTextView(EditText edtComment) {
            this.edtComment = edtComment;
        }

        public String getFileName() {
            return fileName;
        }

        public int getSize() {
            if(fileData == null)
                return 0;
            return fileData.length;
        }

        public byte[] getData() {
            return fileData;
        }

        public boolean isCamera() {
            return isCamera;
        }

        public String getComment() {
            if(edtComment== null)
                return "";
            return edtComment.getText().toString();
        }

        public String getFileId() {
            return fileId;
        }

        public void setFileId(String fileId) {
            this.fileId = fileId;
        }

    }
}
