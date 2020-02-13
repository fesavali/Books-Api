package com.javacodegeeks.books;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class BookListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private ProgressBar mLoadingProgress;
    private RecyclerView rvBooks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        TextView tvResult = (TextView) findViewById(R.id.tvResponse);
        mLoadingProgress = (ProgressBar) findViewById(R.id.pb_loading);
        rvBooks = (RecyclerView) findViewById(R.id.rv_books);
        LinearLayoutManager booksLayoutManager = new LinearLayoutManager
                (this, RecyclerView.VERTICAL, false);
        rvBooks.setLayoutManager(booksLayoutManager);
        try {
            URL bookUrl = ApiUtil.buildUrl("cooking");
            new BooksQueryTask().execute(bookUrl);
//            String jsonResult = ApiUtil.getJson(bookUrl);
//            tvResult.setText(jsonResult);
        }
        catch (Exception e){
            Log.d("Error", e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.book_list_menu, menu);
        final MenuItem  searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        return  true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        try {
            URL bookUrl = ApiUtil.buildUrl(query);
            new BooksQueryTask().execute(bookUrl);
        }catch (Exception e){
            Log.d("error", e.getMessage());
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    public class BooksQueryTask extends AsyncTask<URL, Void, String>{

        @Override
        protected String doInBackground(URL... urls) {
            URL searchUrl = urls[0];
            String result = null;
            try {
                result = ApiUtil.getJson(searchUrl);
            }
            catch (IOException e){
                Log.d("Error", e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
//            TextView tvResult = (TextView) findViewById(R.id.tvResponse);
            TextView tvError = (TextView) findViewById(R.id.tv_error);
            mLoadingProgress.setVisibility(View.INVISIBLE);
            if(result == null){
                rvBooks.setVisibility(View.INVISIBLE);
                tvError.setVisibility(View.VISIBLE);
            }else{
                rvBooks.setVisibility(View.VISIBLE);
                tvError.setVisibility(View.INVISIBLE);
            }
//            tvResult.setText(result);
            ArrayList<Book> books = ApiUtil.getBooksFromJson(result);
            String resultString = "";
//            for(Book book : books){
//                resultString = resultString + book.title + "\n" + book.publishedDate + "\n\n";
//            }
//            tvResult.setText(resultString);
            BooksAdapter adapter = new BooksAdapter(books);
            rvBooks.setAdapter(adapter);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingProgress.setVisibility(View.VISIBLE);
        }
    }
}
