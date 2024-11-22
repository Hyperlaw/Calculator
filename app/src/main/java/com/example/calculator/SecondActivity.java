package com.example.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SecondActivity extends AppCompatActivity {

    String[] item = {"Item 1", "Item 2", "Item 3"};

    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapterItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_second);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView textView = findViewById(R.id.text_view);
        String text = getIntent().getStringExtra("key1");
        textView.setText(text);



        autoCompleteTextView = findViewById(R.id.dropdown);
        adapterItems = new ArrayAdapter<>(this, R. layout.list_items, item);
        autoCompleteTextView.setAdapter(adapterItems);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
            }
        });

        ImageButton heartButton = findViewById(R.id.heartButton);


        final boolean[] isLiked = {false};

        heartButton.setOnClickListener(v -> {
            if (isLiked[0]) {
                heartButton.setImageResource(R.drawable.ic_heart);
            } else {
                heartButton.setImageResource(R.drawable.ic_heart_click);
            }
            isLiked[0] = !isLiked[0];
        });

        Button nextButton = findViewById(R.id.next_button);

        nextButton.setOnClickListener(v -> {
            if (MainActivity.instance != null) {
                MainActivity.instance.finish();
            }
            finish();
            System.exit(0);
        });









    }
}