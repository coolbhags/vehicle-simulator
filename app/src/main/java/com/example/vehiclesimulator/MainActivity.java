package com.example.vehiclesimulator;

import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity  {
    private TextView speedTextView;

    private float currentSpeed = 0.0f;

    // Handler to simulate acceleration continuously
    private final Handler accelerationHandler = new Handler(Looper.getMainLooper());

    private boolean isBrakeButtonPressed = false;

    private SeekBar steeringSeekBar;
    private TextView steeringAngleTextView;

    // Runnable to simulate acceleration
    private final Runnable accelerationRunnable = new Runnable() {
        @Override
        public void run() {
            // Increase the speed continuously
            simulateAccelerator();

            // Repeat the acceleration simulation after a short delay
            accelerationHandler.postDelayed(this, 100); // You can adjust the delay as needed
        }
    };

    // Handler to simulate deceleration continuously when the brake button is pressed
    private final Handler brakeHandler = new Handler(Looper.getMainLooper());

    // Runnable to simulate deceleration
    private final Runnable brakeRunnable = new Runnable() {
        @Override
        public void run() {
            // Gradually decrease the speed when the brake button is pressed
            simulateDeceleration();

            // Repeat the deceleration simulation after a short delay
            brakeHandler.postDelayed(this, 100); // You can adjust the delay as needed
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        speedTextView = findViewById(R.id.speedTextView);
        Button acceleratorButton = findViewById(R.id.acceleratorButton);
        Button brakeButton = findViewById(R.id.brakeButton);
        steeringSeekBar = findViewById(R.id.steeringSeekBar);
        steeringAngleTextView = findViewById(R.id.steeringAngleTextView);


        // Set touch listener for the accelerator button
        acceleratorButton.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // Start simulating acceleration when the button is pressed
                accelerationHandler.post(accelerationRunnable);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                // Stop simulating acceleration when the button is released
                accelerationHandler.removeCallbacks(accelerationRunnable);
                // Start simulating deceleration when the button is released
                brakeHandler.post(brakeRunnable);
            }
            return false;
        });

        brakeButton.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // Start simulating deceleration when the brake button is pressed
                brakeHandler.post(brakeRunnable);
                isBrakeButtonPressed = true;
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                // Stop simulating deceleration when the brake button is released
                brakeHandler.removeCallbacks(brakeRunnable);
                isBrakeButtonPressed = false;
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                // Continue simulating deceleration with updated pressure during movement
                simulateDeceleration(event);
            }
            return true;
        });

        Button clearButton = findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearData();
            }
        });

        steeringSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateSteeringAngle(progress - 90); // Normalize the progress to range from -90° to 90°
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Handle touch start if needed
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Handle touch stop if needed
            }
        });
    }

    private void updateSteeringAngle(int angle) {
        // Update the TextView to display the steering angle
        steeringAngleTextView.setText("Steering Angle: " + angle + "°");
    }
    private void simulateDeceleration(MotionEvent event) {
        // You can adjust the decrement value based on your simulation requirements
        float brakeIntensity = 2.0f; // Default deceleration intensity

        // Adjust brake intensity based on touch pressure
        float pressure = event.getPressure();
        float adjustedDeceleration = brakeIntensity * pressure;

        // Only show the Harsh Brake toast when the brake button is pressed
        if (isBrakeButtonPressed && isHarshBrake(pressure)) {
            showToast("Harsh Break");
        }

        currentSpeed -= adjustedDeceleration;

        // Display the updated speed in the UI
        displaySpeed(currentSpeed);

        // Stop decelerating when the speed becomes zero or negative
        if (currentSpeed <= 0) {
            currentSpeed = 0.0f;
            displaySpeed(currentSpeed);
            brakeHandler.removeCallbacks(brakeRunnable);
        }
    }



    // Method to simulate accelerator action
    private void simulateAccelerator() {
        // You can adjust the increment value based on your simulation requirements
        float accelerationIncrement = 25.0f;
        currentSpeed += accelerationIncrement;

        // Display the updated speed in the UI
        displaySpeed(currentSpeed);

        // You can add further logic or event detection here based on the updated speed
    }

    // Method to simulate deceleration action
    // Method to simulate deceleration action

    // Method to simulate deceleration action
    private void simulateDeceleration() {
        // You can adjust the decrement value based on your simulation requirements

        // Adjust brake intensity based on your logic or requirements
        float adjustedDeceleration = 2.0f;

        currentSpeed -= adjustedDeceleration;

        // Display the updated speed in the UI
        displaySpeed(currentSpeed);

        // Check for harsh brake condition and display a Toast
        if (isBrakeButtonPressed && isHarshBrake(adjustedDeceleration)) {
            showToast("Harsh Break");
        }

        // Stop decelerating when the speed becomes zero or negative
        if (currentSpeed <= 0) {
            currentSpeed = 0.0f;
            displaySpeed(currentSpeed);
            brakeHandler.removeCallbacks(brakeRunnable);
        }
    }


    // Method to check for a harsh brake based on pressure
    private boolean isHarshBrake(float pressure) {
        // Adjust the pressure threshold as needed
        float harshBrakePressureThreshold = 0.8f; // You can adjust this threshold
        return pressure > harshBrakePressureThreshold;
    }


    // Method to display the current speed in the UI
    @SuppressLint("SetTextI18n")
    private void displaySpeed(float speed) {
        speedTextView.setText("Current Speed: " + speed + " km/h");
    }

    // Method to show a Toast message
    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }


    // ! Clear Button

    private void clearData() {
        currentSpeed = 0.0f;
        displaySpeed(currentSpeed);
        isBrakeButtonPressed = false;
        showToast("Data Cleared");
    }







}