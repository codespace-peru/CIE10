package pe.com.codespace.cie10;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import java.util.HashMap;
import java.util.List;

/**
 * Creado por Carlos on 23/11/13.
 */
class AdapterExpandableList extends BaseExpandableListAdapter {
    private Context context;
    private List<Tools.RowCapitulo> _listHeader;
    private HashMap<Tools.RowCapitulo, List<Tools.RowGrupo>> _listChild;

    public AdapterExpandableList(Context context, List<Tools.RowCapitulo> listHeader, HashMap<Tools.RowCapitulo, List<Tools.RowGrupo>> listChild){
        this.context = context;
        this._listHeader = listHeader;
        this._listChild = listChild;
    }


    @Override
    public int getGroupCount() {
        return this._listHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listChild.get(this._listHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this._listChild.get(this._listHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View view, ViewGroup viewGroup) {
        View row = view;
        Tools.TextHolderCapitulo holder;

        if(row == null){
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.explistview_capitulo, viewGroup, false);
            holder = new Tools.TextHolderCapitulo(row);
            row.setTag(holder);
        }
        else{
            holder = (Tools.TextHolderCapitulo) row.getTag();
        }

        Tools.RowCapitulo temp = (Tools.RowCapitulo) getGroup(groupPosition);
        holder.myNumCap.setText(String.valueOf(temp.numCap));
        holder.myTitle1.setText(temp.title1);
        holder.myTitle2.setText(temp.title2);
        return row;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View view, ViewGroup viewGroup) {
        View row = view;
        Tools.TextHolderGrupo holder;

        if(row == null){
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.explistview_grupo, viewGroup, false);
            holder = new Tools.TextHolderGrupo(row);
            row.setTag(holder);
        }
        else {
            holder = (Tools.TextHolderGrupo) row.getTag();
        }

        Tools.RowGrupo temp = (Tools.RowGrupo) getChild(groupPosition, childPosition);
        if(temp != null){
            holder.myNumCap.setText(String.valueOf(temp.numCap));
            holder.myNumGrupo.setText(String.valueOf(temp.numGroup));
            holder.myTitle1.setText("(" +temp.codInicial + "-"+ temp.codFinal + ")");
            holder.myTitle2.setText(temp.title);
            holder.myCodInicial.setText(temp.codInicial);
            holder.myCodFinal.setText(temp.codFinal);
        }
        return row;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}




