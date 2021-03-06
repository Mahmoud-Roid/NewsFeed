package com.example.newsfeed;

import android.app.VoiceInteractor;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.lang.reflect.Method;
import java.util.List;

public class Health extends Fragment{
    ListView listView;
    ListAdapter listAdapter;

    TextView tv_state;
    ProgressBar progressBar;
    private final String API_URL = "https://content.guardianapis.com/healthcare-network?page-size=25&api-key=c1e2801c-b32e-4c89-a833-a626c5da5273";

    public Health() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_view, container, false);
        listView = view.findViewById(R.id.list_view);

        tv_state = view.findViewById(R.id.tv_state);
        progressBar = view.findViewById(R.id.progress_circular);
        listView.setEmptyView(tv_state);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sendRequestByVolley(API_URL);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fields fields = listAdapter.getItem(position);
                String urlLink = fields.getUrl();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(urlLink));
                startActivity(intent);
            }
        });

    }

    private void sendRequestByVolley(String api_url) {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, api_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                listAdapter = new ListAdapter(getContext(), QueryUtils.parseResponse(response), R.color.category_health);
                listView.setAdapter(listAdapter);
                progressBar.setVisibility(View.GONE);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof NetworkError) {
                            tv_state.setText(R.string.networkError);
                        } else if (error instanceof ServerError) {
                            tv_state.setText(R.string.serverError);
                        } else if (error instanceof AuthFailureError) {
                            tv_state.setText(R.string.authFailureError);
                        } else if (error instanceof ParseError) {
                            tv_state.setText(R.string.parseError);
                        } else if (error instanceof NoConnectionError) {
                            tv_state.setText(R.string.noConnectionError);
                        } else if (error instanceof TimeoutError) {
                            tv_state.setText(R.string.timeoutError);
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
        requestQueue.add(stringRequest);
    }

}