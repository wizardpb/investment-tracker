(ns investment-tracker.dbinit.core
  (:require [datomic.api :as d])
  (:import (java.util Date)))

(def uri "datomic:free://localhost:4334/investment-tracker")
(def schema-dir "resources/schema/")
(def schema-files
  ["instruments.edn" "securities.edn" "pricing.edn"])

;; Mark as the earliest txn

(defn make-marker-tx
  [date & attrs]
  (let [base {:db/id        #db/id[:db.part/tx]
              :db/txInstant date}]
    (if attrs (merge base attrs) base)))

(defn add-tx-attributes [conn]
  @(d/transact conn
               [(make-marker-tx (Date. 500))
                {:db/id                 #db/id[:db.part/db]
                 :db/ident              :db/provenance
                 :db/valueType          :db.type/string
                 :db/cardinality        :db.cardinality/one
                 :db/doc                "Transaction provenance"
                 :db.install/_attribute :db.part/db}
                {:db/id                 #db/id[:db.part/db]
                 :db/ident              :db/actor-id
                 :db/valueType          :db.type/string
                 :db/cardinality        :db.cardinality/one
                 :db/doc                "The actor initiating the txn"
                 :db.install/_attribute :db.part/db}])
  )

(defn load-schema [conn]
  (letfn [(load-schema-file [index fname]
            (let [schema (read-string (slurp (str schema-dir fname)))
                  marker-tx [{:db/id        #db/id[:db.part/tx]
                              :db/txInstant (Date. (* 1000 (inc index)))
                              :db/provenance fname
                              :db/actor-id "Setup"}]]
              @(d/transact conn (concat marker-tx schema))))]
    (map-indexed load-schema-file schema-files)))

(defn rebuild-db []
  (d/delete-database uri)
  (d/create-database uri)
  (let [conn (d/connect uri)]
    (add-tx-attributes conn)
    (load-schema conn)))