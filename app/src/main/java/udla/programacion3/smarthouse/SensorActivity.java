package udla.programacion3.smarthouse;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

public class SensorActivity extends AppCompatActivity {

    Button btnHit;
    TextView txtJson;
    ProgressDialog pd;
    static String lista="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);


        SensorActivity.JsonTask j = new SensorActivity.JsonTask();
        j.execute("http://191.102.85.226/smarthouse/api/default/get_sensores_vivienda");

        String[] array;
        if(!lista.equals("")){
            array = lista.split("]");

            ArrayAdapter<String> itemsAdapter =
                    new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, array);
            ListView listView = (ListView) findViewById(R.id.listaSensores);
            listView.setAdapter(itemsAdapter);
        }

        Context context = getApplicationContext();
        CharSequence text = "Hello toast!";
        int duration = Toast.LENGTH_SHORT;

    }


    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(SensorActivity.this);
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
                            result += objetos.getJSONObject(i).get("NombreSensor").toString() + " | " + objetos.getJSONObject(i).get("DescripcionSensor").toString() + " | Ref: " + objetos.getJSONObject(i).get("Referencia").toString()+ " | Ubicación: "+  objetos.getJSONObject(i).get("DescripcionPV").toString() +"]";
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
            SensorActivity.lista = result;
        }
    }
}