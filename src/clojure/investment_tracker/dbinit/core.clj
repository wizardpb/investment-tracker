(ns investment-tracker.dbinit.core
  (:require [investment-tracker.util :refer :all]
            [investment-tracker.db :as db]
            [investment-tracker.system :as sys]
            [datomic.api :as d])
  (:import (java.util Date)))

(def schema-dir "dev-resources/schema/")
(def data-dir "dev-resources/schema/data/")

(def schema-files
  ["system.edn" "inv_account.edn" "fin_trans.edn" "security.edn" "position.edn" "user.edn"])

(def data-files
  ["accounts.edn" "users.edn"])

(defn add-tx-attributes [conn]
  @(d/transact
     conn
     (db/make-txn-dated (Date. 500) (read-string (slurp (str schema-dir "db_txn.edn")))))
  )

(defn load-edn-file [conn index dir fname]
  (let [schema (read-string (slurp (str dir fname)))]
    (println fname "...")
    @(d/transact conn (db/->Txn (Date. (* 1000 index)) :SchemaCreate "Setup" fname schema))))


(defn load-files-from [dir files conn base-index]
  (let [nfiles (count files)]
    (doall
      (map load-edn-file (repeat nfiles conn) (range base-index (+ base-index nfiles)) (repeat nfiles dir) files))))

(defn rebuild-db []
  (d/delete-database sys/db-uri)
  (d/create-database sys/db-uri)
  (let [conn (d/connect sys/db-uri)]
    (add-tx-attributes conn)
    (load-files-from schema-dir schema-files conn 1)
    (load-files-from data-dir data-files conn (inc (count schema-files)))))