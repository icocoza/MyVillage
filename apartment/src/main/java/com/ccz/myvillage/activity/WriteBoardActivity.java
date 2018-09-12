package com.ccz.myvillage.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ccz.myvillage.IConst;
import com.ccz.myvillage.R;
import com.ccz.myvillage.activity.dialog.AlertManager;
import com.ccz.myvillage.activity.dialog.IDialogListResultListener;
import com.ccz.myvillage.common.ImageUtils;
import com.ccz.myvillage.common.StrUtils;
import com.ccz.myvillage.common.ws.IWsFileListener;
import com.ccz.myvillage.common.ws.IWsListener;
import com.ccz.myvillage.common.ws.WsFileMgr;
import com.ccz.myvillage.common.ws.WsMgr;
import com.ccz.myvillage.constants.ECmd;
import com.ccz.myvillage.constants.EError;
import com.ccz.myvillage.constants.EItemType;
import com.ccz.myvillage.controller.NetMessage;
import com.ccz.myvillage.dto.BoardFile;
import com.ccz.myvillage.dto.BoardItem;
import com.ccz.myvillage.dto.Category;
import com.ccz.myvillage.dto.ContentItems;
import com.ccz.myvillage.form.request.ReqAddBoard;
import com.ccz.myvillage.form.request.ReqAddVote;
import com.ccz.myvillage.form.response.ResContent;
import com.ccz.myvillage.form.response.ResUploadFile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.java_websocket.client.WebSocketClient;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class WriteBoardActivity extends CommonActivity implements IWsListener {
    static public final int REQUEST_CAMERA = 100;

    final int GALLERY_RESULT_CODE = 9242;
    final int CAMERA_RESULT_CODE = 9243;
    final int VOTE_RESULT_CODE = 9244;

    private ArrayList<Category> categoryList;
    private int selectedCategoryIndex = -1;

    private Category category;
    private ResContent boardContent;
    private BoardItem boardItem;

    private String imageFilePath;
    private WsFileMgr wsFileMgr;
    private ContentItems contentItems = new ContentItems();
    private ArrayList<String> voteItems;

    private TextView tvUpload;
    private LinearLayout placeHolder;
    private ProgressBar pbProgress;
    EditText edtTitle, edtContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_board);

        WsMgr.getInst().setOnWsListener(this, this);

        tvUpload = (TextView)findViewById(R.id.tvUpload);
        placeHolder = (LinearLayout)findViewById(R.id.placeHolder);
        pbProgress = (ProgressBar)findViewById(R.id.pbProgress);
        edtTitle = (EditText)findViewById(R.id.edtTitle);
        edtContent = (EditText)findViewById(R.id.edtContent);

        placeHolder.requestFocus();
        category = (Category)getIntent().getSerializableExtra("category");
        if(category == null) {
            categoryList = (ArrayList<Category>) getIntent().getSerializableExtra("categories");
            showCategoryList();
        }else {
            boardContent = (ResContent)getIntent().getSerializableExtra("content");
            boardItem = (BoardItem)getIntent().getSerializableExtra("item");
            selectedCategoryIndex = category.getCategory();
            voteItems = boardContent.getVoteTitleList();
            showContentForModify();
            tvUpload.setText(getString(R.string.modify2));
        }

        //edtContent.setText("테스트 URL 테스트 입니다. https://news.v.daum.net/v/20180903150103126?rcmd=rn 두번째 링크는 https://news.naver.com/main/read.nhn?mode=LSD&mid=shm&sid1=101&oid=018&aid=0004189465 공갈 링크는 http://www.aaa234234.127 입니다. 어떻게 될까요?");
        //edtContent.setText("테스트 URL https://news.v.daum.net/v/20180903220008425?rcmd=rn 테스트 입니다. 두번째 링크는 https://techcrunch.com/2018/09/02/how-to-help-californians-whose-tap-water-is-tainted/ 공갈 링크는 http://www.aaa234234.127 입니다. 어떻게 될까요?");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_RESULT_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                final Uri uri = data.getData();
                loadContents(uri);
            } else if (resultCode == Activity.RESULT_CANCELED) {
            }
        }else if(requestCode == CAMERA_RESULT_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                loadContents(imageFilePath);
            }
        }else if( requestCode == VOTE_RESULT_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                addVoteContent(data);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                capturePhoto();
            } else {

            }
        }
    }

    public void onClickGallery(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_RESULT_CODE);
    }

    public void onClickCamera(View view) {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
            return;
        }
        capturePhoto();
    }

    public void onClickVote(View view) {
        startVoteActivity();
    }

    public void onClickCancel(View view) {
        finish();
    }

    public void onClickCategory(View view) {
        showCategoryList();
    }

    /*
     * 1. 첫번째로 파일을 모두 업로딩 하여 fileid를 획득한다.
     * 2. 모든 파일이 업로딩된 이후 게시판글과  fileid 목록을 어볼딩 한다.
     * */

    public void onClickUpload(View view) {
        pbProgress.setVisibility(View.VISIBLE);
        if(fileInitWsSocketManager() == true)   //has uploading file
            return;
        try {
            tvUpload.setEnabled(false);
            if(category == null)
                sendAddBoard();
            else
                sendUpdateBoard();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.board_add_fail), Toast.LENGTH_SHORT).show();
            tvUpload.setEnabled(true);
            pbProgress.setVisibility(View.GONE);
        }
    }

    public void onClickVoteModify(View view) {
        PopupMenu p = new PopupMenu(getApplicationContext(), view);
        getMenuInflater().inflate(R.menu.menu_vote_popup, p.getMenu());
        p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.modify:// modify
                        startVoteActivity();
                        return true;
                    case R.id.delete:// delete
                        findViewById(R.id.layoutVote).setVisibility(View.GONE);
                        voteItems = null;
                        return true;
                }
                return false;
            }
        });
        p.show();
    }

    private void startVoteActivity() {
        Intent in = new Intent(this, WriteVoteActivity.class);
        if(voteItems!=null)
            in.putExtra("voteItems", voteItems);
        startActivityForResult(in, VOTE_RESULT_CODE);
    }

    private void capturePhoto() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(pictureIntent.resolveActivity(getPackageManager()) != null) try {
            File photoFile = createImageFile();
            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(this, "com.ccz.myvillage.provider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(pictureIntent, CAMERA_RESULT_CODE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        imageFilePath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected boolean processMessage(WebSocketClient wsconn, ECmd cmd, JsonNode jsonNode, String origMessage) throws IOException {
        if(super.processMessage(wsconn, cmd, jsonNode, origMessage) == true)
            return true;
        switch(cmd) {
            case fileinit:
                if("ok".equals(jsonNode.get("result").asText())) {
                    String fileid = jsonNode.get("fileid").asText();
                    wsFileMgr.sendFileStart(contentItems.getCurrentId(), fileid);
                }else{
                    tvUpload.setVisibility(View.VISIBLE);
                    pbProgress.setVisibility(View.GONE);
                    Toast.makeText(this, getString(R.string.board_fileadd_fail), Toast.LENGTH_SHORT).show();
                }
                break;
            case addboard:
            case addvote:
                pbProgress.setVisibility(View.GONE);
                if("ok".equals(jsonNode.get("result").asText())) {
                    Toast.makeText(this, getString(R.string.board_add_ok), Toast.LENGTH_LONG).show();
                    setResult(1);
                }else {
                    Toast.makeText(this, getString(R.string.board_add_fail), Toast.LENGTH_LONG).show();
                    setResult(0);
                }
                finish();
                break;
            case updateboard:
                pbProgress.setVisibility(View.GONE);
                if("ok".equals(jsonNode.get("result").asText())) {
                    Toast.makeText(this, getString(R.string.board_update_ok), Toast.LENGTH_LONG).show();
                    setResult(1);
                }else {
                    Toast.makeText(this, getString(R.string.board_update_fail), Toast.LENGTH_LONG).show();
                    setResult(0);
                }
                finish();
                break;
        }
        return true;
    }

    private boolean fileInitWsSocketManager() {
        if(contentItems!=null && contentItems.size() < 1)
            return false;

        wsFileMgr = new WsFileMgr();
        try {
            wsFileMgr.connectServer(this, IConst.host, new IWsFileListener() {
                @Override
                public void onFileSent(WebSocketClient wsconn, int id, int size, int totalSize) {

                }

                @Override
                public void onFileComplete(WebSocketClient wsconn, int id) {

                }

                //파일 매니저가 연결되면 기본 네트웍 매니저를 통해 파일 업로딩 요청
                @Override
                public void onOpen(WebSocketClient wsconn) {
                    sendStartFile();
                }

                @Override
                public void onMessage(WebSocketClient wsconn, String s) {
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        JsonNode jsonNode = mapper.readTree(s);
                        ECmd cmd = ECmd.getType(jsonNode.get("cmd").asText());
                        if(ECmd.filesstart == cmd) {
                            resFlieStart(jsonNode);
                        }else if(ECmd.uploadfile ==cmd) {
                            resUploadFile(jsonNode, s);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onClose(WebSocketClient wsconn, int i, String s, boolean b) {

                }

                @Override
                public void onError(WebSocketClient wsconn, Exception e) {

                }
            });
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return true;
    }

    private void showCategoryList() {
        List<String> categories = categoryList.stream().map(x -> x.getTitle()).collect(Collectors.toList());
        AlertManager.showList(this, null, categories, new IDialogListResultListener() {
            @Override
            public void onDialogResult(boolean yesOrNo, int type) {
                if(yesOrNo == true & selectedCategoryIndex>-1) {
                    TextView tvCategory = (TextView)findViewById(R.id.tvCategory);
                    tvCategory.setText(categoryList.get(selectedCategoryIndex).getTitle());
                }
            }

            @Override
            public void onItemSelected(int which) {
                selectedCategoryIndex = which;
            }
        });
    }

    private void showContentForModify() {
        TextView tvCategory = (TextView)findViewById(R.id.tvCategory);
        tvCategory.setText(category.getTitle());
        tvCategory.setEnabled(false);

        ((EditText)findViewById(R.id.edtTitle)).setText(boardItem.getTitle());
        ((EditText)findViewById(R.id.edtContent)).setText(boardContent.getContent());
        findViewById(R.id.ivVote).setEnabled(false);

        for(BoardFile boardFile : boardContent.getFiles()) {
            addContent(boardFile);
        }
    }

    private void loadContents(Uri uri) {
        try {
            Bitmap bm = ImageUtils.resizeContent(this, uri, IConst.IMG_MAX_WIDTH, IConst.IMG_MAX_HEIGHT);
            addContent(uri.toString(), bm, false);
        }catch(Exception e) {
            Toast.makeText(this, getString(R.string.fail_load_content), Toast.LENGTH_LONG).show();
        }
    }

    private void loadContents(String photoPath) {
        try {
            Bitmap bm = ImageUtils.resizeContent(photoPath, IConst.IMG_MAX_WIDTH, IConst.IMG_MAX_HEIGHT);
            addContent(photoPath, bm, true);
        }catch(Exception e) {
            Toast.makeText(this, getString(R.string.fail_load_content), Toast.LENGTH_LONG).show();
        }
    }

    private void addVoteContent(Intent intent) {
        voteItems = (ArrayList<String>) intent.getSerializableExtra("voteItems");
        if(voteItems!=null)
            findViewById(R.id.layoutVote).setVisibility(View.VISIBLE);
    }

    private void addContent(String key, Bitmap bmp, boolean isCamera) throws IOException {
        if(contentItems.add(key, bmp, isCamera) == false) {
            Toast.makeText(this, getString(R.string.exceed_max_upsize), Toast.LENGTH_LONG).show();
            return;
        }
        View view = getLayoutInflater().inflate(R.layout.layout_image_content, null);
        ImageView ivPhoto = (ImageView)view.findViewById(R.id.ivPhoto);
        ivPhoto.setImageBitmap(bmp);
        ImageView ivDelete = (ImageView)view.findViewById(R.id.ivDelete);
        ivDelete.setTag(key);
        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placeHolder.removeView((LinearLayout)view.getParent().getParent());
                contentItems.del((String) view.getTag());
            }
        });
        EditText edtComment = (EditText)view.findViewById(R.id.edtComment);
        contentItems.setEditText(key, edtComment);
        placeHolder.addView(view);
    }

    private void addContent(BoardFile boardFile) {
        contentItems.add(boardFile);
        View view = getLayoutInflater().inflate(R.layout.layout_image_content, null);
        ImageView ivPhoto = (ImageView)view.findViewById(R.id.ivPhoto);
        Picasso.get().load(boardFile.getFileUrl()).transform(new Transformation() {
            @Override
            public Bitmap transform(Bitmap source) {
                Bitmap scaledBmp = ImageUtils.scaleUpContent(source, IConst.ScreenPixels.getWidth(), IConst.ScreenPixels.getHeight());
                if(source != scaledBmp)
                    source.recycle();
                return scaledBmp;
            }

            @Override
            public String key() {
                return boardFile.getFileUrl();
            }
        }).into(ivPhoto);

        ImageView ivDelete = (ImageView)view.findViewById(R.id.ivDelete);
        ivDelete.setTag(boardFile.getFileid());
        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placeHolder.removeView((LinearLayout)view.getParent().getParent());
                contentItems.del((String) view.getTag());
            }
        });
        EditText edtComment = (EditText)view.findViewById(R.id.edtComment);
        contentItems.setEditText(boardFile.getFileid(), edtComment);
        placeHolder.addView(view);
    }

    private void sendStartFile() {
        ContentItems.ContentItem item;
        while( (item = contentItems.next()) != null) {
            if(item.isUrl() == false)
                break;
        }
        if(item != null) {
            ObjectNode node = NetMessage.makeDefaultNode(ECmd.fileinit);
            node.put("filename", item.getFileName());
            node.put("filetype", StrUtils.getFileExt(item.getFileName()));
            node.put("filesize", item.getSize());
            node.put("comment", item.getComment());
            WsMgr.getInst().send(node.toString());
        }else {
            try {
                if(category == null)
                    sendAddBoard();
                else
                    sendUpdateBoard();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendAddBoard() throws JsonProcessingException {
        ECmd cmd = (voteItems == null || voteItems.size() < 1) ? ECmd.addboard : ECmd.addvote;
        ReqAddVote addBoard = makeBoardData(cmd, edtTitle.getText().toString(), edtContent.getText().toString());
        if(voteItems!=null && voteItems.size() > 0)
            addBoard.setItemList(voteItems);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(addBoard);
        WsMgr.getInst().send(json);
    }

    private void sendUpdateBoard() throws JsonProcessingException {
        ReqAddVote addBoard = makeBoardData(ECmd.updateboard, edtTitle.getText().toString(), edtContent.getText().toString());
        addBoard.setBoardid(boardItem.getBoardid());
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(addBoard);
        WsMgr.getInst().send(json);
    }

    private ReqAddVote makeBoardData(ECmd cmd, String title, String content) {
        ReqAddVote addBoard = new ReqAddVote();
        addBoard.setCmd(cmd);
        addBoard.setTitle(title);
        addBoard.setContent(content);
        addBoard.setHasimage(contentItems.size()>0);
        addBoard.setHasfile(false);
        addBoard.setCategory(selectedCategoryIndex);
        addBoard.setItemtype(getItemType());
        addBoard.setFileids(contentItems.getFileIds());
        return addBoard;
    }

    private EItemType getItemType(){
        if(voteItems!=null && voteItems.size() > 0)
            return EItemType.vote;
        return contentItems.size() > 0 ? EItemType.textimage : EItemType.text;
    }

    private void resFlieStart(JsonNode node) {
        wsFileMgr.sendFile(contentItems.current().getData());
    }

    private void resUploadFile(JsonNode node, String s) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ResUploadFile res = mapper.readValue(s, ResUploadFile.class);
            if(res.getResult().equals(EError.complete.toString())) {
                contentItems.current().setFileId(res.getFileid());
            }
            sendStartFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
