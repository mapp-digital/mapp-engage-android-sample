package com.appoxee.testapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.appoxee.Appoxee;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class HighLoadActivity extends AppCompatActivity {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private Button btnRunTest;
    private ProgressBar progressBar;
    private final AtomicBoolean testRunning = new AtomicBoolean(false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_high_load);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        progressBar = findViewById(R.id.progressBar);
        btnRunTest = findViewById(R.id.btnRunTest);
        btnRunTest.setOnClickListener(v -> runTest());
    }

    /**
     * @noinspection BusyWait
     */
    private void runTest() {
        int max = 20;
        int delay = 0;
        AtomicInteger current = new AtomicInteger(0);
        progressBar.setMax(max);
        toggleTestState();
        executorService.execute(() -> {
            try {
                while (testRunning.get() && current.get() <= max) {
                    Appoxee.instance().setAlias("user-20122024-01@mapptest.com");
                    Thread.sleep(delay);
                    Appoxee.instance().setPushEnabled(true);
                    Thread.sleep(delay);
                    Appoxee.instance().logOut(false);
                    Thread.sleep(delay);
                    runOnUiThread(() -> {
                        progressBar.setProgress(current.incrementAndGet());
                    });
                }
                if (testRunning.get()) {
                    toggleTestState();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void toggleTestState() {
        runOnUiThread(() -> {
            boolean current = !testRunning.get();
            testRunning.set(current);
            btnRunTest.setText(getString(current ? R.string.stop_test : R.string.run_test));
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (testRunning.get()) {
            toggleTestState();
        }
    }
}