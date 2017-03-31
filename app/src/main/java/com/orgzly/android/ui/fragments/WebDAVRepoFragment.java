package com.orgzly.android.ui.fragments;

import android.app.ActionBar;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.orgzly.R;
import com.orgzly.android.repos.Repo;
import com.orgzly.android.repos.RepoFactory;
import com.orgzly.android.repos.WebDAVRepo;
import com.orgzly.android.ui.CommonActivity;
import com.orgzly.android.ui.util.ActivityUtils;
import com.orgzly.android.util.AppPermissions;
import com.orgzly.android.util.MiscUtils;

/**
 * Created by rafa on 29/03/17.
 */

public class WebDAVRepoFragment extends RepoFragment {
    private static final String TAG = WebDAVRepoFragment.class.getName();

    private static final String ARG_REPO_ID = "repo_id";

    /** Name used for {@link android.app.FragmentManager}. */
    public static final String FRAGMENT_TAG = WebDAVRepoFragment.class.getName();

    private WebDAVRepoFragmentListener mListener;

    private ImageView webDAVIcon;
    private Button mWebDAVLinkUnlinkButton;

    private TextInputLayout directoryInputLayout;
    private EditText mDirectory;

    public static WebDAVRepoFragment getInstance() {
        return new WebDAVRepoFragment();
    }

    public static DirectoryRepoFragment getInstance(long repoId) {
        DirectoryRepoFragment fragment = new DirectoryRepoFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_repo_webdav, container, false);

        /* Dropbox link / unlink button. */
        mWebDAVLinkUnlinkButton = (Button) view.findViewById(R.id.fragment_repo_dropbox_link_button);
        mWebDAVLinkUnlinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener.isWebDAVLinked()) {
                    //areYouSureYouWantToUnlink();
                } else {
                    //toogleLink();
                }
            }
        });

        webDAVIcon = (ImageView) view.findViewById(R.id.fragment_repo_dropbox_icon);

        mDirectory = (EditText) view.findViewById(R.id.fragment_repo_dropbox_directory);

        // Not working when done in XML
        mDirectory.setHorizontallyScrolling(false);
        mDirectory.setMaxLines(3);

        mDirectory.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //save();
                return true;
            }
        });

        //setDirectoryFromArgument();

        directoryInputLayout = (TextInputLayout) view.findViewById(R.id.fragment_repo_dropbox_directory_input_layout);

        MiscUtils.clearErrorOnTextChange(mDirectory, directoryInputLayout);

        /* Open a soft keyboard. */
        if (getActivity() != null) {
            ActivityUtils.openSoftKeyboard(getActivity(), mDirectory);
        }

        return view;
    }

    public interface WebDAVRepoFragmentListener extends RepoFragmentListener {
        boolean onWebDAVLinkToggleRequest();
        boolean isWebDAVLinked();
    }
}