(ns investment-tracker.tax-lot
  (:require [investment-tracker.db :as db])
  (:use investment-tracker.protocols))

(defrecord Tax-Lot [quantity price create-date effective-date realized-gain unrealized-gain transactions]
  Storeable
  (db-namespace [_] "tax-lot")
  (update-db [this keys] (db/update-record this keys))
  (update-db [this] (update-db this (keys this)))

  Valuable
  (value [this _]
    ; Ignore the effectve date - Position will do this for us
    (* price quantity)
    )
  )

(defn db->Tax-Lot [entity]
  (db/make-record map->Tax-Lot entity))
