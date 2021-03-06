package com.example.fishermanapp;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class MainActivity extends AppCompatActivity {



    //15
    private FishViewModel fishViewModel;
    //r
    private Fish selectedFish;

    FishAdapter fishAdapter;

    //22
    public static final int NEW_FISH_ACTIVITY_REQUEST_CODE = 1;

    //r
    public static final int EDIT_FISH_ACTIVITY_REQUEST_CODE = 2;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_FISH_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Fish fish = new Fish(data.getStringExtra(EditFishActivity.EXTRA_EDIT_FISH_NAZWA),
                    data.getStringExtra(EditFishActivity.EXTRA_EDIT_FISH_DATA),
                    data.getStringExtra(EditFishActivity.EXTRA_EDIT_FISH_WIELKOSC),
                    data.getStringExtra(EditFishActivity.EXTRA_EDIT_FISH_WAGA),
                    data.getStringExtra(EditFishActivity.EXTRA_EDIT_FISH_JEZIORO));
            fishViewModel.insert(fish);
            Snackbar.make(findViewById(R.id.main_layout), getString(R.string.fish_added), Snackbar.LENGTH_LONG).show();

        } else if (requestCode == EDIT_FISH_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            selectedFish.setNazwa(data.getStringExtra(EditFishActivity.EXTRA_EDIT_FISH_NAZWA));
            selectedFish.setData(data.getStringExtra(EditFishActivity.EXTRA_EDIT_FISH_DATA));
            selectedFish.setWielkosc(data.getStringExtra(EditFishActivity.EXTRA_EDIT_FISH_WIELKOSC));
            selectedFish.setWaga(data.getStringExtra(EditFishActivity.EXTRA_EDIT_FISH_WAGA));
            selectedFish.setJezioro(data.getStringExtra(EditFishActivity.EXTRA_EDIT_FISH_JEZIORO));

            fishViewModel.update(selectedFish);
            Snackbar.make(findViewById(R.id.main_layout), getString(R.string.fish_updated), Snackbar.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), R.string.empty_not_save, Toast.LENGTH_LONG).show();

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);






        FloatingActionButton addButton = findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditFishActivity.class);
                startActivityForResult(intent, NEW_FISH_ACTIVITY_REQUEST_CODE);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final FishAdapter fishAdapter = new FishAdapter();
        recyclerView.setAdapter(fishAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fishViewModel = ViewModelProviders.of(this).get(FishViewModel.class);
        fishViewModel.findAll().observe(this, new Observer<List<Fish>>() {
            @Override
            public void onChanged(List<Fish> fishes) {
                fishAdapter.setFishes(fishes);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //14
    private class FishHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView fishNazwaTextView;
        private TextView fishDataTextView;
        private TextView fishWielkoscTextView;
        private TextView fishWagaTextView;
        private TextView fishJezioroTextView;
        //r
        private Fish fish;

        public FishHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.fish_list_item, parent, false));


            fishNazwaTextView = itemView.findViewById(R.id.fish_nazwa);
            fishDataTextView = itemView.findViewById(R.id.fish_data);
            fishWielkoscTextView = itemView.findViewById(R.id.fish_wielkosc);
            fishWagaTextView = itemView.findViewById(R.id.fish_waga);
            fishJezioroTextView = itemView.findViewById(R.id.fish_jezioro);
            //r
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void bind(Fish f) {

            fishNazwaTextView.setText(f.getNazwa());
            fishDataTextView.setText(f.getData());
            fishWielkoscTextView.setText(f.getWielkosc());
            fishWagaTextView.setText(f.getWaga());
            fishJezioroTextView.setText(f.getJezioro());

            fish = f;
        }


        @Override
        public void onClick(View view) {
            selectedFish = fish;

            Intent intent = new Intent(MainActivity.this, EditFishActivity.class);

            intent.putExtra("FISH_NAZWA", fishNazwaTextView.getText().toString());
            intent.putExtra("FISH_DATA", fishDataTextView.getText().toString());
            intent.putExtra("FISH_WIELKOSC", fishWielkoscTextView.getText().toString());
            intent.putExtra("FISH_WAGA", fishWagaTextView.getText().toString());
            intent.putExtra("FISH_JEZIORO", fishJezioroTextView.getText().toString());

            startActivityForResult(intent, EDIT_FISH_ACTIVITY_REQUEST_CODE);
        }

        @Override
        public boolean onLongClick(View view) {
            fishViewModel.delete(fish);
            return true;
        }
    }


    private class FishAdapter extends RecyclerView.Adapter<FishHolder> {

        private List<Fish> fishes;

        @NonNull
        @Override
        public FishHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new FishHolder(getLayoutInflater(), parent);
        }

        @Override
        public void onBindViewHolder(@NonNull FishHolder holder, int position) {

            if (fishes != null) {
                Fish fish = fishes.get(position);
                holder.bind(fish);
            } else {
                Log.d("MainActivity", "No fishes");
            }
        }

        @Override
        public int getItemCount() {
            if (fishes != null) {
                return fishes.size();
            } else
                return 0;
        }

        void setFishes(List<Fish> fishes) {
            this.fishes = fishes;
            notifyDataSetChanged();
        }
    }



    private class SearchTaskAsynch extends AsyncTask<String,Void,List<Fish>> {

        String SearchingText;

        @Override
        protected List<Fish> doInBackground(String ...Id){
            SearchingText=Id[0];
            List<Fish> fishes = fishViewModel.findFish("%"+SearchingText+"%");
            return fishes;
        }

        @Override
        protected void onPostExecute(List<Fish> result){// ta funkcja odpala sie na zakończenie, result to wynik
            fishAdapter.setFishes(result);
            fishAdapter.notifyDataSetChanged();
        }

    }
}
