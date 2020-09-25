package br.com.gamecep;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class IdentificacaoActivity extends AppCompatActivity {

    EditText nomeEditText;
    EditText cepEditText;
    Button verificarLocalizacaoButton;
    TextView estadoLabelTextView;
    TextView estadoTextView;
    TextView cidadeLabelTextView;
    TextView cidadeTextView;
    TextView bairroLabelTextView;
    TextView bairroTextView;
    TextView ruaLabelTextView;
    TextView ruaTextView;
    TextView localValidoTextView;
    Button criarServidorButton;
    Button  entrarJogoButton;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identificacao);
        setTitle("Identificação");
        inicializarElementos();

        verificarLocalizacaoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listarConexoes();
                if (estaConectado()) {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            verificaLocalizacao();
                        }
                    });
                    thread.start();
                }
            }
        });

        criarServidorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(IdentificacaoActivity.this,
                        ServidorActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        entrarJogoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IdentificacaoActivity.this,
                        ClienteActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


    } //fim de onCreate


    private void verificaLocalizacao() {
        estadoTextView.setText("");
        cidadeTextView.setText("");
        bairroTextView.setText("");
        ruaTextView.setText("");
//        localValidoTextView.setText("");

        String cep = cepEditText.getText().toString();
        Log.v("GameCep", cep);
        try {
            URL url = new URL("https://viacep.com.br/ws/" + cep + "/json/");

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
//                final String resultadoErro = resultadoJsonObject.getString("erro");
//                Log.v("GameCep", "erro " + resultadoErro.toString());
//
//                if (resultadoErro == null){
//                    Toast.makeText(this,
//                            "CEP inválido", Toast.LENGTH_LONG).show();
//                }
                final String resultadoEstado = resultadoJsonObject.getString("uf").toString();
                final String resultadoCidade = resultadoJsonObject.getString("localidade").toString();
                final String resultadoBairro = resultadoJsonObject.getString("bairro").toString();
                final String resultadoRua = resultadoJsonObject.getString("logradouro").toString();

                estadoTextView.setText(resultadoEstado);
                cidadeTextView.setText(resultadoCidade);
                bairroTextView.setText(resultadoBairro);
                ruaTextView.setText(resultadoRua);

                bundle = new Bundle();
                bundle.putString("nome", nomeEditText.getText().toString());
                bundle.putString("cep", cepEditText.getText().toString());
                bundle.putString("estado", estadoTextView.getText().toString());
                bundle.putString("cidade", cidadeTextView.getText().toString());
                bundle.putString("bairro", bairroTextView.getText().toString());
                bundle.putString("rua", ruaTextView.getText().toString());
                Log.v("GameCep", bundle.toString());

//                if (estadoTextView.getText() == ""){
//                    localValidoTextView.setText("Localização inválida. Tente novamente!!!");
//                } else {
//                    localValidoTextView.setText("Localização válida");
//                }


            } else {
                Toast.makeText(this,
                        "Sem conexão", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


//    {
//        "erro": true
//    }

//    {
//        "cep": "60411-200",
//            "logradouro": "Rua General Silva Júnior",
//            "complemento": "",
//            "bairro": "Fátima",
//            "localidade": "Fortaleza",
//            "uf": "CE",
//            "ibge": "2304400",
//            "gia": "",
//            "ddd": "85",
//            "siafi": "1389"
//    }

    private boolean estaConectado() {
        ConnectivityManager connectivityManager;
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Network[] networks = connectivityManager.getAllNetworks();
        NetworkInfo networkInfo;
        boolean temConexao = false;

        for (Network network : networks) {
            networkInfo = connectivityManager.getNetworkInfo(network);
            if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                NetworkCapabilities networkCapabilities;
                networkCapabilities = connectivityManager.getNetworkCapabilities(network);
                if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.v("GameCep", "tem conexao");
                    temConexao = true;
                }
            }
        }
        return temConexao;
    } //fim de estaConectado

    private void listarConexoes() {
        ConnectivityManager connectivityManager;
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Network[] networks = connectivityManager.getAllNetworks();
        NetworkInfo networkInfo;
        for (Network network : networks) {
            networkInfo = connectivityManager.getNetworkInfo(network);
            if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                Log.v("GameCep", "tipo de conexão conectada:" + networkInfo.getTypeName());
            }
        }

    }

    private void inicializarElementos() {
        nomeEditText = findViewById(R.id.nomeTextView);
        cepEditText = findViewById(R.id.cepTextView);
        verificarLocalizacaoButton = findViewById(R.id.verificarLocalizacaoButton);
        estadoLabelTextView = findViewById(R.id.estadoLabelTextView);
        estadoTextView = findViewById(R.id.estadoTextView);
        cidadeLabelTextView = findViewById(R.id.cidadeLabelTextView);
        cidadeTextView = findViewById(R.id.cidadeTextView);
        bairroLabelTextView = findViewById(R.id.bairroLabelTextView);
        bairroTextView = findViewById(R.id.bairroTextView);
        ruaLabelTextView = findViewById(R.id.ruaLabelTextView);
        ruaTextView = findViewById(R.id.ruaTextView);
        localValidoTextView = findViewById(R.id.localValidoTextView);
        criarServidorButton = findViewById(R.id.criarServidorButton);
        entrarJogoButton = findViewById(R.id.entrarJogoButton);
    }


} //fim de IdentificacaoActivity