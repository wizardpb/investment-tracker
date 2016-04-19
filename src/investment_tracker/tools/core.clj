(ns investment-tracker.tools.core
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [clojure.data.csv :as csv]))

(def secuirty-files ["NASDAQ.csv" "NYSE.csv"])
(def schema-dir "resources/schema/")
(def data-dir "resources/schema/data/")

(defn make-security-tx [rec]
  (str " {:db/id #db/id[:db.part/user] :instrument/name \"" (second rec) "\" :instrument/type :instrument.type/equity :equity/ticker :" (first rec) "}\n"))

(defn make-security-tx-file []
  (with-open [out-file (io/writer (str schema-dir "securities.edn"))]
    (.write out-file "[\n")
    (doseq [fname secuirty-files]
     (with-open [in-file (io/reader (str data-dir fname))]
       (println fname "...")
       (doseq [rec (filter #(not (some #{\. \^} (first %1))) (rest (csv/read-csv in-file)))]
         (.write out-file (make-security-tx rec)))))
    (.write out-file "]\n")))