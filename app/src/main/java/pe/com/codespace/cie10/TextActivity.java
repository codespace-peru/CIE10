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
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;


public class TextActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    SQLiteHelperCIE10 myDBHelper;
    ArrayList<Tools.RowCategoria> miArray;
    SearchView searchView;
    TextView textView;
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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;
        myDBHelper = SQLiteHelperCIE10.getInstance(this);
        myList = (ListView) findViewById(R.id.lvText);
        textView = (TextView) findViewById(R.id.tvFavoritos);

        codInicial = getIntent().getExtras().getString("codigoInicial");
        codFinal = getIntent().getExtras().getString("codigoFinal");
        nombreGrupo = getIntent().getExtras().getString("nombregrupo");
        capitulo = getIntent().getExtras().getInt("numerocapitulo");
        grupo = getIntent().getExtras().getInt("numerogrupo");

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle(nombreGrupo);
            getSupportActionBar().setSubtitle("(" +codInicial + "-" +codFinal+")");
        }

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
        assert adView != null;
        adView.loadAd(adRequest);

    }

    public void prepararData() {
        String[][] rows = myDBHelper.getCategorias(codInicial,codFinal);
        miArray = new ArrayList<>();

        for (String[] row : rows) {
            Tools.RowCategoria rowCategoria = new Tools.RowCategoria(capitulo, grupo, row[0], row[1],Integer.parseInt(row[2]));
            miArray.add(rowCategoria);
        }
    }

	public void cargarData(){
        myListAdapter = new AdapterListView(this, miArray);
        myList.setAdapter(myListAdapter);
        textView.setText(getResources().getString(R.string.show_ocurrencias1) + " " + myList.getCount() + " " + getResources().getString(R.string.show_ocurrencias3));
	}

    private void MostrarCantidadFiltro(int cant) {
        assert textView != null;
        textView.setText(getResources().getString(R.string.show_ocurrencias1) + " " + cant + " " + getResources().getString(R.string.show_ocurrencias3));
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
        getMenuInflater().inflate(R.menu.menu_actionbar_text, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint(getResources().getString(R.string.action_filter) + "...");
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
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        myListAdapter.getFilter().filter(s, new Filter.FilterListener() {
            @Override
            public void onFilterComplete(int count) {
                MostrarCantidadFiltro(count);
            }
        });
        return false;
    }

	@Override
    protected void onResume() {
        super.onResume();
        prepararData();
        cargarData();

    }
}
