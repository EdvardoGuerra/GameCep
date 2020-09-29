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
import java.net.Socket;

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
    String nomeInimigo;
    String cepInimigo;
    TextView nomeTextView;
    TextView cepTextView;
    TextView estadoInimigoTextView;
    TextView cidadeInimigoTextView;
    TextView bairroInimigoTextView;
    TextView ruaInimigoTextView;
    TextView portaTextView;
    TextView ipTextView;
    EditText cepInicioEditText;
    TextView cepFimTextView;
    TextView dicaTextView;
    TextView pontosTextView;
    Button localizarInimigoButton;
    TextView nomeInimigoTextView;
    ServerSocket welcomeSocket;
    Socket clientSocket;
    DataOutputStream socketOutput;
    BufferedReader socketEntrada;
    DataInputStream fromClient;
    boolean continuarRodando = false;
    public static final int numPorta = 9090;
    DataInputStream socketInput;
    String cepInimigoInicio;
    String cepInimigoFim;
    int pontos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente);
        setTitle(getString(R.string.cliente));
        inicializarElementos();

        nomeInimigo = "Fulano";
        cepInimigo = "00000000";

        Intent intent = getIntent();
        bundle = intent.getExtras();
        if (bundle != null) {
            Log.v("GameCep", "Bundle: " + bundle.getString("estado", "Erro"));
        }
        if (bundle != null) {
            nome = bundle.getString("nome", "Erro");
            cep = bundle.getString("cep", "Erro");
            ipAddress = bundle.getString("ipAddress", "0.0.0.0");
        }
        nomeTextView.setText(nome);
        cepTextView.setText(cep);
        ipTextView.setText(ipAddress);
        portaTextView.setText(R.string._9090);
        pontosTextView.setText(R.string._100);

        conectar();


    }

    public void conectar() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    porta=9090;
                    Log.v("GameCep", "IpAddress: " + ipAddress + " Porta: " + porta);
                    clientSocket = new Socket(ipAddress, porta);
                    dicaTextView.post(new Runnable() {
                        @Override
                        public void run() {
                            dicaTextView.setText(String.format("Conectado:%s:9090", ipAddress));
                        }
                    });
                    socketInput = new DataInputStream(clientSocket.getInputStream());
                    socketOutput = new DataOutputStream(clientSocket.getOutputStream());

                    //envia seu proprio nome e cep para o inimigo
                    socketOutput.writeUTF(nome+";"+cep);

                    //lê nome e cep do inimigo (cliente)
                    String mensagem = socketInput.readUTF();
                    String[] mensagemSeparada = mensagem.split(";");
                    nomeInimigo = mensagemSeparada[0];
                    cepInimigo = mensagemSeparada[1];
                    nomeInimigo = mensagemSeparada[0];
                    cepInimigo = mensagemSeparada[1];
                    cepInimigoInicio = cepInimigo.substring(0, 3);
                    cepInimigoFim = cepInimigo.substring(3, 5) + "-" + cepInimigo.substring(5);

                    Thread threadAtualizaNome = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            atualizaNome();
                        }
                    });
                    threadAtualizaNome.start();


                } catch (Exception e) {
                    e.printStackTrace();
                    Log.v("GameCep", "Não conectou");
                }
            }
        });
        t.start();

    } //fim de conectar

    public void atualizaNome() {
        nomeInimigoTextView.post(new Runnable() {
            @Override
            public void run() {
                nomeInimigoTextView.setText(nomeInimigo);
            }
        });
        cepFimTextView.post(new Runnable() {
            @Override
            public void run() {
                cepFimTextView.setText(cepInimigoFim);
            }
        });
    }

    public void localizarInimigo(View view) {
        int cepParcial = Integer.parseInt(cepInicioEditText.getText().toString());
        int cepParcialInimigo = Integer.parseInt(cepInimigoInicio);
        if (cepParcial == cepParcialInimigo) {
            dicaTextView.setText(R.string.parabens);
        } else if (cepParcial > cepParcialInimigo) {
            dicaTextView.setText(R.string.inimigo_menor);
            pontos--;
            Log.v("GameCep", "pontos: " + pontos);
            Thread threadPontos = new Thread(new Runnable() {
                @Override
                public void run() {
                    atualizarPontos();
                }
            });
            threadPontos.start();


        } else if (cepParcial < cepParcialInimigo) {
            dicaTextView.setText(R.string.inimigo_maior);
            pontos--;
            Log.v("GameCep", "pontos: " + pontos);
            Thread threadPontos = new Thread(new Runnable() {
                @Override
                public void run() {
                    atualizarPontos();
                }
            });
            threadPontos.start();

        }
    }

    public void atualizarPontos() {
        pontosTextView.post(new Runnable() {
            @Override
            public void run() {
                String s = String.valueOf(pontos);
                pontosTextView.setText(s);
            }
        });
    }

    private void inicializarElementos() {
        nomeTextView = findViewById(R.id.nomeTextView);
        cepTextView = findViewById(R.id.cepTextView);
        estadoInimigoTextView = findViewById(R.id.estadoInimigoTextView);
        cidadeInimigoTextView = findViewById(R.id.cidadeInimigoTextView);
        bairroInimigoTextView = findViewById(R.id.bairroInimigoTextView);
        ruaInimigoTextView = findViewById(R.id.ruaInimigoTextView);
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