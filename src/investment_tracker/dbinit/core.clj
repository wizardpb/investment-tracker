(ns investment-tracker.dbinit.core
  (:require [investment-tracker.util :refer :all]
            [datomic.api :as d])
  (:import (java.util Date)))

(def uri "datomic:free://localhost:4334/investment-tracker")
(def schema-dir "dev-resources/schema/")
(def data-dir "dev-resources/schema/data/")

(def schema-files
  ["system.edn" "account.edn" "fin_trans.edn" "security.edn" "position.edn"])

(def data-files
  ["accounts.edn"])

(defn add-tx-attributes [conn]
  @(d/transact
     conn
     (make-txn-dated (Date. 500) (read-string (slurp (str schema-dir "db_txn.edn")))))
  )

(defn load-edn-file [conn index dir fname]
  (let [schema (read-string (slurp (str dir fname)))
        txn-details {:date (Date. (* 1000 index))
                     :type :SchemaCreate
                     :actor "Setup"
                     :desc fname}]
    (println fname "...")
    @(d/transact conn (->Txn txn-details schema))))


(defn load-files-from [dir files conn base-index]
  (let [nfiles (count files)]
    (doall
      (map load-edn-file (repeat nfiles conn) (range base-index (+ base-index nfiles)) (repeat nfiles dir) files))))

(defn rebuild-db []
  (d/delete-database uri)
  (d/create-database uri)
  (let [conn (d/connect uri)]
    (add-tx-attributes conn)
    (load-files-from schema-dir schema-files conn 1)
    (load-files-from data-dir data-files conn (inc (count schema-files)))))