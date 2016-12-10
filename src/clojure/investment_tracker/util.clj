(ns investment-tracker.util
  (:require [java-time :as jt])
  (:import (java.util Date)
           (java.text SimpleDateFormat)))

(defn format-date [^Date date format-string]
  (.format (SimpleDateFormat. format-string) date))



