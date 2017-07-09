(ns investment-tracker.position
  (:require [investment-tracker.db :as db])
  (:use investment-tracker.protocols)
  )

(defrecord Position [security lots])

(defn db->Position [entity-map]
  (db/make-record map->Position entity-map))



