package pe.com.codespace.cie10;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.MenuItemCompat;

import android.view.MenuItem;
import android.view.View;

import android.widget.ImageView;
import android.widget.TextView;


/**
 * Created por Carlos el 01/03/14.
 */
public class Tools {

    
    public static class RowCapitulo {
        int numCap;
        String title1;
        String title2;
        String codInicial;
        String codFinal;
        RowCapitulo(int num1, String title1, String title2, String codIni, String codFin){
            this.numCap = num1;
            this.title1 = title1;
            this.title2 = title2;
            this.codInicial = codIni;
            this.codFinal = codFin;
        }
    }

    public static class RowGrupo {
        int numCap;
        int numGroup;
        String title;
        String codInicial;
        String codFinal;

        RowGrupo(int num1, int num2, String title, String codInicial, String codFinal){
            this.numCap = num1;
            this.numGroup = num2;
            this.title = title;
            this.codInicial = codInicial;
            this.codFinal = codFinal;

        }
    }

    public static class RowCategoria {
        int numCap;
        int numGroup;
        String codigoCategoria;
        String nombreCategoria;
        int favorito;
        RowCategoria(int num1, int num2, String cod, String nomb, int fav){
            this.numCap = num1;
            this.numGroup = num2;
            this.codigoCategoria = cod;
            this.nombreCategoria = nomb;
            this.favorito = fav;
        }
    }

    public static class TextHolderCapitulo {
        TextView myNumCap;
        TextView myTitle1;
        TextView myTitle2;
        TextView myCodInicial;
        TextView myCodFinal;
        ImageView myImageCap;
        TextHolderCapitulo(View v)
        {
            myNumCap = (TextView) v.findViewById(R.id.tvNumCapitulo);
            myTitle1 = (TextView) v.findViewById(R.id.tvTitle1Group);
            myTitle2 = (TextView) v.findViewById(R.id.tvTitle2Group);
            myCodInicial = (TextView) v.findViewById(R.id.tvCodInicialItem);
            myCodFinal = (TextView) v.findViewById(R.id.tvCodFinalItem);
            myImageCap = (ImageView) v.findViewById(R.id.imgVerCapitulo);
        }
    }

    public static class TextHolderGrupo {
        TextView myNumCap;
        TextView myNumGrupo;
        TextView myTitle1;
        TextView myTitle2;
        TextView myCodInicial;
        TextView myCodFinal;
        TextHolderGrupo(View v)
        {
            myNumCap = (TextView) v.findViewById(R.id.tvNumeroCapitulo);
            myNumGrupo = (TextView) v.findViewById(R.id.tvNumeroGrupo);
            myTitle1 = (TextView) v.findViewById(R.id.tvTitle1Item);
            myTitle2 = (TextView) v.findViewById(R.id.tvTitle2Item);
            myCodInicial = (TextView) v.findViewById(R.id.tvCodInicialItem);
            myCodFinal = (TextView) v.findViewById(R.id.tvCodFinalItem);
        }
    }

    public static class TextHolderCategoria {
        TextView myNumCap;
        TextView myNumGrupo;
        TextView myCodigo;
        TextView myNombre;
        ImageView myFavorite;
        TextHolderCategoria(View v)
        {
            myNumCap = (TextView) v.findViewById(R.id.tvCodCapitulo);
            myNumGrupo = (TextView) v.findViewById(R.id.tvCodGrupo);
            myCodigo = (TextView) v.findViewById(R.id.tvCodCategoria);
            myNombre = (TextView) v.findViewById(R.id.tvNombreCategoria);
            myFavorite = (ImageView) v.findViewById(R.id.imgFavorito);
        }
    }

	public static void MostrarFavoritos(Context context){
        Intent intent = new Intent(context, FavoritosActivity.class);
        context.startActivity(intent);
    }

	public static void QuerySubmit(Context context, MenuItem menuItem, String query){
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra("searchText", query);
        context.startActivity(intent);
        MenuItemCompat.collapseActionView(menuItem);
    }

	public static void ShareApp(Context context){
        Social.share(context, context.getResources().getString(R.string.action_share), context.getResources().getString(R.string.share_description) + " " + Uri.parse("https://play.google.com/store/apps/details?id=pe.com.codespace.cie10"));
    }

}