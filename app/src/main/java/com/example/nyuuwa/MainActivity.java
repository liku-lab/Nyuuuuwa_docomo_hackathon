package com.example.nyuuwa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.view.View.OnClickListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    static final String TAG = "ListViewTest";

    private EditText editText;
    private String name = "main";

    static List<String> dataList = new ArrayList<String>();
    static ArrayAdapter<String> adapter;

    private TestOpenHelper helper;
    private SQLiteDatabase db;


    ListView listView;
    Button sendButton;
    Button changeButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 引数の「xxx.db」部分はDBファイル名を指定
//        deleteDatabase("xxxx.db");
        setContentView(R.layout.activity_main);
        findViews();
        setListeners();
        setAdapters();

    }

    protected void findViews() {
        listView = (ListView) findViewById(R.id.listView1);
        sendButton = (Button) findViewById(R.id.sbutton);
        changeButton = (Button) findViewById(R.id.cbutton);
        editText = (EditText) findViewById(R.id.edit_text);
    }

    protected void setListeners() {
        sendButton.setOnClickListener(this);
        changeButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sbutton:
                symbol2emoji();
                break;
            case R.id.cbutton:
                changeButton();
                break;
        }
    }

    protected void setAdapters() {
        adapter = new ArrayAdapter<String>(
                this,
                R.layout.list,
                dataList);
        listView.setAdapter(adapter);
       readData();
    }

    protected void symbol2emoji(){
        String text  = editText.getText().toString();

        if(text.length() == 0) {
            // 入力テキストがない場合
            return;
        }
        // アイコンと名前を追加
        text = "\uD83E\uDDD4　" + text;

//        文末に「。」追加
        char lastChar = text.charAt(text.length() - 1);
        if (lastChar == '。') {
        } else {
            if ((lastChar == '?') || (lastChar == '？')) {
            } else {
                text = text + "。";
            }
        }

//        「、」変換
        text = text.replace("、", "\uD83D\uDE04");
//        「。」変換
        text = text.replace("。", "\uD83D\uDE0A");
//        「? or ？」変換
        text = text.replace("?","\uD83E\uDD14");
        text = text.replace("？","\uD83E\uDD14");
        saveData(name,text);
        adapter.add(text);

        // 入力テキストを削除
        editText.getText().clear();
    }

    // クリック処理
    protected void changeButton() {
        Intent intent = new Intent(this, SubActivity.class); // 画面指定
        startActivity(intent);                          //  画面を開く
    }

    public void saveData(String name, String message) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("message", message);

        db.insert("testdb", null, values);
    }

    private void readData() {
        if (helper == null) {
            helper = new TestOpenHelper(getApplicationContext());
        }

        if (db == null) {
            db = helper.getReadableDatabase();
        }
        Log.d("debug", "**********Cursor");
        Cursor cursor = db.query(
                "testdb",   // The table to query
                new String[] {"message" },   // The array of columns to return (pass null to get all)
                null,    // The columns for the WHERE clause
                null,// The values for the WHERE clause
                null,         // don't group the rows
                null,         // don't filter by row groups
                null     // The sort order
        );

        cursor.moveToFirst();

        for (int i = 0; i < cursor.getCount(); i++) {
            if(cursor.getCount()==i+1) {
                adapter.add(cursor.getString(0));
            }
            cursor.moveToNext();
        }

        // 忘れずに！
        cursor.close();


    }
}
