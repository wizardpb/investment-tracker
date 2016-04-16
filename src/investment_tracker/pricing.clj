(ns investment-tracker.pricing
  (:require [clojure.string :as str]
            [clj-http.client :as client])
  (:import
    (java.util Date)))

(defprotocol IFinEnv
  (closingPrice [this ticker])
  (closeData [this ticker]))

(defrecord FinEnv [tradeDate equityPrices]
  IFinEnv
  (closingPrice [this ticker]
    (:Close (get equityPrices ticker)))
  (closeData [this ticker]
    (get equityPrices ticker)))

(def yahoo-historical-uri "http://query.yahooapis.com/v1/public/yql")
(def yql-query-pattern
  (str "select * from yahoo.finance.historicaldata "
       "where symbol in (%1$s) "
       "and startDate = \"%2$tY-%2$tm-%2$td\" "
       "and endDate = \"%2$tY-%2$tm-%2$td\"")
  )

(defn format-yahoo-url
  "Build and format a URL to obtain closing prices for tickers on date"
  [tickers date]
  (let [equity-string
        (str "\"" (str/join "\",\"" (map #(name %1) tickers)) "\"")
        ]
    (str yahoo-historical-uri "?"
         "q=" (str/escape (format yql-query-pattern equity-string date) {\space,"%20"})
         "&env=store://datatables.org/alltableswithkeys"
         "&format=json")))

(defn parse-yahoo-json
  "Take a map built from returned JSON data and parse into a map indexing closing prices by ticker"
  [json-quotes]
  (letfn [(parse-ticker [quote-map]
            [
             (keyword (:Symbol quote-map))
             (into {} (map
                        (fn [k] [k (BigDecimal. (k quote-map))])
                        [:Open :High :Low :Close]))])]
          (into {} (map parse-ticker json-quotes)))
  )

(defn yahoo-lookup-prices
  [date equities]
  (parse-yahoo-json
    (get-in
      (client/get (format-yahoo-url equities date) {:as :json})
      [:body :query :results :quote])))

(defn ->FinEnvFromYahoo
  "Create a FinEnv from Yahoo historical data. Input is the trade date and a list of instruments to include. Only
  equities supported"
  [^Date date instruments]
  (->FinEnv date (yahoo-lookup-prices date instruments))
  )