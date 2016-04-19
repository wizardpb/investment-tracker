(ns investment-tracker.tools.core
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [clojure.data.csv :as csv]))

(def secuirty-files ["NASDAQ.csv" "NYSE.csv"])
(def schema-dir "resources/schema/")
(def data-dir "resources/schema/data/")

(defn make-security-tx [rec]
  (let [ticker (str ":" (first rec))
        name (second rec)]
    (str
      "{:db/id #db/id[:db.part/user] :domain/datatype :Equity :instrument/name \"" name "\" :instrument/type :instrument.type/equity :equity/ticker " ticker "}\n")))

(defn make-security-tx-file
  "Write out a file of Datomic txns, generating them from a CSV input file of a list
  of securities - first column is the ticker, second is the name.
  Filter out tickers with inadmissible keyword characters - mainly '.' and '^'"
  []
  (with-open [out-file (io/writer (str schema-dir "securities.edn"))]
    (.write out-file "[\n")
    (doseq [fname secuirty-files]
     (with-open [in-file (io/reader (str data-dir fname))]
       (println fname "...")
       (doseq [rec (filter #(not (some #{\. \^} (first %1))) (rest (csv/read-csv in-file)))]
         (.write out-file (make-security-tx rec)))))
    (.write out-file "]\n")))