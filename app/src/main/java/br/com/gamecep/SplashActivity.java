package br.com.gamecep;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                mostrarMainActivity();
                mostrarIdentificacaoActivity();
            }
        }, 3000);
    }

    private void mostrarIdentificacaoActivity() {
        Intent intent = new Intent(SplashActivity.this, IdentificacaoActivity.class);
        startActivity(intent);
        finish();
    }

    private void mostrarMainActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}