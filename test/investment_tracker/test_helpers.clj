(ns investment-tracker.test-helpers
  (:require [clojure.test :refer :all]
            [investment-tracker.dbinit.core :as di]
            [investment-tracker.system :as sys]
            [investment-tracker.config :as c]
            [investment-tracker.account :refer :all]
            [investment-tracker.dbinit.core :as di]))

(defmacro with-test-db [ & body]
  `(try
     (di/rebuild-db c/test-base)
     (sys/go c/test-base)
     (do ~@body)
     (finally (sys/stop))))

(defn get-test-account []
  (get-account "142365483"))
