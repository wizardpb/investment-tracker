(ns investment-tracker.util
  (:require [java-time :as jt])
  (:import (java.util Date)
           (java.text SimpleDateFormat)))

(defn format-date [^Date date format-string]
  (.format (SimpleDateFormat. format-string) date))

(defn make-txn [attrs]
  [(merge {:db/id #db/id[:db.part/tx]} attrs)])

(defn make-txn-dated [date attrs]
  (make-txn {:db/txInstant date}))

(defn ->Txn
  ([{:keys [date type actor desc]} contents]

   (let [details {:db.tx/type        (or type :UserTxn)
                  :db.tx/actor-id    (or actor "System")
                  :db.tx/description (or desc "")}]
     (concat
       (if date
         (make-txn-dated date details)
         (make-txn details))
       contents)))
  ([actor desc contents] (->Txn {:actor actor :desc desc} contents)))

(defn ->Entity [attrs]
  (merge {:db/id #db/id[:db.part/user]} attrs))



