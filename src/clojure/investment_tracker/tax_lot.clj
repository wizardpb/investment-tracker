(ns investment-tracker.tax-lot
  (:require [investment-tracker.db :as db])
  (:use investment-tracker.protocols))

(defrecord Tax-Lot [quantity txns]
  Storeable
  (associate [this txn] (db/transact [{:db/id (:id this) :tax-lot/transactions (:id txn)}]))
  (update-db [this keys] (db/update-record this keys))
  (update-db [this] (update-db this [:quantity]))
  )

(defn db->Tax-Lot [entity]
  (db/make-record map->Tax-Lot entity))
