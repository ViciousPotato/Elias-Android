package me.viciouspotato.elias_android.elias;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ListFragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import android.widget.TextView;
import android.widget.Toast;
import me.viciouspotato.elias_android.elias.model.BitContent;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A list fragment representing a list of Bits. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link BitDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class BitListFragment extends ListFragment {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        // public void onItemSelected(String id);
        public void onItemSelected(String content);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
       @Override
       public void onItemSelected(String id) {
       }
    };

    private ArrayList<BitContent.BitItem> bitItems;

    public ArrayList<BitContent.BitItem> getBitItems() { return bitItems; }
    public void setBitItems(ArrayList<BitContent.BitItem> items) { bitItems = items; }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BitListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: replace with a real list adapter.
      /*
        setListAdapter(new ArrayAdapter<BitContent.BitItem>(
                getActivity(),
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1,
                BitContent.ITEMS));
                */
      new BitLoadingTask().execute();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        BitContent.BitItem item = (BitContent.BitItem)getListAdapter().getItem(position);
        mCallbacks.onItemSelected(item.content);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

  private class BitLoadingTask extends AsyncTask<Void, Void, String> {
    protected String doInBackground(Void... v) {
      DefaultHttpClient client = new DefaultHttpClient();
      try {
        HttpGet httpGet = new HttpGet("http://viciouspotato.me/bit/0/10");
        HttpResponse response = client.execute(httpGet);
        return IOUtils.toString(response.getEntity().getContent());
      } catch (Exception e) {
        Toast.makeText(BitListFragment.this.getActivity(), e.getMessage(), Toast.LENGTH_LONG);
        e.printStackTrace();
      }

      return "";
    }

    @Override
    protected void onPostExecute(String result) {
      super.onPostExecute(result);

      ArrayList<BitContent.BitItem> arr = new ArrayList<BitContent.BitItem>();

      try {
        JSONObject obj = new JSONObject(new JSONTokener(result));
        JSONObject bits = obj.getJSONObject("bits");
        for (Iterator<String> iter = bits.keys(); iter.hasNext();) {
          String date = iter.next();
          JSONArray dateBits = bits.getJSONArray(date);

          for (int i = 0; i < dateBits.length(); i++) {
            JSONObject o = dateBits.getJSONObject(i);
            BitContent.BitItem item = new BitContent.BitItem(
                o.getString("_id"), o.getString("content")
            );
            arr.add(item);
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }

      BitListFragment.this.setListAdapter(
          new ArrayAdapter<BitContent.BitItem>(
              BitListFragment.this.getActivity(),
              android.R.layout.simple_list_item_activated_1,
              android.R.id.text1,
              arr) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
              View row;

              if (null == convertView) {
                LayoutInflater inflater = BitListFragment.this.getActivity().getLayoutInflater();
                row = inflater.inflate(android.R.layout.simple_list_item_activated_1, null);
              } else {
                row = convertView;
              }

              TextView tv = (TextView) row.findViewById(android.R.id.text1);
              tv.setText(Html.fromHtml(getItem(position).content));

              return row;
            }
          });
    }
  }
}
