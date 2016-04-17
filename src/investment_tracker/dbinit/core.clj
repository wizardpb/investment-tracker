(ns investment-tracker.schema.core
  (:require
    [datomic.api :as d]
    [clojure.data.csv :as csv])
  )

(def uri "datomic:free://localhost:4334/investment-tracker")
(def schema-dir "resources/schema/")
(def schema-files
  ["instruments.edn" "securities.edn"])

(defn load-schema [conn]
  (doseq [fname schema-files]
    (println fname "...")
    @(d/transact conn (read-string (slurp (str schema-dir fname)))))
  "Done"
  )