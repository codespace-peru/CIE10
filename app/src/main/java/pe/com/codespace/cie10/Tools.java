package pe.com.codespace.cie10;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
//import android.support.v4.view.MenuItemCompat;
//import android.support.v7.widget.SearchView;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Created by Carlos on 01/03/14.
 */
public class Tools {

    public static boolean isNumeric(String str)
    {
        try
        {
            double d = Double.parseDouble(str);
            return true;
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
    }


    public static class RowCapitulo {
        int numCap;
        String title1;
        String title2;
        RowCapitulo(int num1, String title1, String title2, String codIni, String codFin){
            this.numCap = num1;
            this.title1 = title1;
            this.title2 = title2 + " (" + codIni + "-" + codFin + ")";
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
        Integer favorito;
        RowCategoria(int num1, int num2, String cod, String nomb, Integer fav){
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
        TextHolderCapitulo(View v)
        {
            myNumCap = (TextView) v.findViewById(R.id.tvNumCapitulo);
            myTitle1 = (TextView) v.findViewById(R.id.tvTitle1Group);
            myTitle2 = (TextView) v.findViewById(R.id.tvTitle2Group);
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

    public static String remove(String input) {
        // Cadena de caracteres original a sustituir.
        String original = "áàäéèëíìïóòöúùuÁÀÄÉÈËÍÌÏÓÒÖÚÙÜçÇ";
        // Cadena de caracteres ASCII que reemplazarán los originales.
        String ascii = "aaaeeeiiiooouuuAAAEEEIIIOOOUUUcC";
        String output = input;
        for (int i=0; i<original.length(); i++) {
            // Reemplazamos los caracteres especiales.
            output = output.replace(original.charAt(i), ascii.charAt(i));
        }//for i
        return output;
    }

    public static String remove2(String input) {
        // Descomposición canónica
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        // Nos quedamos únicamente con los caracteres ASCII
        Pattern pattern = Pattern.compile("\\P{ASCII}");
        return pattern.matcher(normalized).replaceAll("");
    }
}