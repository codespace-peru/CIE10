package pe.com.codespace.cie10;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends ActionBarActivity implements SearchView.OnQueryTextListener {

    SQLiteHelperCIE10 myDBHelper;
    String searchText;
    SearchView searchView;
    MenuItem menuItem;
    ListView myList;
    String[][] resultados;
    AdapterListView myListAdapter;
    Tools.RowCategoria rowCategoria;
    List<Tools.RowCategoria> myListCategorias = new ArrayList<Tools.RowCategoria>();
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Intent intent = getIntent();
        searchText = intent.getExtras().getString("searchText");
        TextView textView = (TextView) findViewById(R.id.tvResultados);
        myDBHelper = SQLiteHelperCIE10.getInstance(this);
        resultados = myDBHelper.searchTexto(searchText);
        switch (resultados.length){
            case 0:
                textView.setText(Html.fromHtml("No se encontraron ocurrencias de <b><i>'" + searchText + "'</i></b>"));
                break;
            case 1:
                textView.setText(Html.fromHtml("Se muestra 1 ocurrencia de <b><i>'" + searchText + "'</i></b>"));
                break;
            default:
                textView.setText(Html.fromHtml("Se muestran " + resultados.length + " ocurrencias de <b><i>'" + searchText + "'</i></b>"));
                break;
        }

        for(int i=0;i<resultados.length;i++){
            rowCategoria = new Tools.RowCategoria(Integer.parseInt(resultados[i][0]),Integer.parseInt(resultados[i][1]),resultados[i][2],resultados[i][3],Integer.parseInt(resultados[i][4]));
            myListCategorias.add(rowCategoria);
        }
        myList = (ListView) findViewById(R.id.lvSearchText);
        myListAdapter = new AdapterListView(this,myListCategorias);
        myList.setAdapter(myListAdapter);

        //Agregar el adView
        AdView adView = (AdView)this.findViewById(R.id.adViewSearch);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches.size() > 0){
                Intent intent = new Intent(this,SearchActivity.class);
                intent.putExtra("searchText",Tools.remove(matches.get(0).toString()));
                this.startActivity(intent);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_actionbar_main, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Búsqueda...");
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                searchItem.collapseActionView();
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        menuItem = item;
        int id = item.getItemId();
        switch (id){
            case R.id.action_voice:
                SpeechRecognitionHelper speech = new SpeechRecognitionHelper();
                speech.run(this);
                break;
            case R.id.action_favorites:
                Intent intent = new Intent(SearchActivity.this, TextActivity.class);
                intent.putExtra("favorito",true);
                this.startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Intent intent = new Intent(this,SearchActivity.class);
        intent.putExtra("searchText", query);
        this.finish();
        this.startActivity(intent);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }
}
