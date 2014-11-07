package pe.com.codespace.cie10;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
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


public class TextActivity extends ActionBarActivity implements SearchView.OnQueryTextListener {

    SQLiteHelperCIE10 myDBHelper;
    List<Tools.RowCategoria> miArray;
    SearchView searchView;
    MenuItem menuItem;
    String codInicial;
    String codFinal;
    String nombreGrupo;
    int capitulo;
    int grupo;
    Context context;
    boolean favorites = false;
    TextView titulo;
    AdapterListView myListAdapter;
    ListView myList;
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        context = this;
        myDBHelper = SQLiteHelperCIE10.getInstance(this);
        myList = (ListView) findViewById(R.id.lvText);

        codInicial = getIntent().getExtras().getString("codigoInicial");
        codFinal = getIntent().getExtras().getString("codigoFinal");
        nombreGrupo = getIntent().getExtras().getString("nombregrupo");
        capitulo = getIntent().getExtras().getInt("numerocapitulo");
        grupo = getIntent().getExtras().getInt("numerogrupo");
        favorites = getIntent().getExtras().getBoolean("favorito");
        titulo = (TextView) findViewById(R.id.tvTitleText);
        if(favorites){
           prepararFavoritos();
            titulo.setText("Mis Favoritos");
        }else{
            prepararData();
            titulo.setText(nombreGrupo);
        }

        myListAdapter = new AdapterListView(this,miArray);
        myList.setAdapter(myListAdapter);

        //Agregar el adView
        AdView adView = (AdView)this.findViewById(R.id.adViewText);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

    }

    private void prepararData() {
        String[][] rows = myDBHelper.getCategorias(codInicial,codFinal);
        miArray = new ArrayList<Tools.RowCategoria>();

        for(int i=0; i<rows.length;i++){
            Tools.RowCategoria rowCategoria = new Tools.RowCategoria(capitulo,grupo,rows[i][0],rows[i][1],Integer.parseInt(rows[i][2]));
            miArray.add(rowCategoria);
        }
    }

    private void prepararFavoritos() {
        String[][] rows = myDBHelper.getFavoritos();
        miArray = new ArrayList<Tools.RowCategoria>();
        TextView textView = (TextView) findViewById(R.id.tvFavoritos);

        for(int i=0; i<rows.length;i++){
            Tools.RowCategoria rowCategoria = new Tools.RowCategoria(capitulo,grupo,rows[i][0],rows[i][1],Integer.parseInt(rows[i][2]));
            miArray.add(rowCategoria);
        }
        if(miArray.size()==0){
            textView.setVisibility(View.VISIBLE);
            textView.setText("No se encontraron Favoritos");
        }
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
        if(favorites==true){
            MenuItem favoriteItem = menu.findItem(R.id.action_favorites);
            favoriteItem.setVisible(false);
        }
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
                Intent intent = new Intent(TextActivity.this, TextActivity.class);
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
        //this.finish();
        this.startActivity(intent);
        MenuItemCompat.collapseActionView(menuItem);
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
        if(!favorites){
            prepararData();
            titulo.setText(nombreGrupo);
            myListAdapter = new AdapterListView(this,miArray);
            myList.setAdapter(myListAdapter);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }
}
