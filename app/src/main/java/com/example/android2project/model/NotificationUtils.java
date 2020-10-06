package com.example.android2project.model;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NotificationUtils {
    //https://fcm.googleapis.com/v1/projects/petclan-2fdce/messages:send

    private final static String TAG = "NotificationUtils";

    public static void sendNotification(final Context context, final JSONObject rootObject) {
        final String url = "https://fcm.googleapis.com/fcm/send";
        final String ipayek = "key=AAAAgHuON0g:APA91bH5HRhIng-B5_Zugw3c8RMJTn8YrbZgYbXRNglQayt6fKp3L0e-2bzNRyXUvaBx4sR2MwLI8oVO2Mkz4b0h5K8IZ27FROzg6vH4R64AOoUTpK8MTkftWbpOm9sCNyIB2jI0xCBO";

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", ipayek);
                return headers;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return rootObject.toString().getBytes();
            }
        };

        queue.add(request);
        queue.start();
    }
}
