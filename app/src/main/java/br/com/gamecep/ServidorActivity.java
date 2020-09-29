package br.com.gamecep;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class ServidorActivity extends AppCompatActivity {
    String nome;
    String cep;
    int porta;
    int ip;
    int pontos = 100;
    String ipAddress;
    Bundle bundle;
    String nomeInimigo;
    String cepInimigo;
    String cepInimigoInicio;
    String cepInimigoFim;
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
    DataOutputStream socketOutput;
    BufferedReader socketEntrada;
    DataInputStream fromClient;
    boolean continuarRodando = false;
//    String resultadoInimigoEstado;
//    String resultadoInimigoCidade;
//    String resultadoInimigoBairro;
//    String resultadoInimigoRua;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servidor);
        setTitle("Servidor");
        inicializarElementos();

        nomeInimigo = "Fulano";
        cepInimigo = "00000000";

        Intent intent = getIntent();
        bundle = intent.getExtras();
        Log.v("GameCep", "Bundle: " + bundle.getString("estado", "Erro"));
        if (bundle != null) {
            nome = bundle.getString("nome", "Erro");
            cep = bundle.getString("cep", "Erro");
            ip = bundle.getInt("ip", 0);
            ipAddress =
                    String.format("%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff), (ip >> 24 & 0xff));
            Log.v("GameCep", "ip recuperado: " + ipAddress);
        }
        nomeTextView.setText(nome);
        cepTextView.setText(cep);
        ipTextView.setText(ipAddress);
        portaTextView.setText("9090");
        pontosTextView.setText("100");

        ligarServidor();
    }

    public void ligarServidor() {
        ConnectivityManager connManager;
        connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Network[] networks = connManager.getAllNetworks();

        for (Network minhaRede : networks) {
            NetworkInfo netInfo = connManager.getNetworkInfo(minhaRede);
            if (netInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                NetworkCapabilities propDaRede = connManager.getNetworkCapabilities(minhaRede);

                if (propDaRede.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    WifiManager wifiManager =
                            (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ligarServerCodigo();
                        }
                    });
                    t.start();
                }
            }
        }
    }

    public void ligarServerCodigo() {

        String result = "";
        try {
            Log.v("GameCep", "Ligando o Server");

            bundle.putInt("porta", porta);
            porta = 9090;
            welcomeSocket = new ServerSocket(porta);
            Socket connectionSocket = welcomeSocket.accept();


            //Instanciando os canais de stream
            fromClient = new DataInputStream(connectionSocket.getInputStream());
            socketOutput = new DataOutputStream(connectionSocket.getOutputStream());
            continuarRodando = true;

            //envia seu próprio nome e cep para inimigo
            if (connectionSocket.isConnected()){
                socketOutput.writeUTF(nome + ";" + cep);
                socketOutput.flush();
            }

            //lê nome e cep do inimigo (cliente)
            String mensagem = fromClient.readUTF();
            String[] mensagemSeparada = mensagem.split(";");
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

//            nomeInimigoTextView.setText(nomeInimigo);
            Log.v("GameCep", "Nome inimigo via socket " + nomeInimigo);
            Log.v("GameCep", "CEP inimigo via socket " + cepInimigo);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

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
            dicaTextView.setText("Parabéns. Você acertou");
            verificaLocalizacao();
        } else if (cepParcial > cepParcialInimigo) {
            dicaTextView.setText("Errou. CEP Inimigo é menor");
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
            dicaTextView.setText("Errou. CEP Inimigo é maior");
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

    public void inicializarElementos() {
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

    public void verificaLocalizacao() {

        try {
            URL url = new URL("https://viacep.com.br/ws/" + cepInimigo + "/json/");
            Log.v("GameCep", "localizando " + cepInimigo);

            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();

            String resultado[] = new String[1];
            int respostaConexao = connection.getResponseCode();


            if (respostaConexao == HttpsURLConnection.HTTP_OK) {
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(connection.getInputStream(),
                                "utf-8"));
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = reader.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                resultado[0] = response.toString();
                Log.v("GameCep", "resutado " + resultado[0]);

                JSONObject resultadoJsonObject = new JSONObject(resultado[0]);

                final String resultadoInimigoEstado = resultadoJsonObject.getString("uf").toString();
                Log.v("GameCep", "estado inimigo " + resultadoInimigoEstado);
                final String resultadoInimigoCidade = resultadoJsonObject.getString("localidade").toString();
                final String resultadoInimigoBairro = resultadoJsonObject.getString("bairro").toString();
                final String resultadoInimigoRua = resultadoJsonObject.getString("logradouro").toString();
                Log.v("GameCep", resultadoInimigoRua);

                final Thread atualizaEnderecoInimigoThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        atualizaEnderecoInimigo(resultadoInimigoEstado, resultadoInimigoCidade, resultadoInimigoBairro, resultadoInimigoRua);
                    }
                });
                atualizaEnderecoInimigoThread.start();
//                estadoInimigoTextView.setText(resultadoInimigoEstado);
//                cidadeInimigoTextView.setText(resultadoInimigoCidade);
//                bairroInimigoTextView.setText(resultadoInimigoBairro);
//                ruaInimigoTextView.setText(resultadoInimigoRua);

            } else {
                Toast.makeText(this,
                        "Sem conexão", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void atualizaEnderecoInimigo(String estado, String cidade, String bairro, String rua) {
        final String resultadoInimigoEstado = estado;
        final String resultadoInimigoCidade = cidade;
        final String resultadoInimigoBairro = cidade;
        final String resultadoInimigoRua = cidade;
        estadoInimigoTextView.post(new Runnable() {
            @Override
            public void run() {
                estadoInimigoTextView.setText(resultadoInimigoEstado);
            }
        });
        cidadeInimigoTextView.post(new Runnable() {
            @Override
            public void run() {
                cidadeInimigoTextView.setText(resultadoInimigoCidade);
            }
        });
        bairroInimigoTextView.post(new Runnable() {
            @Override
            public void run() {
                bairroInimigoTextView.setText(resultadoInimigoBairro);
            }
        });
        ruaInimigoTextView.post(new Runnable() {
            @Override
            public void run() {
                ruaInimigoTextView.setText(resultadoInimigoRua);
            }
        });

    }

}
