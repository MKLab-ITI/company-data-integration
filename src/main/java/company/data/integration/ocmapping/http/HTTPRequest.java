/*
 * Copyright 2017 vasgat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package company.data.integration.ocmapping.http;

import static com.google.common.net.HttpHeaders.USER_AGENT;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author vasgat
 */
public class HTTPRequest {

    private String url;
    private String response;
    private String urlParameters;

    public HTTPRequest(String url) {
        this.url = url;
        this.urlParameters = "";
        this.response = "";
    }

    public void GET(String method) throws UnsupportedEncodingException {
        if (urlParameters.equals("")) {
            url = url + "/" + URLEncoder.encode(method, "UTF-8");
        } else {
            url = url + "/" + URLEncoder.encode(method, "UTF-8") + "?" + urlParameters;
        }
        sendGet();
    }

    public String getResponse() {
        return response;
    }

    public void addParameter(String name, String value) {

        if (urlParameters.equals("")) {
            urlParameters += name + "=" + value;
        } else {
            urlParameters += "&" + name + "=" + value;
        }
    }

    // HTTP GET request
    private void sendGet() {

        try {
            System.out.println("URL: " + url);

            URL obj = new URL(url);

            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("GET");

            //add request header
            con.setRequestProperty("User-Agent", USER_AGENT);

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            this.response = response.toString();

        } catch (MalformedURLException ex) {
            Logger.getLogger(HTTPRequest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ProtocolException ex) {
            Logger.getLogger(HTTPRequest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HTTPRequest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
