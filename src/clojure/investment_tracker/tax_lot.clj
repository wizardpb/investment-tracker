(ns investment-tracker.tax-lot
  (:require [investment-tracker.db :as db])
  (:use investment-tracker.protocols))

(defrecord Tax-Lot [quantity transactions]
  Storeable
  (update-db [this keys] (db/update-record this keys))
  (update-db [this] (update-db this [:quantity]))
  )

(defn db->Tax-Lot [entity]
  (db/make-record map->Tax-Lot entity))
