package whatsapp.cursoandroid.com.whatsapp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;

import java.util.HashMap;

import whatsapp.cursoandroid.com.whatsapp.R;
import whatsapp.cursoandroid.com.whatsapp.helper.Preferencias;

public class ValidadorActivity extends AppCompatActivity {

    private EditText codigoValidacao;
    private Button validar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validador);

        codigoValidacao = (EditText) findViewById(R.id.edit_cod_validacaoID);
        validar         = (Button) findViewById(R.id.botao_validarID);

        SimpleMaskFormatter simpleMaskFormatter = new SimpleMaskFormatter( "NNNN" );
        MaskTextWatcher maskCodigoValidacao = new MaskTextWatcher(codigoValidacao, simpleMaskFormatter);

        codigoValidacao.addTextChangedListener(maskCodigoValidacao);

        validar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //recuperar dados das preferencia do usuario
                Preferencias preferencias = new Preferencias( ValidadorActivity.this );
                HashMap<String, String> usuario = preferencias.getDadosusuario();

                String tokenGerado = usuario.get("token");
                String tokenDigitado = codigoValidacao.getText().toString();

                //validar o token
                if ( tokenDigitado.equals(tokenGerado)){
                    Toast.makeText(ValidadorActivity.this, "Token validado corretamente", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(ValidadorActivity.this, "Token Não válido", Toast.LENGTH_LONG).show();
                }



            }
        });





    }


}
