(ns investment-tracker.dbinit.core
  (:require [datomic.api :as d])
  (:import (java.util Date)))

(def uri "datomic:free://localhost:4334/investment-tracker")
(def schema-dir "dev-resources/schema/")
(def schema-files
  ["system.edn" "instruments.edn" "securities.edn" "position.edn"])

;; Mark as the earliest txn

(defn make-marker-tx
  ([date attrs]
   (let [base {:db/id #db/id[:db.part/tx] :db/txInstant date}]
     [(merge base attrs)]))
  ([date] [{:db/id #db/id[:db.part/tx] :db/txInstant date}]))

(defn add-tx-attributes [conn]
  @(d/transact
     conn
     (concat (make-marker-tx (Date. 500)) (read-string (slurp (str schema-dir "transaction.edn")))))
  )

(defn load-schema-file [conn index fname]
  (let [schema (read-string (slurp (str schema-dir fname)))
        marker-tx (make-marker-tx
                    (Date. (* 1000 index))
                    {:db.tx/type        :SchemaCreate
                     :db.tx/actor-id    "Setup"
                     :db.tx/description fname}
                    )
        ]
    (println fname "...")
    @(d/transact conn (concat marker-tx schema))))

(defn load-schema [conn]
  (let [nfiles (count schema-files)]
    (doall
      (map load-schema-file (repeat nfiles conn) (range 1 (inc nfiles)) schema-files))))

(defn rebuild-db []
  (d/delete-database uri)
  (d/create-database uri)
  (let [conn (d/connect uri)]
    (add-tx-attributes conn)
    (load-schema conn)))