(ns investment-tracker.dbinit.core
  (:require [investment-tracker.config :as conf]
            [investment-tracker.util :refer :all]
            [investment-tracker.db :as db]
            [datomic.api :as d]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [clojure.string :as str])
  (:import (java.util Date)))

(def secuirty-files ["NASDAQ.csv" "NYSE.csv"])
(def schema-dir "dev-resources/schema/")
(def data-dir "dev-resources/data/")

(def schema-files
  [ "custodian.edn" "account.edn" "fin_trans.edn" "security.edn" "position.edn" "user.edn" "tax_lot.edn"])

(def data-files
  [ "accounts.edn" "users.edn" "securities.edn"])

(defn add-tx-attributes [conn]
  (println "Adding txn attributes ...")
  @(d/transact
     conn
     (vec (concat (db/make-txn (Date. 500) {}) (read-string (slurp (str schema-dir "db_txn.edn"))))))
  )

(defn load-edn-file [conn index dir fname]
  (let [schema (read-string (slurp (str dir fname)))
        tx-data (db/->Txn (Date. ^Long (* 1000 index)) :SchemaCreate "Setup" fname schema)]
    (println fname "...")
    @(d/transact conn tx-data)))


(defn load-files-from [dir files conn base-index]
  (let [nfiles (count files)]
    (doall
      (map load-edn-file (repeat nfiles conn) (range base-index (+ base-index nfiles)) (repeat nfiles dir) files))))

(defn make-security-tx [rec]
  (let [ticker (str ":" (str/replace (first rec) \. \-))
        name (str "\"" (second rec) "\"")]
    (str
      "{"
      ":db/id #db/id[:db.part/user]"
      " :investment-tracker.security/name " name
      " :investment-tracker.security/type :Equity"
      " :investment-tracker.security/ticker " ticker
      "}\n")))

(defn make-security-tx-file
  "Write out a file of Datomic txns, generating them from a CSV input file of a list
  of securities - first column is the ticker, second is the name.
  Filter out tickers with inadmissible keyword characters - mainly '.' and '^'"
  []
  (with-open [out-file (io/writer (str data-dir "securities.edn"))]
    (.write out-file "[\n")
    (doseq [fname secuirty-files]
      (with-open [in-file (io/reader (str data-dir fname))]
        (println fname "...")
        (doseq [rec (filter #(not (some #{\^} (first %1))) (rest (csv/read-csv in-file)))]
          (.write out-file (make-security-tx rec)))))
    (.write out-file "]\n")))

(defn rebuild-db []
  (d/delete-database (:db-uri conf/settings))
  (d/create-database (:db-uri conf/settings))
  (let [conn (d/connect (:db-uri conf/settings))]
    (reduce +
      (map #(count (:tempids %))
       (concat
         (add-tx-attributes conn)
         (load-files-from schema-dir schema-files conn 1)
         (load-files-from data-dir data-files conn (inc (count schema-files))))))))