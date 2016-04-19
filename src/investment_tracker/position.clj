(ns investment-tracker.position
  (:require [investment-tracker.finenv :refer :all])
  (:import (java.util Date)))

(defn ->Position
  "Create a new Position of a given type for a given instrument. It has no lots"
  [type instrument]
  {:domain/datatype       type
   :position/instrument   instrument
   :position/closing-date (Date. 0)
   :position/closing-value 0M
   :position/lots []
   })

(defn ->EquityPosition [instrument]
  (->Position :EquityPosition instrument))

(defn ->CashPosition [instrument]
  (->Position :CashPosition instrument))

(defn ->TaxLot
  "Create a new TaxLot"
  [attr-map]
  (merge
    {:domain/datatype :TaxLot}
    attr-map))