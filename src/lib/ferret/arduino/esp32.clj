(native-header "WiFi.h"
               "HTTPClient.h")

(defn wifi-connect [^c_str ssid ^c_str password]
  "WiFi.begin(ssid, password);
   while (WiFi.status() != WL_CONNECTED)
     delay(100);")

(defn http-get
  ([url]
   (http-get url {}))
  ([^c_str u opts]
   "HTTPClient http;
    String url(u);

    // check :query-params
    if (var params = run(opts, obj<keyword>(1313))){
       url += \"?\";
       for_each(param, params){
          auto key = string::to<String>(rt::first(param));
          auto val = string::to<String>(rt::first(rt::rest(param)));
          url += key + \"=\"+ val + \"&\";
       }
    }

    http.begin(url);
    auto http_status = http.GET();

    var data;
    if(http_status == HTTP_CODE_OK)
      data = obj<string>(http.getString().c_str());

    http.end();
    return rt::list(obj<number>(http_status), data);"))

(defn http-post
  ([url]
   (http-post url {}))
  ([^c_str url opts]
   "HTTPClient http;
    http.begin(url);

    //check :headers
    if (var headers = run(opts, obj<keyword>(790))){
       for_each(header, headers){
          auto key = string::to<String>(rt::first(header));
          auto val = string::to<String>(rt::first(rt::rest(header)));
          http.addHeader(key,val);
       }
    }

    auto http_status = http.POST(string::to<String>(run(opts, obj<keyword>(488))));

    var data;
    if(http_status == HTTP_CODE_OK || http_status == HTTP_CODE_CREATED)
      data = obj<string>(http.getString().c_str());

    http.end();
    return rt::list(obj<number>(http_status), data);"))
