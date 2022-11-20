package com.example.assignment3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainScreen extends AppCompatActivity {
    private RecyclerView rv;
    private ContactAdapter contactAdapter;
    private ArrayList<User> users;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        rv = findViewById(R.id.contacts);
        buildRecyclerView();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });
        return true;
    }

    private void filter(String filter_text) {
        ArrayList<User> filteredList = new ArrayList<>();
        for (User user : users) {
            if (user.getName().toLowerCase().contains(filter_text.toLowerCase())) {
                filteredList.add(user);
            }
        }
        if(filteredList.isEmpty()) {
            Toast.makeText(this, "No contacts found", Toast.LENGTH_SHORT).show();
        } else {
            contactAdapter.filterList(filteredList);
        }
    }

    private void buildRecyclerView() {
        users = new ArrayList<>();

        users.add(new User("John", "1", "1", "03125337819", "http://192.168.100.2:8080/chat_app/users/talalahmed252@gmail.com.jpg"));
        users.add(new User("TALAL", "1", "1", "03125337819", "http://192.168.100.2:8080/chat_app/users/talalahmed252@gmail.com.jpg"));
        users.add(new User("AMRECA", "1", "1", "03125337819", "http://192.168.100.2:8080/chat_app/users/talalahmed252@gmail.com.jpg"));
        users.add(new User("INDIA", "1", "1", "03125337819", "http://192.168.100.2:8080/chat_app/users/talalahmed252@gmail.com.jpg"));
        users.add(new User("MALAYSIA", "1", "1", "03125337819", "http://192.168.100.2:8080/chat_app/users/talalahmed252@gmail.com.jpg"));

        contactAdapter = new ContactAdapter(users, MainScreen.this);
        rv.setLayoutManager(new LinearLayoutManager(MainScreen.this));
        rv.setAdapter(contactAdapter);
    }
}