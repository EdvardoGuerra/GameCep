package br.com.gamecep;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;

public class ServidorActivity extends AppCompatActivity {
    String nome;
    String cep;
    String estado;
    String cidade;
    String bairro;
    String rua;
    int porta;
    int ip;
    int pontos =  100;
    String ipAddress;
    Bundle bundle;
    String nomeInimigo;
    String cepInimigo;
    String cepInimigoInicio;
    String cepInimigoFim;
    TextView nomeTextView;
    TextView cepTextView;
    TextView estadoTextView;
    TextView cidadeTextView;
    TextView bairroTextView;
    TextView ruaTextView;
    TextView portaTextView;
    TextView ipTextView;
    EditText cepInicioEditText;
    TextView cepFimTextView;
    TextView dicaTextView;
    TextView pontosTextView;
    Button localizarInimigoButton;
    TextView nomeInimigoTextView;
    ServerSocket welcomeSocket;
    DataOutputStream socketOutput;
    BufferedReader socketEntrada;
    DataInputStream fromClient;
    boolean continuarRodando = false;
    long pings, pongs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servidor);

        setTitle("Servidor");

        inicializarElementos();

        nomeInimigo = "Fulano";
        cepInimigo = "69040150";

        Intent intent = getIntent();
        bundle = intent.getExtras();
        Log.v("GameCep", "Bundle: " + bundle.getString("estado", "Erro"));
        if (bundle != null) {
            nome = bundle.getString("nome", "Erro");
            cep = bundle.getString("cep", "Erro");
            estado = bundle.getString("estado", "Erro");
            cidade = bundle.getString("cidade", "Erro");
            bairro = bundle.getString("bairro", "Erro");
            rua = bundle.getString("rua", "Erro");
            porta = bundle.getInt("porta", 0);
            ip = bundle.getInt("ip", 0);
            ipAddress =
                    String.format("%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff), (ip >> 24 & 0xff));
        }
        nomeTextView.setText(nome);
        cepTextView.setText(cep);
        estadoTextView.setText(estado);
        cidadeTextView.setText(cidade);
        bairroTextView.setText(bairro);
        ruaTextView.setText(rua);
        ipTextView.setText(ipAddress);
        portaTextView.setText("9090");
        pontosTextView.setText("100");

        identificarInimigo();
    }

    public void identificarInimigo(){
        cepInimigoInicio = cepInimigo.substring(0, 3);
        cepInimigoFim = cepInimigo.substring(3,5) + "-" + cepInimigo.substring(5);
        Log.v("GameCep", cepInimigo);
        Log.v("GameCep", cepInimigoInicio);
        Log.v("GameCep", cepInimigoFim);

        nomeInimigoTextView.setText(nomeInimigo);
        cepFimTextView.setText(cepInimigoFim);
    }

    public void localizarInimigo(View view){
        int cepParcial = Integer.parseInt(cepInicioEditText.getText().toString());
        int cepParcialInimigo = Integer.parseInt(cepInimigoInicio);
        if (cepParcial == cepParcialInimigo){
            dicaTextView.setText("Parabéns. Você acertou");
        } else if (cepParcial > cepParcialInimigo){
            dicaTextView.setText("Errou. CEP Inimigo é menor");
            pontos--;
            Log.v("GameCep", "pontos: " + pontos);
            atualizarPontos();


        } else if (cepParcial < cepParcialInimigo){
            dicaTextView.setText("Errou. CEP Inimigo é maior");
            pontos--;
            Log.v("GameCep", "pontos: " + pontos);
            atualizarPontos();

        }
    }

    private void atualizarPontos() {
        pontosTextView.post(new Runnable() {
            @Override
            public void run() {
                pontosTextView.setText(pontos);
            }
        });
    }

    private void inicializarElementos() {
        nomeTextView = findViewById(R.id.nomeTextView);
        cepTextView = findViewById(R.id.cepTextView);
        estadoTextView = findViewById(R.id.estadoTextView);
        cidadeTextView = findViewById(R.id.cidadeTextView);
        bairroTextView = findViewById(R.id.bairroTextView);
        ruaTextView = findViewById(R.id.ruaTextView);
        portaTextView = findViewById(R.id.portaTextView);
        ipTextView = findViewById(R.id.ipTextView);
        cepInicioEditText = findViewById(R.id.cepInicioEditText);
        cepFimTextView = findViewById(R.id.cepFimTextView);
        dicaTextView = findViewById(R.id.dicaTextView);
        pontosTextView = findViewById(R.id.pontosTextView);
        localizarInimigoButton = findViewById(R.id.localizarInimigoButton);
        nomeInimigoTextView = findViewById(R.id.nomeInimigoTextView);
    }



}
