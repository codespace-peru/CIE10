package pe.com.codespace.cie10;

import android.content.Intent;
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


public class FavoritosActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private SQLiteHelperCIE10 myDBHelper;
    private ArrayList<Tools.RowCategoria> miArray;
    AdapterListView myListAdapter;
    TextView textView;

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

        myDBHelper = SQLiteHelperCIE10.getInstance(this);
        ListView myList = (ListView) findViewById(R.id.lvText);
        prepararFavoritos();

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle(getResources().getString(R.string.my_favorites));
        }

        myListAdapter = new AdapterListView(this, miArray);
        assert myList != null;
        myList.setAdapter(myListAdapter);

        //Agregar el adView
        AdView adView = (AdView)this.findViewById(R.id.adViewText);
        AdRequest adRequest = new AdRequest.Builder().build();
        assert adView != null;
        adView.loadAd(adRequest);

    }

    private void prepararFavoritos() {
        String[][] rows = myDBHelper.getFavoritos();
        miArray = new ArrayList<>();
        textView = (TextView) findViewById(R.id.tvFavoritos);

        for (String[] row : rows) {
            Tools.RowCategoria rowCategoria = new Tools.RowCategoria(Integer.parseInt(row[2]), Integer.parseInt(row[3]), row[0], row[1], 1);
            miArray.add(rowCategoria);
        }
        if(miArray.size()==0){
            assert textView != null;
            textView.setText(getResources().getString(R.string.text_none_favorites));
        }
        else{
            textView.setText(getResources().getString(R.string.show_ocurrencias1) + " " + miArray.size() + " " + getResources().getString(R.string.show_ocurrencias3));
        }
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
                this.startActivity(intent);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_actionbar_text, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        MenuItem favoriteItem = menu.findItem(R.id.action_favorites);
        favoriteItem.setVisible(false);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
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

    }


