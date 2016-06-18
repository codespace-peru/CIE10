package pe.com.codespace.cie10;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;


/**
 * Creado por Carlos on 17/02/14.
 */
public class AdapterListView extends ArrayAdapter<Tools.RowCategoria> {

    private final Context context;
    private final List<Tools.RowCategoria> values;
    SQLiteHelperCIE10 myDBHelper;

    public AdapterListView(Context pContext, List<Tools.RowCategoria> pValues) {
        super(pContext, R.layout.explistview_capitulo, pValues);
        this.context = pContext;
        this.values = pValues;
        myDBHelper = SQLiteHelperCIE10.getInstance(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view = convertView;
        Tools.TextHolderCategoria holder;

        if(view==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listview_categoria, parent, false);
            holder = new Tools.TextHolderCategoria(view);
            view.setTag(holder);
        }
        else{
            holder = (Tools.TextHolderCategoria) view.getTag();
        }

        final Tools.RowCategoria arts = values.get(position);
        holder.myNumCap.setText(String.valueOf(arts.numCap));
        holder.myNumGrupo.setText(String.valueOf(arts.numGroup));
        holder.myCodigo.setText(arts.codigoCategoria);
        holder.myNombre.setText(arts.nombreCategoria);
        switch (arts.favorito){
            case 0:
                holder.myFavorite.setImageResource(R.drawable.favorito_off);
                break;
            case 1:
                holder.myFavorite.setImageResource(R.drawable.favorito_on);
                break;
        }
        holder.myFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String codCat = arts.codigoCategoria;
                ImageView imageView = (ImageView) view.findViewById(R.id.imgFavorito);
                boolean flag = myDBHelper.es_favorito(codCat);
                if (flag) {
                    myDBHelper.eliminarFavorito(codCat);
                    imageView.setImageResource(R.drawable.favorito_off);
                    Toast.makeText(context, codCat + " " + context.getResources().getString(R.string.text_del_favorites) , Toast.LENGTH_LONG).show();
                } else {
                    myDBHelper.setFavorito(codCat);
                    imageView.setImageResource(R.drawable.favorito_on);
                    Toast.makeText(context, codCat + " " + context.getResources().getString(R.string.text_add_favorites), Toast.LENGTH_LONG).show();
                }
				if (context instanceof TextActivity) {
                    ((TextActivity) context).prepararData();
                    ((TextActivity) context).cargarData();
                }
            }
        });

        return view;
    }
}
