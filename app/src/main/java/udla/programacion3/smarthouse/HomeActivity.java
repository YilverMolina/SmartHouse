package udla.programacion3.smarthouse;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HomeActivity extends AppCompatActivity {

    Button btnHit;
    TextView txtJson;
    ProgressDialog pd;
    EditText Ed_Username;
    EditText Ed_Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnHit = (Button) findViewById(R.id.Ingresar);
        Ed_Username = (EditText) findViewById(R.id.TUsername);
        Ed_Password = (EditText) findViewById(R.id.TPassword);
        //txtJson = (TextView) findViewById(R.id.textjson);

        btnHit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = Ed_Username.getText().toString();
                String password = Ed_Password.getText().toString();
                new JsonTask().execute("http://191.102.85.226/smarthouse/api/default/get_validar_usuario?username="+username+"&pass="+password);
            }
        });


    }

    public void lanzar(View view) {
        Intent i = new Intent(this, MainActivity.class );
        startActivity(i);
    }

    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(HomeActivity.this);
            pd.setMessage("Cargando datos");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = null;
                try {
                    url = new URL(params[0]);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuilder buffer = new StringBuilder();
                String line = "", l2="";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");


                    l2=line;

                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }



                JSONArray objetos=null;
                try {
                    objetos = new JSONArray(buffer.toString());
                    if(objetos.length()>0) {
                        Intent i = new Intent(getApplicationContext(), MainActivity.class );
                        startActivity(i);
                        return objetos.getJSONObject(0).get("Username").toString() + "";
                    }else{
                        return "";
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }  finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing()){
                pd.dismiss();
            }
            //txtJson.setText(result);
        }
    }
}