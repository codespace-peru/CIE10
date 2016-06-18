package pe.com.codespace.cie10;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.List;


public class TextActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

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
    
    AdapterListView myListAdapter;
    ListView myList;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_MENU || super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);

        Toolbar toolbar = (Toolbar) findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);


        context = this;
        myDBHelper = SQLiteHelperCIE10.getInstance(this);
        myList = (ListView) findViewById(R.id.lvText);

        codInicial = getIntent().getExtras().getString("codigoInicial");
        codFinal = getIntent().getExtras().getString("codigoFinal");
        nombreGrupo = getIntent().getExtras().getString("nombregrupo");
        capitulo = getIntent().getExtras().getInt("numerocapitulo");
        grupo = getIntent().getExtras().getInt("numerogrupo");
        prepararData();

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle(nombreGrupo);
        }

        cargarData();
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String cod = ((TextView) view.findViewById(R.id.tvCodCategoria)).getText().toString();
                String nombre = ((TextView) view.findViewById(R.id.tvNombreCategoria)).getText().toString();
                alert.setTitle(cod);
                alert.setMessage(nombre);
                alert.setNegativeButton(R.string.title_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                alert.show();
            }
        });

        //Agregar el adView
        AdView adView = (AdView)this.findViewById(R.id.adViewText);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        //Analytics
        Tracker tracker = ((AnalyticsApplication)  getApplication()).getTracker(AnalyticsApplication.TrackerName.APP_TRACKER);
        String nameActivity = getApplicationContext().getPackageName() + "." + this.getClass().getSimpleName();
        tracker.setScreenName(nameActivity);
        tracker.enableAdvertisingIdCollection(true);
        tracker.send(new HitBuilders.AppViewBuilder().build());

    }

    public void prepararData() {
        String[][] rows = myDBHelper.getCategorias(codInicial,codFinal);
        miArray = new ArrayList<>();
        boolean flag;
        int fav;

        for (String[] row : rows) {
            flag = myDBHelper.es_favorito(row[0]);
            if(flag)  fav=1; else fav=0;
            Tools.RowCategoria rowCategoria = new Tools.RowCategoria(capitulo, grupo, row[0], row[1], fav);
            miArray.add(rowCategoria);
        }
    }

	public void cargarData(){
        myListAdapter = new AdapterListView(this, miArray);
        myList.setAdapter(myListAdapter);
	}


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MyValues.VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches.size() > 0){
                Intent intent = new Intent(this,SearchActivity.class);
                intent.putExtra("searchText",matches.get(0).toString());
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
        searchView.setQueryHint(getResources().getString(R.string.action_search) + "...");
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
                SpeechRecognitionHelper.run(this);
                break;
            case R.id.action_favorites:
                Tools.MostrarFavoritos(this);
                break;
            case R.id.action_share:
                Tools.ShareApp(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {        
        Tools.QuerySubmit(this, menuItem, query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

	@Override
    protected void onResume() {
        super.onResume();
        prepararData();
        cargarData();

    }
}
