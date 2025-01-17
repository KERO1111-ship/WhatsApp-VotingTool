
// MainActivity.java
package com.example.whatsappvotingtool;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText questionInput;
    private EditText choiceInput;
    private LinearLayout choicesContainer;
    private Button addChoiceButton, startButton, enableAccessibility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        questionInput = findViewById(R.id.questionInput);
        choiceInput = findViewById(R.id.choiceInput);
        choicesContainer = findViewById(R.id.choicesContainer);
        addChoiceButton = findViewById(R.id.addChoiceButton);
        startButton = findViewById(R.id.startButton);
        enableAccessibility = findViewById(R.id.enableAccessibility);

        // Add choices dynamically
        addChoiceButton.setOnClickListener(v -> {
            String choice = choiceInput.getText().toString().trim();
            if (!choice.isEmpty()) {
                EditText newChoice = new EditText(this);
                newChoice.setText(choice);
                newChoice.setEnabled(false);
                choicesContainer.addView(newChoice);
                choiceInput.setText("");
            } else {
                Toast.makeText(this, "Please enter a choice", Toast.LENGTH_SHORT).show();
            }
        });

        // Open Accessibility Settings
        enableAccessibility.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
        });

        // Start Sending Messages
        startButton.setOnClickListener(v -> {
            String question = questionInput.getText().toString().trim();

            if (question.isEmpty()) {
                Toast.makeText(this, "Please enter the question", Toast.LENGTH_SHORT).show();
                return;
            }

            StringBuilder fullMessage = new StringBuilder(question + "\n\n");
            for (int i = 0; i < choicesContainer.getChildCount(); i++) {
                EditText choiceView = (EditText) choicesContainer.getChildAt(i);
                fullMessage.append(i + 1).append(") ").append(choiceView.getText().toString()).append("\n");
            }

            if (choicesContainer.getChildCount() == 0) {
                Toast.makeText(this, "Please add at least one choice", Toast.LENGTH_SHORT).show();
                return;
            }

            // Start the Accessibility Service
            Intent serviceIntent = new Intent(this, WhatsAppAccessibilityService.class);
            serviceIntent.putExtra("message", fullMessage.toString());
            startService(serviceIntent);

            Toast.makeText(this, "Service started! Open WhatsApp to begin.", Toast.LENGTH_LONG).show();
        });
    }
}
