package br.com.gamecep;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;

public class ClienteActivity extends AppCompatActivity {

    String nome;
    String cep;
    String estado;
    String cidade;
    String bairro;
    String rua;
    int porta;
    int ip;
    String ipAddress;
    Bundle bundle;
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
    public static final int numPorta = 9090;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente);

        setTitle("Cliente");
        inicializarElementos();

        Intent intent = getIntent();
        bundle = intent.getExtras();
        Log.v("GameCep", "Bundle: " + bundle.getString("estado", "Erro"));
        if (bundle != null) {
            nome = bundle.getString("nome", "Erro");
            Log.v("GameCep", nome);
            cep = bundle.getString("cep", "Erro");
            estado = bundle.getString("estado", "Erro");
            cidade = bundle.getString("cidade", "Erro");
            bairro = bundle.getString("bairro", "Erro");
            rua = bundle.getString("rua", "Erro");
            porta = bundle.getInt("porta", 0);
            ipAddress = bundle.getString("ipAddress", "0.0.0.0");
//            ipAddress = String.format("%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff), (ip >> 24 & 0xff));
        }
        nomeTextView.setText(nome);
        cepTextView.setText(cep);
        estadoTextView.setText(estado);
        cidadeTextView.setText(cidade);
        bairroTextView.setText(bairro);
        ruaTextView.setText(rua);
        ipTextView.setText(ipAddress);
        portaTextView.setText("9090");

        conectar();


    }

    private void conectar() {

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