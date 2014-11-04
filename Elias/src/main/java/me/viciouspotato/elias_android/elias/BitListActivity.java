package me.viciouspotato.elias_android.elias;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.cengalabs.flatui.FlatUI;
import me.viciouspotato.elias_android.elias.util.MultipartEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.*;


/**
 * An activity representing a list of Bits. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link BitDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link BitListFragment} and the item details
 * (if present) is a {@link BitDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link BitListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class BitListActivity extends Activity
        implements BitListFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Uri fileUri;
    private final String TAG = "BitList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FlatUI.initDefaultValues(this);
        FlatUI.setDefaultTheme(FlatUI.GRASS);

        setContentView(R.layout.activity_bit_list);

        getActionBar().setBackgroundDrawable(FlatUI.getActionBarDrawable(this, FlatUI.GRASS, false));

        if (findViewById(R.id.bit_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((BitListFragment) getFragmentManager()
                    .findFragmentById(R.id.bit_list))
                    .setActivateOnItemClick(true);
        }

        // TODO: If exposing deep links into your app, handle intents here.
    }

    /**
     * Callback method from {@link BitListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(BitDetailFragment.ARG_ITEM_ID, id);
            BitDetailFragment fragment = new BitDetailFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .replace(R.id.bit_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, BitDetailActivity.class);
            detailIntent.putExtra(BitDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
      if (resultCode == RESULT_OK) {
        /*
        * BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        * bmOptions.inJustDecodeBounds = true; // ??
        * BitmapFactory.decodeFile(fileUri.getPath(), bmOptions);
        * */
        Bundle extras = data.getExtras();
        Bitmap imageBitmap = (Bitmap)extras.get("data");

        new UploadTask().execute(imageBitmap);

      } else if (resultCode == RESULT_CANCELED) {

      } else {
      }
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.listbar, menu);

    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.takephoto:
        openCamera();
        break;
      default:
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  void openCamera() {
    try {
      Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      // Create temp file
      // File outputFile = File.createTempFile(TAG, "jpg", getCacheDir());
      // fileUri = Uri.fromFile(outputFile);

      // intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
      startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    } catch (Exception e) {
      Log.w("Warning", "File creation failed.");
    }
  }

  private class UploadTask extends AsyncTask<Bitmap, Void, Void> {
    protected Void doInBackground(Bitmap... bitmaps) {
      setProgress(0);

      Bitmap bitmap = bitmaps[0];
      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
      InputStream in = new ByteArrayInputStream(stream.toByteArray());

      DefaultHttpClient httpClient = new DefaultHttpClient();
      try {
        HttpPost httpPost = new HttpPost("http://viciouspotato.me/upload");

        MultipartEntity reqEntity = new MultipartEntity();
        reqEntity.addPart("snap", "snap.jpg", in);

        httpPost.setEntity(reqEntity);

        HttpResponse response = null;
        try {
          response = httpClient.execute(httpPost);
        } catch (ClientProtocolException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }

        Log.i(TAG, response.toString());
      } finally {

      }

      return null;
    }
  }
}
