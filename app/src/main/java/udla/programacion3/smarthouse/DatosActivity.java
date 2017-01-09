package udla.programacion3.smarthouse;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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

public class DatosActivity extends AppCompatActivity {

    Button btnHit;
    TextView txtJson;
    ProgressDialog pd;
    static String lista="";
    EditText sensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos);

        btnHit = (Button) findViewById(R.id.buscar);
        //txtJson = (TextView) findViewById(R.id.textjson);

        sensor = (EditText) findViewById(R.id.ed_sensor);

        //JsonTask j = new JsonTask();
        //j.execute("http://191.102.85.226/smarthouse/api/default/get_datos_sensor/1");

        String[] array;
        if(!lista.equals("")){
            array = lista.split(",");

            ArrayAdapter<String> itemsAdapter =
                    new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, array);
            ListView listView = (ListView) findViewById(R.id.listaDatos);
            listView.setAdapter(itemsAdapter);
        }

        Context context = getApplicationContext();
        CharSequence text = "Hello toast!";
        int duration = Toast.LENGTH_SHORT;

        //Toast toast = Toast.makeText(context, lista, duration);
        //toast.show();

        btnHit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = sensor.getText().toString();
                new DatosActivity.JsonTask().execute("http://191.102.85.226/smarthouse/api/default/get_datos_sensor/" + id);
            }
        });


    }

    public void lanzar() {
        String[] array;
        if(!lista.equals("")){
            array = lista.split(",");

            ArrayAdapter<String> itemsAdapter =
                    new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, array);
            ListView listView = (ListView) findViewById(R.id.listaDatos);
            listView.setAdapter(itemsAdapter);
        }
    }

    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(DatosActivity.this);
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
                    String result="";
                    objetos = new JSONArray(buffer.toString());
                    if(objetos.length()>0) {

                        for (int i=0; i<objetos.length();i++){
                            result += "Valor: " + objetos.getJSONObject(i).get("ValorDato").toString() + " | Tipo: " + objetos.getJSONObject(i).get("Unidad").toString() + " | Fecha: " + objetos.getJSONObject(i).get("Fecha").toString()+",";
                        }
                        return result;
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
            DatosActivity.lista = result;

            //txtJson.setText(result);
        }
    }
}