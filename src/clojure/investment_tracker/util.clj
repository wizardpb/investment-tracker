(ns investment-tracker.util
  (:require [java-time :as jt])
  (:import (java.util Date)
           (java.text SimpleDateFormat)))

(defn format-date
  ([^Date date format-string]
   (.format (SimpleDateFormat. format-string) date))
  ([^Date date] (format-date date "yyyy-MM-DD")))

(defn parse-date
  ([date-string format-string]
   (.parse (SimpleDateFormat. format-string) date-string))
  ([date-string] (parse-date date-string "yyyy-MM-DD")))



