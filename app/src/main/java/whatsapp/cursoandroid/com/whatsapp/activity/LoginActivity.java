package whatsapp.cursoandroid.com.whatsapp.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;

import java.util.Random;

import whatsapp.cursoandroid.com.whatsapp.R;
import whatsapp.cursoandroid.com.whatsapp.helper.Permissao;
import whatsapp.cursoandroid.com.whatsapp.helper.Preferencias;

public class LoginActivity extends AppCompatActivity {

    private EditText nome;
    private EditText telefone;
    private EditText codPais;
    private Button cadastrar;
    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.SEND_SMS,
            Manifest.permission.INTERNET
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Permissao.validaPermissoes(1, this, permissoesNecessarias);

        nome       = (EditText) findViewById(R.id.edit_nome);
        telefone   = (EditText) findViewById(R.id.edit_telefone);
        codPais    = (EditText) findViewById(R.id.edit_cod_pais);
        cadastrar  = (Button) findViewById(R.id.bt_cadastrar);

        //Definir mascaras //Formatando a apresentação dos numeros do telefone e código do país
        SimpleMaskFormatter simpleMaskCodPais = new SimpleMaskFormatter("+NNN");
        SimpleMaskFormatter simpleMaskTelefone = new SimpleMaskFormatter("NNN-NNN-NNN");

        MaskTextWatcher maskCodPais = new MaskTextWatcher(codPais, simpleMaskCodPais);
        MaskTextWatcher maskTelefone = new MaskTextWatcher(telefone, simpleMaskTelefone);

        codPais.addTextChangedListener( maskCodPais );
        telefone.addTextChangedListener( maskTelefone );

        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nomeUsuario = nome.getText().toString();
                String telefoneCompleto =
                        codPais.getText().toString() +
                        telefone.getText().toString();

                String telefoneSemFormatacao = telefoneCompleto.replace("+", "");
                telefoneSemFormatacao = telefoneSemFormatacao.replace("-", "");

                //Gerar o Token no intervalo de 1000 a 9999
                Random randomico = new Random();
                int numeroRandomico = randomico.nextInt( 9999 - 1000 ) + 1000;
                String token = String.valueOf( numeroRandomico );
                String mensagemEnvio = "WhatsApp Código de Confirmação: " + token;

                //Salvar os dados para validação   #O procedimento mais seguro seria fazer isso em um servidor, porém para aprendizado faremos com Sharepreferences
                Preferencias preferencias = new Preferencias( LoginActivity.this );
                preferencias.salvarUsuarioPreferencias(nomeUsuario, telefoneSemFormatacao, token);

                //Envio do SMS
                telefoneSemFormatacao = "5554";
                boolean enviadoSMS =  enviaSMS( "+" + telefoneSemFormatacao, mensagemEnvio );

                //verificar se o SMS foi enviado e enviar para a activity Validador
                if ( enviadoSMS ) {

                    Intent intent = new Intent ( LoginActivity.this, ValidadorActivity.class);
                    startActivity( intent );
                    finish(); //destroir/finalizar a activity que estamos (LoginActivity)

                }else {
                    Toast.makeText(LoginActivity.this, "Problema ao enviar SMSm tente novamente", Toast.LENGTH_LONG).show();
                }

                //Log.i("fone:", "f:" + telefoneSemFormatacao);
                /*
                //criar um HashMap e salva no usuario
                HashMap<String, String> usuario = preferencias.getDadosusuario();
                Log.i("TOKEN", "NOME:" + usuario.get("nome") + " FONE: "+ usuario.get("telefone") );
                */
            }
        });


    }

    /*Envio do SMS*/
    private boolean enviaSMS(String telefone, String mensagem){

        try{

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(telefone, null, mensagem, null, null);

            return true;

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

    //método chamado para quando o usuário NEGA a permissão
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for( int resultado : grantResults ){

            if( resultado == PackageManager.PERMISSION_DENIED){
                alertaValidacaoPermissao();
            }

        }

    }

    private void alertaValidacaoPermissao(){

        AlertDialog.Builder builder = new AlertDialog.Builder( this );
        builder.setTitle("Permissões negadas");
        builder.setMessage("Para utilizar esse app, é necessário aceitar as permissões");

        builder.setPositiveButton("CONFIRMAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

}
