(ns investment-tracker.position
  (:require [investment-tracker.db :as db])
  (:use investment-tracker.protocols
        investment-tracker.tax-lot)
  (:import (investment_tracker.security Security)
           (investment_tracker.tax_lot Tax-Lot)))

(defrecord Position [security lots]
  Storeable
  (db-namespace [_] "position")
  (update-db [this keys] (db/update-record this keys))
  (update-db [this] (db/update-record this [:value :value-date])))

(defn db->Position [entity-map]
  (db/make-record map->Position entity-map))



