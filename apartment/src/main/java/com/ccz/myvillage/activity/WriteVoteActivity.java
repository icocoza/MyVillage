package com.ccz.myvillage.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ccz.myvillage.IConst;
import com.ccz.myvillage.R;

import java.util.ArrayList;
import java.util.List;

public class WriteVoteActivity extends AppCompatActivity {
    LinearLayout layoutHolder;
    List<String> voteItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_vote);

        voteItems = (ArrayList<String>) getIntent().getSerializableExtra("voteItems");

        initVoteView();
    }

    public void onClickCancel(View view) {
        finish();
    }

    public void onClickUpload(View view) {
        if(checkVoteItems()==false)
            return;
        Intent in = new Intent();
        in.putExtra("voteItems", (ArrayList<String>)voteItems);
        setResult(Activity.RESULT_OK, in);
        finish();
    }

    public void onClickAddItem(View view) {
        if(layoutHolder.getChildCount() == IConst.MAX_VOTE_COUNT) {
            Toast.makeText(this, String.format(getString(R.string.vote_exceed_count), IConst.MAX_VOTE_COUNT), Toast.LENGTH_SHORT).show();
            return;
        }
        addVoteView("");
    }

    private void initVoteView() {
        layoutHolder = (LinearLayout)findViewById(R.id.layoutHolder);
        layoutHolder.removeAllViews();  //기존 뷰 모두 ㅈㅔ거 (화면구성 예제)
        if(voteItems == null) {
            addVoteView("");
            addVoteView("");
        }else {
            for(String voteItem : voteItems)
                addVoteView(voteItem);
        }

    }

    private void addVoteView(String text) {
        View view = getLayoutInflater().inflate(R.layout.view_vote_writer, null);
        if(text != null && text.length()>0)
            ((EditText)view.findViewById(R.id.edtVoteText)).setText(text);
        view.findViewById(R.id.ivDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutHolder.removeView((LinearLayout)view.getParent().getParent());
            }
        });
        layoutHolder.addView(view);
    }

    private boolean checkVoteItems() {
        if(layoutHolder.getChildCount()<2) {
            Toast.makeText(this, getString(R.string.vote_min_2), Toast.LENGTH_SHORT).show();
            addVoteView("");
            return false;
        }
        voteItems = new ArrayList<>();
        for(int i=0; i<layoutHolder.getChildCount(); i++) {
            EditText edtVote = layoutHolder.getChildAt(i).findViewById(R.id.edtVoteText);
            String voteText = edtVote.getText().toString();
            if(voteText.trim().length()<1) {
                Toast.makeText(this, getString(R.string.vote_not_empty), Toast.LENGTH_SHORT).show();
                edtVote.requestFocus();
                return false;
            }
            voteItems.add(voteText);
        }
        return true;
    }
}
