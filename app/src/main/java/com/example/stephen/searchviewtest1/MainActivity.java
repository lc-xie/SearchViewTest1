package com.example.stephen.searchviewtest1;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.stephen.searchviewtest1.bean.School;
import com.example.stephen.searchviewtest1.bean.SchoolJson;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{
    private SchoolJson schoolJson;
    private List<School>schoolList=new ArrayList<>();
    private SearchView sv;
    private ListView lv;
    //自动完成的列表
//    private String[] mStrings=new String[]{};
    private List<String>list=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sendRequestWithOkHttp();
        lv=(ListView)findViewById(R.id.list_view_search_view);
        ArrayAdapter<String>  adapter =new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,list);
        lv.setAdapter(adapter);
        lv.setTextFilterEnabled(true);//设置是否通过输入信息过滤无关选项

        sv=(SearchView)findViewById(R.id.sv);
        //设置该SearchView默认是否自动缩小为图标
        sv.setIconifiedByDefault(false);
        //为该SearchView组件设置事件监听器
        sv.setOnQueryTextListener(this);
        //设置该SearchView显示搜索按钮
        sv.setSubmitButtonEnabled(true);
        //设置该SearchView内默认显示的提示文本
        sv.setQueryHint("查找");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    //用户输入字符时激发该方法
    @Override
    public boolean onQueryTextChange(String newText) {
        if(TextUtils.isEmpty(newText))
        {
            //清楚ListView的过滤
            lv.clearTextFilter();
        }
        else
        {
            //使用用户输入的内容对ListView的列表项进行过滤
            lv.setFilterText(newText);
        }
        return true;
    }
    //单击搜索按钮时激发该方法
    @Override
    public boolean onQueryTextSubmit(String query) {
        Toast.makeText(this, "您选择的是："+query, Toast.LENGTH_SHORT).show();
        return true;
    }

    private static final String TAG = "MainActivity";
    private void sendRequestWithOkHttp(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection =null;
                BufferedReader reader=null;
                try{
                    URL url=new URL("http://www.xue1314.com/api/user/getSchoolList");
                    connection=(HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(10000);
                    connection.setReadTimeout(10000);
                    InputStream in =connection.getInputStream();
                    reader=new BufferedReader(new InputStreamReader(in));
                    StringBuilder response=new StringBuilder();
                    String line;
                    while ((line=reader.readLine())!=null){
                        response.append(line);
                    }
                    Log.i(TAG, "run: "+(response==null));
                    parseJSONWithGson(response.toString());
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if(reader!=null){
                        try{
                            reader.close();
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    private void parseJSONWithGson(String string){
        Gson gson=new Gson();
        schoolJson=gson.fromJson(string,SchoolJson.class);
        schoolList=schoolJson.getData();
        for(int i=0;i<schoolList.size();i++){
            School school=schoolList.get(i);
            list.add(school.getName());
            //mStrings[i]=school.getName();
        }
    }

}