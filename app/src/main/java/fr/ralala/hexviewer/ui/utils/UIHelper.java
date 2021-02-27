package fr.ralala.hexviewer.ui.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import fr.ralala.hexviewer.R;

/**
 * ******************************************************************************
 * <p><b>Project HexViewer</b><br/>
 * UI Helper functions
 * </p>
 *
 * @author Keidan
 * <p>
 * ******************************************************************************
 */
public class UIHelper {

  /**
   * Shake a view on error.
   *
   * @param owner   The owner view.
   * @param errText The error text.
   */
  public static void shakeError(TextView owner, String errText) {
    TranslateAnimation shake = new TranslateAnimation(0, 10, 0, 0);
    shake.setDuration(500);
    shake.setInterpolator(new CycleInterpolator(5));
    if (owner != null) {
      if (errText != null)
        owner.setError(errText);
      owner.clearAnimation();
      owner.startAnimation(shake);
    }
  }

  /**
   * Displays a confirm dialog.
   *
   * @param c       The Android context.
   * @param title   The dialog title.
   * @param message The dialog message.
   * @param yes     Listener used when the 'yes' button is clicked.
   */
  public static void showConfirmDialog(final Context c, String title,
                                       String message, final android.view.View.OnClickListener yes) {
    new AlertDialog.Builder(c)
        .setCancelable(false)
        .setIcon(R.mipmap.ic_launcher)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
          if (yes != null) yes.onClick(null);
        })
        .setNegativeButton(android.R.string.no, (dialog, whichButton) -> {
        }).show();
  }

  /**
   * Displays a toast.
   *
   * @param c       The Android context.
   * @param message The toast message.
   */
  public static void toast(final Context c, final String message) {
    /* Create a toast with the launcher icon */
    Toast toast = Toast.makeText(c, message, Toast.LENGTH_LONG);
    TextView tv = toast.getView().findViewById(android.R.id.message);
    if (null != tv) {
      Drawable drawable = ContextCompat.getDrawable(c, R.mipmap.ic_launcher);
      if (drawable != null) {
        final Bitmap b = ((BitmapDrawable) drawable).getBitmap();
        final Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 32, 32, false);
        tv.setCompoundDrawablesWithIntrinsicBounds(new BitmapDrawable(c.getResources(), bitmapResized), null, null, null);
        tv.setCompoundDrawablePadding(5);
      }
    }
    toast.show();
  }


  public interface DialogPositiveClick {
    void onClick(AlertDialog dialog, EditText editText);
  }

  /**
   * Creation of a dialog box with an edittext in it.
   *
   * @param c             The Android context.
   * @param title         Title of the dialog box.
   * @param defaultValue  Default value.
   * @param positiveClick Listener called when clicking on the validation button.
   */
  @SuppressLint("InflateParams")
  public static void createTextDialog(final Context c, String title, String defaultValue, DialogPositiveClick positiveClick) {
    AlertDialog.Builder builder = new AlertDialog.Builder(c);
    builder.setCancelable(false)
        .setIcon(R.mipmap.ic_launcher)
        .setTitle(title)
        .setPositiveButton(android.R.string.yes, null)
        .setNegativeButton(android.R.string.no, (dialog, whichButton) -> {
        });
    LayoutInflater factory = LayoutInflater.from(c);
    builder.setView(factory.inflate(R.layout.content_dialog_simple_text, null));
    final AlertDialog dialog = builder.create();
    dialog.show();
    EditText et = dialog.findViewById(R.id.editText);
    if (et != null)
      et.setText(defaultValue);
    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener((v) -> positiveClick.onClick(dialog, et));
  }
}
