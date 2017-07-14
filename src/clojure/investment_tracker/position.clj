(ns investment-tracker.position
  (:require [investment-tracker.db :as db]
            [datomic.api :as d])
  (:use investment-tracker.protocols
        investment-tracker.security
        investment-tracker.tax-lot)
  )

(defrecord Position [security lots]
  Storeable
  (db-namespace [_] "position")
  (update-db [this keys] (db/update-record this keys))
  (update-db [this] (db/update-record this (keys this))))

(defn db->Position [entity-map]
  (-> (db/make-record map->Position entity-map)
    (update :security #(db->Security (db/get-entity (:db/id %))))))

(defn add-lot [this trade])


