package pe.com.codespace.cie10;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    SQLiteHelperCIE10 myDBHelper;
    String searchText;
    SearchView searchView;
    TextView textView;

    ListView myList;
    String[][] resultados;
    AdapterListView myListAdapter;
    Tools.RowCategoria rowCategoria;
    ArrayList<Tools.RowCategoria> myListCategorias = new ArrayList<>();

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_MENU || super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle(R.string.search_title);
        }

        Intent intent = getIntent();
        searchText = intent.getExtras().getString("searchText");
        textView = (TextView) findViewById(R.id.tvResultados);
        myDBHelper = SQLiteHelperCIE10.getInstance(this);
        resultados = myDBHelper.searchTexto(searchText);

        switch (resultados.length){
            case 0:
                assert textView != null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
                    textView.setText(Html.fromHtml(getResources().getString(R.string.sin_ocurrencias) + " <b><i>'" + searchText + "'</i></b>", Html.FROM_HTML_MODE_LEGACY));
                else
                    textView.setText(Html.fromHtml(getResources().getString(R.string.sin_ocurrencias) + " <b><i>'" + searchText + "'</i></b>"));

                break;
            case 1:
                assert textView != null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
                    textView.setText(Html.fromHtml(getResources().getString(R.string.una_ocurrencia) +  " <b><i>'" + searchText + "'</i></b>", Html.FROM_HTML_MODE_LEGACY));
                else
                    textView.setText(Html.fromHtml(getResources().getString(R.string.una_ocurrencia) +  " <b><i>'" + searchText + "'</i></b>"));
                break;
            default:
                assert textView != null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
                    textView.setText(Html.fromHtml(getResources().getString(R.string.show_ocurrencias1) + " " + resultados.length + " " + getResources().getString(R.string.show_ocurrencias2) + " <b><i>'" + searchText + "'</i></b>", Html.FROM_HTML_MODE_LEGACY));
                else
                    textView.setText(Html.fromHtml(getResources().getString(R.string.show_ocurrencias1) + " " + resultados.length + " " + getResources().getString(R.string.show_ocurrencias2) + " <b><i>'" + searchText + "'</i></b>"));
                break;
        }



        for (String[] resultado : resultados) {
            rowCategoria = new Tools.RowCategoria(Integer.parseInt(resultado[0]), Integer.parseInt(resultado[1]), resultado[2], resultado[3], Integer.parseInt(resultado[4]));
            myListCategorias.add(rowCategoria);
        }
        myList = (ListView) findViewById(R.id.lvSearchText);
        myListAdapter = new AdapterListView(this,myListCategorias);
        myList.setAdapter(myListAdapter);

        //Agregar el adView
        AdView adView = (AdView)this.findViewById(R.id.adViewSearch);
        AdRequest adRequest = new AdRequest.Builder().build();
        assert adView != null;
        adView.loadAd(adRequest);

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
                intent.putExtra("searchText", matches.get(0).toString());
                finish();
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
    public boolean onQueryTextChange(final String s) {
        myListAdapter.getFilter().filter(s, new Filter.FilterListener() {
            @Override
            public void onFilterComplete(int count) {
                MostrarCantidadFiltro(count);
            }
        });
        return false;
    }


}
