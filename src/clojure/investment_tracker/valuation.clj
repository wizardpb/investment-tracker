(ns investment-tracker.valuation)

;; Valuation multi methods

(defmulti value
          "Value some domain object - generally an Account, Positon or Lot"
          :domain/datatype )
