package fr.ralala.hexviewer.ui.adapters;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import fr.ralala.hexviewer.ApplicationCtx;
import fr.ralala.hexviewer.R;
import fr.ralala.hexviewer.models.Line;
import fr.ralala.hexviewer.models.LineData;
import fr.ralala.hexviewer.models.LineFilter;
import fr.ralala.hexviewer.ui.utils.UIHelper;
import fr.ralala.hexviewer.utils.SysHelper;

/**
 * ******************************************************************************
 * <p><b>Project HexViewer</b><br/>
 * Adapter used with the hex text list view.
 * </p>
 *
 * @author Keidan
 * <p>
 * License: GPLv3
 * <p>
 * ******************************************************************************
 */
public class HexTextArrayAdapter extends SearchableListArrayAdapter<Line> {
  private static final int ID = R.layout.listview_hex_row;
  private Set<Integer> mSelectedItemsIds;
  private final ApplicationCtx mApp;
  private final LineNumbersTitle mTitle;

  public static class LineNumbersTitle {
    public TextView titleLineNumbers;
    public TextView titleContent;
  }

  public HexTextArrayAdapter(final Context activity, final List<LineData<Line>> objects,
                             LineNumbersTitle title,
                             UserConfig userConfigPortrait,
                             UserConfig userConfigLandscape) {
    super(activity, ID, objects, userConfigPortrait, userConfigLandscape);
    mTitle = title;
    mSelectedItemsIds = new HashSet<>();
    mApp = ApplicationCtx.getInstance();
  }

  /**
   * Returns true if the item is selected.
   *
   * @param position The position
   * @return boolean
   */
  @Override
  protected boolean isSelected(int position) {
    return mSelectedItemsIds.contains(position);
  }

  /**
   * Performs a hexadecimal search in a plain text string.
   *
   * @param line     The current line.
   * @param index    The line index.
   * @param query    The query.
   * @param tempList The output list.
   * @param loc      The locale.
   */
  @Override
  protected void extraFilter(final LineData<Line> line, int index, String query, final ArrayList<LineFilter<Line>> tempList, Locale loc) {
    final int maxLength = String.format("%X", getItemsCount()).length();
    final String s = String.format("%0" + maxLength + "X", index);
    if (s.toLowerCase(loc).contains(query)) {
      tempList.add(new LineFilter<>(line, index));
    }
  }

  /**
   * Toggles the item selection.
   *
   * @param position Item position.
   */
  public void toggleSelection(int position, boolean checked) {
    if (checked) {
      mSelectedItemsIds.add(position);
    } else {
      mSelectedItemsIds.remove(position);
    }
    notifyDataSetChanged();
  }

  /**
   * Removes the item selection.
   */
  public void removeSelection() {
    mSelectedItemsIds = new HashSet<>();
    notifyDataSetChanged();
  }


  /**
   * Returns the selected ids.
   *
   * @return SparseBooleanArray
   */
  public List<Integer> getSelectedIds() {
    List<Integer> li = new ArrayList<>(mSelectedItemsIds);
    Collections.sort(li);
    return li;
  }


  /**
   * Inflate the view.
   *
   * @param convertView This value may be null.
   * @return The view.
   */
  protected @NonNull
  View inflateView(final View convertView) {
    View v = convertView;
    if (v == null) {
      final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      if (inflater != null) {
        v = inflater.inflate(ID, null);
        Holder holder = new Holder();
        holder.content = v.findViewById(R.id.content);
        holder.lineNumbers = v.findViewById(R.id.lineNumbers);
        v.setTag(holder);
      }
    }
    return v == null ? new View(getContext()) : v;
  }


  /**
   * Applies the necessary changes if the "updated" field is true.
   *
   * @param tv        TextView
   * @param fd        FilterData
   * @param landscape Landscape mode ?
   */
  private void applyUpdated(final TextView tv, final LineFilter<Line> fd, boolean landscape) {
    String str = fd.getData().getValue().getPlain();
    if (fd.getData().isUpdated()) {
      SpannableString spanString = new SpannableString(str);
      spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
      tv.setText(spanString);
    } else {
      tv.setText(str);
    }
    if (mApp.isLineNumber()) {
      tv.measure(0, 0);       //must call measure!
      Point p = UIHelper.getScreenSize(mApp);
      if (!landscape && ((100 * tv.getMeasuredWidth()) / p.x) >= 75)
        tv.setText(str.substring(0, 48));
    }
  }

  /**
   * Fills the view.
   *
   * @param v        This can't be null.
   * @param position The position of the item within the adapter's data set of the item whose view we want.
   */
  @Override
  protected void fillView(final @NonNull View v, final int position) {
    if (v.getTag() != null) {
      final Holder holder = (Holder) v.getTag();
      LineFilter<Line> fd = getFilteredList().get(position);

      if (fd.getData().isFalselyDeleted())
        return;
      boolean landscape;
      if (mApp.isLineNumber()) {
        final int maxLength = String.format("%X", getItemsCount() * SysHelper.MAX_BY_ROW).length();
        final String s = String.format("%0" + maxLength + "X", fd.getOrigin() * SysHelper.MAX_BY_ROW);
        final @ColorInt int color = ContextCompat.getColor(getContext(),
            R.color.colorLineNumbers);
        holder.lineNumbers.setText(s);
        holder.lineNumbers.setTextColor(color);
        landscape = applyUserConfig(holder.lineNumbers);
        holder.lineNumbers.setVisibility(View.VISIBLE);

        if (position == 0) {
          mTitle.titleLineNumbers.setText(String.format("%" + maxLength + "s", " "));
          mTitle.titleContent.setText(getContext().getString(R.string.title_content));
          mTitle.titleContent.setTextColor(color);
        }
        applyUserConfig(mTitle.titleContent);
        applyUserConfig(mTitle.titleLineNumbers);

      } else {
        holder.lineNumbers.setVisibility(View.GONE);
        landscape = false;
      }
      applyUpdated(holder.content, fd, landscape);
      holder.content.setTextColor(ContextCompat.getColor(getContext(),
          fd.getData().isUpdated() ? R.color.colorTextUpdated : R.color.textColor));
      applyUserConfig(holder.content);
      v.setBackgroundColor(ContextCompat.getColor(getContext(), isSelected(position) ? R.color.colorAccent : R.color.windowBackground));
    }
  }

  private static class Holder {
    private TextView lineNumbers;
    private TextView content;
  }
}

