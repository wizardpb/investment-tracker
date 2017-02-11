(ns user
  (:require [clojure.pprint :refer :all]
            [clojure.string :as str]
            [clojure.test :refer :all]
            [clojure.spec :as s]
            [datomic.api :as d]
            [clojure.tools.namespace.repl :refer (refresh refresh-all)]
            [clojure.java.classpath :as cp]
            [investment-tracker.dbinit.core :as di]
            [investment-tracker.tools.core :refer :all]
            [investment-tracker.config :as c]
            )

  (:use investment-tracker.db
        investment-tracker.system
        investment-tracker.authentication
        investment-tracker.finenv
        )

  (:import (org.apache.commons.io FileUtils)
           (java.io File)))

(println "Loading user.clj")


(defn db-sys-conn []
  (get-in system [:db :conn]))

(comment
  (di/rebuild-db)
  (def conn (db-sys-conn))
  (def conn (d/connect (:db-uri c/settings)))
  )

(def conn nil)

(defn db []
  (d/db conn))

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
