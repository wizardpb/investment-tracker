(ns investment-tracker.position
  (:require [investment-tracker.finenv :refer :all])
  (:import (java.util Date Collection)))

(defn ->Position
  "Create a new Position of a given type for a given security. It has no lots"
  [type security lots]
  {:domain/datatype       type
   :position/security   security
   :position/lots lots
   })

(defn ->EquityPosition [security lots]
  (->Position :EquityPosition security lots))

(defn ->CashPosition [security lots]
  (->Position :CashPosition security lots))

(defn ->TaxLot
  "Create a new TaxLot"
  [qty fin-trans]
  {:pre [(instance? Collection fin-trans)]}
  {:domain/datatype         :TaxLot
   :tax-lot/quantity        qty
   :tax-lot/transactions    fin-trans
   :tax-lot/realized-gain   0M
   :tax-lot/unrealized-gain 0M
   }
  )