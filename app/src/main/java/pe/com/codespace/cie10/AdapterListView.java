package pe.com.codespace.cie10;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import pe.com.codespace.cie10.Tools.RowCategoria;

/**
 * Creado por Carlos on 17/02/14.
 */
public class AdapterListView extends BaseAdapter implements Filterable {

    private final Context context;
    private ArrayList<RowCategoria> values;
    SQLiteHelperCIE10 myDBHelper;
    CustomFilter filter;
    private ArrayList<RowCategoria> filterList;

    public AdapterListView(Context pContext, ArrayList<RowCategoria> pValues) {
        this.context = pContext;
        this.values = pValues;
        this.filterList = pValues;
        myDBHelper = SQLiteHelperCIE10.getInstance(context);
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public Object getItem(int position) {
        return values.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
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

        final RowCategoria arts = values.get(position);
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
                    Toast.makeText(context, codCat + " " + context.getResources().getString(R.string.text_del_favorites) , Toast.LENGTH_SHORT).show();
                } else {
                    myDBHelper.setFavorito(codCat);
                    imageView.setImageResource(R.drawable.favorito_on);
                    Toast.makeText(context, codCat + " " + context.getResources().getString(R.string.text_add_favorites), Toast.LENGTH_SHORT).show();
                }
				if (context instanceof TextActivity) {
                    ((TextActivity) context).prepararData();
                    ((TextActivity) context).cargarData();
                }
            }
        });

        return view;
    }

    @Override
    public Filter getFilter() {
        if(filter ==null){
            filter = new CustomFilter();
        }

        return filter;
    }

    class CustomFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if(constraint != null && constraint.length()>0){
                constraint = constraint.toString().toUpperCase();
                ArrayList<RowCategoria> filters = new ArrayList<>();
                for(int i=0; i<filterList.size();i++){
                    if(filterList.get(i).nombreCategoria.toUpperCase().contains(constraint)){
                        RowCategoria row = new RowCategoria(filterList.get(i).numCap, filterList.get(i).numGroup, filterList.get(i).codigoCategoria, filterList.get(i).nombreCategoria, filterList.get(i).favorito);
                        filters.add(row);
                    }
                }
                results.count = filters.size();
                results.values = filters;
            }
            else {
                results.count = filterList.size();
                results.values = filterList;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            values = (ArrayList<RowCategoria>) results.values;
            notifyDataSetChanged();
        }
    }
}
