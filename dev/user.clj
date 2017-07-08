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
            [investment-tracker.system :as sys])

  (:use investment-tracker.system
        investment-tracker.authentication
        investment-tracker.finenv
        investment-tracker.security
        investment-tracker.account
        investment-tracker.position
        )

  (:import (org.apache.commons.io FileUtils)
           (java.io File)))

(defn conn []
  (get-in sys/system [:db :conn]))

(defn db []
  (d/db (conn)))

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

(s/def ::a int?)
(s/def ::typeas (s/cat ::acol (s/* (s/keys :req [::a]))))
(s/def ::b string?)
(s/def ::typeb (s/keys :req [::b ::typeas]))
