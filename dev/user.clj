(ns user
  (:require [clojure.pprint :refer :all]
            [clojure.string :as str]
            [clojure.test :refer :all]
            [datomic.api :as d]
            [investment-tracker.dbinit.core :as di]
            [investment-tracker.tools.core :refer :all]
            [investment-tracker.system :as system]
            [clojure.tools.namespace.repl :refer (refresh refresh-all)]
            )
  (:import (org.apache.commons.io FileUtils)
           (java.io File)))

(println "Loading user.clj")

(defn connectdb []
  (def conn (d/connect di/uri)))

(comment
  (di/rebuild-db)
  )

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

(def system nil)

(defn init []
  (alter-var-root #'system (fn [_] (system/system "resources/public"))))

(defn start []
  (alter-var-root #'system system/start))

(defn stop []
  (alter-var-root #'system (fn [s] (when s (system/stop s)))))

(defn go []
  (init)
  (start))