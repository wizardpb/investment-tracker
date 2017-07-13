(ns investment-tracker.tax-lot
  (:require [investment-tracker.db :as db])
  (:use investment-tracker.protocols))

(defrecord Tax-Lot [quantity transactions]
  Storeable
  (db-namespace [_] "tax-lot")
  (update-db [this keys] (db/update-record this keys))
  (update-db [this] (update-db this (keys this)))
  )

(defn db->Tax-Lot [entity]
  (db/make-record map->Tax-Lot entity))
