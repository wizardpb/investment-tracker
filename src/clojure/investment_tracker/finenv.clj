(ns investment-tracker.finenv
  (:require [clojure.string :as str]
            [clj-http.client :as client]
            [clojure.pprint :refer :all])
  (:import
    (java.util Date)))

(defprotocol IFinEnv
  (value-date [this])
  (price [this ticker])
  (price-data [this ticker]))

(defrecord FinEnv [valueDate equityPrices]
  IFinEnv
  (value-date [this] valueDate)
  (price [this ticker]
    (:Close (get equityPrices ticker)))
  (price-data [this ticker]
    (get equityPrices ticker)))

(def yahoo-historical-uri "http://query.yahooapis.com/v1/public/yql")
(def yql-query-pattern
  (str "select * from yahoo.finance.historicaldata "
       "where symbol in (%1$s) "
       "and startDate = \"%2$tY-%2$tm-%2$td\" "
       "and endDate = \"%2$tY-%2$tm-%2$td\"")
  )

(defn format-yahoo-historical-url
  "Build and format a URL to obtain closing prices for tickers on date"
  [tickers date]
  (let [equity-string
        (str "\"" (str/join "\",\"" (map #(name %1) tickers)) "\"")
        ]
    (str yahoo-historical-uri "?"
         "q=" (str/escape (format yql-query-pattern equity-string date) {\space,"%20"})
         "&env=store://datatables.org/alltableswithkeys"
         "&format=json")))

(defn parse-yahoo-historical-json
  "Take a map built from returned JSON data and parse into a map indexing closing prices by ticker"
  [json-quotes]
  (letfn [(parse-ticker [quote-map]
            [
             (keyword (:Symbol quote-map))
             (into {} (map
                        (fn [k]
                          [k (BigDecimal. (k quote-map))])
                        [:Open :High :Low :Close]))])]
          (into {} (map parse-ticker json-quotes)))
  )

(defn lookup-yahoo-historical-prices
  [date equities]
  (let [response (client/get (format-yahoo-historical-url equities date) {:as :json})
        quotes (get-in response [:body :query :results :quote])]
    (parse-yahoo-historical-json
      (if (= 1 (get-in response [:body :query :count])) [quotes] quotes))))

(defn ->FinEnvClosePrices
  "Create a FinEnv from Yahoo historical (close) data. Input is the trade date and a list of instruments to include. Only
  equities supported"
  [date instruments]
  (->FinEnv date (lookup-yahoo-historical-prices date instruments))
  )