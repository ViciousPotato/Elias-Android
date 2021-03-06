package me.viciouspotato.elias_android.elias;

import android.os.Bundle;
import android.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import me.viciouspotato.elias_android.elias.model.BitContent;

/**
 * A fragment representing a single Bit detail screen.
 * This fragment is either contained in a {@link BitListActivity}
 * in two-pane mode (on tablets) or a {@link BitDetailActivity}
 * on handsets.
 */
public class BitDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private BitContent.BitItem mItem;

    private String itemContent;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BitDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            itemContent = getArguments().getString(ARG_ITEM_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bit_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (itemContent != null) {
            Spanned txt = Html.fromHtml(itemContent);
            ((TextView) rootView.findViewById(R.id.bit_detail)).setText(txt);
        }

        return rootView;
    }
}
