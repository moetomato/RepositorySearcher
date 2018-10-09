package com.moeko.admin.repositorysearcher;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.SimpleAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    EditText searchBox;
    ListView listView;
    RadioGroup radioGroupSort;
    SimpleAdapter adapter;
    List<Map<String, String>> list;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //画面のセット
        setContentView(R.layout.activity_main);

        //クエリを入力する検索ボックス
        searchBox = (EditText) findViewById(R.id.editText);
        searchBox.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {
                loadRepositories();
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        listView = (ListView)findViewById(R.id.listView);
        list = new ArrayList<Map<String, String>>();
        adapter = new SimpleAdapter(
                this,
                list,
                android.R.layout.simple_list_item_2,
                new String[] {"main", "sub"},
                new int[] {android.R.id.text1, android.R.id.text2}
        );
        listView.setAdapter(adapter);

        radioGroupSort =(RadioGroup) findViewById(R.id.radioGroupSort);
        radioGroupSort.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                    loadRepositories();
            }
        });
    }
    private void loadRepositories(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GitHubService service = retrofit.create(GitHubService.class);

        String searchQuery = searchBox.getText().toString();
        if(searchQuery.isEmpty()){
            list.clear();
            return;
        }

        int checkedRadioButtonId = radioGroupSort.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) radioGroupSort.findViewById(checkedRadioButtonId);
        String checkedSortButtonText = radioButton.getTag().toString();

        Call<Repositories> userCall = service.getRepositories(searchQuery,checkedSortButtonText);
        userCall.enqueue(new Callback<Repositories>(){
            @Override
            public void onResponse (Call < Repositories > call, Response < Repositories > response){
                Repositories repositories = response.body();
                list.clear();
                if (repositories == null) return;
                for (Repositories.Repository repository : repositories.items) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("main", repository.full_name);
                    map.put("sub", repository.description);
                    list.add(map);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure (Call < Repositories > call, Throwable t){
            }
        });
    }
}