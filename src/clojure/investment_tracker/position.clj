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
  (update-db [this] (update-db this (keys this)))

  Valuable
  (value [this valueDate]
    (transduce
      (comp (filter #(<= 0 (compare valueDate (:effective-date %)))) (map #(value % nil)))
      + 0M (:lots this))))

(defn db->Position [entity-map]
  (-> (db/make-record map->Position entity-map)
    (update :security #(db->Security (db/get-entity (:db/id %))))
    (update :lots #(map db->Tax-Lot %))))

(defn add-lot
  "Add a lot to this position. We assume it has been created in a consistent fashion for this"
  [this tax-lot]
  (-> (assoc this :lots (update-db tax-lot))
    (update-db)
    :id
    (db/get-entity)
    db->Position))


