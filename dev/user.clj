(ns user
  (:require [clojure.pprint :refer :all]
            [clojure.string :as str]
            [clojure.test :refer :all]
            [clojure.spec :as s]
            [datomic.api :as d]
            [clojure.tools.namespace.repl :refer (refresh refresh-all)]
            [clojure.java.classpath :as cp]
            [investment-tracker.config :as c]
            [investment-tracker.db :as db]
            [investment-tracker.dbinit.core :as di]
            [investment-tracker.system :as sys]
            [investment-tracker.test-helpers :refer :all])

  (:use investment-tracker.protocols
        investment-tracker.authentication
        investment-tracker.fin-trans
        investment-tracker.finenv
        investment-tracker.security
        investment-tracker.account
        investment-tracker.position
        investment-tracker.tax-lot
        investment-tracker.util
        )

  (:import (org.apache.commons.io FileUtils)
           (java.io File)))

;(defn conn []
;  (get-in sys/system [:db :conn]))
;
;(defn db []
;  (d/db (conn)))

(defn rebuild-db
  [conf]
  (sys/stop)
  (di/rebuild-db conf))

(defn rebuild-dev []
  (rebuild-db c/dev-base))

(defn rebuild-test []
  (rebuild-db c/test-base))

(defn rebuild-dbs []
  (rebuild-dev)
  (rebuild-test))

(def test-dir "test/")

(defn test-ns-sym [fname]
  (symbol (-> fname
            (str/replace #"\.clj$" "")
            (str/replace #"test/" "")
            (str/replace #"/" ".")
            (str/replace #"_" "-"))))

(defn file-paths [^String base-dir]
  (map #(.getPath %1)
    (FileUtils/listFiles
      ^File (File. base-dir)
      #^"[Ljava.lang.String;" (into-array ["clj"])
      true)))

(defn run-my-tests []
  (let [test-files (file-paths test-dir)]
    (doseq [fname test-files]
      (load-file fname))
    (apply run-tests (map test-ns-sym test-files))))

