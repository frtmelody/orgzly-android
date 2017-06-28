package com.orgzly.android.ui.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.orgzly.BuildConfig;
import com.orgzly.R;
import com.orgzly.android.provider.clients.ReposClient;
import com.orgzly.android.repos.Repo;
import com.orgzly.android.repos.RepoFactory;
import com.orgzly.android.repos.WebDAVRepo;
import com.orgzly.android.ui.CommonActivity;
import com.orgzly.android.ui.util.ActivityUtils;
import com.orgzly.android.util.AppPermissions;
import com.orgzly.android.util.LogUtils;
import com.orgzly.android.util.MiscUtils;
import com.orgzly.android.util.UriUtils;


public class WebDAVRepoFragment extends RepoFragment {
    private static final String TAG = DirectoryRepoFragment.class.getName();

    private static final String ARG_REPO_ID = "repo_id";

    /** Name used for {@link android.app.FragmentManager}. */
    public static final String FRAGMENT_TAG = DirectoryRepoFragment.class.getName();

    private WebDAVRepoRepoFragmentListener mListener;


    private TextInputLayout directoryInputLayout;
    private EditText mUriView;
    private EditText mUsernameView;
    private EditText mUserPasswdView;
    private Button linkWebDAVButton;

    private String username;
    private String password;
    private Uri server_uri;

    public static WebDAVRepoFragment getInstance() {
        return new WebDAVRepoFragment();
    }

    public static WebDAVRepoFragment getInstance(long repoId) {
        WebDAVRepoFragment fragment = new WebDAVRepoFragment();
        Bundle args = new Bundle();

        args.putLong(ARG_REPO_ID, repoId);

        fragment.setArguments(args);

        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public WebDAVRepoFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Would like to add items to the Options Menu.
         * Required (for fragments only) to receive onCreateOptionsMenu() call.
         */
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /* This makes sure that the container activity has implemented
         * the callback interface. If not, it throws an exception
         */
        try {
            mListener = (WebDAVRepoRepoFragmentListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement " + WebDAVRepoRepoFragmentListener.class);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_repo_webdav, container, false);

        directoryInputLayout = (TextInputLayout) view.findViewById(R.id.fragment_repo_webdav_directory_input_layout);
        mUriView = (EditText) view.findViewById(R.id.fragment_repo_webdav_directory);
        mUsernameView = (EditText) view.findViewById(R.id.fragment_repo_webdav_username);
        mUserPasswdView = (EditText) view.findViewById(R.id.fragment_repo_webdav_passwd);
        linkWebDAVButton = (Button) view.findViewById(R.id.fragment_repo_webdav_link_button);

        // Not working when done in XML
        mUriView.setHorizontallyScrolling(false);
        mUriView.setMaxLines(3);

        mUriView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                save();
                return true;
            }
        });

        return view;
    }

    private void save() {

        String directory = mUriView.getText().toString();
        String username = mUsernameView.getText().toString();
        String pssw = mUserPasswdView.getText().toString();

        if (TextUtils.isEmpty(directory)) {
            directoryInputLayout.setError(getString(R.string.can_not_be_empty));
            return;
        } else {
            directoryInputLayout.setError(null);
        }

        Uri uri = Uri.parse(directory + "/remote.php/dav/files/");

        if (!TextUtils.isEmpty(username)) {
            uri = Uri.parse(uri.toString() + username);
        }

        int duration = Toast.LENGTH_SHORT;

        Context context = this.getContext();
        Toast toast = Toast.makeText(context, uri.toString(), duration);
        toast.show();

        Repo repo = RepoFactory.getFromUri(getActivity(), uri);
        if (mListener != null){
            mListener.onRepoCreateRequest(repo);
        }


    }

    /**
     * Callback for options menu.
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (BuildConfig.LOG_DEBUG) LogUtils.d(TAG, menu, inflater);

        inflater.inflate(R.menu.done_or_close, menu);

        /* Remove search item. */
        // menu.removeItem(R.id.options_menu_item_search);
    }

    /**
     * Callback for options menu.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.close:
                if (mListener != null) {
                    mListener.onRepoCancelRequest();
                }
                return true;

            case R.id.done:
                save();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public interface WebDAVRepoRepoFragmentListener extends RepoFragmentListener {
        boolean isLinked();
    }
}
