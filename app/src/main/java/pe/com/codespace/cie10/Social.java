package pe.com.codespace.cie10;

import android.content.Context;
import android.content.Intent;

/**
 * Creado por Carlos on 10/05/2015.
 */
final class Social {
    public static void share(Context ctx, String subject,String text) {
        final Intent intent = new Intent(Intent.ACTION_SEND);

        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        ctx.startActivity(Intent.createChooser(intent, ctx.getString(R.string.action_share)));
    }
}
