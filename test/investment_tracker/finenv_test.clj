(ns investment-tracker.finenv-test
  (:require [clojure.test :refer :all]
            [investment-tracker.finenv :refer :all]
            [clj-http.client :as client])
  (:import (java.util Date))
  )

(def yahoo-response-template
  {:status 200,
   :headers {"X-YQL-Host" "main-2ee0aaab-fc35-11e5-bac7-d4ae52974741",
             "Server" "ATS",
             "Age" "0",
             "Content-Type" "application/json; charset=UTF-8",
             "Access-Control-Allow-Origin" "*",
             "X-Content-Type-Options" "nosniff",
             "Connection" "close",
             "Transfer-Encoding" "chunked",
             "Date" "Mon, 18 Apr 2016 12:09:35 GMT",
             "Cache-Control" "no-cache"},
   :body {:query {:count nil,
                  :created "2016-04-18T12:09:35Z",
                  :lang "en-US",
                  :results {:quote nil}}},
   :request-time 85,
   :trace-redirects ["http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.historicaldata%20where%20symbol%20in%20(\"YHOO\")%20and%20startDate%20=%20\"2016-04-15\"%20and%20endDate%20=%20\"2016-04-15\"&env=store://datatables.org/alltableswithkeys&format=json"],
   :orig-content-encoding nil})

(deftest yahoo-api
  (testing "URL formatting"
    (let [date (Date. 116 0 31)
          equities [:YHOO :AAPL]]
      (is (=
            (format-yahoo-historical-url equities date)
            (str "http://query.yahooapis.com/v1/public/yql?q="
                 (str "select%20*%20from%20yahoo.finance.historicaldata%20"
                      "where%20symbol%20in%20(\"YHOO\",\"AAPL\")%20"
                      "and%20startDate%20=%20\"2016-01-31\"%20"
                      "and%20endDate%20=%20\"2016-01-31\"")
                 "&env=store://datatables.org/alltableswithkeys"
                 "&format=json")))))
  (testing "JSON parsing"
    (is (=
          (parse-yahoo-historical-json [{:Symbol "YHOO",
                                :Date            "2016-04-13",
                                :Open            "36.939999",
                                :High            "37.349998",
                                :Low             "36.900002",
                                :Close           "37.310001",
                                :Volume          "17158700",
                                :Adj_Close       "37.310001"}
                               {:Symbol    "AAPL",
                                :Date      "2016-04-13",
                                :Open      "110.800003",
                                :High      "112.339996",
                                :Low       "110.800003",
                                :Close     "112.040001",
                                :Volume    "32691800",
                                :Adj_Close "112.040001"}])
          {:YHOO {
                  :Open  36.939999M,
                  :High  37.349998M,
                  :Low   36.900002M,
                  :Close 37.310001M
                  }
           :AAPL {
                  :Open  110.800003M,
                  :High  112.339996M,
                  :Low   110.800003M,
                  :Close 112.040001M,
                  }})
        ))

  (testing "Env creation - multiple tickers"
    (let [env (with-redefs
                [client/get
                 (fn [url opts]
                   (assoc-in
                     (assoc-in yahoo-response-template [:body :query :count] 2)
                     [:body :query :results :quote]
                     [{:Symbol "YHOO",
                       :Date "2016-04-15",
                       :Open "37.130001",
                       :High "37.150002",
                       :Low "36.419998",
                       :Close "36.509998",
                       :Volume "19016200",
                       :Adj_Close "36.509998"}
                      {:Symbol "AAPL",
                       :Date "2016-04-15",
                       :Open "112.110001",
                       :High "112.300003",
                       :Low "109.730003",
                       :Close "109.849998",
                       :Volume "46418500",
                       :Adj_Close "109.849998"}])
                   )]
                (->FinEnvClosePrices
                  (Date. 116 3 15)
                  [:YHOO :AAPL]))]
      (is (= (value-date env) (Date. 116 3  15)))
      (is (= (price env :YHOO) 36.509998M))
      (is (= (price-data env :YHOO) {:Close 36.509998M
                                    :High  37.150002M
                                    :Low   36.419998M
                                    :Open  37.130001M})))
    )
  (testing "Env creation - single ticker. Quote data is slightly different, no list"
    (let [env (with-redefs
                [client/get
                 (fn [url opts]
                   (assoc-in
                     (assoc-in yahoo-response-template [:body :query :count] 1)
                     [:body :query :results :quote]
                     {:Symbol    "YHOO",
                      :Date      "2016-04-15",
                      :Open      "37.130001",
                      :High      "37.150002",
                      :Low       "36.419998",
                      :Close     "36.509998",
                      :Volume    "19016200",
                      :Adj_Close "36.509998"}
                     ))]
                (->FinEnvClosePrices
                  (Date. 116 3 15)
                  [:YHOO]))]
      (is (= (value-date env) (Date. 116 3 15)))
      (is (= (price env :YHOO) 36.509998M))
      (is (= (price-data env :YHOO) {:Close 36.509998M
                                    :High  37.150002M
                                    :Low   36.419998M
                                    :Open  37.130001M})))
    )
  )