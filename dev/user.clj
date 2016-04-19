(ns user
  (:require [clojure.pprint]
            [datomic.api :as d]
            [investment-tracker.dbinit.core :as di]
            [investment-tracker.tools.core :refer :all]
            [investment-tracker.position :refer :all])
  )

(println "loaded user.clj")

;(def conn (d/connect di/uri))