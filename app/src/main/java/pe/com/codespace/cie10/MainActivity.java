package pe.com.codespace.cie10;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.speech.RecognizerIntent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;


public class MainActivity extends ActionBarActivity implements SearchView.OnQueryTextListener {

    private List<Tools.RowCapitulo> listHeader;
    private HashMap<Tools.RowCapitulo, List<Tools.RowGrupo>> listChild;
    SQLiteHelperCIE10 myDBHelper;
    private SearchView searchView;
    MenuItem menuItem;
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
    }

    private void prepararData() {
        listHeader = new ArrayList<Tools.RowCapitulo>();
        listChild = new HashMap<Tools.RowCapitulo, List<Tools.RowGrupo>>();
        List<Tools.RowGrupo> gruposList;

        String[][] capitulos = myDBHelper.getCapitulos();
        for(int i=0; i<capitulos.length;i++){
            Tools.RowCapitulo rowGroup = new Tools.RowCapitulo(Integer.parseInt(capitulos[i][0]),capitulos[i][1],capitulos[i][2], capitulos[i][3], capitulos[i][4]);
            listHeader.add(rowGroup);
            gruposList = new ArrayList<Tools.RowGrupo>();
            String[][] grupos = myDBHelper.getGrupos(i+1);
            for(int j=0;j<grupos.length;j++){
                Tools.RowGrupo rowItem = new Tools.RowGrupo(i, Integer.parseInt(grupos[j][0]), grupos[j][1],grupos[j][2],grupos[j][3]);
                gruposList.add(rowItem);

            }
            listChild.put(listHeader.get(i),gruposList);
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
                Intent intent = new Intent(MainActivity.this, TextActivity.class);
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
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }

}
