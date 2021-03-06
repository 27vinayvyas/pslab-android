package io.pslab.fragment;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import io.pslab.R;
import io.pslab.others.InitializationVariable;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static io.pslab.others.ScienceLabCommon.scienceLab;

/**
 * Created by viveksb007 on 15/3/17.
 */

public class HomeFragment extends Fragment {

    public static InitializationVariable booleanVariable;
    public static boolean isWebViewShowing = false;
    @BindView(R.id.tv_device_status)
    TextView tvDeviceStatus;
    @BindView(R.id.tv_device_version)
    TextView tvVersion;
    @BindView(R.id.img_device_status)
    ImageView imgViewDeviceStatus;
    @BindView(R.id.tv_device_description)
    TextView deviceDescription;
    @BindView(R.id.tv_connect_msg)
    LinearLayout tvConnectMsg;
    @BindView(R.id.pslab_web_view)
    WebView webView;
    @BindView(R.id.home_content_scroll_view)
    ScrollView svHomeContent;
    @BindView(R.id.web_view_progress)
    ProgressBar wvProgressBar;
    @BindView(R.id.steps_header_text)
    TextView stepsHeader;
    private boolean deviceFound = false, deviceConnected = false;
    private Unbinder unbinder;

    public static HomeFragment newInstance(boolean deviceConnected, boolean deviceFound) {
        HomeFragment homeFragment = new HomeFragment();
        homeFragment.deviceConnected = deviceConnected;
        homeFragment.deviceFound = deviceFound;
        return homeFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (booleanVariable == null) {
            booleanVariable = new InitializationVariable();
        }
        if (scienceLab.calibrated)
            booleanVariable.setVariable(true);
        else
            booleanVariable.setVariable(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        stepsHeader.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        deviceDescription.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        if (deviceFound & deviceConnected) {
            tvConnectMsg.setVisibility(View.GONE);
            try {
                tvVersion.setText(scienceLab.getVersion());
                tvVersion.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imgViewDeviceStatus.setImageResource(R.drawable.icons8_usb_connected_100);
            tvDeviceStatus.setText(getString(R.string.device_connected_successfully));
        } else {
            imgViewDeviceStatus.setImageResource(R.drawable.icons_usb_disconnected_100);
            tvDeviceStatus.setText(getString(R.string.device_not_found));
        }
        deviceDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webView.loadUrl("https://pslab.io");
                svHomeContent.setVisibility(View.GONE);
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        wvProgressBar.setIndeterminate(true);
                        wvProgressBar.setVisibility(View.VISIBLE);
                    }

                    public void onPageFinished(WebView view, String url) {
                        wvProgressBar.setVisibility(View.GONE);
                        webView.setVisibility(View.VISIBLE);
                    }
                });
                isWebViewShowing = true;
            }
        });

        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    WebView webView = (WebView) v;
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_BACK:
                            if (webView.canGoBack()) {
                                webView.goBack();
                                return true;
                            }
                            break;
                        default:
                            return false;
                    }
                }
                return false;
            }
        });

        return view;
    }

    public void hideWebView() {
        webView.setVisibility(View.GONE);
        svHomeContent.setVisibility(View.VISIBLE);
        isWebViewShowing = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
