(ns investment-tracker.finenv
  (:require [clojure.string :as str]
            [clojure.pprint :refer :all]
            [yql-finance.core :as yq])
  (:import (java.util Date)))

(defrecord FinEnv [valueDate equityPrices])

(defn value-date [finenv]
  (:valueDate finenv))

(defn quote-for [ticker finenv]
  ((comp ticker :equityPrices) finenv))

(defn close-price [ticker finenv]
  (:Close (quote-for ticker finenv)))

(defn open-price [ticker finenv]
  (:Open (quote-for ticker finenv)))

(defn ->FinEnvClosePrices
  "Create a FinEnv from Yahoo historical (close) data. Input is the trade date and a list of instruments to include. Only
  equities supported"
  [date tickers]
  (->> (yq/get-historical-data tickers date date)
    (map (fn [[t ql]] [t (first ql)]))
    flatten
    (apply hash-map)
    (->FinEnv date))
  )

(defn ->FinEnvCurrentPrices
  "Create a FinEnv from Yahoo current quote data. Input is a list of instruments to include. Only
equities supported"
  [tickers]
  (->FinEnv (Date.) (yq/get-quotes tickers)))