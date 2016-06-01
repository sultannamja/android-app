package com.kelsos.mbrc.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.data.library.Artist;
import com.kelsos.mbrc.data.library.Artist_Table;
import com.raizlabs.android.dbflow.list.FlowQueryList;
import com.raizlabs.android.dbflow.sql.language.SQLite;

public class ArtistEntryAdapter extends RecyclerView.Adapter<ArtistEntryAdapter.ViewHolder> {
  private final LayoutInflater inflater;
  private FlowQueryList<Artist> data;
  private Typeface robotoRegular;
  private MenuItemSelectedListener mListener;

  @Inject public ArtistEntryAdapter(Context context) {
    this.data = new FlowQueryList<>(SQLite.select().from(Artist.class).orderBy(Artist_Table.artist, true));
    data.setTransact(true);
    inflater = LayoutInflater.from(context);
    robotoRegular = Typeface.createFromAsset(context.getAssets(), "fonts/roboto_regular.ttf");
  }

  public void setMenuItemSelectedListener(MenuItemSelectedListener listener) {
    mListener = listener;
  }

  /**
   * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
   * an item.
   * <p/>
   * This new ViewHolder should be constructed with a new View that can represent the items
   * of the given type. You can either create a new View manually or inflate it from an XML
   * layout file.
   * <p/>
   * The new ViewHolder will be used to display items of the adapter using
   * {@link #onBindViewHolder(ViewHolder, int)}. Since it will be re-used to display different
   * items in the data set, it is a good idea to cache references to sub views of the View to
   * avoid unnecessary {@link View#findViewById(int)} calls.
   *
   * @param parent The ViewGroup into which the new View will be added after it is bound to
   * an adapter position.
   * @param viewType The view type of the new View.
   * @return A new ViewHolder that holds a View of the given view type.
   * @see #getItemViewType(int)
   * @see #onBindViewHolder(ViewHolder, int)
   */
  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    final View view = inflater.inflate(R.layout.ui_list_single, parent, false);
    ViewHolder holder = new ViewHolder(view, robotoRegular);

    holder.indicator.setOnClickListener(v -> {
      PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
      popupMenu.inflate(R.menu.popup_artist);
      popupMenu.setOnMenuItemClickListener(menuItem -> {
        if (mListener == null) {
          return false;
        }

        mListener.onMenuItemSelected(menuItem, data.get(holder.getAdapterPosition()));
        return true;
      });
      popupMenu.show();
    });

    holder.itemView.setOnClickListener(v -> {
      if (mListener == null) {
        return;
      }

      mListener.onItemClicked(data.get(holder.getAdapterPosition()));
    });
    return holder;
  }

  /**
   * Called by RecyclerView to display the data at the specified position. This method
   * should update the contents of the {@link ViewHolder#itemView} to reflect the item at
   * the given position.
   * <p/>
   * Note that unlike {@link android.widget.ListView}, RecyclerView will not call this
   * method again if the position of the item changes in the data set unless the item itself
   * is invalidated or the new position cannot be determined. For this reason, you should only
   * use the <code>position</code> parameter while acquiring the related data item inside this
   * method and should not keep a copy of it. If you need the position of an item later on
   * (e.g. in a click listener), use {@link ViewHolder#getPosition()} which will have the
   * updated position.
   *
   * @param holder The ViewHolder which should be updated to represent the contents of the
   * item at the given position in the data set.
   * @param position The position of the item within the adapter's data set.
   */
  @Override public void onBindViewHolder(ViewHolder holder, int position) {
    final Artist entry = data.get(position);
    holder.title.setText(TextUtils.isEmpty(entry.getArtist()) ? holder.empty : entry.getArtist());
  }

  /**
   * Returns the total number of items in the data set hold by the adapter.
   *
   * @return The total number of items in this adapter.
   */
  @Override public int getItemCount() {
    return data.size();
  }

  public interface MenuItemSelectedListener {
    void onMenuItemSelected(MenuItem menuItem, Artist entry);

    void onItemClicked(Artist artist);
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.line_one) TextView title;
    @BindView(R.id.ui_item_context_indicator) LinearLayout indicator;
    @BindString(R.string.empty) String empty;

    public ViewHolder(View itemView, Typeface typeface) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      title.setTypeface(typeface);
    }
  }
}