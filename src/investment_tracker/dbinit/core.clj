(ns investment-tracker.dbinit.core
  (:require [datomic.api :as d])
  (:import (java.util Date)))

(def uri "datomic:free://localhost:4334/investment-tracker")
(def schema-dir "resources/schema/")
(def schema-files
  ["instruments.edn" "securities.edn" "pricing.edn"])

;; Mark as the earliest txn

(defn load-schema [conn]
  (letfn [(load-schema-file [index fname]
            (let [schema (read-string (slurp (str schema-dir fname)))
                  marker-tx [{:db/id        #db/id[:db.part/tx]
                              :db/txInstant (Date. (* 1000 (inc index)))}]]
              @(d/transact conn (concat marker-tx schema))))]
    (map-indexed load-schema-file schema-files)))

(defn rebuild-db []
  (d/delete-database uri)
  (d/create-database uri)
  (let [conn (d/connect uri)]
    (load-schema conn)))