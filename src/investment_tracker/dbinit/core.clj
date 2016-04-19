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
  (print attrs)
  (let [base {:db/id        #db/id[:db.part/tx]
              :db/txInstant date}]
    [(if attrs (merge base (first attrs)) base)]))

(defn add-tx-attributes [conn]
  @(d/transact conn
               (concat
                 (make-marker-tx (Date. 500))
                 (read-string (slurp (str schema-dir "transaction.edn")))))
  )

(defn load-schema [conn]
  (letfn [(load-schema-file [index fname]
            (let [schema (read-string (slurp (str schema-dir fname)))
                  marker-tx (make-marker-tx
                              (Date. (* 1000 (inc index)))
                              {:db.tx/type        :SchemaCreate
                               :db.tx/actor-id    "Setup"
                               :db.tx/description fname}
                              )
                  ]
              @(d/transact conn (concat marker-tx schema))))]
    (map-indexed load-schema-file schema-files)))

(defn rebuild-db []
  (d/delete-database uri)
  (d/create-database uri)
  (let [conn (d/connect uri)]
    (add-tx-attributes conn)
    (load-schema conn)))