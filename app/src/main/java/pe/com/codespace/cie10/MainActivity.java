package pe.com.codespace.cie10;

import android.speech.RecognizerIntent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private List<Tools.RowCapitulo> listHeader;
    private HashMap<Tools.RowCapitulo, List<Tools.RowGrupo>> listChild;
    SQLiteHelperCIE10 myDBHelper;
    MenuItem menuItem;

	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_MENU || super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        final ExpandableListView myExpand = (ExpandableListView) findViewById(R.id.explvMain);
        myDBHelper = SQLiteHelperCIE10.getInstance(this);
        prepararData();
        AdapterExpandableList myAdapter = new AdapterExpandableList(this,listHeader,listChild);
        myExpand.setAdapter(myAdapter);
        myExpand.setGroupIndicator(null);

        myExpand.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int lastExpandedPosition = -1;
            @Override
            public void onGroupExpand(int pos) {
                if(lastExpandedPosition != -1 && pos != lastExpandedPosition)
                    myExpand.collapseGroup(lastExpandedPosition);
                lastExpandedPosition = pos;
            }
        });

        myExpand.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                Intent intent = new Intent(view.getContext(), TextActivity.class);
                String codInicial = ((TextView) view.findViewById(R.id.tvCodInicialItem)).getText().toString();
                String codFinal = ((TextView) view.findViewById(R.id.tvCodFinalItem)).getText().toString();
                String grupo = ((TextView) view.findViewById(R.id.tvTitle2Item)).getText().toString();
                intent.putExtra("numerocapitulo",groupPosition + 1);
                intent.putExtra("numerogrupo",childPosition + 1);
                intent.putExtra("nombregrupo",grupo + "\n(" +codInicial + "-" + codFinal + ")");
                intent.putExtra("codigoInicial", codInicial);
                //Concatenamos el 9 para que considere todas las categorías
                codFinal = codFinal + "9";
                intent.putExtra("codigoFinal", codFinal);
                startActivity(intent);
                return true;
            }
        });

        //Agregar el adView
        AdView adView = (AdView)this.findViewById(R.id.adViewMain);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
		
		//Analytics
        Tracker tracker = ((AnalyticsApplication)  getApplication()).getTracker(AnalyticsApplication.TrackerName.APP_TRACKER);
        String nameActivity = getApplicationContext().getPackageName() + "." + this.getClass().getSimpleName();
        tracker.setScreenName(nameActivity);
        tracker.enableAdvertisingIdCollection(true);
        tracker.send(new HitBuilders.AppViewBuilder().build());
    }

    private void prepararData() {
        listHeader = new ArrayList<>();
        listChild = new HashMap<>();
        List<Tools.RowGrupo> gruposList;

        String[][] capitulos = myDBHelper.getCapitulos();
        for(int i=0; i<capitulos.length;i++){
            Tools.RowCapitulo rowGroup = new Tools.RowCapitulo(Integer.parseInt(capitulos[i][0]),capitulos[i][1],capitulos[i][2], capitulos[i][3], capitulos[i][4]);
            listHeader.add(rowGroup);
            gruposList = new ArrayList<>();
            String[][] grupos = myDBHelper.getGrupos(i+1);
            for (String[] grupo : grupos) {
                Tools.RowGrupo rowItem = new Tools.RowGrupo(i, Integer.parseInt(grupo[0]), grupo[1], grupo[2], grupo[3]);
                gruposList.add(rowItem);

            }
            listChild.put(listHeader.get(i),gruposList);
        }
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
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
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



}
