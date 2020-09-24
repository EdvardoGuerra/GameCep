package br.com.gamecep;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

public class ServidorActivity extends AppCompatActivity {
    String nome;
    String cep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servidor);

        setTitle("Servidor");

        Bundle bundle;
        bundle = getIntent().getExtras();
        if (bundle != null){
            nome = bundle.getString("nome", "Erro");
            cep = bundle.getString("cep", "Erro");

            Log.v("GameCep", "Nome: " + nome);
            Log.v("GameCep", "CEP: " + cep);
        }
    }
}